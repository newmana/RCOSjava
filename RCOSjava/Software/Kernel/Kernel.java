package Software.Kernel;

import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import Hardware.CPU.CPU;
import Hardware.CPU.Interrupt;
import Hardware.CPU.Instruction;
import Hardware.Memory.Memory;
import Software.Interrupt.InterruptHandler;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.OS.ChIn;
import MessageSystem.Messages.OS.ChOut;
import MessageSystem.Messages.OS.NumIn;
import MessageSystem.Messages.OS.NumOut;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.OS.Schedule;
import MessageSystem.Messages.OS.SemaphoreClose;
import MessageSystem.Messages.OS.SemaphoreCreate;
import MessageSystem.Messages.OS.SemaphoreOpen;
import MessageSystem.Messages.OS.SemaphoreSignal;
import MessageSystem.Messages.OS.SemaphoreWait;
import MessageSystem.Messages.Universal.NullProcess;
import MessageSystem.Messages.Universal.RunningToBlocked;
import MessageSystem.Messages.Universal.RunningToReady;
import MessageSystem.Messages.Universal.ProcessFinished;
import MessageSystem.Messages.Universal.ReadBytes;
import MessageSystem.Messages.Universal.WriteBytes;
import MessageSystem.Messages.Universal.SetContext;
import MessageSystem.Messages.Universal.InstructionExecution;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import Software.Process.RCOSProcess;
import Software.Memory.MemoryManager;
import Software.Memory.MemoryRequest;

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
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 1st February 1996
 * @see Hardware.CPU.CPU
 */
public class Kernel extends OSMessageHandler
{
  private static final String MESSENGING_ID = "Kernel";

  private int quantum = 2;
  private int timerInterrupts = 0;
  private int timeProcessOn;
  private CPU myCPU;
  private Hashtable interruptHandlers = new Hashtable();
  private RCOSProcess currentProcess;
  private Schedule scheduleMessage = new Schedule(this);

  /**
   * Initialise Kernel
   * @param postOffice	central post office for messaging system
   */
  public Kernel(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);
    myCPU = new CPU(this);
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
    return(myCPU.getTicks()-timeProcessOn);
  }

  /**
   * @return whether there is a current process or not (null or not) currently
   * being executed..
   */
  public boolean runningProcess()
  {
    return (currentProcess != null);
  }

  /**
   * @return the current process being used.  Copies the current process and
   * context to be returned.
   */
  public RCOSProcess getCurrentProcess()
  {
    RCOSProcess tmpProcess = new RCOSProcess(currentProcess);
    tmpProcess.setContext(myCPU.getContext());
    return tmpProcess;
  }

  /**
   * Sets the current process and context (from within the RCOSProcess data
   * structure).  The current process should usually be set to null first
   * although this is not enforced.
   *
   * @param newCurrentProcess the new process to overwrite the current one
   * with.
   */
  public void setCurrentProcess(RCOSProcess newCurrentProcess)
  {
//    System.out.println("Setting Current Process: " + newCurrentProcess);
    currentProcess = newCurrentProcess;
    setCurrentContext(newCurrentProcess);
  }

  /**
   * Sets the current process and context as well as the code and stack segments
   * of the CPU to null.
   */
  public void setCurrentProcessNull()
  {
//    System.out.println("Setting Current Process to Null");
    currentProcess = null;
    myCPU.setProcessCode(null);
    myCPU.setProcessStack(null);
//    System.out.println("CPU Code to execute: " + myCPU.hasCodeToExecute());
//    System.out.println("Kernel Code to execute: " + this.runningProcess());
  }

  /**
   * Performs one execution cycle on the CPU if there is a running process.
   * Handles the interrupts and increments the CPU tick.
   */
  public void performInstructionExecutionCycle()
  {
    sendMessage(this.scheduleMessage);
    myCPU.performInstructionExecutionCycle();
    //Sends context and current instruction to the kernel.
    if (runningProcess())
    {
      SetContext msg = new
        SetContext(this, myCPU.getContext());
      sendMessage(msg);
      InstructionExecution iMsg = new
        InstructionExecution(this, myCPU.getProcessStack());
      sendMessage(iMsg);
    }
    myCPU.handleInterrupts();
    myCPU.incTicks();
  }

  /**
   * Sets the current context of the CPU based on a given process.
   *
   * @param newProcess the process contains a contex that is accessed using
   * getContext.
   */
  public void setCurrentContext(RCOSProcess newProcess)
  {
    if (newProcess != null)
      myCPU.setContext(newProcess.getContext());
    else
      myCPU.setContext(null);
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
      //RCOSProcess oldCurrent = new RCOSProcess( myCPU.currentProcess );
      RCOSProcess oldCurrent = getCurrentProcess();

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
   * This occurs when a new process is to be executed on the CPU.  If there
   * was an existing process it's existing stack is saved and the new process
   * is put in its place.  This include overwriting the exsting code pages and
   * calling the setCurrentProcess method.
   */
  public void switchProcess(RCOSProcess newProcess)
  {
//    System.out.println("-----Start Switch Process-----");
//    System.out.println("New Process: " + newProcess.getPID());
    //Save memory if program hasn't terminated.
    if (myCPU.hasCodeToExecute())
    {
      /*MemoryRequest memSave = new
        MemoryRequest(newProcess.getPID(), MemoryManager.CODE_SEGMENT,
        myCPU.getProcessCode().getSegmentSize(),
        myCPU.getProcessCode());
      WriteBytes msg = new WriteBytes(this, memSave);
      sendMessage(msg);*/
      MemoryRequest memSave = new MemoryRequest(getCurrentProcess().getPID(),
        MemoryManager.STACK_SEGMENT, myCPU.getProcessStack().getSegmentSize(),
        myCPU.getProcessStack());
      WriteBytes msg = new WriteBytes(this, memSave);
      sendMessage(msg);
    }

    setCurrentProcess(newProcess);
//    System.out.println("New Current Process: " + newProcess.getPID());

    //Get new memory
    MemoryRequest memRead = new MemoryRequest(newProcess.getPID(),
      MemoryManager.CODE_SEGMENT, newProcess.getCodePages()*MemoryManager.PAGE_SIZE);
    ReadBytes msg = new ReadBytes(this, memRead);
    sendMessage(msg);

    memRead.setMemoryType(MemoryManager.STACK_SEGMENT);
    memRead.setSize(newProcess.getStackPages()*MemoryManager.PAGE_SIZE);
    msg = new ReadBytes(this, memRead);
    sendMessage(msg);
//    System.out.println("-----End Switch Process-----");
  }

  /**
   * Sets the process code (the non-changing executing program).
   *
   * @param newMemory the value to set the process code to.
   */
  public void setProcessCode(Memory newMemory)
  {
    myCPU.setProcessCode(newMemory);
  }

  /**
   * Sets the process stacking (the working area of the executing program).
   *
   * @param newMemory the value to set the process stack to.
   */
  public void setProcessStack(Memory newMemory)
  {
    myCPU.setProcessStack(newMemory);
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
      handleTimerInterrupt();
    else if (anInterrupt.getType().compareTo("ProcessFinished") == 0)
      handleProcessFinishedInterrupt();
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
    myCPU.getProcessStack().write(
      myCPU.getContext().getStackPointer(), value);
  }

  /**
   * Performs the necessary actions for the CSP instruction.  Uses the current
   * values of the CPU.
   */
  public void handleSystemCall()
    throws java.io.IOException
  {
    int iCall = myCPU.getContext().
      getInstructionRegister().getWordParameter();
    if (iCall == Instruction.SYS_CHIN)
    {
      ChIn aMessage = new
        ChIn(this, getCurrentProcess().getTerminalId());
      sendMessage(aMessage);
    }
    else if (iCall == Instruction.SYS_CHOUT)
    {
      //Decrement of stack pointer before getting value. Bug fix.
      myCPU.getContext().decStackPointer();
      ChOut aMessage = new
        ChOut(this, getCurrentProcess().getTerminalId(),
        (char)
        myCPU.getProcessStack().read(myCPU.getContext().getStackPointer()));
      sendMessage(aMessage);
    }
    else if (iCall == Instruction.SYS_NUMIN)
    {
      NumIn aMessage = new
        NumIn(this,
          getCurrentProcess().getTerminalId());
      sendMessage(aMessage);
    }
    else if (iCall == Instruction.SYS_NUMOUT)
    {
      NumOut aMessage = new
        NumOut(this, getCurrentProcess().getTerminalId(),
          (short) myCPU.getProcessStack().read(
          myCPU.getContext().getStackPointer()));
      sendMessage(aMessage);
      myCPU.getContext().decStackPointer();
    }
    else if (iCall == Instruction.SYS_STROUT)
    {
      int length = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      myCPU.getContext().setStackPointer(
        (short) (myCPU.getContext().getStackPointer() - length));

      for (int count=myCPU.getContext().getStackPointer()+1;
           count<=myCPU.getContext().getStackPointer()+length;
           count++)
      {
        ChOut aMessage = new
          ChOut(this, getCurrentProcess().getTerminalId(),
            (char) myCPU.getProcessStack().read(count));
        sendMessage(aMessage);
      }
    }
    else if (iCall == Instruction.SYS_SEM_CREATE)
    {
      int iInitValue = myCPU.getProcessStack().read(
        myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      // get the semaphore's name using getName()
      // send the message
      SemaphoreCreate msg = new SemaphoreCreate(
        this, getName(), getCurrentProcess().getPID(), iInitValue);
      sendMessage(msg);
    }
    else if (iCall == Instruction.SYS_SEM_OPEN)
    {
      //get the semaphore's name using getName()
      //send the message
      SemaphoreOpen msg = new SemaphoreOpen(
        this, getName(), getCurrentProcess().getPID());
      sendMessage(msg);
    }
    else if (iCall == Instruction.SYS_SEM_CLOSE)
    {
      int iSemaphoreID = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      SemaphoreClose msg = new SemaphoreClose(
        this, iSemaphoreID, getCurrentProcess().getPID());
    }
    else if (iCall == Instruction.SYS_SEM_SIGNAL)
    {
      int iSemaphoreID = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      SemaphoreSignal msg = new SemaphoreSignal(
        this, iSemaphoreID, getCurrentProcess().getPID());
    }
    else if (iCall == Instruction.SYS_SEM_WAIT)
    {
      int iSemaphoreID = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      SemaphoreWait msg = new SemaphoreWait(
        this, iSemaphoreID, getCurrentProcess().getPID());
      sendMessage(msg);
    }
    else if (iCall == Instruction.SYS_SHR_CREATE)
    {
      int iLength = myCPU.getProcessStack().read(
        myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      //get the shared mem name using getName()
      //send the message
      //SharedMemoryCreateMessage msg = new SharedMemoryCreateMessage(this,
      //  getName(), myCPU.getCurrentProcess().getPID(), iLength);
      //sendMessage(msg);
    }
    else if (iCall == Instruction.SYS_SHR_OPEN)
    {
      //get the shared mem name using getName()
      //send the message
      //SharedMemoryOpenMessage msg = new SharedMemoryOpenMessage(this,
      //  getName(), myCPU.getCurrentProcess().getPID());
      //sendMessage(msg);
    }
    else if (iCall == Instruction.SYS_SHR_CLOSE)
    {
      int iSharedMemID = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      //SharedMemoryCloseMessage msg = new SharedMemoryCloseMessage(this,
      //  iSharedMemID, myCPU.getCurrentProcess().getPID());
    }
    else if (iCall == Instruction.SYS_SHR_READ)
    {
      int iOffset = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      int iSharedMemID = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      //SharedMemoryReadMessage msg = SharedMemoryReadMessage(this,
      //  iSharedMemID, iOffset);
    }
    else if (iCall == Instruction.SYS_SHR_WRITE)
    {
      int iNewValue = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      int iOffset = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      myCPU.getContext().decStackPointer();
      int iSharedMemID = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      //SharedMemoryWriteMessage msg = SharedMemoryWriteMessage(this,
      //  iSharedMemID, iOffset, (short) iNewValue);
    }
    else if (iCall == Instruction.SYS_SHR_SIZE)
    {
      int iSharedMemID = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
      //SharedMemorySizeMessae msg = SharedMemorySizeMessage(this,
      //  iSharedMemID);
    }
    else if (iCall == Instruction.SYS_FORK)
    {
    }
    //System.out.println( "KERNEL: HandleSystemCall FINISHED" );
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
  // - take a name off the stack from current StackPointer
  // - fixed a bug where it was missing it by one ie. my_sem
  //   became amy_se.

  public String getName()
  {
    int iLength = myCPU.getProcessStack().read(myCPU.getContext().getStackPointer());
//    myCPU.getContext()StackPointer--;

    char[] theName = new char[iLength];

    myCPU.getContext().setStackPointer(
     (short) (myCPU.getContext().getStackPointer()
     - iLength));

    int index = 0;

    for (int count = myCPU.getContext().getStackPointer();
      count < myCPU.getContext().getStackPointer()+iLength;
      count++)
      {
        theName[index] = (char) myCPU.getProcessStack().read(count);
        index++;
      }
    return (new String(theName));
  }

  /**
   handle Timer interrupts
   - check if timerInterrupts < QUANTUM
   - if it is ignore
   - else update CPU ticks and do ProcessSwitch
  */
  public void handleTimerInterrupt()
  {
    //System.out.println("-----Start Handling Timer Interrupt-----");
    timerInterrupts++;

    if (timerInterrupts >= quantum)
    {
      //System.out.println("Quantum Expired");
      timerInterrupts = 0;

      if (myCPU.hasCodeToExecute())
      {
        RCOSProcess oldProcess = getCurrentProcess();
        oldProcess.addToCPUTicks(getCurrentProcessTicks());
        // no need to get a copy of the code as it won't change
        // Send a message to the ProcessScheduler to update old current
        // processes data structures
        RunningToReady rrMsg = new RunningToReady(this,
          oldProcess);
        //System.out.println("Running to Ready: " + oldProcess.getPID());
        sendMessage(rrMsg);
        //System.out.println("Sent Running to Ready: " + oldProcess.getPID());
      }
    }
    //System.out.println("-----Finish Handling Timer Interrupt-----");
  }

  /**
   * An inbuilt handler for the process finished interrupt.  When a process
   * if finished execute the old process is removed, the process time
   * calculated, and a ProcessFinished message is sent.
   */
  public void handleProcessFinishedInterrupt()
  {
    //System.out.println("-----Start Handling Process Finished-----");
    RCOSProcess oldCurrent = new RCOSProcess(getCurrentProcess());
    //System.out.println("Got process: " + oldCurrent.getPID());
    oldCurrent.addToCPUTicks(getCurrentProcessTicks());
    ProcessFinished pfMsg = new ProcessFinished(this,
      oldCurrent);
    sendMessage(pfMsg);
    //System.out.println("-----End Handling Process Finished-----");
  }

  public synchronized void processMessage(OSMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }

  public synchronized void processMessage(UniversalMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }
}
