package net.sourceforge.rcosjava.hardware.cpu;

import java.io.*;
import java.util.Vector;

import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.interrupt.InterruptQueue;
import net.sourceforge.rcosjava.hardware.cpu.Instruction;

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
  private boolean paused;
  private boolean interruptsEnabled;
  private boolean codeToExecute;
  private int ticks = 0;
  private Context myContext;
  private Memory processStack, processCode;
  private InterruptQueue interruptsQueue;
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
    interruptsEnabled = true;
  }

  /**
   * The new CPU context to set the CPU.  Currently, there is no protection
   * or error checking.  The kernel is assumed to know what it's doing.
   */
  public void setContext(Context newContext)
  {
    myContext = newContext;
  }

  /**
   * Returns the current state of the CPU (the context).
   */
  public Context getContext()
  {
    return myContext;
  }

  /**
   * Returns if the context given has completed (will return false) or if there
   * is an active process with more code to execute.
   */
  public boolean hasCodeToExecute()
  {
    return (codeToExecute);
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

  /**
   * Overwrites the current process stack.  The stack is a fixed size and the
   * CPU holds this interally.  Not very realistic but easliy implemented.
   *
   * @param newMemory the new memory value of the process stack.
   */
  public void setProcessStack(Memory newMemory)
  {
    processStack = newMemory;
  }

  /**
   * Overwrites the current process code.  Sets the code to exceute to true
   * if the process code given is not null.  Again, the CPU has a variable
   * storage system to hold all of the process code.  For simple implementation.
   *
   * @param newMemory the new memory value of the process code.
   */
  public void setProcessCode(Memory newMemory)
  {
    codeToExecute = !(newMemory == null);
    processCode = newMemory;
  }

  /**
   * Perform main Instruction Execution cycle.
   *
   * @returns true if the process ran an instruction correctly and not at the
   * end of a process
   */
  public boolean performInstructionExecutionCycle()
  {
    if (hasCodeToExecute())
    {
      // fetch and execute if we aren't on the NullProcess
      try
      {
        fetchInstruction();
        executeInstruction();
        return true;
      }
      catch (java.io.IOException e)
      {
        System.err.println("IO Exception while executing code: " +
          e.getMessage());
      }
      catch (java.lang.NullPointerException e2)
      {
        System.err.println("Null Pointer while executing code: " +
          e2.getMessage());
      }
      catch (java.lang.Exception e3)
      {
        System.err.println("Exception while executing code: " +
          e3.getMessage());
      }
    }
    //Check again to see if we came to the end of the program during
    //the last execution cycle.
    if (hasCodeToExecute())
    {
      Interrupt aInt = new Interrupt(-1, "ProcessFinished");
      generateInterrupt(aInt);
    }
    return false;
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
    return (new Instruction((processCode.read(address*8) & 0xff),
      ((byte) processCode.read(address*8+4)),
      ((short) ((processCode.read(address*8+5) << 8) +
      processCode.read(address*8+6)))));
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
  public void executeInstruction()
    throws java.io.IOException
  {
    int iCommand = getContext().getInstructionRegister().getOpCode();

    if (iCommand == Instruction.OPCODE_LIT)
    {
      getContext().incStackPointer();
      processStack.write(getContext().getStackPointer(),
        getContext().getInstructionRegister().getWordParameter());
    }
    else if (iCommand == Instruction.OPCODE_OPR)
    {
      handleOperator();
    }
    else if (iCommand == Instruction.OPCODE_LOD)
    {
      if (getContext().getInstructionRegister().getByteParameter() == 255)
      {
        processStack.write(getContext().getStackPointer(),
         (short) processStack.read(getContext().getStackPointer()));
      }
      else
      {
        getContext().incStackPointer();
        processStack.write(getContext().getStackPointer(),
          (processStack.read(findBase(
           getContext().getInstructionRegister().getByteParameter()) +
           getContext().getInstructionRegister().getWordParameter())));
      }
    }
    else if (iCommand == Instruction.OPCODE_STO)
    {
        processStack.write(
          findBase(getContext().getInstructionRegister().getByteParameter())
          + getContext().getInstructionRegister().getWordParameter(),
          processStack.read(getContext().getStackPointer()));
        getContext().decStackPointer();
    }
    else if (iCommand == Instruction.OPCODE_CAL)
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
    else if (iCommand == Instruction.OPCODE_INT)
    {
      getContext().setStackPointer((short)
        (getContext().getStackPointer() +
        getContext().getInstructionRegister().getWordParameter()));
    }
    else if (iCommand == Instruction.OPCODE_JMP)
    {
      getContext().setProgramCounter(
        getContext().getInstructionRegister().getWordParameter());
    }
    else if (iCommand == Instruction.OPCODE_JPC)
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
    else if (iCommand == Instruction.OPCODE_CSP)
    {
      myKernel.handleSystemCall();
    }
    else if (iCommand == Instruction.OPCODE_LODX)
    {
      getContext().getInstructionRegister().setWordParameter(
        (short)(getContext().getInstructionRegister().getWordParameter() +
        processStack.read(getContext().getStackPointer())));
      processStack.write(getContext().getStackPointer(),
       (processStack.read(
         findBase(getContext().getInstructionRegister().getByteParameter())
       + getContext().getInstructionRegister().getWordParameter())));
    }
    else if (iCommand == Instruction.OPCODE_STOX)
    {
      getContext().getInstructionRegister().setWordParameter((short)
        (processStack.read(getContext().getStackPointer()-1) +
         getContext().getInstructionRegister().getWordParameter()));
       processStack.write(
         (findBase(getContext().getInstructionRegister().getByteParameter()) +
         getContext().getInstructionRegister().getWordParameter()),
         processStack.read(getContext().getStackPointer()));
    }
  }

  /**
   * Carry out instruction execution for OPR opcode fixed DEC opcode - AN.
   * Swapped around decrement of PC in opcodes.  Each if should be commented.
   */
  private void handleOperator()
  {
    int iOperator =
      getContext().getInstructionRegister().getWordParameter();

    if (iOperator == Instruction.OPERATOR_RET)
    {
      getContext().setStackPointer((short)
        (getContext().getBasePointer() - 1));
      getContext().setBasePointer((short)
        (processStack.read(getContext().getStackPointer()+2)));
      getContext().setProgramCounter((short)
        (processStack.read(getContext().getStackPointer()+3)));
      codeToExecute = (getContext().getBasePointer() >= 0);
    }
    else if (iOperator == Instruction.OPERATOR_NEG)
    {
      processStack.write(getContext().getStackPointer(),
        (short) (0 - processStack.read(getContext().getStackPointer())));
    }
    else if (iOperator == Instruction.OPERATOR_ADD)
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) +
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_SUB)
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) -
        processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_MUL)
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) *
        processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_DIV)
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) /
        processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_LOW)
    {
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) & 1));
    }
    else if (iOperator == Instruction.OPERATOR_MOD)
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (short) (processStack.read(getContext().getStackPointer()) %
        processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_EQ)
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
    else if (iOperator == Instruction.OPERATOR_NE)
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
    else if (iOperator == Instruction.OPERATOR_LT)
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
    else if (iOperator == Instruction.OPERATOR_GE)
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
    else if (iOperator == Instruction.OPERATOR_GT)
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
    else if (iOperator == Instruction.OPERATOR_LE)
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
    else if (iOperator == Instruction.OPERATOR_OR)
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) |
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_AND)
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) &
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_XOR)
    {
      getContext().decStackPointer();
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) ^
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_NOT)
    {
      processStack.write(getContext().getStackPointer(),
       (short) ~processStack.read(getContext().getStackPointer()));
    }
    else if (iOperator == Instruction.OPERATOR_SHL)
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) <<
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_SHR)
    {
      getContext().decStackPointer();
      processStack.write(getContext().getStackPointer(),
        (processStack.read(getContext().getStackPointer()) >>
         processStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_INC)
    {
      processStack.write(getContext().getStackPointer(),
       (short) processStack.read(getContext().getStackPointer())+1);
    }
    else if (iOperator == Instruction.OPERATOR_DEC)
    {
      processStack.write(getContext().getStackPointer(),
       (short) processStack.read(getContext().getStackPointer())-1);
    }
    else if (iOperator == Instruction.OPERATOR_CPY)
    {
      getContext().incStackPointer();
      processStack.write(getContext().getStackPointer(),
       processStack.read(getContext().getStackPointer()-1));
    }
    else
    {
      System.out.println("Illegal Instruction");
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
        Interrupt intCurrentInterrupt = interruptsQueue.getInterrupt(ticks);
        while (intCurrentInterrupt != null)
        {
          myKernel.handleInterrupt(intCurrentInterrupt);
          intCurrentInterrupt = interruptsQueue.getInterrupt(ticks);
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
