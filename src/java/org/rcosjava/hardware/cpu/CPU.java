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
  private boolean codeFinished;

  /**
   * Internal flag which holds whether there is a code currently running with
   * code to execute.
   */
  private boolean codeToExecute;

  /**
   * Keeps a track of the number of ticks (timer interrupts) have occurred.
   */
  private int ticks = 0;

  /**
   * The context of the currently executing code (base pointer, etc).
   */
  private Context myContext = new Context();

  /**
   * The currently executing stack.
   */
  private Memory stack;

  /**
   * The currently executing code.
   */
  private Memory code;

  /**
   * The queue of interrupts currently waiting to be handled.
   */
  private InterruptQueue interruptsQueue;

  /**
   * The kernel - used to call handle interrupts on.
   */
  private Kernel myKernel;

  /**
   * Initialise the CPU by making it aware that the kernel exists.
   *
   * @param newKernel the kernel that this CPU is being called by.
   */
  public CPU(Kernel newKernel)
  {
    myKernel = newKernel;

    // Interrupt Information
    interruptsQueue = new InterruptQueue(10, 10);
    paused = false;
    codeToExecute = false;
    codeFinished = false;
    interruptsEnabled = true;
  }

  /**
   * Sets the Kernel.
   *
   * @param newKernel the kernel to call.
   */
  public void setKernel(Kernel newKernel)
  {
    myKernel = newKernel;
  }

  /**
   * Called by device drivers and other sources of interrupts passed an
   * Interrupt which is added to the InterruptQueue
   *
   * @param newInterrupt Description of Parameter
   */
  public void addInterrupt(Interrupt newInterrupt)
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
   * Sets the CodeFinished attribute of the CPU object
   */
  public void setCodeFinished()
  {
    Interrupt interrupt = new Interrupt(-1, "CodeFinished");
    addInterrupt(interrupt);
    codeFinished = false;
  }

  /**
   * Sets true or false to the current code finished.
   *
   * @param isCodeFinished if the code is finished or not.
   */
  public void setCodeFinished(boolean isCodeFinished)
  {
    codeFinished = isCodeFinished;
  }

  /**
   * Returns if the code has finished or not.
   *
   * @return if the code has finished or not.
   */
  public boolean codeFinished()
  {
    return codeFinished;
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
   * Overwrites the current stack. The stack is a fixed size and the CPU
   * holds this interally. Not very realistic but easliy implemented.
   *
   * @param newStack the new memory value of the stack.
   */
  public void setStack(Memory newStack)
  {
    stack = newStack;
  }

  /**
   * Overwrites the current code. Sets the code to exceute to true if
   * the process code given is not null. Again, the CPU has a variable storage
   * system to hold all of the process code. For simple implementation.
   *
   * @param newCode the new memory value of the process code.
   */
  public void setCode(Memory newCode)
  {
    codeToExecute = !(newCode == null);
    code = newCode;
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
   * Returns the stack (the actual real live stack not a copy).
   *
   * @return The Stack value
   */
  public Memory getStack()
  {
    return stack;
  }

  /**
   * Returns the code (the actual real live code not a copy).
   *
   * @return The Code value
   */
  public Memory getCode()
  {
    return code;
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

    if (ticks % TIMER_PERIOD == 0)
    {
      addInterrupt(new Interrupt(ticks, "TimerInterrupt"));
    }
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
   * @return true if it ran an instruction correctly and not at the
   *      end of execution
   */
  public boolean performInstructionExecutionCycle(boolean ignorePaused)
  {
    // Handle fetch and execute cycle.
    if (((!isPaused()) && (hasCodeToExecute())) ||
        (ignorePaused && hasCodeToExecute()))
    {
      executeCode();
    }

    // Handle interrupt cycle.
    if (interruptsEnabled)
    {
      handleInterrupts();
    }

    // Check to see if the current
    return checkCode();
  }

  /**
   * Handle system call.
   *
   * @throws IOException if there's an IOException.
   */
  public void systemCall() throws IOException
  {
    myKernel.systemCall();
  }

  /**
   * Execution of code cycle.  Fetch and instruction, execute it, increment
   * CPU ticks.
   */
  private void executeCode()
  {
    // fetch and execute code
    try
    {
      fetchInstruction();
      executeInstruction();
      incTicks();
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
   * Handle interrupts.  Calls the registered Kernel's handleInterrupt with the
   * first interrupt on the queue.
   */
  private void handleInterrupts()
  {
    Interrupt tmpInterrupt = interruptsQueue.getInterrupt(ticks);
    if (tmpInterrupt != null)
    {
      myKernel.handleInterrupt(tmpInterrupt);
    }
  }

  /**
   * Checks to see if the current code has finished executing.
   *
   * @return true if the current code has finished executing.
   */
  private boolean checkCode()
  {
    boolean continueExecuting = true;

    //Check again to see if we came to the end of the program during
    //the last execution cycle.
    if (codeFinished)
    {
      setCodeFinished();
      continueExecuting = codeFinished;
    }

    return continueExecuting;
  }

  /**
   * Set all CPU data structures up
   */
  public void initialiseCPU()
  {
    getContext().initialise();
    code = new Memory();
    stack = new Memory();
  }

  /**
   * Given a memory address obtain the matching instruction. Places the
   * instruction into the the current context's instruction register and
   * increment the program counter.
   */
  private void fetchInstruction()
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
  private void executeInstruction() throws java.io.IOException
  {
    getContext().getInstructionRegister().execute(this);
  }
  /**
   * Given a memory location return instruction.
   *
   * @param address Description of Parameter
   * @return The Instruction value
   */
  Instruction getInstruction(int address)
  {
    short instr1 = (short) code.read(address * 8 + 5);
    short instr2 = (short) code.read(address * 8 + 6);
    short loc = (short) ((256 * (instr1 & 255)) + (instr2 & 255));

    Instruction tmpInstruction = Instruction.INSTRUCTIONS[
        code.read(address * 8) & 0xff];
    tmpInstruction.setByteParameter(((byte) code.read(address * 8 + 4)));
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
      base = stack.read(base);
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
