package org.rcosjava.hardware.cpu;

import java.io.*;
import java.util.*;

import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.interrupt.InterruptQueue;
import org.rcosjava.software.kernel.Kernel;

/**
 * Implements Pcode CPU for RCOS.java. Based on PCode interpreter used in RCOS
 * which was adapted from a series of articles in Byte magazine in the late
 * 1970s.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 14/01/96 Created parses pcode object file interprets Hello World program
 * most pcode implemented. DJ. </DD>
 * <DD> 21/01/96 Finally worked out the public static stuff up LODX, STOX
 * working putting into stand-alone class to work with Applet. Performed
 * numerous modifications to organisation including new class Context and
 * extensions to Instruction. Fixed problem with CPY and CAL now executes
 * PRIMES.PCD. DJ </DD>
 * <DD> 28/01/96 reorganised classes and methods mainly to remove large
 * unnecessary switch statements. Moved class Instruction into separate file. DJ
 * </DD>
 * <DD> 13/02/96 Modifying to use Kernel. Kernel will contain the CPU. CPU no
 * longer extends SimpleMessageHandler. DJ </DD>
 * <DD> 23/03/96 Add InterruptQueue and GenerateInterrupt method. Moved into CPU
 * package. Context class moved into separate file. Added HandleInterrupt to
 * cycle. DJ </DD>
 * <DD> 30/03/96 Stack, Context etc taken from Kernel.CurrentProcess. DJ </DD>
 * <DD> 01/01/97 Sends process switch to process Scheduler. AN </DD>
 * <DD> 04/03/97 Checked implementation and fixed some bugs. AN </DD>
 * <DD> 01/07/97 Uses Memory class instead of bytes for processes. AN </DD>
 * <DD> 03/09/97 Added LOD 255,0 support. AN </DD>
 * <DD> 04/09/97 Removed CPU from message system. Access through Kernel only. AN
 * </DD>
 * <DD> 05/09/98 Removed all messages sending from CPU. AN </DD>
 * <DD> 10/10/98 Finished removal of String based opcodes. AN </DD>
 * <DD> 10/12/2001 Implements STO 255,0 and STOX and LODX correctly. AN </DD>
 * </DT>
 * <P>
 * @author David Jones
 * @author Andrew Newman
 * @created 14th January 1996
 * @see org.rcosjava.software.kernel.Kernel
 * @see org.rcosjava.hardware.cpu.Context
 * @see org.rcosjava.hardware.memory.Memory
 * @version 1.00 $Date$
 */
public class CPU implements Serializable
{
  /**
   * The number of periods (10) that the CPU is call the Timer Interrupt.
   */
  public static final int TIMER_PERIOD = 10;

  /**
   * Internal flag to determine if the CPU has currently stopped execution
   * (true).
   */
  private boolean paused;

  /**
   * Internal flag which holds whether interrupts are handled.
   */
  private boolean interruptsEnabled;

  /**
   * Internal flag which holds whether the current process has finished
   * execution.
   */
  private boolean processFinished;

  /**
   * Internal flag which holds whether there is a process currently running with
   * code to execute.
   */
  private boolean codeToExecute;

  /**
   * Keeps a track of the number of ticks (timer interrupts) have occurred.
   */
  private int ticks = 0;

  /**
   * The context of the currently executing process (base pointer, etc).
   */
  private Context myContext = new Context();

  /**
   * The currently executing process stack.
   */
  private Memory processStack;

  /**
   * The currently executing process code.
   */
  private Memory processCode;

  /**
   * The queue of interrupts currently waiting to be handled.
   */
  private InterruptQueue interruptsQueue;

  /**
   * A reference to the kernel that is currently using this CPU.
   */
  private transient Kernel myKernel;

  /**
   * Initialise the CPU by making it aware that the kernel exists.
   *
   * @param newKernel the kernel that this CPU is being called by.
   */
  public CPU(Kernel newKernel)
  {
    // Kernel
    myKernel = newKernel;

    // Interrupt Information
    interruptsQueue = new InterruptQueue(10, 10);
    paused = false;
    codeToExecute = false;
    processFinished = false;
    interruptsEnabled = true;
  }

  /**
   * Allows you to set a new Kernel.
   *
   * @param newKernel new kernel to set.
   */
  public void setKernel(Kernel newKernel)
  {
    myKernel = newKernel;
  }

  /**
   * Returns the current kernel.
   *
   * @return the current kernel.
   */
  public Kernel getKernel()
  {
    return myKernel;
  }

  /**
   * Sets the ProcessFinished attribute of the CPU object
   */
  public void setProcessFinished()
  {
    Interrupt interrupt = new Interrupt(-1, "ProcessFinished");

    generateInterrupt(interrupt);
    processFinished = false;
  }

  /**
   * Sets true or false to the current process finished.
   *
   * @param isProcessFinished if the process is finished or not.
   */
  public void setProcessFinished(boolean isProcessFinished)
  {
    processFinished = isProcessFinished;
  }

  /**
   * Returns if the process has finished or not.
   *
   * @return if the process has finished or not.
   */
  public boolean processFinished()
  {
    return processFinished;
  }

  /**
   * The new CPU context to set the CPU. Currently, there is no protection or
   * error checking. The kernel is assumed to know what it's doing.
   *
   * @param newContext the new context.
   */
  public void setContext(Context newContext)
  {
    myContext = newContext;
  }

  /**
   * Set whether the code has more to execute or not.
   *
   * @param newCodeToExecute whether the code has more to execute or not.
   */
  public void setCodeToExecute(boolean newCodeToExecute)
  {
    codeToExecute = newCodeToExecute;
  }

  /**
   * Overwrites the current process stack. The stack is a fixed size and the CPU
   * holds this interally. Not very realistic but easliy implemented.
   *
   * @param newProcessStack the new memory value of the process stack.
   */
  public void setProcessStack(Memory newProcessStack)
  {
    processStack = newProcessStack;
  }

  /**
   * Overwrites the current process code. Sets the code to exceute to true if
   * the process code given is not null. Again, the CPU has a variable storage
   * system to hold all of the process code. For simple implementation.
   *
   * @param newProcessCode the new memory value of the process code.
   */
  public void setProcessCode(Memory newProcessCode)
  {
    codeToExecute = !(newProcessCode == null);
    processCode = newProcessCode;
  }

  /**
   * Gets the Paused attribute of the CPU object
   *
   * @return The Paused value
   */
  public boolean isPaused()
  {
    return paused;
  }

  /**
   * Returns the current state of the CPU (the context).
   *
   * @return The Context value
   */
  public Context getContext()
  {
    return myContext;
  }

  /**
   * Returns the number of execution cycles that the CPU has performed.
   *
   * @return The Ticks value
   */
  public int getTicks()
  {
    return ticks;
  }

  /**
   * Returns the process stack (the actual real live stack not a copy).
   *
   * @return The ProcessStack value
   */
  public Memory getProcessStack()
  {
    return processStack;
  }

  /**
   * Returns the process code (the actual real live code not a copy).
   *
   * @return The ProcessCode value
   */
  public Memory getProcessCode()
  {
    return processCode;
  }

  /**
   * Returns the string representation of Instruction Register (Instruction).
   *
   * @return The InstructionRegister value
   */
  public String getInstructionRegister()
  {
    return (getContext().getInstructionRegister().toString());
  }

  /**
   * Description of the Method
   */
  public void pause()
  {
    paused = true;
  }

  /**
   * Description of the Method
   */
  public void unpause()
  {
    paused = false;
  }

  /**
   * Increase the number of ticks by one.
   */
  public void incTicks()
  {
    ticks++;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean hasCodeToExecute()
  {
    return codeToExecute;
  }

  /**
   * Perform main Instruction Execution cycle.
   *
   * @param ignorePaused ignore if the CPU is paused as to whether to execute
   *   an instruction
   * @return true if the process ran an instruction correctly and not at the
   *      end of a process
   */
  public boolean performInstructionExecutionCycle(boolean ignorePaused)
  {
    if (((!isPaused()) && (hasCodeToExecute())) ||
        (ignorePaused && hasCodeToExecute()))
    {
      executeCode();
    }
    return checkProcess();
  }

  /**
   * Description of the Method
   */
  public void executeCode()
  {
    // fetch and execute if we aren't on the NullProcess
    try
    {
      fetchInstruction();
      executeInstruction();
    }
    catch (java.io.IOException e)
    {
      System.err.println("IO Exception while executing code: " +
          e.getMessage());
      e.printStackTrace();
    }
    catch (java.lang.NullPointerException e2)
    {
      System.err.println("Null Pointer while executing code: " +
          e2.getMessage());
      e2.printStackTrace();
    }
    catch (java.lang.Exception e3)
    {
      System.err.println("Exception while executing code: " +
          e3.getMessage());
      e3.printStackTrace();
    }
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean checkProcess()
  {
    boolean continueExecuting = true;

    //Check again to see if we came to the end of the program during
    //the last execution cycle.
    if (processFinished)
    {
      setProcessFinished();
      continueExecuting = processFinished;
    }

    handleInterrupts();
    incTicks();

    return continueExecuting;
  }

  /**
   * Set all CPU data structures up
   */
  public void initialiseCPU()
  {
    getContext().initialise();
    processCode = new Memory();
    processStack = new Memory();
  }

  /**
   * Given a memory address obtain the matching instruction. Places the
   * instruction into the rpCurrentProcess.context.InstructionRegister.
   */
  public void fetchInstruction()
  {
    getContext().setInstructionRegister(
        getInstruction(getContext().getProgramCounter()));
    getContext().incProgramCounter();
  }

  /**
   * Attempts to use the context, memory and registers to execute a single
   * instruction successfully. Each if-then-else statement should be commented.
   *
   * @throws java.io.IOException TBD
   */
  public void executeInstruction() throws java.io.IOException
  {
    getContext().getInstructionRegister().execute(this);
  }

  /**
   * Called by device drivers and other sources of interrupts passed an
   * Interrupt which is added to the InterruptQueue
   *
   * @param newInterrupt Description of Parameter
   */
  public void generateInterrupt(Interrupt newInterrupt)
  {
    // if the interrupt should occur straight away time == -1
    // change this to be the current time + 1
    if (newInterrupt.getTime() == -1)
    {
      newInterrupt.setTime(ticks);
    }
    interruptsQueue.insert((Object) newInterrupt);
  }

  /**
   * Execute once every instruction execution cycle if interruptsEnabled -
   * checks InterruptQ to see if any Interrupts have occurred at the current
   * time
   */
  public void handleInterrupts()
  {
    if (interruptsEnabled)
    {
      if (!interruptsQueue.queueEmpty())
      {
        //is there an interrupt for the current time?
        Interrupt currentInterrupt = interruptsQueue.getInterrupt(ticks);

        while (currentInterrupt != null)
        {
          myKernel.handleInterrupt(currentInterrupt);
          currentInterrupt = interruptsQueue.getInterrupt(ticks);
        }
      }
      //if ((ticks % TIMER_PERIOD == 0) && (runningProcess()))
      if (ticks % TIMER_PERIOD == 0)
      {
        myKernel.handleTimerInterrupt();
      }
    }
  }

  /**
   * Given a memory location return instruction.
   *
   * @param address Description of Parameter
   * @return The Instruction value
   */
  Instruction getInstruction(int address)
  {
    short instr1 = (short) processCode.read(address * 8 + 5);
    short instr2 = (short) processCode.read(address * 8 + 6);
    short loc = (short) ((256 * (instr1 & 255)) + (instr2 & 255));

    Instruction tmpInstruction = Instruction.INSTRUCTIONS[
      processCode.read(address * 8) & 0xff];
    tmpInstruction.setByteParameter(((byte) processCode.read(address * 8 + 4)));
    tmpInstruction.setWordParameter(loc);

    return tmpInstruction;
  }

  /**
   * Returns the basepointer that should be used for this particular LOD(X)
   * STO(X).
   *
   * @param level Description of Parameter
   * @return Description of the Returned Value
   */
  short findBase(short level)
  {
    short base = getContext().getBasePointer();

    while (level > 0)
    {
      // jump to next base down
      base = processStack.read(base);
      level--;
    }

    return (base);
  }

  /**
   * Carry out instruction execution for OPR opcode fixed DEC opcode - AN.
   * Swapped around decrement of PC in opcodes. Each if should be commented.
   */
  void handleOperator()
  {
    Instruction call = getContext().getInstructionRegister();
    Operator tmpOperator = Operator.OPERATIONS[call.getWordParameter()];
    tmpOperator.execute(this);
  }

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
    os.defaultWriteObject();
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
    is.defaultReadObject();
  }
}
