package net.sourceforge.rcosjava.hardware.cpu;

import java.io.Serializable;

/**
 * The P-Code Instruction class.  Contains data for a single P-Code instruction.
 * Each value is stored as an integer but a string representation can be
 * returned.  This is mainly for the user to understand.
 * <P>
 * <DT><B>Usage Example:</B><DD>
 * <CODE>
 *      Instruction newInstruction = new Instruction(0, 0, 0);
 * </CODE>
 * </DD></DT>
 * <P>
 * Creates LIT 0,0 OP-Code.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 28/01/96 Created by removing class from CPU.java
 * reorganised classes and methods mainly to remove large unnecessary switch
 * statements.
 * </DD><DD>
 * 23/03/96 Moved into CPU package.
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.hardware.cpu.CPU
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 28th of January 1996
 */
public class Instruction implements Cloneable, Serializable
{
  private OpCode opCode;
  private byte byteParam;
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
   * Construct an fully fledged Instruction.  Should throw an
   * exception if the opCode and parameters are invalid.
   *
   * @param newOpCode the index to the thirteen opcodes available.
   * @param newByteParam the 0, lexical offset or condition of the opCode.
   * @param newWordParam the literal value of the opCode.
   */
  public Instruction(int newOpCode, byte newByteParam, short newWordParam)
  {
    opCode = OpCode.getOpCodesByValue(newOpCode);
    byteParam = newByteParam;
    setWordParam(newWordParam);
  }

  /**
   * Construct a complete Instruction from a string.  The expected format is
   * opcode[space]byte_parameter[comma]word_param
   */
  public Instruction(String newInstruction)
  {
    newInstruction = newInstruction.trim();
    int spaceLocation = newInstruction.indexOf(" ");
    int commaLocation = newInstruction.indexOf(",");
    String opStr = newInstruction.substring(0, spaceLocation);
    String byteStr = newInstruction.substring(spaceLocation+1,
      commaLocation).trim();
    String wordStr = newInstruction.substring(commaLocation+1,
      newInstruction.length()).trim();

    if (opStr == ""  || byteStr == "" || wordStr == "")
      throw new java.lang.IllegalArgumentException("Incorrect instruction");

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
   * Sets the word parameter based on the current value of OpCode and the
   * String given.  Uses the ByName calls of SystemCall and Operator.
   *
   * @param wordShort the new value to set the word parameter to.
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
   * Sets the word parameter based on the current value of OpCode and the
   * short given.  Uses the ByValue calls of SystemCall and Operator.
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
      wordParam = WordParameter.ILLEGAL;
  }

  /**
   * Returns the opCode stored in the instruction.  Returns an OPCODE_ILLEGAL
   * if the opcode is wrong.
   */
  public int getOpCode()
  {
    return opCode.getValue();
  }

  /**
   * Returns the byte parameters.  There is no checking.  It will always return
   * a value.
   */
  public byte getByteParameter()
  {
    return byteParam;
  }

  /**
   * Returns a valid word parameter based on the current opcode.  Will return
   * OPERATOR_ILLEGAL if the parameter is incorrect for the current opcode.
   */
  public short getWordParameter()
  {
    return wordParam.getValue();
  }

  /**
   * Sets the new word parameter.  There is no error checking performed.
   *
   * @param newWordParamter the new value to set.
   */
  public void setWordParameter(short newWordParameter)
  {
    setWordParam(newWordParameter);
  }

  /**
   * Returns true if the instruction is a Character In instruction
   */
  public boolean isChIn()
  {
    return (wordParam == SystemCall.CHARACTER_IN);
  }

  /**
   * Returns true if the instruction is a Character Out instruction
   */
  public boolean isChOut()
  {
    return (wordParam == SystemCall.CHARACTER_OUT);
  }

  /**
   * Returns true if the instruction is a Number In instruction
   */
  public boolean isNumIn()
  {
    return (wordParam == SystemCall.NUMBER_IN);
  }

  /**
   * Returns true if the instruction is a Number Out instruction
   */
  public boolean isNumOut()
  {
    return (wordParam == SystemCall.NUMBER_OUT);
  }

  /**
   * Returns true if the instruction is a String Out instruction
   */
  public boolean isStrOut()
  {
    return (wordParam == SystemCall.STRING_OUT);
  }

  /**
   * Returns true if the instruction is a Semaphore Close instruction
   */
  public boolean isSemaphoreClose()
  {
    return  (wordParam == SystemCall.SEMAPHORE_CLOSE);
  }

  /**
   * Returns true if the instruction is a Semaphore Create instruction
   */
  public boolean isSemaphoreCreate()
  {
    return  (wordParam == SystemCall.SEMAPHORE_CREATE);
  }

  /**
   * Returns true if the instruction is a Semaphore Open instruction
   */
  public boolean isSemaphoreOpen()
  {
    return (wordParam == SystemCall.SEMAPHORE_OPEN);
  }

  /**
   * Returns true if the instruction is a Semaphore Signal instruction
   */
  public boolean isSemaphoreSignal()
  {
    return (wordParam == SystemCall.SEMAPHORE_SIGNAL);
  }

  /**
   * Returns true if the instruction is a Semaphore Wait instruction
   */
  public boolean isSemaphoreWait()
  {
    return (wordParam == SystemCall.SEMAPHORE_WAIT);
  }

  /**
   * Returns true if the instruction is a Shared Memory Close instruction
   */
  public boolean isSharedMemoryClose()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_CLOSE);
  }

  /**
   * Returns true if the instruction is a Shared Memory Create instruction
   */
  public boolean isSharedMemoryCreate()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_CREATE);
  }

  /**
   * Returns true if the instruction is a Shared Memory Open instruction
   */
  public boolean isSharedMemoryOpen()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_OPEN);
  }

  /**
   * Returns true if the instruction is a Shared Memory Read instruction
   */
  public boolean isSharedMemoryRead()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_READ);
  }

  /**
   * Returns true if the instruction is a Shared Memory Size instruction
   */
  public boolean isSharedMemorySize()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_SIZE);
  }

  /**
   * Returns true if the instruction is a Shared Memory Write instruction
   */
  public boolean isSharedMemoryWrite()
  {
    return (wordParam == SystemCall.SHARED_MEMORY_WRITE);
  }

  /**
   * Returns true if the instruction is a Fork instruction
   */
  public boolean isFork()
  {
    return (wordParam == SystemCall.FORK);
  }

  /**
   * Returns true if the instruction is a Call Function instruction
   */
  public boolean isCallFunction()
  {
    return (opCode == OpCode.CALL);
  }

  /**
   * Returns true if the instruction is a Call Standard System Procedure
   * instruction
   */
  public boolean isCallSystemProcedure()
  {
    return (opCode == OpCode.CALL_SYSTEM_PROCEDURE);
  }
  /**
   * Returns true if the instruction is a Interval instruction
   */
  public boolean isInterval()
  {
    return (opCode == OpCode.INTERVAL);
  }
  /**
   * Returns true if the instruction is a Jump instruction
   */
  public boolean isJump()
  {
    return (opCode == OpCode.JUMP);
  }
  /**
   * Returns true if the instruction is a Jump on Compare instruction
   */
  public boolean isJumpCompare()
  {
    return (opCode == OpCode.JUMP_ON_CONDITION);
  }
  /**
   * Returns true if the instruction is a Literal instruction
   */
  public boolean isLiteral()
  {
    return (opCode == OpCode.LITERAL);
  }
  /**
   * Returns true if the instruction is a Load instruction
   */
  public boolean isLoad()
  {
    return (opCode == OpCode.LOAD);
  }
  /**
   * Returns true if the instruction is a Load X instruction
   */
  public boolean isLoadX()
  {
    return (opCode == OpCode.LOAD_INDEXED);
  }
  /**
   * Returns true if the instruction is a Operation (logical or arithmetic)
   * instruction
   */
  public boolean isOperation()
  {
    return (opCode == OpCode.OPERATION);
  }
  /**
   * Returns true if the instruction is a Store instruction
   */
  public boolean isStore()
  {
    return (opCode == OpCode.STORE);
  }
  /**
   * Returns true if the instruction is a Store X instruction
   */
  public boolean isStoreX()
  {
    return (opCode == OpCode.STORE_INDEXED);
  }
  /**
   * Returns true if the instruction is a Add instruction
   */
  public boolean isAdd()
  {
    return (wordParam == Operator.ADD);
  }
  /**
   * Returns true if the instruction is a And instruction
   */
  public boolean isAnd()
  {
    return (wordParam == Operator.AND);
  }
  /**
   * Returns true if the instruction is a Copy instruction
   */
  public boolean isCopy()
  {
    return (wordParam == Operator.COPY);
  }
  /**
   * Returns true if the instruction is a Decrement instruction
   */
  public boolean isDecrement()
  {
    return (wordParam == Operator.DECREMENT);
  }
  /**
   * Returns true if the instruction is a Divide instruction
   */
  public boolean isDivide()
  {
    return (wordParam == Operator.DIVIDE);
  }
  /**
   * Returns true if the instruction is a logical Equals instruction
   */
  public boolean isEquals()
  {
    return (wordParam == Operator.EQUAL);
  }
  /**
   * Returns true if the instruction is a logical Greater than or equals to
   * instruction
   */
  public boolean isGreaterThanOrEqualTo()
  {
    return (wordParam == Operator.GREATER_THAN_OR_EQUAL);
  }
  /**
   * Returns true if the instruction is a logical Greater Than instruction
   */
  public boolean isGreaterThan()
  {
    return (wordParam == Operator.GREATER_THAN);
  }
  /**
   * Returns true if the instruction is a Increment instruction
   */
  public boolean isIncrement()
  {
    return (wordParam == Operator.INCREMENT);
  }
  /**
   * Returns true if the instruction is a logical Less than or equal to instruction
   */
  public boolean isLessThanOrEqualTo()
  {
    return (wordParam == Operator.LESS_THAN_OR_EQUAL);
  }
  /**
   * Returns true if the instruction is a Low bit instruction
   */
  public boolean isLow()
  {
    return (wordParam == Operator.LOW_BIT);
  }
  /**
   * Returns true if the instruction is a logical Less Than instruction
   */
  public boolean isLessThan()
  {
    return (wordParam == Operator.LESS_THAN);
  }
  /**
   * Returns true if the instruction is a Modulus instruction
   */
  public boolean isModulus()
  {
    return (wordParam == Operator.MODULUS);
  }
  /**
   * Returns true if the instruction is a Multiply instruction
   */
  public boolean isMultiply()
  {
    return (wordParam == Operator.MULTIPLY);
  }
  /**
   * Returns true if the instruction is a logical Not Equal to instruction
   */
  public boolean isNotEqualTo()
  {
    return (wordParam == Operator.NOT_EQUAL);
  }
  /**
   * Returns true if the instruction is a Negative instruction
   */
  public boolean isNegative()
  {
    return (wordParam == Operator.NEGATIVE);
  }
  /**
   * Returns true if the instruction is a logical Not instruction
   */
  public boolean isNot()
  {
    return (wordParam == Operator.NOT);
  }
  /**
   * Returns true if the instruction is a logical Or instruction
   */
  public boolean isOr()
  {
    return (wordParam == Operator.OR);
  }
  /**
   * Returns true if the instruction is a return instruction
   */
  public boolean isReturn()
  {
    return (wordParam == Operator.RETURN);
  }
  /**
   * Returns true if the instruction is a bitwise shift left instruction
   */
  public boolean isShiftLeft()
  {
    return (wordParam == Operator.SHIFT_LEFT);
  }
  /**
   * Returns true if the instruction is a bitwise shift right instruction
   */
  public boolean isShiftRight()
  {
    return (wordParam == Operator.SHIFT_RIGHT);
  }
  /**
   * Returns true if the instruction is a subtract instruction
   */
  public boolean isSubtract()
  {
    return (wordParam == Operator.SUBTRACT);
  }
  /**
   * Returns true if the instruction is a logical xor instruction
   */
  public boolean isXor()
  {
    return (wordParam == Operator.XOR);
  }

  /**
   * Makes a shallow copy of the instruction.
   */
  public Object clone()
  {
    Instruction newInstruction = new Instruction(opCode.getValue(), byteParam,
      wordParam.getValue());
    return newInstruction;
  }

  /**
   * Compares the object based on opCode, byteParam and wordParam.
   *
   * @param obj the object (has to be Instruction) to compare.
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
   * Returns the binary value of the instruction dependant upon what the opCode
   * that is currently stored.  8 bytes.
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
   * Converts a short to an array of bytes.
   *
   * @param existingValue the short value to convert from.
   * @param the two length byte to store the result in.
   */
  private byte[] convertShortToBytes(short existingValue)
  {
    byte newBytes[] = new byte[2];
    newBytes[0] = (byte) (existingValue >>> 8 & 0xff);
    newBytes[1] = (byte) (existingValue & 0xff);
    return newBytes;
  }

  /**
   * Returns the string value of the instruction dependant upon what the opCode
   * that is currently stored.
   */
  public String toString()
  {
    return (opCode + " " + byteParam + ", " + wordParam);
  }
}