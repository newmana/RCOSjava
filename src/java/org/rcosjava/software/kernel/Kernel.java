package org.rcosjava.software.kernel;

import java.io.*;
import java.util.*;

import org.rcosjava.RCOS;
import org.rcosjava.hardware.cpu.CPU;
import org.rcosjava.hardware.cpu.Context;
import org.rcosjava.hardware.cpu.Instruction;
import org.rcosjava.hardware.cpu.Interrupt;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.messaging.messages.os.ChIn;
import org.rcosjava.messaging.messages.os.ChOut;
import org.rcosjava.messaging.messages.os.NumIn;
import org.rcosjava.messaging.messages.os.NumOut;
import org.rcosjava.messaging.messages.os.RegisterInterruptHandler;
import org.rcosjava.messaging.messages.os.Schedule;
import org.rcosjava.messaging.messages.os.SemaphoreClose;
import org.rcosjava.messaging.messages.os.SemaphoreCreate;
import org.rcosjava.messaging.messages.os.SemaphoreOpen;
import org.rcosjava.messaging.messages.os.SemaphoreSignal;
import org.rcosjava.messaging.messages.os.SemaphoreWait;
import org.rcosjava.messaging.messages.os.SharedMemoryCloseMessage;
import org.rcosjava.messaging.messages.os.SharedMemoryCreateMessage;
import org.rcosjava.messaging.messages.os.SharedMemoryOpenMessage;
import org.rcosjava.messaging.messages.os.SharedMemoryReadMessage;
import org.rcosjava.messaging.messages.os.SharedMemorySizeMessage;
import org.rcosjava.messaging.messages.os.SharedMemoryWriteMessage;
import org.rcosjava.messaging.messages.universal.InstructionExecution;
import org.rcosjava.messaging.messages.universal.NullProcess;
import org.rcosjava.messaging.messages.universal.ProcessFinished;
import org.rcosjava.messaging.messages.universal.ReadBytes;
import org.rcosjava.messaging.messages.universal.RunningToBlocked;
import org.rcosjava.messaging.messages.universal.RunningToReady;
import org.rcosjava.messaging.messages.universal.SetContext;
import org.rcosjava.messaging.messages.universal.WriteBytes;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.software.interrupt.InterruptHandler;
import org.rcosjava.software.interrupt.ProcessFinishedInterruptHandler;
import org.rcosjava.software.interrupt.TimerInterruptHandler;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;
import org.rcosjava.software.process.RCOSProcess;
import org.rcosjava.software.process.ProcessState;

import org.apache.log4j.*;

/**
 * This is a simple kernel implementation for RCOS.java. It is a microkernel
 * based system responsible for: sending messages to appropriate components for
 * system calls, sending messages to appropriate components for interrupts, and
 * receiving messages from various components to do h/w specific tasks.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 13/02/96 Created version to handle simple interaction between CPU and
 * Terminal. DJ </DD>
 * <DD> 23/03/96 Modified to use packages. DJ </DD>
 * <DD> 07/04/97 Fixed hang when multitasking with blocked processes. AN </DD>
 *
 * <DD> 08/04/97 Receives and responds to Quantum message. AN </DD>
 * <DD> 09/04/97 Fixed CHOUT problem and sent message as id not Kernel. </DD>
 *
 * <DD> 10/10/97 Fixed semaphore name, was one off. AN </DD>
 * <DD> 12/10/97 Implementation of File and Shared Memory interrupts. AN </DD>
 *
 * <DD> 11/08/98 Removed String comparison for instructions. AN </DD>
 * <DD> 12/08/98 Implemented Shared Memory and File system calls. AN </DD>
 * <DD> 13/08/98 Fixed incomplete/buggy Semaphore and Shared memory. AN </DD>
 *
 * <DD> 02/04/2001 Added schedule message call. AN </DD>
 * <DD> 31/12/2001 Again fiddled with CHOUT dec was not correct after changes to
 * CPU AN. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 1st February 1996
 * @see org.rcosjava.hardware.cpu.CPU
 * @version 1.00 $Date$
 */
public class Kernel extends OSMessageHandler
{
  /**
   * Serial id.
   */
  private static final long serialVersionUID = 8922262686985556996L;

  /**
   * Logging class.
   */
  private final static Logger log = Logger.getLogger(Kernel.class);

  /**
   * The name of the Kernel registered to the post office.
   */
  private final static String MESSENGING_ID = "Kernel";

  /**
   * Number of ticks before a process is pre-empted.
   */
  private int quantum = 2;

  /**
   * Total number of timer interrupts.
   */
  private int timerInterrupts = 0;

  /**
   * The number of ticks a process has been on.
   */
  private int timeProcessOn;

  /**
   * CPU accessed by the Kernel.
   */
  private CPU myCPU;

  /**
   * Stores the interrupts.
   */
  private HashMap interruptHandlers = new HashMap();

  /**
   * This is the schedule message to send to the process scheduler. A global so
   * that it's not reinitialized.
   */
  private transient Schedule scheduleMessage = new Schedule(this);

  /**
   * Currently executing RCOS process.
   */
  private RCOSProcess currentProcess;

  /**
   * Whether the kernel is currently executing a process or not.
   */
  private boolean runningProcess;

  /**
   * Step execution.
   */
  private boolean stepExecution = false;

  /**
   * Initialise Kernel
   *
   * @param postOffice central post office for messaging system
   */
  public Kernel(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);
    myCPU = new CPU(this);
    runningProcess = false;

    // Register timer interrupt.
    RegisterInterruptHandler msg = new RegisterInterruptHandler(this,
      new TimerInterruptHandler(this));
    sendMessage(msg);

    // Register process finished interrupt.
    msg = new RegisterInterruptHandler(this,
      new ProcessFinishedInterruptHandler(this));
    sendMessage(msg);
  }

  /**
   * Sets the quantum (number of execution cycles per process) that the kernel
   * uses. Lower values means higher process switching but more processes are
   * executed within a time period. Higher values reduces the context switching
   * but less process are executed.
   *
   * @param newQuantum the new value of the quantum.
   */
  public void setQuantum(int newQuantum)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Setting Quantum:" + newQuantum);
    }
    quantum = newQuantum;
  }

  /**
   * Updates the kernel's own temporary timeProcess on counter from the CPU's
   * getTicks method.
   */
  public void setCurrentProcessTicks()
  {
    if (log.isDebugEnabled())
    {
      log.debug("Setting Current Process Ticks: " + myCPU.getTicks());
    }
    timeProcessOn = myCPU.getTicks();
  }

  /**
   * Sets the process code (the non-changing executing program).
   *
   * @param newProcessCode the value to set the process code to.
   */
  public void setProcessCode(Memory newProcessCode)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Setting process code: " + newProcessCode);
    }
    myCPU.setCode(newProcessCode);
  }

  /**
   * Sets the process stacking (the working area of the executing program).
   *
   * @param newProcessStack the value to set the process stack to.
   */
  public void setProcessStack(Memory newProcessStack)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Setting process stack: " + newProcessStack);
    }

    myCPU.setStack(newProcessStack);
    if ((newProcessStack != null) && (myCPU.getCode() != null) &&
        (myCPU.getContext() != null))
    {
      if (log.isDebugEnabled())
      {
        log.debug("Setting process running");
      }
      processRunning();
    }
  }

  /**
   * Sets the current context of the CPU based on a given process.
   *
   * @param newContext the context that a process contains.
   */
  public void setContext(Context newContext)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Setting context: " + newContext);
    }
    myCPU.setContext(newContext);
  }

  /**
   * @return the number of CPU ticks minue the internel kernel value
   *      timeProcessOn.
   */
  public int getCurrentProcessTicks()
  {
    return (myCPU.getTicks() - timeProcessOn);
  }

  /**
   * @return the current PID that is executing in the Kernel
   */
  public int getCurrentProcessPID()
  {
    if (log.isDebugEnabled())
    {
      log.debug("Current PID: " + currentProcess.getPID());
    }
    return currentProcess.getPID();
  }

  // getName

  /**
   * Take a name off the stack from current StackPointer - fixed a bug where it
   * was missing it by one ie. my_sem became amy_se.
   *
   * @return The Name value
   */
  public String getName()
  {
    if (log.isDebugEnabled())
    {
      log.debug("Getting name");
      log.debug("Name length: " + myCPU.getStack().read(myCPU.getContext().
          getStackPointer()));
    }
    int length = myCPU.getStack().read(myCPU.getContext().
        getStackPointer());

    char[] name = new char[length];

    myCPU.getContext().setStackPointer(
        (short) (myCPU.getContext().getStackPointer() - length));

    int index = 0;

    for (int count = myCPU.getContext().getStackPointer();
        count < myCPU.getContext().getStackPointer() + length;
        count++)
    {
      name[index] = (char) myCPU.getStack().read(count);
      index++;
    }

    if (log.isDebugEnabled())
    {
      log.debug("Got name: " + name);
    }
    return (new String(name));
  }

  /**
   * @return whether there is a current process or not (null or not) currently
   *      being executed.
   */
  public boolean runningProcess()
  {
    return runningProcess;
  }

  /**
   * Set that a process is running.
   */
  public void processRunning()
  {
    runningProcess = true;
  }

  /**
   * Description of the Method
   */
  public void processStopped()
  {
    runningProcess = false;
  }

  /**
   * Execute one step in the process.
   */
  public void step()
  {
    stepExecution = true;
  }

  /**
   * Whether the CPU/Kernel is paused.
   *
   * @return whether the CPU/Kernel is paused.
   */
  public boolean isPaused()
  {
    return myCPU.isPaused();
  }

  /**
   * Description of the Method
   */
  public void pause()
  {
    myCPU.pause();
  }

  /**
   * Description of the Method
   */
  public void unpause()
  {
    myCPU.unpause();
  }

  /**
   * Sets the current process and context as well as the code and stack segments
   * of the CPU to null. That there is no currently running process.
   */
  private void nullProcess()
  {
    if (log.isDebugEnabled())
    {
      log.debug("Null process");
    }

    // Make sure that the messages are processed before stopping and removing
    // the current process.
    postOffice.deliverMessages();

    processStopped();
    myCPU.setContext(new Context());
    myCPU.setCode(null);
    myCPU.setStack(null);
  }

  /**
   * Sets the process as finished and handles the interrupt (should handle
   * process finished interrupt).
   */
  public void killProcess()
  {
    if (log.isDebugEnabled())
    {
      log.debug("Killing process");
    }
    myCPU.setCodeFinished();
  }

  /**
   * Performs one execution cycle on the CPU if there is a running process.
   * Handles the interrupts and increments the CPU tick.
   */
  public void performInstructionExecutionCycle()
  {
    // Schedule and then perform an instruction
    if ((!myCPU.isPaused()) || (stepExecution))
    {
      myCPU.performInstructionExecutionCycle(stepExecution);
      sendMessage(scheduleMessage);
    }

    postOffice.deliverMessages();

    //Sends context and current instruction.
    if ((!myCPU.isPaused() && runningProcess()) ||
        (stepExecution && runningProcess))
    {
      if (log.isDebugEnabled())
      {
        log.debug("Setting context and instruction");
      }

      SetContext contextMsg = new SetContext(this, myCPU.getContext());
      sendMessage(contextMsg);

      InstructionExecution executionMsg = new InstructionExecution(this,
          myCPU.getStack());
      sendMessage(executionMsg);

      // Ensure all messages are delivered.
      postOffice.deliverMessages();
    }

    if (stepExecution)
    {
      stepExecution = false;
    }
  }

  /**
   * This occurs when a new process is to be executed on the CPU. If there was
   * an existing process it's existing stack is saved and the new process is put
   * in its place. This include overwriting the existing code pages and calling
   * the setCurrentProcess method.
   *
   * @param newProcess Description of Parameter
   */
  public void switchProcess(RCOSProcess newProcess)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Switching process to: " + newProcess);
    }

    currentProcess = newProcess;
    setContext(currentProcess.getContext());

    //Get new memory
    MemoryRequest memRead = new MemoryRequest(newProcess.getPID(),
        MemoryManager.CODE_SEGMENT, newProcess.getCodePages() *
        MemoryManager.PAGE_SIZE);
    ReadBytes msg = new ReadBytes(this, memRead);
    sendMessage(msg);

    memRead = new MemoryRequest(newProcess.getPID(),
        MemoryManager.STACK_SEGMENT,
        newProcess.getStackPages() * MemoryManager.PAGE_SIZE);
    msg = new ReadBytes(this, memRead);
    sendMessage(msg);
  }

  /**
   * Called by device drivers and other sources of interrupts passed an
   * Interrupt which is added to the InterruptQueue
   *
   * @param newInterrupt the new interrupt to add to be processed.
   */
  public void addInterrupt(Interrupt newInterrupt)
  {
    myCPU.addInterrupt(newInterrupt);
  }

  /**
   * Inserts a new interrupt handler into the list of interrupt handlers. This
   * must be done before handleInterrupt is called.
   *
   * @param newIH contains the interrupt handler to add.
   */
  public void insertInterruptHandler(InterruptHandler newIH)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Adding handler: " + newIH);
      log.debug("Adding handler type: " + newIH.getType());
    }
    interruptHandlers.put(newIH.getType(), newIH);
  }

  /**
   * Handles an interrupt. Determines the type of the interrupt and executes
   * based on these. Timer Interrupt and Process finished are handled internally
   * within the kernel. All others must have a interrupt handler registered with
   * the kernel.
   *
   * @param anInterrupt the interrupt to be handled.
   */
  public void handleInterrupt(Interrupt anInterrupt)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Handling interrupt: " + anInterrupt.getType());
      log.debug("Handling interrupt time: " + anInterrupt.getTime());
    }

    InterruptHandler aIH = (InterruptHandler) interruptHandlers.get(
      anInterrupt.getType());

    //If aIH is equal to null then the Interrupt Handler doesn't
    //exist.  Process otherwise.  May-be add an error message or
    //something later.
    if (aIH != null)
    {
      aIH.handleInterrupt();
    }
  }

  /**
   * A return value is usually generated by an system call.
   *
   * @param value to set the process stack at the current stack pointer address.
   */
  public void returnValue(short value)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Return Value: " + value);
      log.debug("Context: " + myCPU.getContext());
    }

    // place return value onto stack
    myCPU.getContext().incStackPointer();
    myCPU.getStack().write(myCPU.getContext().getStackPointer(), value);
  }

  /**
   * Performs the necessary actions for the CSP instruction. Uses the current
   * values of the CPU.
   *
   * @throws java.io.IOException
   */
  public void systemCall() throws java.io.IOException
  {
    Instruction call = myCPU.getContext().getInstructionRegister();

    if (log.isDebugEnabled())
    {
      log.debug("Handling System Call: " + call);
    }

    if (call.isChIn())
    {
      ChIn message = new ChIn(this, getCurrentProcess().getTerminalId());
      sendMessage(message);
    }
    else if (call.isChOut())
    {
      ChOut message = new ChOut(this, getCurrentProcess().getTerminalId(),
          (char)  myCPU.getStack().read(
            myCPU.getContext().getStackPointer()));
      sendMessage(message);
    }
    else if (call.isNumIn())
    {
      if (log.isDebugEnabled())
      {
        log.debug("Handle System Call NUM IN: " +
            getCurrentProcess().getTerminalId());
      }
      NumIn message = new NumIn(this, getCurrentProcess().getTerminalId());
      sendMessage(message);
    }
    else if (call.isNumOut())
    {
      NumOut message = new NumOut(this, getCurrentProcess().getTerminalId(),
        (short) myCPU.getStack().read(
          myCPU.getContext().getStackPointer()));

      sendMessage(message);
      myCPU.getContext().decStackPointer();
    }
    else if (call.isStrOut())
    {
      int length = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());

      myCPU.getContext().decStackPointer();

      myCPU.getContext().setStackPointer(
          (short) (myCPU.getContext().getStackPointer() - length));

      for (int count = myCPU.getContext().getStackPointer() + 1;
          count <= myCPU.getContext().getStackPointer() + length;
          count++)
      {
        ChOut message = new
            ChOut(this, getCurrentProcess().getTerminalId(),
            (char) myCPU.getStack().read(count));
        sendMessage(message);
      }
    }
    else if (call.isSemaphoreCreate())
    {
      int initValue = myCPU.getStack().read(
          myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();

      // get the semaphore's name using getName()
      // send the message
      SemaphoreCreate message = new SemaphoreCreate(
          this, getName(), getCurrentProcess().getPID(), initValue);
      sendMessage(message);
    }
    else if (call.isSemaphoreOpen())
    {
      //get the semaphore's name using getName()
      //send the message
      SemaphoreOpen message = new SemaphoreOpen(
          this, getName(), getCurrentProcess().getPID());

      sendMessage(message);
    }
    else if (call.isSemaphoreClose())
    {
      int semaphoreId = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());
      myCPU.getContext().decStackPointer();

      if (log.isDebugEnabled())
      {
        log.debug("Kernel SemClose got: " + semaphoreId + "," + getCurrentProcess().getPID());
      }

      SemaphoreClose message = new SemaphoreClose(
          this, semaphoreId, getCurrentProcess().getPID());
      sendMessage(message);
    }
    else if (call.isSemaphoreSignal())
    {
      int semaphoreId = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());
      myCPU.getContext().decStackPointer();

      if (log.isDebugEnabled())
      {
        log.debug("Kernel SemSig got: " + semaphoreId);
        log.debug("Kernel SemSig got: " + myCPU.getStack().read(myCPU.getContext().
            getStackPointer()+1));
        log.debug("Kernel SemSig got: " + myCPU.getStack().read(myCPU.getContext().
            getStackPointer()-1));
      }

      SemaphoreSignal message = new SemaphoreSignal(
          this, semaphoreId, getCurrentProcess().getPID());
      sendMessage(message);
    }
    else if (call.isSemaphoreWait())
    {
      int semaphoreId = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());
      myCPU.getContext().decStackPointer();

      if (log.isDebugEnabled())
      {
        log.debug("Kernel SemWait got: " + semaphoreId);
        log.debug("Kernel SemWait got: " + myCPU.getStack().read(myCPU.getContext().
            getStackPointer()+1));
        log.debug("Kernel SemWait got: " + myCPU.getStack().read(myCPU.getContext().
            getStackPointer()-1));
      }

      SemaphoreWait message = new SemaphoreWait(
          this, semaphoreId, getCurrentProcess().getPID());

      sendMessage(message);
    }
    else if (call.isSharedMemoryCreate())
    {
      int length = myCPU.getStack().read(
          myCPU.getContext().getStackPointer());

      myCPU.getContext().decStackPointer();

      //get the shared mem name using getName()
      //send the message
      SharedMemoryCreateMessage message = new SharedMemoryCreateMessage(this,
          getName(), getCurrentProcess().getPID(), length);

      sendMessage(message);
    }
    else if (call.isSharedMemoryOpen())
    {
      //get the shared mem name using getName()
      //send the message
      SharedMemoryOpenMessage message = new SharedMemoryOpenMessage(this,
          getName(), getCurrentProcess().getPID());

      sendMessage(message);
    }
    else if (call.isSharedMemoryClose())
    {
      int sharedMemId = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());

      myCPU.getContext().decStackPointer();

      SharedMemoryCloseMessage message = new SharedMemoryCloseMessage(this,
          sharedMemId, getCurrentProcess().getPID());
      sendMessage(message);
    }
    else if (call.isSharedMemoryRead())
    {
      int offset = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());

      myCPU.getContext().decStackPointer();

      int sharedMemId = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());

      myCPU.getContext().decStackPointer();

      SharedMemoryReadMessage message = new SharedMemoryReadMessage(this,
          sharedMemId, offset, getCurrentProcess().getPID());

      sendMessage(message);
    }
    else if (call.isSharedMemoryWrite())
    {
      int newValue = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());

      myCPU.getContext().decStackPointer();

      int offset = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());

      myCPU.getContext().decStackPointer();

      int sharedMemId = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());

      SharedMemoryWriteMessage message = new SharedMemoryWriteMessage(this,
          sharedMemId, offset, (short) newValue, getCurrentProcess().getPID());

      sendMessage(message);
    }
    else if (call.isSharedMemorySize())
    {
      int sharedMemId = myCPU.getStack().read(myCPU.getContext().
          getStackPointer());

      myCPU.getContext().decStackPointer();

      SharedMemorySizeMessage message = new SharedMemorySizeMessage(this,
          sharedMemId);

      sendMessage(message);
    }
    else if (call.isFork())
    {
    }
    //  System.err.println( "EXEC" );
//                    break;
    //  System.err.println( "F_ALLOC" );
//                    break;
    //  System.err.println( "F_OPEN" );
//                    break;
    //  System.err.println( "F_CREAT" );
//                    break;
    //  System.err.println( "F_CLOSE" );
//                    break;
    //  System.err.println( "F_EOF" );
//                    break;
    //  System.err.println( "F_DEL" );
//                    break;
    //  System.err.println( "F_READ" );
//                    break;
    //  System.err.println( "F_WRITE" );
//                    break;
  }

  /**
   * When an I/O event or other block event occurs the current process is
   * removed from the CPU and a RunningToBlocked message is sent with the
   * oldCurrent process.
   */
  public void blockCurrentProcess()
  {
    if (log.isDebugEnabled())
    {
      log.debug("Block Current Process");
      log.debug("To Execute" + myCPU.hasCodeToExecute());
      log.debug("Current Process" + getCurrentProcess().getPID());
    }

    if (myCPU.hasCodeToExecute())
    {
      //Save current process and process context
      RCOSProcess oldCurrent = getCurrentProcess();
      oldCurrent.addToCPUTicks(getCurrentProcessTicks());
      oldCurrent.setStatus(ProcessState.BLOCKED);

      // decrement program counter to force the blocking
      // instruction to be re-executed when the process is woken up
      // BUT only do it if the body of the message is null
      //oldCurrent.getContext().decProgramCounter();

      // no need to get a copy of the code as it won't change
      // Send a message to the ProcessScheduler to update old current
      // processes data structures
      RunningToBlocked msg = new RunningToBlocked(this, oldCurrent);
      sendMessage(msg);

      //Set the current process to nothing
      nullProcess();
    }
  }

  /**
   * Check if timerInterrupts < QUANTUM<BR>
   * if it is ignore<BR>
   * else update CPU ticks and do ProcessSwitch
   */
  public void handleTimer()
  {
    if (log.isDebugEnabled())
    {
      log.debug("Handling timer interrupt");
    }

    timerInterrupts++;

    if (timerInterrupts >= quantum)
    {
      timerInterrupts = 0;

      if (myCPU.hasCodeToExecute())
      {
        if (log.isDebugEnabled())
        {
          log.debug("Current processes context: " +
              getCurrentProcess().getContext());
        }

        //Save currently executing process
        RCOSProcess oldProcess = getCurrentProcess();
        oldProcess.addToCPUTicks(getCurrentProcessTicks());

        //Assume that the stack is the only thing worth writing back that the
        //programs cannot modify their own memory?
        MemoryRequest memSave = new MemoryRequest(getCurrentProcess().getPID(),
            MemoryManager.STACK_SEGMENT,
            myCPU.getStack().getSegmentSize(), myCPU.getStack());
        WriteBytes msg = new WriteBytes(this, memSave);
        sendMessage(msg);

        nullProcess();

        // Send a message to the ProcessScheduler to update old current
        // processes data structures.  But only if the current process is still
        // running.  The above null process may take the process from Read to
        // Blocked - if that is the case don't send this.

        if (oldProcess.getState() == ProcessState.RUNNING)
        {
          RunningToReady tmpMsg = new RunningToReady(this, oldProcess);
          sendMessage(tmpMsg);
        }
      }
    }
  }

  /**
   * An inbuilt handler for the process finished interrupt. When a process is
   * finished execute the old process is removed, the process time calculated,
   * and a ProcessFinished message is sent.
   */
  public void processFinished()
  {
    if (log.isDebugEnabled())
    {
      log.debug("Handling Process Finished Interrupt");
      log.debug("Current Process PID: " + getCurrentProcess().getPID());
    }

    //Get the current process and add CPU ticks spent.
    RCOSProcess oldCurrent = getCurrentProcess();
    oldCurrent.addToCPUTicks(getCurrentProcessTicks());

    //Let process scheduler and others know that the process has finished
    ProcessFinished tmpMsg = new ProcessFinished(this, oldCurrent);
    sendMessage(tmpMsg);

    //Set the current process to null
    nullProcess();
  }

  /**
   * Private method to give access to the current running process.
   *
   * @return The CurrentProcess value
   */
  private RCOSProcess getCurrentProcess()
  {
    currentProcess.setContext(myCPU.getContext());
    return currentProcess;
  }

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
    os.writeInt(quantum);
    os.writeInt(timerInterrupts);
    os.writeInt(timeProcessOn);
    os.writeObject(myCPU);
    os.writeObject(interruptHandlers);
    os.writeObject(currentProcess);
    os.writeBoolean(runningProcess);
    os.writeBoolean(stepExecution);
  }

  /**
   * Handle deserialization of the contents.  Ensures non-serializable
   * components correctly created.
   *
   * @param is stream that is being read.
   */
  private void readObject(ObjectInputStream is) throws IOException,
      ClassNotFoundException
  {
    register(MESSENGING_ID, RCOS.getOSPostOffice());

    quantum = is.readInt();
    timerInterrupts = is.readInt();
    timeProcessOn = is.readInt();

    myCPU = (CPU) is.readObject();
    myCPU.setKernel(this);

    interruptHandlers = (HashMap) is.readObject();
    currentProcess = (RCOSProcess) is.readObject();
    runningProcess = is.readBoolean();
    stepExecution = is.readBoolean();

    scheduleMessage = new Schedule(this);
  }
}