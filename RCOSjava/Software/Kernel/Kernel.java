//***************************************************************************
// FILE    : Kernel.java
// PACKAGE : Kernel
// PURPOSE : Kernel for RCOS.java (Kernel.java)
//           - implements micro-kernel responsible for
//           * sending messages to appropriate components for system calls
//           * sending messages to appropriate components for interrupts
//           * receiving messages from various components to do h/w specific
//             tasks
// AUTHOR  : David Jones, Andrew Newman
// MODIFIED:
// HISTORY : 13/02/96  Created version to handle simple interaction
//                     between CPU and Terminal. DJ
//           23/03/96  Modified to use packages. DJ
//           07/04/97  Fixed hang when multitasking with blocked
//                     processes. AN
//           08/04/97  Receives and responds to Quantum message. AN
//           09/04/97  Fixed CHOUT problem and sent message as id not Kernel.
//           10/10/97  Fixed semaphore name, was one off. AN
//           12/10/97  Implementation of File and Shared
//                     Memory interrupts. AN
//           11/08/98  Removed String comparison for instructions.  AN
//           12/08/98  Implemented Shared Memory and File system calls. AN
//           13/08/98  Fixed incomplete/buggy Semaphore and Shared memory. AN
//
// @version	1.0 12th August, 1998
// @author	David Jones (d.jones@cqu.edu.au), Andrew Newman
// @see		MessageSystem.SimpleMessageHandler
//
//***************************************************************************/

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

public class Kernel extends OSMessageHandler
{
  private int iQuantum = 2;
  private int iTimerInterrupts = 0;
  private int iTimeProcessOn;
  private CPU myCPU;
  private Hashtable InterruptHandlers = new Hashtable();
  private static final String MESSENGING_ID = "Kernel";
  private RCOSProcess currentProcess;

  // Initialise Kernel
  // @param aPostOffice	central post office for messaging system
  public Kernel(OSOffice aPostOffice)
  {
    super(MESSENGING_ID, aPostOffice);
    myCPU = new CPU(this);
  }

  public boolean runningProcess()
  {
    return (currentProcess != null);
  }

  public RCOSProcess getCurrentProcess()
  {
    RCOSProcess tmpProcess = new RCOSProcess(currentProcess);
    tmpProcess.setContext(myCPU.getContext());
    return tmpProcess;
  }

  public void setCurrentProcess(RCOSProcess newCurrentProcess)
  {
//    System.out.println("Setting Current Process: " + newCurrentProcess);
    currentProcess = newCurrentProcess;
    setCurrentContext(newCurrentProcess);
  }

  public void setCurrentProcessNull()
  {
//    System.out.println("Setting Current Process to Null");
    currentProcess = null;
    myCPU.setProcessCode(null);
    myCPU.setProcessStack(null);
//    System.out.println("CPU Code to execute: " + myCPU.hasCodeToExecute());
//    System.out.println("Kernel Code to execute: " + this.runningProcess());
  }

  public void performInstructionExecutionCycle()
  {
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

  public void generateInterrupt(Interrupt newInterrupt)
  {
    myCPU.generateInterrupt(newInterrupt);
  }

  public void handleInterrupt(Interrupt intInterrupt)
  {
    if (intInterrupt.getType().compareTo("TimerInterrupt") == 0)
      handleTimerInterrupt();
    else if (intInterrupt.getType().compareTo("ProcessFinished") == 0)
      handleProcessFinishedInterrupt();
    else
    {
      InterruptHandler aIH = (InterruptHandler) InterruptHandlers.get(
        intInterrupt.getType());

      //If aIH is equal to null then the Interrupt Handler doesn't
      //exist.  Process otherwise.  May-be add an error message or
      //something later.

      if (aIH != null)
        aIH.handleInterrupt();
    }
  }

  public void setQuantum(int newQuantum)
  {
    iQuantum = newQuantum;
  }

  public void insertInterruptHandler(InterruptHandler newIH)
  {
    InterruptHandlers.put(newIH.getType(), newIH);
  }

  public void setCurrentContext(RCOSProcess newProcess)
  {
    if (newProcess != null)
      myCPU.setContext(newProcess.getContext());
    else
      myCPU.setContext(null);
  }

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

  public void returnValue(short sValue)
  {
    // place return value onto stack
    myCPU.getContext().incStackPointer();
    myCPU.getProcessStack().write(
      myCPU.getContext().getStackPointer(), sValue);
  }

  public void setCurrentProcessTicks()
  {
    iTimeProcessOn = myCPU.getTicks();
  }

  public int getCurrentProcessTicks()
  {
    return(myCPU.getTicks()-iTimeProcessOn);
  }

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

  public void setProcessCode(Memory mMemory)
  {
    myCPU.setProcessCode(mMemory);
  }

  public void setProcessStack(Memory mMemory)
  {
    myCPU.setProcessStack(mMemory);
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

  //**********************************/
  // perform necessary actions for CSP

  public void handleSystemCall()
    throws java.io.IOException
  {
    int iCall = myCPU.getContext().
      getInstructionRegister().getWParameter();
    if (iCall == Instruction.SYS_CHIN)
    {
      ChIn aMessage = new
        ChIn(this, getCurrentProcess().getTerminalID());
      sendMessage(aMessage);
    }
    else if (iCall == Instruction.SYS_CHOUT)
    {
      //Decrement of stack pointer before getting value. Bug fix.
      myCPU.getContext().decStackPointer();
      ChOut aMessage = new
        ChOut(this, getCurrentProcess().getTerminalID(),
        (char)
        myCPU.getProcessStack().read(myCPU.getContext().getStackPointer()));
      sendMessage(aMessage);
    }
    else if (iCall == Instruction.SYS_NUMIN)
    {
      NumIn aMessage = new
        NumIn(this,
          getCurrentProcess().getTerminalID());
      sendMessage(aMessage);
    }
    else if (iCall == Instruction.SYS_NUMOUT)
    {
      NumOut aMessage = new
        NumOut(this, getCurrentProcess().getTerminalID(),
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
          ChOut(this, getCurrentProcess().getTerminalID(),
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
    iTimerInterrupts++;

    if (iTimerInterrupts >= iQuantum)
    {
      //System.out.println("Quantum Expired");
      iTimerInterrupts = 0;

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
}
