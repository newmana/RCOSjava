package net.sourceforge.rcosjava.hardware.cpu;

import java.io.*;
import java.util.*;

import net.sourceforge.rcosjava.hardware.memory.*;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.interrupt.InterruptQueue;

/**
 * Implements Pcode CPU for RCOS.java. Based on PCode interpreter used in
 * RCOS which was adapted from a series of articles in Byte magazine in the
 * late 1970s.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 14/01/96 Created parses pcode object file interprets Hello World
 * program most pcode implemented. DJ.
 * </DD><DD>
 * 21/01/96 Finally worked out the public static stuff up LODX, STOX
 * working putting into stand-alone class to work with Applet.
 * Performed numerous modifications to organisation including new
 * class Context and extensions to Instruction. Fixed problem with CPY
 * and CAL now executes PRIMES.PCD. DJ
 * </DD><DD>
 * 28/01/96 reorganised classes and methods mainly to remove large
 * unnecessary switch statements.  Moved class Instruction into
 * separate file. DJ
 * </DD><DD>
 * 13/02/96 Modifying to use Kernel. Kernel will contain the CPU.
 * CPU no longer extends SimpleMessageHandler. DJ
 * </DD><DD>
 * 23/03/96 Add InterruptQueue and GenerateInterrupt method. Moved
 * into CPU package. Context class moved into separate file. Added
 * HandleInterrupt to cycle. DJ
 * </DD><DD>
 * 30/03/96 Stack, Context etc taken from Kernel.CurrentProcess. DJ
 * </DD><DD>
 * 01/01/97 Sends process switch to process Scheduler. AN
 * </DD><DD>
 * 04/03/97 Checked implementation and fixed some bugs. AN
 * </DD><DD>
 * 01/07/97 Uses Memory class instead of bytes for processes. AN
 * </DD><DD>
 * 03/09/97 Added LOD 255,0 support. AN
 * </DD><DD>
 * 04/09/97 Removed CPU from message system. Access through Kernel
 * only. AN
 * </DD><DD>
 * 05/09/98 Removed all messages sending from CPU. AN
 * </DD><DD>
 * 10/10/98 Finished removal of String based opcodes. AN
 * </DD><DD>
 * 10/12/2001 Implements STO 255,0 and STOX and LODX correctly. AN
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.software.kernel.Kernel
 * @see net.sourceforge.rcosjava.hardware.cpu.Context
 * @see net.sourceforge.rcosjava.hardware.memory.Memory
 * @author David Jones
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 14th January 1996
 */
public class CPU
{
  /**
   * The number of periods (10) that the CPU is call the Timer Interrupt.
   */
  public final int TIMER_PERIOD = 10;

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
   * Internal flag which holds whether there is a process currently running
   * with code to execute.
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
  private Kernel myKernel;

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

  public boolean isPaused()
  {
    return paused;
  }

  public void pause()
  {
    paused = true;
  }

  public void unpause()
  {
    paused = false;
  }

  public void setProcessFinished()
  {
    Interrupt interrupt = new Interrupt(-1, "ProcessFinished");
    generateInterrupt(interrupt);
    processFinished = false;
  }

  /**
   * The new CPU context to set the CPU.  Currently, there is no protection
   * or error checking.  The kernel is assumed to know what it's doing.
   *
   * @param newContext the new context.
   */
  public void setContext(Context newContext)
  {
    myContext = newContext;
  }

  /**
   * Overwrites the current process stack.  The stack is a fixed size and the
   * CPU holds this interally.  Not very realistic but easliy implemented.
   *
   * @param newProcessStack the new memory value of the process stack.
   */
  public void setProcessStack(Memory newProcessStack)
  {
    processStack = newProcessStack;
  }

  /**
   * Overwrites the current process code.  Sets the code to exceute to true
   * if the process code given is not null.  Again, the CPU has a variable
   * storage system to hold all of the process code.  For simple implementation.
   *
   * @param newProcessCode the new memory value of the process code.
   */
  public void setProcessCode(Memory newProcessCode)
  {
    codeToExecute = !(newProcessCode == null);
    processCode = newProcessCode;
  }

  /**
   * Returns the current state of the CPU (the context).
   */
  public Context getContext()
  {
    return myContext;
  }

  /**
   * Returns the number of execution cycles that the CPU has performed.
   */
  public int getTicks()
  {
    return ticks;
  }

  /**
   * Increase the number of ticks by one.
   */
  public void incTicks()
  {
    ticks++;
  }

  /**
   * Returns the process stack (the actual real live stack not a copy).
   */
  public Memory getProcessStack()
  {
    return processStack;
  }

  /**
   * Returns the process code (the actual real live code not a copy).
   */
  public Memory getProcessCode()
  {
    return processCode;
  }

  public boolean hasCodeToExecute()
  {
    return codeToExecute;
  }

  /**
   * Perform main Instruction Execution cycle.
   *
   * @returns true if the process ran an instruction correctly and not at the
   * end of a process
   */
  public boolean performInstructionExecutionCycle()
  {
    if ((!isPaused()) && (hasCodeToExecute()))
    {
      executeCode();
    }
    return checkProcess();
  }

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

  // build a Vector of String representing code of current program
  /*public String[] getCode()
  {
    String code[] = new String[rpCurrentProcess.getFileSize()/8];
    Instruction tmp;
    int count;

    for (count=0; count*8 < rpCurrentProcess.getFileSize(); count++)
    {
      tmp = getInstruction(count);
      code[count] = tmp.getStringInstruction();
    }
    return (code);
  }*/

  // return the contents of the stack as an array of SHORT just big
  // enough to hold all elements
  /*public short[] getStack()
  {
    getContext().incStackPointer();
    short newStack[] = new short[getContext().getStackPointer()] ;
    int count;

    for (count=0;count <= getContext().getStackPointer();count++)
    {
      newStack[count] = (short) processStack.read(count);
    }
    return (newStack);
  }*/

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
   * Given a memory address obtain the matching instruction.  Places the
   * instruction into the rpCurrentProcess.context.InstructionRegister.
   */
  public void fetchInstruction()
  {
    getContext().setInstructionRegister(
      getInstruction(getContext().getProgramCounter()));
    getContext().incProgramCounter();
  }

  /**
   * Returns the string representation of Instruction Register (Instruction).
   */
  public String getInstructionRegister()
  {
    return (getContext().getInstructionRegister().toString());
  }

  /**
   * Given a memory location return instruction.
   */
  private Instruction getInstruction(int address)
  {
    short instr1 = (short) processCode.read(address*8+5);
    short instr2 = (short) processCode.read(address*8+6);
    short loc = (short) ((256*(instr1 & 255)) + (instr2 & 255));

    return (new Instruction((processCode.read(address*8) & 0xff),
      ((byte) processCode.read(address*8+4)), loc));
  }

  /**
   * Returns the basepointer that should be used for this particular LOD(X)
   * STO(X).
   *
   * @param sLevel is the number of levels to go down.
   */
  private short findBase(short level)
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
   * Attempts to use the context, memory and registers to execute a single
   * instruction successfully.  Each if-then-else statement should be commented.
   *
   * @throws java.io.IOException TBD
   */
  public void executeInstruction() throws java.io.IOException
  {
    Instruction instruction = getContext().getInstructionRegister();

    if (instruction.isLiteral())
    {
      getContext().incStackPointer();
      processStack.write(getContext().getStackPointer(),
        getContext().getInstructionRegister().getWordParameter());
    }
    else if (instruction.isOperation())
    {
      handleOperator();
    }
    else if (instruction.isCallFunction())
    {
      processStack.write(getContext().getStackPointer()+1,
        findBase(getContext().getInstructionRegister().getByteParameter()));
      processStack.write(getContext().getStackPointer()+2,
        getContext().getBasePointer());
      processStack.write(getContext().getStackPointer()+3,
        getContext().getProgramCounter());
      getContext().setBasePointer(
        (short) (getContext().getStackPointer()+1));
      getContext().setProgramCounter(
        getContext().getInstructionRegister().getWordParameter());
    }
    else if (instruction.isInterval())
    {
      getContext().setStackPointer((short)
        (getContext().getStackPointer() +
        getContext().getInstructionRegister().getWordParameter()));
    }
    else if (instruction.isJump())
    {
      getContext().setProgramCounter(
        getContext().getInstructionRegister().getWordParameter());
    }
    else if (instruction.isJumpCompare())
    {
      if (processStack.read(getContext().getStackPointer())
          ==
          getContext().getInstructionRegister().getByteParameter())
      {
        getContext().setProgramCounter(
          getContext().getInstructionRegister().getWordParameter());
      }
      getContext().decStackPointer();
    }
    else if (instruction.isCallSystemProcedure())
    {
      myKernel.handleSystemCall();
    }
    else if (instruction.isLoad())
    {
      // LOD 255, 0
      if (getContext().getInstructionRegister().getByteParameter() == 255)
      {
        processStack.write(getContext().getStackPointer(),
         (short) processStack.read(getContext().getStackPointer()));
      }
      // LOD L, N
      else
      {
        getContext().incStackPointer();
        processStack.write(getContext().getStackPointer(),
          (processStack.read(
            findBase(getContext().getInstructionRegister().getByteParameter()) +
            getContext().getInstructionRegister().getWordParameter())));
      }
    }
    else if (instruction.isLoadX())
    {
      processStack.write(getContext().getStackPointer(),
       (processStack.read(
         findBase(getContext().getInstructionRegister().getByteParameter()) +
         getContext().getInstructionRegister().getWordParameter() +
         processStack.read(getContext().getStackPointer()))));
    }
    else if (instruction.isStore())
    {
      // STO 255, 0
      if (getContext().getInstructionRegister().getByteParameter() == 255)
      {
        processStack.write(processStack.read(
          processStack.read(getContext().getStackPointer()-1)),
          processStack.read(getContext().getStackPointer()));
        getContext().decStackPointer();
        getContext().decStackPointer();
      }
      // STO L, N
      else
      {
        processStack.write(
          findBase(getContext().getInstructionRegister().getByteParameter()) +
          getContext().getInstructionRegister().getWordParameter(),
          processStack.read(getContext().getStackPointer()));
        getContext().decStackPointer();
      }
    }
    else if (instruction.isStoreX())
    {
      processStack.write(
        findBase(getContext().getInstructionRegister().getByteParameter()) +
        getContext().getInstructionRegister().getWordParameter() +
        processStack.read(getContext().getStackPointer()-1),
        processStack.read(getContext().getStackPointer()));
      getContext().decStackPointer();
      getContext().decStackPointer();
    }
  }

  /**
   * Carry out instruction execution for OPR opcode fixed DEC opcode - AN.
   * Swapped around decrement of PC in opcodes.  Each if should be commented.
   */
  private void handleOperator()
  {
    Instruction call = getContext().getInstructionRegister();

    if (call.isReturn())
    {
      getContext().setStackPointer((short) (getContext().getBasePointer() - 1));
      getContext().setBasePointer((short)
        (processStack.read(getContext().getStackPointer()+2)));
      getContext().setProgramCounter((short)
        (processStack.read(getContext().getStackPointer()+3)));
      //codeToExecute = !(getContext().getBasePointer() <= 0);

      processFinished = getContext().getBasePointer() <= 0;
      codeToExecute = !processFinished;
    }
    else if (call.isNegative())
    {
      processStack.write(getContext().getStackPointer(),
        (short) (0 - processStack.read(getContext().getStackPointer())));
    }
    else if (call.isAdd())
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) +
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isSubtract())
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) -
        processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isMultiply())
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) *
        processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isDivide())
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) /
        processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isLow())
    {
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) & 1));
    }
    else if (call.isModulus())
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) %
        processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isEquals())
    {
      getContext().decStackPointer();
      if (processStack.read(getContext().getStackPointer()) ==
          processStack.read(getContext().getStackPointer()+1))
      {
        processStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        processStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (call.isNotEqualTo())
    {
      getContext().decStackPointer();
      if (processStack.read(getContext().getStackPointer()) !=
          processStack.read(getContext().getStackPointer()+1))
      {
        processStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        processStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (call.isLessThan())
    {
      getContext().decStackPointer();
      if (processStack.read(getContext().getStackPointer()) <
          processStack.read(getContext().getStackPointer()+1))
      {
        processStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        processStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (call.isGreaterThanOrEqualTo())
    {
      getContext().decStackPointer();
      if (processStack.read(getContext().getStackPointer()) >=
          processStack.read(getContext().getStackPointer()+1))
      {
        processStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        processStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (call.isGreaterThan())
    {
      getContext().decStackPointer();
      if (processStack.read(getContext().getStackPointer()) >
          processStack.read(getContext().getStackPointer()+1))
      {
        processStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        processStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (call.isLessThanOrEqualTo())
    {
      getContext().decStackPointer();
      if (processStack.read(getContext().getStackPointer()) <=
          processStack.read(getContext().getStackPointer()+1))
      {
        processStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        processStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (call.isOr())
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) |
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isAnd())
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) &
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isXor())
    {
      getContext().decStackPointer();
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) ^
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isNot())
    {
      processStack.write(getContext().getStackPointer(),
       (short) ~processStack.read(getContext().getStackPointer()));
    }
    else if (call.isShiftLeft())
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) <<
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isShiftRight())
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) >>
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (call.isIncrement())
    {
      processStack.write(getContext().getStackPointer(),
       (short) processStack.read(getContext().getStackPointer())+1);
    }
    else if (call.isDecrement())
    {
      processStack.write(getContext().getStackPointer(),
       (short) processStack.read(getContext().getStackPointer())-1);
    }
    else if (call.isCopy())
    {
      getContext().incStackPointer();
      processStack.write(getContext().getStackPointer(),
       processStack.read(getContext().getStackPointer()-1));
    }
    else
    {
      System.err.println("Illegal Instruction");
    }
  }

  /**
   * Called by device drivers and other sources of interrupts passed an
   * Interrupt which is added to the InterruptQueue
   */
  public void generateInterrupt(Interrupt newInterrupt)
  {
    // if the interrupt should occur straight away time == -1
    // change this to be the current time + 1
    if (newInterrupt.getTime() == -1)
      newInterrupt.setTime(ticks);
    interruptsQueue.insert((Object) newInterrupt);
  }

  /**
   * Execute once every instruction execution cycle if interruptsEnabled
   * - checks InterruptQ to see if any Interrupts have occurred at the
   *   current time
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
}
