package net.sourceforge.rcosjava.software.kernel;

import java.io.*;
import java.util.*;

import net.sourceforge.rcosjava.hardware.cpu.CPU;
import net.sourceforge.rcosjava.hardware.cpu.Context;
import net.sourceforge.rcosjava.hardware.cpu.Interrupt;
import net.sourceforge.rcosjava.hardware.cpu.Instruction;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.interrupt.InterruptHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.os.ChIn;
import net.sourceforge.rcosjava.messaging.messages.os.ChOut;
import net.sourceforge.rcosjava.messaging.messages.os.NumIn;
import net.sourceforge.rcosjava.messaging.messages.os.NumOut;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.os.Schedule;
import net.sourceforge.rcosjava.messaging.messages.os.SemaphoreClose;
import net.sourceforge.rcosjava.messaging.messages.os.SemaphoreCreate;
import net.sourceforge.rcosjava.messaging.messages.os.SemaphoreOpen;
import net.sourceforge.rcosjava.messaging.messages.os.SemaphoreSignal;
import net.sourceforge.rcosjava.messaging.messages.os.SemaphoreWait;
import net.sourceforge.rcosjava.messaging.messages.universal.NullProcess;
import net.sourceforge.rcosjava.messaging.messages.universal.RunningToBlocked;
import net.sourceforge.rcosjava.messaging.messages.universal.RunningToReady;
import net.sourceforge.rcosjava.messaging.messages.universal.ProcessFinished;
import net.sourceforge.rcosjava.messaging.messages.universal.ReadBytes;
import net.sourceforge.rcosjava.messaging.messages.universal.WriteBytes;
import net.sourceforge.rcosjava.messaging.messages.universal.SetContext;
import net.sourceforge.rcosjava.messaging.messages.universal.InstructionExecution;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;

/**
 * This is  a simple kernel implementation for RCOS.java.  It is a microkernel
 * based system responsible for: sending messages to appropriate components for
 * system calls, sending messages to appropriate components for interrupts,
 * and receiving messages from various components to do h/w specific tasks.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 13/02/96 Created version to handle simple interaction between CPU and Terminal. DJ
 * </DD><DD>
 * 23/03/96  Modified to use packages. DJ
 * </DD><DD>
 * 07/04/97  Fixed hang when multitasking with blocked processes. AN
 * </DD><DD>
 * 08/04/97  Receives and responds to Quantum message. AN
 * </DD><DD>
 * 09/04/97  Fixed CHOUT problem and sent message as id not Kernel.
 * </DD><DD>
 * 10/10/97  Fixed semaphore name, was one off. AN
 * </DD><DD>
 * 12/10/97  Implementation of File and Shared Memory interrupts. AN
 * </DD><DD>
 * 11/08/98  Removed String comparison for instructions.  AN
 * </DD><DD>
 * 12/08/98  Implemented Shared Memory and File system calls. AN
 * </DD><DD>
 * 13/08/98  Fixed incomplete/buggy Semaphore and Shared memory. AN
 * </DD><DD>
 * 02/04/2001 Added schedule message call.
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.hardware.cpu.CPU
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 1st February 1996
 */
public class Kernel extends OSMessageHandler
{
  private static final String MESSENGING_ID = "Kernel";

  private int quantum = 2;
  private int timerInterrupts = 0;
  private int timeProcessOn;
  private CPU myCPU;
  private Hashtable interruptHandlers = new Hashtable();
  private Schedule scheduleMessage = new Schedule(this);
  private RCOSProcess currentProcess;
  private boolean runningProcess;

  /**
   * Initialise Kernel
   * @param postOffice	central post office for messaging system
   */
  public Kernel(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);
    myCPU = new CPU(this);
    runningProcess = false;
  }

  /**
   * Sets the quantum (number of execution cycles per process) that the kernel
   * uses.  Lower values means higher process switching but more processes
   * are executed within a time period.  Higher values reduces the context
   * switching but less process are executed.
   *
   * @param newQuantum the new value of the quantum.
   */
  public void setQuantum(int newQuantum)
  {
    quantum = newQuantum;
  }

  /**
   * Updates the kernel's own temporary timeProcess on counter from the CPU's
   * getTicks method.
   */
  public void setCurrentProcessTicks()
  {
    timeProcessOn = myCPU.getTicks();
  }

  /**
   * @return the number of CPU ticks minue the internel kernel value
   * timeProcessOn.
   */
  public int getCurrentProcessTicks()
  {
    return(myCPU.getTicks() - timeProcessOn);
  }

  /**
   * @return the current PID that is executing in the Kernel
   */
  public int getCurrentProcessPID()
  {
    return currentProcess.getPID();
  }

  /**
   * Private method to give access to the current running process.
   */
  private RCOSProcess getCurrentProcess()
  {
    currentProcess.setContext(myCPU.getContext());
    return currentProcess;
  }

  /**
   * @return whether there is a current process or not (null or not) currently
   * being executed.
   */
  public boolean runningProcess()
  {
    return runningProcess;
  }

  /**
   * Set that a process is running.
   *
   */
  public void processRunning()
  {
    runningProcess = true;
  }

  public void processStopped()
  {
    runningProcess = false;
  }

  public void pause()
  {
    myCPU.pause();
  }

  public void unpause()
  {
    myCPU.unpause();
  }

  /**
   * Sets the current process and context as well as the code and stack segments
   * of the CPU to null.  That there is no currently running process.
   */
  public void nullProcess()
  {
    processStopped();
    myCPU.setContext(new Context());
    myCPU.setProcessCode(null);
    myCPU.setProcessStack(null);
  }

  /**
   * Sets the process as finished and handles the interrupt (should handle
   * process finished interrupt).
   */
  public void killProcess()
  {
    myCPU.setProcessFinished();
    myCPU.handleInterrupts();
  }

  /**
   * Performs one execution cycle on the CPU if there is a running process.
   * Handles the interrupts and increments the CPU tick.
   */
  public void performInstructionExecutionCycle()
  {
    if (!myCPU.isPaused())
    {
      sendMessage(scheduleMessage);
      myCPU.performInstructionExecutionCycle();
    }

    postOffice.deliverMessages();

    //Sends context and current instruction.
    if (!myCPU.isPaused() && runningProcess())
    {
      SetContext contextMsg = new SetContext(this, myCPU.getContext());
      sendMessage(contextMsg);
      InstructionExecution executionMsg = new
        InstructionExecution(this, myCPU.getProcessStack());
      sendMessage(executionMsg);
    }
  }

  /**
   * Sets the process code (the non-changing executing program).
   *
   * @param newProcessCode the value to set the process code to.
   */
  public void setProcessCode(Memory newProcessCode)
  {
    myCPU.setProcessCode(newProcessCode);
  }

  /**
   * Sets the process stacking (the working area of the executing program).
   *
   * @param newProcessStack the value to set the process stack to.
   */
  public void setProcessStack(Memory newProcessStack)
  {
    myCPU.setProcessStack(newProcessStack);
    if ((newProcessStack != null) && (myCPU.getProcessCode() != null) &&
      (myCPU.getContext() != null))
    {
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
    myCPU.setContext(newContext);
  }

  /**
   * This occurs when a new process is to be executed on the CPU.  If there
   * was an existing process it's existing stack is saved and the new process
   * is put in its place.  This include overwriting the exsting code pages and
   * calling the setCurrentProcess method.
   */
  public void switchProcess(RCOSProcess newProcess)
  {
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
      newProcess.getStackPages()*MemoryManager.PAGE_SIZE);
    msg = new ReadBytes(this, memRead);
    sendMessage(msg);
  }

  /**
   * Provides direct access to the CPU to generate an interrupts.  It adds a
   * new interrupt to be dealt with by the CPU (during a execution cycle
   * probably).  The interrupts are stored and generated by the CPU not
   * the Kernel.  The Kernel handles the interrupts.
   *
   * @param newInterrupt the new interrupt to add.
   */
  public void generateInterrupt(Interrupt newInterrupt)
  {
    myCPU.generateInterrupt(newInterrupt);
  }

  /**
   * Inserts a new interrupt handler into the list of interrupt handlers.  This
   * must be done before handleInterrupt is called.
   *
   * @param newIH contains the interrupt handler to add.
   */
  public void insertInterruptHandler(InterruptHandler newIH)
  {
    interruptHandlers.put(newIH.getType(), newIH);
  }

  /**
   * Handles an interrupt.  Determines the type of the interrupt and executes
   * based on these.  Timer Interrupt and Process finished are handled
   * internally within the kernel.  All others must have a interrupt handler
   * registered with the kernel.
   *
   * @param anInterrupt the interrupt to be handled.
   */
  public void handleInterrupt(Interrupt anInterrupt)
  {
    if (anInterrupt.getType().compareTo("TimerInterrupt") == 0)
    {
      handleTimerInterrupt();
    }
    else if (anInterrupt.getType().compareTo("ProcessFinished") == 0)
    {
      handleProcessFinishedInterrupt();
    }
    else
    {
      InterruptHandler aIH = (InterruptHandler) interruptHandlers.get(
        anInterrupt.getType());

      //If aIH is equal to null then the Interrupt Handler doesn't
      //exist.  Process otherwise.  May-be add an error message or
      //something later.

      if (aIH != null)
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
    // place return value onto stack
    myCPU.getContext().incStackPointer();
    myCPU.getProcessStack().write(myCPU.getContext().getStackPointer(), value);
  }

  /**
   * Performs the necessary actions for the CSP instruction.  Uses the current
   * values of the CPU.
   *
   * @throws java.io.IOException
   */
  public void handleSystemCall() throws java.io.IOException
  {
    Instruction call = myCPU.getContext().getInstructionRegister();

    if (call.isChIn())
    {
      ChIn message = new
        ChIn(this, getCurrentProcess().getTerminalId());
      sendMessage(message);
    }
    else if (call.isChOut())
    {
      //Decrement of stack pointer before getting value. Bug fix.
      myCPU.getContext().decStackPointer();
      ChOut message = new
        ChOut(this, getCurrentProcess().getTerminalId(),
        (char)
          myCPU.getProcessStack().read(myCPU.getContext().getStackPointer()));
      sendMessage(message);
    }
    else if (call.isNumIn())
    {
      NumIn message = new
        NumIn(this, getCurrentProcess().getTerminalId());
      sendMessage(message);
    }
    else if (call.isNumOut())
    {
      NumOut message = new
        NumOut(this, getCurrentProcess().getTerminalId(),
          (short) myCPU.getProcessStack().read(
          myCPU.getContext().getStackPointer()));
      sendMessage(message);
      myCPU.getContext().decStackPointer();
    }
    else if (call.isStrOut())
    {
      int length = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      myCPU.getContext().setStackPointer(
        (short) (myCPU.getContext().getStackPointer() - length));

      for (int count=myCPU.getContext().getStackPointer()+1;
           count<=myCPU.getContext().getStackPointer()+length;
           count++)
      {
        ChOut message = new
          ChOut(this, getCurrentProcess().getTerminalId(),
            (char) myCPU.getProcessStack().read(count));
        sendMessage(message);
      }
    }
    else if (call.isSemaphoreCreate())
    {
      int initValue = myCPU.getProcessStack().read(
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
      int semaphoreId = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      SemaphoreClose message = new SemaphoreClose(
        this, semaphoreId, getCurrentProcess().getPID());
      sendMessage(message);
    }
    else if (call.isSemaphoreSignal())
    {
      int semaphoreId = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      SemaphoreSignal message = new SemaphoreSignal(
        this, semaphoreId, getCurrentProcess().getPID());
      sendMessage(message);
    }
    else if (call.isSemaphoreWait())
    {
      int semaphoreId = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      SemaphoreWait message = new SemaphoreWait(
        this, semaphoreId, getCurrentProcess().getPID());
      sendMessage(message);
    }
    else if (call.isSharedMemoryCreate())
    {
      int length = myCPU.getProcessStack().read(
        myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      //get the shared mem name using getName()
      //send the message
      //SharedMemoryCreateMessage message = new SharedMemoryCreateMessage(this,
      //  getName(), myCPU.getCurrentProcess().getPID(), iLength);
      //sendMessage(message);
    }
    else if (call.isSharedMemoryOpen())
    {
      //get the shared mem name using getName()
      //send the message
      //SharedMemoryOpenMessage message = new SharedMemoryOpenMessage(this,
      //  getName(), myCPU.getCurrentProcess().getPID());
      //sendMessage(message);
    }
    else if (call.isSharedMemoryClose())
    {
      int shareMemId = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      //SharedMemoryCloseMessage message = new SharedMemoryCloseMessage(this,
      //  iSharedMemID, myCPU.getCurrentProcess().getPID());
    }
    else if (call.isSharedMemoryRead())
    {
      int offset = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      int sharedMemId = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      //SharedMemoryReadMessage message = SharedMemoryReadMessage(this,
      //  iSharedMemID, iOffset);
    }
    else if (call.isSharedMemoryWrite())
    {
      int newValue = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      int offset = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      int sharedMemId = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      //SharedMemoryWriteMessage message = SharedMemoryWriteMessage(this,
      //  iSharedMemID, iOffset, (short) iNewValue);
    }
    else if (call.isSharedMemorySize())
    {
      int sharedMemId = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      //SharedMemorySizeMessae message = SharedMemorySizeMessage(this,
      //  iSharedMemID);
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

  // getName

  /**
   * Take a name off the stack from current StackPointer
   * - fixed a bug where it was missing it by one ie. my_sem
   *   became amy_se.
   */
  public String getName()
  {
    int length = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
    //myCPU.getContext().decStackPointer();

    char[] name = new char[length];

    myCPU.getContext().setStackPointer(
     (short) (myCPU.getContext().getStackPointer() - length));

    int index = 0;

    for (int count = myCPU.getContext().getStackPointer();
      count < myCPU.getContext().getStackPointer()+length;
      count++)
      {
        name[index] = (char) myCPU.getProcessStack().read(count);
        index++;
      }
    return (new String(name));
  }

  /**
   * When an I/O event or other block event occurs the current process is
   * removed from the CPU and a RunningToBlocked message is sent with the
   * oldCurrent process.
   */
  public void blockCurrentProcess()
  {
    getCurrentProcess().addToCPUTicks(getCurrentProcessTicks());
    if (myCPU.hasCodeToExecute())
    {
      //Save current process and process context
      RCOSProcess oldCurrent = getCurrentProcess();

      //Set the current process to nothing
      NullProcess nullMsg = new NullProcess(this);
      sendMessage(nullMsg);

      // decrement program counter to force the blocking
      // instruction to be re-executed when the process is woken up
      // BUT only do it if the body of the message is null
      oldCurrent.getContext().decProgramCounter();

      // no need to get a copy of the code as it won't change
      // Send a message to the ProcessScheduler to update old current
      // processes data structures
      RunningToBlocked msg = new RunningToBlocked(this, oldCurrent);
      sendMessage(msg);
    }
  }

  /**
   * Check if timerInterrupts < QUANTUM<BR>
   * if it is ignore<BR>
   * else update CPU ticks and do ProcessSwitch
  */
  public void handleTimerInterrupt()
  {
    timerInterrupts++;

    if (timerInterrupts >= quantum)
    {
      timerInterrupts = 0;

      if (myCPU.hasCodeToExecute())
      {
        //Save currently executing process
        RCOSProcess oldProcess = getCurrentProcess();
        oldProcess.addToCPUTicks(getCurrentProcessTicks());

        //Assume that the stack is the only thing worth writing back that the
        //programs cannot modify their own memory?
        MemoryRequest memSave = new MemoryRequest(getCurrentProcess().getPID(),
          MemoryManager.STACK_SEGMENT, myCPU.getProcessStack().getSegmentSize(),
          myCPU.getProcessStack());
        WriteBytes msg = new WriteBytes(this, memSave);
        sendMessage(msg);

        //Set the current process to nothing
        NullProcess nullMsg = new NullProcess(this);
        sendMessage(nullMsg);

        // no need to get a copy of the code as it won't change
        // Send a message to the ProcessScheduler to update old current
        // processes data structures
        RunningToReady tmpMsg = new RunningToReady(this,
          oldProcess);
        sendMessage(tmpMsg);
      }
    }
  }

  /**
   * An inbuilt handler for the process finished interrupt.  When a process
   * is finished execute the old process is removed, the process time
   * calculated, and a ProcessFinished message is sent.
   */
  public void handleProcessFinishedInterrupt()
  {
    //Get the current process and add CPU ticks spent.
    RCOSProcess oldCurrent = getCurrentProcess();
    oldCurrent.addToCPUTicks(getCurrentProcessTicks());

    //Let process scheduler and others know that the process has finished
    ProcessFinished tmpMsg = new ProcessFinished(this, oldCurrent);
    sendMessage(tmpMsg);

    //Set the current process to null
    NullProcess tmpMessage = new NullProcess(this);
    sendMessage(tmpMessage);
  }
}
