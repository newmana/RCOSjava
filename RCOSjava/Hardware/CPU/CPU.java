//*******************************************************************/
// FILE    :  CPU.java
// PURPOSE :  Implements Pcode CPU for RCOS.java. Based on PCode
//            interpreter used in RCOS which was adapted from a series
//            of articles in Byte magazine in the late 1970s
// AUTHOR   : David Jones, Andrew Newman
// MODIFIED :
// VERSION  : 1.00
// HISTORY  : 14/01/96 Created parses pcode object file
//                     interprets Hello World program
//                     most pcode implemented. DJ.
//            21/01/96 Finally worked out the public static stuff up
//                     LODX, STOX working
//                     putting into stand-alone class to work with
//                     Applet
//                     Performed numerous modifications to
//                     organisation including new class Context and
//                     extensions to Instruction. Fixed problem with
//                     CPY and CAL now executes PRIMES.PCD. DJ
//            28/01/96 reorganised classes and methods mainly to
//                     remove large unnecessary switch statements
//                     Moved class Instruction into separate file. DJ
//            13/02/96 Modifying to use Kernel
//                     Kernel will contain the CPU
//                     CPU no longer extends SimpleMessageHandler. DJ
//            23/03/96 Add InterruptQueue and GenerateInterrupt
//                     method. Moved into CPU package
//                     Context class moved into separate file
//                     Added HandleInterrupt to cycle. DJ
//            30/03/96 Stack, Context etc taken from
//                     Kernel.CurrentProcess. DJ
//            01/01/97 Sends process switch to process Scheduler. AN
//            04/03/97 Checked implementation and fixed some bugs. AN
//            01/07/97 Uses Memory class instead of bytes for
//                     processes. AN
//            03/09/97 Added LOD 255,0 support. AN
//            04/09/97 Removed CPU from message system. Access through
//                     Kernel only. AN
//            05/09/98 Removed all messages sending from CPU. AN
//            10/10/98 Finished removal of String based opcodes. AN
//
// @version	1.0  10th October, 1998
// @see		MessageSystem.SimpleMessageHandler
//
//*******************************************************************/

package Hardware.CPU;

import java.io.*;
import java.util.Vector;

import Hardware.Memory.Memory;
import Software.Memory.MemoryManager;
import Software.Kernel.Kernel;
import Software.Process.RCOSProcess;
import Software.Interrupt.InterruptQueue;
import Hardware.CPU.Instruction;

public class CPU
{
  public final int TIMER_PERIOD = 10;
  private boolean bPause;
  private boolean bInterruptsEnabled;
  private boolean bCodeToExecute;
  private int iTicks = 0;
  private Context myContext;
  private Memory mProcessStack, mProcessCode;
  private InterruptQueue iqInterruptsQueue;
  private Kernel myKernel;

  // Initialise CPU
  public CPU(Kernel aKernel)
  {
    // Kernel
    myKernel = aKernel;
    // Interrupt Information
    iqInterruptsQueue = new InterruptQueue(10, 10);
    bPause = false;
    bCodeToExecute = false;
    bInterruptsEnabled = true;
  }

  public void setContext(Context newContext)
  {
    myContext = newContext;
  }

  public Context getContext()
  {
    return myContext;
  }

  public boolean hasCodeToExecute()
  {
    return (bCodeToExecute);
  }

  public int getTicks()
  {
    return iTicks;
  }

  public void incTicks()
  {
    iTicks++;
  }

  /**
   * @return copy of process stack
   */
  public Memory getProcessStack()
  {
    return mProcessStack;
  }

  /**
   * @return copy of process code
   */
  public Memory getProcessCode()
  {
    return mProcessCode;
  }

  public void setProcessStack(Memory mMemory)
  {
    mProcessStack = mMemory;
  }

  public void setProcessCode(Memory mMemory)
  {
    bCodeToExecute = !(mMemory == null);
    mProcessCode = mMemory;
  }

  // perform main Instruction Execution cycle
  // returns true if the process ran an
  // instruction correctly and not at the end
  // of a process
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
        System.out.println("IO Exception while executing code: " +
          e.getMessage());
      }
      catch (java.lang.NullPointerException e2)
      {
        System.out.println("Null Pointer while executing code: " +
          e2.getMessage());
      }
      catch (java.lang.Exception e3)
      {
        System.out.println("Exception while executing code: " +
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
      newStack[count] = (short) mProcessStack.read(count);
    }
    return (newStack);
  }*/

  // set all CPU data structures up
  public void initialiseCPU()
  {
    getContext().initialise();
    mProcessCode = new Memory();
    mProcessStack = new Memory();
  }

  // given a memory address obtain the matching instruction
  // place the instruction into the rpCurrentProcess.context.InstructionRegister
  public void fetchInstruction()
  {
    getContext().setInstructionRegister(
      getInstruction(getContext().getProgramCounter()));
    getContext().incProgramCounter();
  }

  // return string rep of IR
  public String getInstructionRegister()
  {
    return (getContext().getInstructionRegister().toString());
  }

  // given a memory location return instruction
  private Instruction getInstruction(int theAddress)
  {
    return (new Instruction((mProcessCode.read(theAddress*8) & 0xff),
                           ((byte) mProcessCode.read(theAddress*8+4)),
                           ((short) ((mProcessCode.read(theAddress*8+5) << 8)
                                      + mProcessCode.read(theAddress*8+6)))));
  }

  // - return the basepointer that should be used for this particular
  //   LOD(X) STO(X)
  // - parameter is the number of levels to go down
  private short findBase(short sLevel)
  {
    short sBase = getContext().getBasePointer();

    while (sLevel > 0)
    {
      sBase = mProcessStack.read(sBase);  // jump to next base down
      sLevel--;
    }

    return (sBase);
  }

  public void executeInstruction()
    throws java.io.IOException
  {
    int iCommand = getContext().getInstructionRegister().getOpCode();

    if (iCommand == Instruction.OPCODE_LIT)
    {
      getContext().incStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        getContext().getInstructionRegister().getWParameter());
    }
    else if (iCommand == Instruction.OPCODE_OPR)
    {
      handleOperator();
    }
    else if (iCommand == Instruction.OPCODE_LOD)
    {
      if (getContext().getInstructionRegister().getBParameter() == 255)
      {
        mProcessStack.write(getContext().getStackPointer(),
         (short) mProcessStack.read(getContext().getStackPointer()));
      }
      else
      {
        getContext().incStackPointer();
        mProcessStack.write(getContext().getStackPointer(),
          (mProcessStack.read(findBase(
           getContext().getInstructionRegister().getBParameter()) +
           getContext().getInstructionRegister().getWParameter())));
      }
    }
    else if (iCommand == Instruction.OPCODE_STO)
    {
        mProcessStack.write(
          findBase(getContext().getInstructionRegister().getBParameter())
          + getContext().getInstructionRegister().getWParameter(),
          mProcessStack.read(getContext().getStackPointer()));
        getContext().decStackPointer();
    }
    else if (iCommand == Instruction.OPCODE_CAL)
    {
      mProcessStack.write(getContext().getStackPointer()+1,
        findBase(getContext().getInstructionRegister().getBParameter()));
      mProcessStack.write(getContext().getStackPointer()+2,
        getContext().getBasePointer());
      mProcessStack.write(getContext().getStackPointer()+3,
        getContext().getProgramCounter());
      getContext().setBasePointer(
        (short) (getContext().getStackPointer()+1));
      getContext().setProgramCounter(
        getContext().getInstructionRegister().getWParameter());
    }
    else if (iCommand == Instruction.OPCODE_INT)
    {
      getContext().setStackPointer((short)
        (getContext().getStackPointer() +
        getContext().getInstructionRegister().getWParameter()));
    }
    else if (iCommand == Instruction.OPCODE_JMP)
    {
      getContext().setProgramCounter(
        getContext().getInstructionRegister().getWParameter());
    }
    else if (iCommand == Instruction.OPCODE_JPC)
    {
      if (mProcessStack.read(getContext().getStackPointer())
          ==
          getContext().getInstructionRegister().getBParameter())
      {
        getContext().setProgramCounter(
          getContext().getInstructionRegister().getWParameter());
      }
      getContext().decStackPointer();
    }
    else if (iCommand == Instruction.OPCODE_CSP)
    {
      myKernel.handleSystemCall();
    }
    else if (iCommand == Instruction.OPCODE_LODX)
    {
      getContext().getInstructionRegister().setWParameter(
        (short)(getContext().getInstructionRegister().getWParameter() +
        mProcessStack.read(getContext().getStackPointer())));
      mProcessStack.write(getContext().getStackPointer(),
       (mProcessStack.read(
         findBase(getContext().getInstructionRegister().getBParameter())
       + getContext().getInstructionRegister().getWParameter())));
    }
    else if (iCommand == Instruction.OPCODE_STOX)
    {
      getContext().getInstructionRegister().setWParameter((short)
        (mProcessStack.read(getContext().getStackPointer()-1) +
         getContext().getInstructionRegister().getWParameter()));
       mProcessStack.write(
         (findBase(getContext().getInstructionRegister().getBParameter()) +
         getContext().getInstructionRegister().getWParameter()),
         mProcessStack.read(getContext().getStackPointer()));
    }
  }

  // carry out instruction execution for OPR opcode
  // fixed DEC opcode - AN.  Swapped around
  // decrement of PC in opcodes.
  private void handleOperator()
  {
    int iOperator =
      getContext().getInstructionRegister().getWParameter();

    if (iOperator == Instruction.OPERATOR_RET)
    {
      getContext().setStackPointer((short)
        (getContext().getBasePointer() - 1));
      getContext().setBasePointer((short)
        (mProcessStack.read(getContext().getStackPointer()+2)));
      getContext().setProgramCounter((short)
        (mProcessStack.read(getContext().getStackPointer()+3)));
      bCodeToExecute = (getContext().getBasePointer() >= 0);
    }
    else if (iOperator == Instruction.OPERATOR_NEG)
    {
      mProcessStack.write(getContext().getStackPointer(),
        (short) (0 - mProcessStack.read(getContext().getStackPointer())));
    }
    else if (iOperator == Instruction.OPERATOR_ADD)
    {
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (short) (mProcessStack.read(getContext().getStackPointer()) +
         mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_SUB)
    {
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (short) (mProcessStack.read(getContext().getStackPointer()) -
        mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_MUL)
    {
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (short) (mProcessStack.read(getContext().getStackPointer()) *
        mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_DIV)
    {
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (short) (mProcessStack.read(getContext().getStackPointer()) /
        mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_LOW)
    {
      mProcessStack.write(getContext().getStackPointer(),
        (short) (mProcessStack.read(getContext().getStackPointer()) & 1));
    }
    else if (iOperator == Instruction.OPERATOR_MOD)
    {
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (short) (mProcessStack.read(getContext().getStackPointer()) %
        mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_EQ)
    {
      getContext().decStackPointer();
      if (mProcessStack.read(getContext().getStackPointer()) ==
          mProcessStack.read(getContext().getStackPointer()+1))
      {
        mProcessStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        mProcessStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (iOperator == Instruction.OPERATOR_NE)
    {
      getContext().decStackPointer();
      if (mProcessStack.read(getContext().getStackPointer()) !=
          mProcessStack.read(getContext().getStackPointer()+1))
      {
        mProcessStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        mProcessStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (iOperator == Instruction.OPERATOR_LT)
    {
      getContext().decStackPointer();
      if (mProcessStack.read(getContext().getStackPointer()) <
          mProcessStack.read(getContext().getStackPointer()+1))
      {
        mProcessStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        mProcessStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (iOperator == Instruction.OPERATOR_GE)
    {
      getContext().decStackPointer();
      if (mProcessStack.read(getContext().getStackPointer()) >=
          mProcessStack.read(getContext().getStackPointer()+1))
      {
        mProcessStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        mProcessStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (iOperator == Instruction.OPERATOR_GT)
    {
      getContext().decStackPointer();
      if (mProcessStack.read(getContext().getStackPointer()) >
          mProcessStack.read(getContext().getStackPointer()+1))
      {
        mProcessStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        mProcessStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (iOperator == Instruction.OPERATOR_LE)
    {
      getContext().decStackPointer();
      if (mProcessStack.read(getContext().getStackPointer()) <=
          mProcessStack.read(getContext().getStackPointer()+1))
      {
        mProcessStack.write(getContext().getStackPointer(),1);
      }
      else
      {
        mProcessStack.write(getContext().getStackPointer(),0);
      }
    }
    else if (iOperator == Instruction.OPERATOR_OR)
    {
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (mProcessStack.read(getContext().getStackPointer()) |
         mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_AND)
    {
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (mProcessStack.read(getContext().getStackPointer()) &
         mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_XOR)
    {
      getContext().decStackPointer();
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (mProcessStack.read(getContext().getStackPointer()) ^
         mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_NOT)
    {
      mProcessStack.write(getContext().getStackPointer(),
       (short) ~mProcessStack.read(getContext().getStackPointer()));
    }
    else if (iOperator == Instruction.OPERATOR_SHL)
    {
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (mProcessStack.read(getContext().getStackPointer()) <<
         mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_SHR)
    {
      getContext().decStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
        (mProcessStack.read(getContext().getStackPointer()) >>
         mProcessStack.read(getContext().getStackPointer()+1)));
    }
    else if (iOperator == Instruction.OPERATOR_INC)
    {
      mProcessStack.write(getContext().getStackPointer(),
       (short) mProcessStack.read(getContext().getStackPointer())+1);
    }
    else if (iOperator == Instruction.OPERATOR_DEC)
    {
      mProcessStack.write(getContext().getStackPointer(),
       (short) mProcessStack.read(getContext().getStackPointer())-1);
    }
    else if (iOperator == Instruction.OPERATOR_CPY)
    {
      getContext().incStackPointer();
      mProcessStack.write(getContext().getStackPointer(),
       mProcessStack.read(getContext().getStackPointer()-1));
    }
    else
    {
      System.out.println("Illegal Instruction");
    }
  }

  // called by device drivers and other sources of interrupts
  // passed an Interrupt which is added to the InterruptQueue
  public void generateInterrupt(Interrupt aInterrupt)
  {
    // if the interrupt should occur straight away time == -1
    // change this to be the current time + 1
    if (aInterrupt.getTime() == -1)
      aInterrupt.setTime(iTicks);

    iqInterruptsQueue.insert((Object) aInterrupt);
  }

  // execute once every instruction execution cycle if bInterruptsEnabled
  // - checks InterruptQ to see if any Interrupts have occurred at the
  //   current time
  public void handleInterrupts()
  {
    if (bInterruptsEnabled)
    {
      if (!iqInterruptsQueue.queueEmpty())
      {
        //is there an interrupt for the current time?
        Interrupt intCurrentInterrupt = iqInterruptsQueue.getInterrupt(iTicks);
        while (intCurrentInterrupt != null)
        {
          myKernel.handleInterrupt(intCurrentInterrupt);
          intCurrentInterrupt = iqInterruptsQueue.getInterrupt(iTicks);
        }
      }
      //if ((iTicks % TIMER_PERIOD == 0) && (runningProcess()))
      if (iTicks % TIMER_PERIOD == 0)
      {
        myKernel.handleTimerInterrupt();
      }
    }
  }
}
