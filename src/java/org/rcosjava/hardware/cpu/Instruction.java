package org.rcosjava.hardware.cpu;

import java.io.Serializable;

/**
 * The P-Code Instruction class. Contains data for a single P-Code instruction.
 * Each value is stored as an integer but a string representation can be
 * returned. This is mainly for the user to understand.
 * <P>
 * <DT> <B>Usage Example:</B>
 * <DD> <CODE>
 *      Instruction newInstruction = new Instruction(0, 0, 0);
 * </CODE> </DD> </DT>
 * <P>
 * Creates LIT 0,0 OP-Code.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 28/01/96 Created by removing class from CPU.java reorganised classes and
 * methods mainly to remove large unnecessary switch statements. </DD>
 * <DD> 23/03/96 Moved into CPU package. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 28th of January 1996
 * @see org.rcosjava.hardware.cpu.CPU
 * @version 1.00 $Date$
 */
public class Instruction implements Cloneable, Serializable
{
  /**
   * Literal instruction.
   */
  public static final Instruction LIT_INSTRUCTION = new Instruction(
    OpCode.LITERAL)
  {
    void execute(CPU cpu)
    {
      cpu.getContext().incStackPointer();
      cpu.getStack().write(cpu.getContext().getStackPointer(),
        cpu.getContext().getInstructionRegister().getWordParameter());
    }
  };

  /**
   * Operation instruction.
   */
  public static final Instruction OPR_INSTRUCTION = new Instruction(
    OpCode.OPERATION)
  {
    void execute(CPU cpu)
    {
      cpu.handleOperator();
    }
  };

  /**
  * Load instruction.
  */
  public static final Instruction LOD_INSTRUCTION = new Instruction(
    OpCode.LOAD)
  {
    void execute(CPU cpu)
    {
      // LOD 255, 0
      if (cpu.getContext().getInstructionRegister().getByteParameter() == 255)
      {
        cpu.getStack().write(cpu.getContext().getStackPointer(),
          (short) cpu.getStack().read(
            cpu.getContext().getStackPointer()));
      }
      // LOD L, N
      else
      {
        cpu.getContext().incStackPointer();
        cpu.getStack().write(cpu.getContext().getStackPointer(),
          (cpu.getStack().read(
          cpu.findBase(
            cpu.getContext().getInstructionRegister().getByteParameter()) +
            cpu.getContext().getInstructionRegister().getWordParameter())));
      }
    }
  };

  /**
   * Store instruction.
   */
  public static final Instruction STO_INSTRUCTION = new Instruction(
    OpCode.STORE)
  {
    void execute(CPU cpu)
    {
      // STO 255, 0
      if (cpu.getContext().getInstructionRegister().getByteParameter() == 255)
      {
        cpu.getStack().write(cpu.getStack().read(
          cpu.getStack().read(cpu.getContext().getStackPointer() - 1)),
          cpu.getStack().read(cpu.getContext().getStackPointer()));
        cpu.getContext().decStackPointer();
        cpu.getContext().decStackPointer();
      }
      // STO L, N
      else
      {
        cpu.getStack().write(
          cpu.findBase(
            cpu.getContext().getInstructionRegister().getByteParameter()) +
            cpu.getContext().getInstructionRegister().getWordParameter(),
            cpu.getStack().read(cpu.getContext().getStackPointer()));
        cpu.getContext().decStackPointer();
      }
    }
  };

  /**
   * Call instruction.
   */
  public static final Instruction CAL_INSTRUCTION = new Instruction(
    OpCode.CALL)
  {
    void execute(CPU cpu)
    {
      cpu.getStack().write(cpu.getContext().getStackPointer() + 1,
        cpu.findBase(
          cpu.getContext().getInstructionRegister().getByteParameter()));
      cpu.getStack().write(cpu.getContext().getStackPointer() + 2,
        cpu.getContext().getBasePointer());
      cpu.getStack().write(cpu.getContext().getStackPointer() + 3,
        cpu.getContext().getProgramCounter());
      cpu.getContext().setBasePointer(
        (short) (cpu.getContext().getStackPointer() + 1));
      cpu.getContext().setProgramCounter(
        cpu.getContext().getInstructionRegister().getWordParameter());
    }
  };

  /**
   * Increment T Register (Stack Pointer) instruction.
   */
  public static final Instruction INT_INSTRUCTION = new Instruction(
    OpCode.INCREMENT_T_REGISTER)
  {
    void execute(CPU cpu)
    {
      cpu.getContext().setStackPointer((short)
          (cpu.getContext().getStackPointer() +
          cpu.getContext().getInstructionRegister().getWordParameter()));
    }
  };

  /**
   * Jump instruction.
   */
  public static final Instruction JMP_INSTRUCTION = new Instruction(OpCode.JUMP)
  {
    void execute(CPU cpu)
    {
      cpu.getContext().setProgramCounter(
          cpu.getContext().getInstructionRegister().getWordParameter());
    }
  };

  /**
   * Jump on condition instruction.
   */
  public static final Instruction JPC_INSTRUCTION = new Instruction(
    OpCode.JUMP_ON_CONDITION)
  {
    void execute(CPU cpu)
    {
      if (cpu.getStack().read(cpu.getContext().getStackPointer())
           ==
          cpu.getContext().getInstructionRegister().getByteParameter())
      {
        cpu.getContext().setProgramCounter(
            cpu.getContext().getInstructionRegister().getWordParameter());
      }
      cpu.getContext().decStackPointer();
    }
  };

  /**
   * Call system procedure instruction.
   */
  public static final Instruction CSP_INSTRUCTION = new Instruction(
    OpCode.CALL_SYSTEM_PROCEDURE)
  {
    void execute(CPU cpu)
    {
      try
      {
        cpu.systemCall();
      }
      catch (java.io.IOException ioe)
      {
        ioe.printStackTrace();
      }
    }
  };

  /**
   * Load indexed instruction.
   */
  public static final Instruction LODX_INSTRUCTION = new Instruction(
    OpCode.LOAD_INDEXED)
  {
    void execute(CPU cpu)
    {
      cpu.getStack().write(cpu.getContext().getStackPointer(),
        (cpu.getStack().read(
          cpu.findBase(
            cpu.getContext().getInstructionRegister().getByteParameter()) +
          cpu.getContext().getInstructionRegister().getWordParameter() +
          cpu.getStack().read(cpu.getContext().getStackPointer()))));
    }
  };

  /**
   * Store indexed instruction.
   */
  public static final Instruction STOX_INSTRUCTION = new Instruction(
    OpCode.STORE_INDEXED)
  {
    void execute(CPU cpu)
    {
      cpu.getStack().write(
        cpu.findBase(
          cpu.getContext().getInstructionRegister().getByteParameter()) +
          cpu.getContext().getInstructionRegister().getWordParameter() +
          cpu.getStack().read(cpu.getContext().getStackPointer() - 1),
          cpu.getStack().read(cpu.getContext().getStackPointer()));
      cpu.getContext().decStackPointer();
      cpu.getContext().decStackPointer();
    }
  };

  /**
   * Illegal instruction.
   */
  public static final Instruction ILL_INSTRUCTION = new Instruction(
    OpCode.ILLEGAL)
  {
    void execute(CPU cpu)
    {
      System.err.println("Illegal Instruction");
    }
  };

  /**
   * Index of instruction.
   */
  public static final Instruction INSTRUCTIONS[] =
  {
    Instruction.LIT_INSTRUCTION, Instruction.OPR_INSTRUCTION,
    Instruction.LOD_INSTRUCTION, Instruction.STO_INSTRUCTION,
    Instruction.CAL_INSTRUCTION, Instruction.INT_INSTRUCTION,
    Instruction.JMP_INSTRUCTION, Instruction.JPC_INSTRUCTION,
    Instruction.CSP_INSTRUCTION, Instruction.ILL_INSTRUCTION,
    Instruction.ILL_INSTRUCTION, Instruction.ILL_INSTRUCTION,
    Instruction.ILL_INSTRUCTION, Instruction.ILL_INSTRUCTION,
    Instruction.ILL_INSTRUCTION, Instruction.ILL_INSTRUCTION,
    Instruction.ILL_INSTRUCTION, Instruction.ILL_INSTRUCTION,
    Instruction.LODX_INSTRUCTION, Instruction.STOX_INSTRUCTION,
    Instruction.ILL_INSTRUCTION
  };

  /**
   * Opcode of the instruction.
   */
  private OpCode opCode;

  /**
   * Byte parameter of the instruction.
   */
  private byte byteParam;

  /**
   * Word parameter of the instruction.
   */
  private WordParameter wordParam;

  /**
   * Default constructor creates opCode, byteParam and wordParam with default
   * values (all illegal).
   */
  public Instruction()
  {
    opCode = OpCode.ILLEGAL;
    byteParam = -1;
    wordParam = WordParameter.ILLEGAL;
  }

  /**
   * Construct an Instruction. Byte param is -1 and Word param is illegal.
   *
   * @param newOpCode the index to the thirteen opcodes available.
   */
  public Instruction(OpCode newOpCode)
  {
    opCode = newOpCode;
    byteParam = (byte) -1;
    wordParam = WordParameter.ILLEGAL;
  }

  /**
   * Construct a complete Instruction from a string. The expected format is
   * opcode[space]byte_parameter[comma]word_param
   *
   * @param newInstruction Description of Parameter
   */
  public Instruction(String newInstruction)
  {
    newInstruction = newInstruction.trim();

    int spaceLocation = newInstruction.indexOf(" ");
    int commaLocation = newInstruction.indexOf(",");
    String opStr = newInstruction.substring(0, spaceLocation);
    String byteStr = newInstruction.substring(spaceLocation + 1,
        commaLocation).trim();
    String wordStr = newInstruction.substring(commaLocation + 1,
        newInstruction.length()).trim();

    if (opStr == "" || byteStr == "" || wordStr == "")
    {
      throw new java.lang.IllegalArgumentException("Incorrect instruction");
    }

    opCode = OpCode.getOpCodesByName(opStr);

    try
    {
      byteParam = Byte.parseByte(byteStr);
    }
    catch (NumberFormatException nfe)
    {
      throw new java.lang.IllegalArgumentException(
          "Incorrect byte parameter: [" + byteStr + "]");
    }

    setWordParam(wordStr);
  }

  /**
   * Sets the new word parameter. There is no error checking performed.
   *
   * @param newWordParameter The new word parameter value.
   */
  public void setWordParameter(short newWordParameter)
  {
    setWordParam(newWordParameter);
  }

  /**
   * Returns the opCode stored in the instruction. Returns an OPCODE_ILLEGAL if
   * the opcode is wrong.
   *
   * @return The OpCode value
   */
  public OpCode getOpCode()
  {
    return opCode;
  }

  /**
   * Sets the new byte parameter. There is no error checking performed.
   *
   * @param newByteParameter The new byte parameter value.
   */
  public void setByteParameter(byte newByteParameter)
  {
    byteParam = newByteParameter;
  }

  /**
   * Returns the byte parameters. There is no checking. It will always return a
   * value.
   *
   * @return The ByteParameter value
   */
  public byte getByteParameter()
  {
    return byteParam;
  }

  /**
   * Returns a valid word parameter based on the current opcode. Will return
   * OPERATOR_ILLEGAL if the parameter is incorrect for the current opcode.
   *
   * @return The WordParameter value
   */
  public short getWordParameter()
  {
    return wordParam.getValue();
  }

  /**
   * Execute the opcode on the CPU.
   *
   * @param cpu the cpu to execute the code.
   */
  void execute(CPU cpu)
  {
    //By default do nothing.
  }

  /**
   * Returns true if the instruction is a Character In instruction.
   *
   * @return true if the instruction is a Character In instruction.
   */
  public boolean isChIn()
  {
    return (wordParam == SystemCall.CHARACTER_IN);
  }

  /**
   * Returns true if the instruction is a Character Out instruction.
   *
   * @return true if the instruction is a Character Out instruction.
   */
  public boolean isChOut()
  {
    return (wordParam == SystemCall.CHARACTER_OUT);
  }

  /**
   * Returns true if the instruction is a Number In instruction.
   *
   * @return true if the instruction is a Number In instruction.
   */
  public boolean isNumIn()
  {
    return (wordParam == SystemCall.NUMBER_IN);
  }

  /**
   * Returns true if the instruction is a Number Out instruction.
   *
   * @return true if the instruction is a Number Out instruction.
   */
  public boolean isNumOut()
  {
    return (wordParam == SystemCall.NUMBER_OUT);
  }

  /**
   * Returns true if the instruction is a String Out instruction
   *
   * @return The StrOut value
   */
  public boolean isStrOut()
  {
    return (wordParam == SystemCall.STRING_OUT);
  }

  /**
   * Returns true if the instruction is a Semaphore Close instruction.
   *
   * @return true if the instruction is a Semaphore Close instruction.
   */
  public boolean isSemaphoreClose()
  {
    return (wordParam == SystemCall.SEMAPHORE_CLOSE);
  }

  /**
   * Returns true if the instruction is a Semaphore Create instruction
   *
   * @return The SemaphoreCreate value
   */
  public boolean isSemaphoreCreate()
  {
    return (wordParam == SystemCall.SEMAPHORE_CREATE);
  }

  /**
   * Returns true if the instruction is a Semaphore Open instruction
   *
   * @return The SemaphoreOpen value
   */
  public boolean isSemaphoreOpen()
  {
    return (wordParam == SystemCall.SEMAPHORE_OPEN);
  }

  /**
   * Returns true if the instruction is a Semaphore Signal instruction
   *
   * @return The SemaphoreSignal value
   */
  public boolean isSemaphoreSignal()
  {
    return (wordParam == SystemCall.SEMAPHORE_SIGNAL);
  }

  /**
   * Returns true if the instruction is a Semaphore Wait instruction
   *
   * @return The SemaphoreWait value
   */
  public boolean isSemaphoreWait()
  {
    return (wordParam == SystemCall.SEMAPHORE_WAIT);
  }

  /**
   * Returns true if the instruction is a Shared Memory Close instruction
   *
   * @return The SharedMemoryClose value
   */
  public boolean isSharedMemoryClose()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_CLOSE);
  }

  /**
   * Returns true if the instruction is a Shared Memory Create instruction
   *
   * @return The SharedMemoryCreate value
   */
  public boolean isSharedMemoryCreate()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_CREATE);
  }

  /**
   * Returns true if the instruction is a Shared Memory Open instruction
   *
   * @return The SharedMemoryOpen value
   */
  public boolean isSharedMemoryOpen()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_OPEN);
  }

  /**
   * Returns true if the instruction is a Shared Memory Read instruction
   *
   * @return The SharedMemoryRead value
   */
  public boolean isSharedMemoryRead()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_READ);
  }

  /**
   * Returns true if the instruction is a Shared Memory Size instruction
   *
   * @return The SharedMemorySize value
   */
  public boolean isSharedMemorySize()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_SIZE);
  }

  /**
   * Returns true if the instruction is a Shared Memory Write instruction
   *
   * @return The SharedMemoryWrite value
   */
  public boolean isSharedMemoryWrite()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_WRITE);
  }

  /**
   * Returns true if the instruction is a Fork instruction
   *
   * @return The Fork value
   */
  public boolean isFork()
  {
    return (wordParam == SystemCall.FORK);
  }

  /**
   * Returns true if the instruction is a Call Function instruction
   *
   * @return The CallFunction value
   */
  public boolean isCallFunction()
  {
    return (opCode == OpCode.CALL);
  }

  /**
   * Returns true if the instruction is a Call Standard System Procedure
   * instruction
   *
   * @return The CallSystemProcedure value
   */
  public boolean isCallSystemProcedure()
  {
    return (opCode == OpCode.CALL_SYSTEM_PROCEDURE);
  }

  /**
   * Returns true if the instruction is a Increment T-Register instruction.
   *
   * @return true if the instruction is a Increment T-Register.
   */
  public boolean isIncrementTRegister()
  {
    return (opCode == OpCode.INCREMENT_T_REGISTER);
  }

  /**
   * Returns true if the instruction is a Jump instruction.
   *
   * @return true if the instruction is a Jump instruction.
   */
  public boolean isJump()
  {
    return (opCode == OpCode.JUMP);
  }

  /**
   * Returns true if the instruction is a Jump on Compare instruction.
   *
   * @return true if the instruction is a Jump on Compare instruction.
   */
  public boolean isJumpCompare()
  {
    return (opCode == OpCode.JUMP_ON_CONDITION);
  }

  /**
   * Returns true if the instruction is a Literal instruction.
   *
   * @return true if the instruction is a Literal instruction.
   */
  public boolean isLiteral()
  {
    return (opCode == OpCode.LITERAL);
  }

  /**
   * Returns true if the instruction is a Load instruction.
   *
   * @return true if the instruction is a Load instruction.
   */
  public boolean isLoad()
  {
    return (opCode == OpCode.LOAD);
  }

  /**
   * Returns true if the instruction is a Load X instruction.
   *
   * @return true if the instruction is a Load X instruction.
   */
  public boolean isLoadX()
  {
    return (opCode == OpCode.LOAD_INDEXED);
  }

  /**
   * Returns true if the instruction is a Operation (logical or arithmetic)
   *   instruction.
   *
   * @return true if the instruction is a Operation (logical or arithmetic)
   *   instruction.
   */
  public boolean isOperation()
  {
    return (opCode == OpCode.OPERATION);
  }

  /**
   * Returns true if the instruction is a Store instruction.
   *
   * @return true if the instruction is a Store instruction.
   */
  public boolean isStore()
  {
    return (opCode == OpCode.STORE);
  }

  /**
   * Returns true if the instruction is a Store X instruction.
   *
   * @return true if the instruction is a Store X instruction.
   */
  public boolean isStoreX()
  {
    return (opCode == OpCode.STORE_INDEXED);
  }

  /**
   * Returns true if the instruction is a Add instruction.
   *
   * @return true if the instruction is a Add instruction.
   */
  public boolean isAdd()
  {
    return (wordParam == Operator.ADD);
  }

  /**
   * Returns true if the instruction is a And instruction.
   *
   * @return The And value
   */
  public boolean isAnd()
  {
    return (wordParam == Operator.AND);
  }

  /**
   * Returns true if the instruction is a Copy instruction.
   *
   * @return true if the instruction is a Copy instruction.
   */
  public boolean isCopy()
  {
    return (wordParam == Operator.COPY);
  }

  /**
   * Returns true if the instruction is a Decrement instruction.
   *
   * @return true if the instruction is a Decrement instruction.
   */
  public boolean isDecrement()
  {
    return (wordParam == Operator.DECREMENT);
  }

  /**
   * Returns true if the instruction is a Divide instruction.
   *
   * @return true if the instruction is a Divide instruction.
   */
  public boolean isDivide()
  {
    return (wordParam == Operator.DIVIDE);
  }

  /**
   * Returns true if the instruction is a logical Equals instruction.
   *
   * @return true if the instruction is a logical Equals instruction.
   */
  public boolean isEquals()
  {
    return (wordParam == Operator.EQUAL);
  }

  /**
   * Returns true if the instruction is a logical Greater than or equals to
   *   instruction.
   *
   * @return true if the instruction is a logical Greater than or equals to
   *   instruction.
   */
  public boolean isGreaterThanOrEqualTo()
  {
    return (wordParam == Operator.GREATER_THAN_OR_EQUAL);
  }

  /**
   * Returns true if the instruction is a logical Greater Than instruction.
   *
   * @return true if the instruction is a logical Greater Than instruction.
   */
  public boolean isGreaterThan()
  {
    return (wordParam == Operator.GREATER_THAN);
  }

  /**
   * Returns true if the instruction is a Increment instruction.
   *
   * @return true if the instruction is a Increment instruction.
   */
  public boolean isIncrement()
  {
    return (wordParam == Operator.INCREMENT);
  }

  /**
   * Returns true if the instruction is a logical Less than or equal to
   *   instruction.
   *
   * @return true if the instruction is a logical Less than or equal to
   *   instruction.
   */
  public boolean isLessThanOrEqualTo()
  {
    return (wordParam == Operator.LESS_THAN_OR_EQUAL);
  }

  /**
   * Returns true if the instruction is a Low bit instruction.
   *
   * @return true if the instruction is a Low bit instruction.
   */
  public boolean isLow()
  {
    return (wordParam == Operator.LOW_BIT);
  }

  /**
   * Returns true if the instruction is a logical Less Than instruction.
   *
   * @return true if the instruction is a logical Less Than instruction.
   */
  public boolean isLessThan()
  {
    return (wordParam == Operator.LESS_THAN);
  }

  /**
   * Returns true if the instruction is a Modulus instruction.
   *
   * @return true if the instruction is a Modulus instruction.
   */
  public boolean isModulus()
  {
    return (wordParam == Operator.MODULUS);
  }

  /**
   * Returns true if the instruction is a Multiply instruction.
   *
   * @return true if the instruction is a Multiply instruction.
   */
  public boolean isMultiply()
  {
    return (wordParam == Operator.MULTIPLY);
  }

  /**
   * Returns true if the instruction is a logical Not Equal to instruction.
   *
   * @return true if the instruction is a logical Not Equal to instruction.
   */
  public boolean isNotEqualTo()
  {
    return (wordParam == Operator.NOT_EQUAL);
  }

  /**
   * Returns true if the instruction is a Negative instruction.
   *
   * @return true if the instruction is a Negative instruction.
   */
  public boolean isNegative()
  {
    return (wordParam == Operator.NEGATIVE);
  }

  /**
   * Returns true if the instruction is a logical Not instruction.
   *
   * @return true if the instruction is a logical Not instruction.
   */
  public boolean isNot()
  {
    return (wordParam == Operator.NOT);
  }

  /**
   * Returns true if the instruction is a logical Or instruction.
   *
   * @return true if the instruction is a logical Or instruction.
   */
  public boolean isOr()
  {
    return (wordParam == Operator.OR);
  }

  /**
   * Returns true if the instruction is a return instruction.
   *
   * @return true if the instruction is a return instruction.
   */
  public boolean isReturn()
  {
    return (wordParam == Operator.RETURN);
  }

  /**
   * Returns true if the instruction is a bitwise shift left instruction.
   *
   * @return true if the instruction is a bitwise shift left instruction.
   */
  public boolean isShiftLeft()
  {
    return (wordParam == Operator.SHIFT_LEFT);
  }

  /**
   * Returns true if the instruction is a bitwise shift right instruction.
   *
   * @return true if the instruction is a bitwise shift right instruction.
   */
  public boolean isShiftRight()
  {
    return (wordParam == Operator.SHIFT_RIGHT);
  }

  /**
   * Returns true if the instruction is a subtract instruction.
   *
   * @return true if the instruction is a subtract instruction.
   */
  public boolean isSubtract()
  {
    return (wordParam == Operator.SUBTRACT);
  }

  /**
   * Returns true if the instruction is a logical xor instruction.
   *
   * @return true if the instruction is a logical xor instruction.
   */
  public boolean isXor()
  {
    return (wordParam == Operator.XOR);
  }

  /**
   * Makes a shallow copy of the instruction.
   *
   * @return Description of the Returned Value
   */
  public Object clone()
  {
    Instruction newInstruction = new Instruction(getOpCode());
    newInstruction.setWordParameter(getWordParameter());
    newInstruction.setByteParameter(getByteParameter());

    return newInstruction;
  }

  /**
   * Compares the object based on opCode, byteParam and wordParam.
   *
   * @param obj the object (has to be Instruction) to compare.
   * @return Description of the Returned Value
   * @returns true if all values are equal.
   */
  public boolean equals(Object obj)
  {
    if (obj != null && (obj.getClass().equals(this.getClass())))
    {
      Instruction instr = (Instruction) obj;

      if ((getOpCode() == instr.getOpCode()) &&
          (getByteParameter() == instr.getByteParameter()) &&
          (this.getWordParameter() == instr.getWordParameter()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the hash code of the object based on opCode, byteParam and
   *   wordParam.
   *
   * @return the hash code of the object.
   */
  public int hashCode()
  {
    return opCode.getValue() ^ byteParam ^ wordParam.getValue();
  }

  /**
   * Returns the binary value of the instruction dependant upon what the opCode
   * that is currently stored. 8 bytes.
   *
   * @return Description of the Returned Value
   */
  public byte[] toByte()
  {
    byte instructionBytes[] = new byte[8];
    byte opCodeBytes[] = new byte[2];
    byte paramByte;
    byte wordBytes[] = new byte[2];

    opCodeBytes = convertShortToBytes((short) opCode.getValue());
    paramByte = byteParam;
    wordBytes = convertShortToBytes(wordParam.getValue());

    instructionBytes[0] = opCodeBytes[1];
    instructionBytes[1] = opCodeBytes[0];
    instructionBytes[2] = 0;
    instructionBytes[3] = 0;
    instructionBytes[4] = paramByte;
    instructionBytes[5] = wordBytes[0];
    instructionBytes[6] = wordBytes[1];

    return instructionBytes;
  }

  /**
   * Returns the string value of the instruction dependant upon what the opCode
   * that is currently stored.
   *
   * @return Description of the Returned Value
   */
  public String toString()
  {
    return (opCode + " " + byteParam + ", " + wordParam);
  }

  /**
   * Sets the word parameter based on the current value of OpCode and the String
   * given. Uses the ByName calls of SystemCall and Operator.
   *
   * @param wordStr The new WordParam value
   */
  private void setWordParam(String wordStr)
  {
    try
    {
      if (opCode == OpCode.CALL_SYSTEM_PROCEDURE)
      {
        wordParam = SystemCall.getSystemCallsByName(wordStr);
      }
      else if (opCode == OpCode.OPERATION)
      {
        wordParam = Operator.getOperatorsByName(wordStr);
      }
      else
      {
        wordParam = new WordParameterValue(Short.parseShort(wordStr));
      }
    }
    catch (NumberFormatException nfe)
    {
      throw new java.lang.IllegalArgumentException(
          "Incorrect word parameter: [" + wordStr + "]");
    }
  }

  /**
   * Sets the word parameter based on the current value of OpCode and the short
   * given. Uses the ByValue calls of SystemCall and Operator.
   *
   * @param wordShort the new value to set the word parameter to.
   */
  private void setWordParam(short wordShort)
  {
    if (opCode == OpCode.CALL_SYSTEM_PROCEDURE)
    {
      wordParam = SystemCall.getSystemCallsByValue(wordShort);
    }
    else if (opCode == OpCode.OPERATION)
    {
      wordParam = Operator.getOperatorsByValue(wordShort);
    }
    else
    {
      wordParam = new WordParameterValue(wordShort);
    }
    if (wordParam == null)
    {
      wordParam = WordParameter.ILLEGAL;
    }
  }

  /**
   * Converts a short to an array of bytes.
   *
   * @param existingValue the short value to convert from.
   * @return Description of the Returned Value
   */
  private byte[] convertShortToBytes(short existingValue)
  {
    byte newBytes[] = new byte[2];

    newBytes[0] = (byte) (existingValue >>> 8 & 0xff);
    newBytes[1] = (byte) (existingValue & 0xff);
    return newBytes;
  }
}
