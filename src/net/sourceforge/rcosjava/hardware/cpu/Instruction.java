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
  /** Constant for LITeral call */
  public static final int OPCODE_LIT = 0x0;
  /** Constant for OPeRation (arithmetic or logical) call */
  public static final int OPCODE_OPR = 0x1;
  /** Constant for LOaD call */
  public static final int OPCODE_LOD = 0x2;
  /** Constant for STOre call */
  public static final int OPCODE_STO = 0x3;
  /** Constant for CALL (a function) call */
  public static final int OPCODE_CAL = 0x4;
  /** Constant for INTerval (?) call */
  public static final int OPCODE_INT = 0x5;
  /** Constant for JuMP call */
  public static final int OPCODE_JMP = 0x6;
  /** Constant for JuMP on Condition */
  public static final int OPCODE_JPC = 0x7;
  /** Constant for Call Standard System Procedure call */
  public static final int OPCODE_CSP = 0x8;
  /** Constant for LOaD indeXed variable (array) call */
  public static final int OPCODE_LODX = 0x12;
  /** Constant for STOre indeXed variable (array) call */
  public static final int OPCODE_STOX = 0x13;
  /** Constant for illegal call */
  public static final int OPCODE_ILLEGAL = 0x14;

  private static final String opCodes[] = { "LIT", "OPR", "LOD", "STO",
    "CAL", "INT", "JMP", "JPC", "CSP", "", "", "", "", "", "", "", "", "",
    "LODX", "STOX", "ILLEGAL_OP_ERROR" };

  /** Operation constant (for OPCODE_OPR) RETurn */
  public static final byte OPERATOR_RET = 0;
  /** Operation constant (for OPCODE_OPR) NEGative */
  public static final byte OPERATOR_NEG = 1;
  /** Operation constant (for OPCODE_OPR) ADDition */
  public static final byte OPERATOR_ADD = 2;
  /** Operation constant (for OPCODE_OPR) SUBtraction */
  public static final byte OPERATOR_SUB = 3;
  /** Operation constant (for OPCODE_OPR) MULtiplicat */
  public static final byte OPERATOR_MUL = 4;
  /** Operation constant (for OPCODE_OPR) DIVision */
  public static final byte OPERATOR_DIV = 5;
  /** Operation constant (for OPCODE_OPR) LOW bit*/
  public static final byte OPERATOR_LOW = 6;
  /** Operation constant (for OPCODE_OPR) MODulus */
  public static final byte OPERATOR_MOD = 7;
  /** Operation constant (for OPCODE_OPR) EQual test */
  public static final byte OPERATOR_EQ = 8;
  /** Operation constant (for OPCODE_OPR) Not Equal test */
  public static final byte OPERATOR_NE = 9;
  /** Operation constant (for OPCODE_OPR) Less Than test */
  public static final byte OPERATOR_LT = 10;
  /** Operation constant (for OPCODE_OPR) Greater than or Equal test */
  public static final byte OPERATOR_GE = 11;
  /** Operation constant (for OPCODE_OPR) Greater Than test */
  public static final byte OPERATOR_GT = 12;
  /** Operation constant (for OPCODE_OPR) Less than or Equal test */
  public static final byte OPERATOR_LE = 13;
  /** Operation constant (for OPCODE_OPR) logical OR */
  public static final byte OPERATOR_OR = 14;
  /** Operation constant (for OPCODE_OPR) logical AND */
  public static final byte OPERATOR_AND = 15;
  /** Operation constant (for OPCODE_OPR) logical XOR */
  public static final byte OPERATOR_XOR = 16;
  /** Operation constant (for OPCODE_OPR) logical NOT */
  public static final byte OPERATOR_NOT = 17;
  /** Operation constant (for OPCODE_OPR) SHift Left operation */
  public static final byte OPERATOR_SHL = 18;
  /** Operation constant (for OPCODE_OPR) SHift Right operation */
  public static final byte OPERATOR_SHR = 19;
  /** Operation constant (for OPCODE_OPR) INCrement */
  public static final byte OPERATOR_INC = 20;
  /** Operation constant (for OPCODE_OPR) DECrement */
  public static final byte OPERATOR_DEC = 21;
  /** Operation constant (for OPCODE_OPR) CoPY */
  public static final byte OPERATOR_CPY = 22;
  /** Operation constant (for OPCODE_OPR) illegal */
  public static final byte OPERATOR_ILLEGAL = 23;

  private static final String operators[] =
       {  "_RET", "_NEG", "_ADD", "_SUB", "_MUL", "_DIV",
          "_LOW", "_MOD", "_EQ", "_NE", "_LT", "_GE",
          "_GT", "_LE", "_OR", "_AND", "_XOR", "_NOT",
          "_SHL", "_SHR", "_INC", "_DEC", "_CPY",
          "ILLEGAL_OPR_ARG"
       };

  /** Operation constant for system call (OPCODE_CSP) CHaracter IN */
  public static final short SYS_CHIN = 0;
  /** Operation constant for system call (OPCODE_CSP) CHaracter OUT */
  public static final short SYS_CHOUT = 1;
  /** Operation constant for system call (OPCODE_CSP) NUMber IN */
  public static final short SYS_NUMIN = 2;
  /** Operation constant for system call (OPCODE_CSP) NUMber OUT */
  public static final short SYS_NUMOUT = 3;
  /** Operation constant for system call (OPCODE_CSP) HEXdecimal IN */
  public static final short SYS_HEXIN = 4;
  /** Operation constant for system call (OPCODE_CSP) HEXdecimal OUT */
  public static final short SYS_HEXOUT = 5;
  /** Operation constant for system call (OPCODE_CSP) EXECute */
  public static final short SYS_EXEC = 6;
  /** Operation constant for system call (OPCODE_CSP) FORK process */
  public static final short SYS_FORK = 7;
  /** Operation constant for system call (OPCODE_CSP) STRing OUT */
  public static final short SYS_STROUT = 8;
  /** Operation constant for system call (OPCODE_CSP) SEMaphore CLOSE */
  public static final short SYS_SEM_CLOSE = 9;
  /** Operation constant for system call (OPCODE_CSP) SEMaphore CREATE */
  public static final short SYS_SEM_CREATE = 10;
  /** Operation constant for system call (OPCODE_CSP) SEMaphore OPEN */
  public static final short SYS_SEM_OPEN = 11;
  /** Operation constant for system call (OPCODE_CSP) SEMaphore SIGNAL */
  public static final short SYS_SEM_SIGNAL = 12;
  /** Operation constant for system call (OPCODE_CSP) SEMaphore WAIT */
  public static final short SYS_SEM_WAIT = 13;
  /** Operation constant for system call (OPCODE_CSP) SHaRed memory CLOSE */
  public static final short SYS_SHR_CLOSE = 14;
  /** Operation constant for system call (OPCODE_CSP) SHaRed memory CREATE */
  public static final short SYS_SHR_CREATE = 15;
  /** Operation constant for system call (OPCODE_CSP) SHaRed memory OPEN */
  public static final short SYS_SHR_OPEN = 16;
  /** Operation constant for system call (OPCODE_CSP) SHaRed memory READ */
  public static final short SYS_SHR_READ = 17;
  /** Operation constant for system call (OPCODE_CSP) SHaRed memory WRITE */
  public static final short SYS_SHR_WRITE = 18;
  /** Operation constant for system call (OPCODE_CSP) SHaRed memory SIZE */
  public static final short SYS_SHR_SIZE = 19;
  /** Operation constant for system call (OPCODE_CSP) File ALLOCation */
  public static final short SYS_F_ALLOC = 20;
  /** Operation constant for system call (OPCODE_CSP) File OPEN */
  public static final short SYS_F_OPEN = 21;
  /** Operation constant for system call (OPCODE_CSP) File CREATe */
  public static final short SYS_F_CREAT = 22;
  /** Operation constant for system call (OPCODE_CSP) File CLOSE */
  public static final short SYS_F_CLOSE = 23;
  /** Operation constant for system call (OPCODE_CSP) File End Of File */
  public static final short SYS_F_EOF = 24;
  /** Operation constant for system call (OPCODE_CSP) File DELete */
  public static final short SYS_F_DEL = 25;
  /** Operation constant for system call (OPCODE_CSP) File READ */
  public static final short SYS_F_READ = 26;
  /** Operation constant for system call (OPCODE_CSP) File WRITE */
  public static final short SYS_F_WRITE = 27;
  /** Operation constant for system call (OPCODE_CSP) Illegal */
  public static final short SYS_ILLEGAL = 28;

  private final String systemCalls[] = {
    "CHIN", "CHOUT", "NUMIN", "NUMOUT", "HEXIN", "HEXOUT",
    "EXEC", "FORK",
    "STROUT",
    "SEM_CLOSE", "SEM_CREATE", "SEM_OPEN", "SEM_SIGNAL", "SEM_WAIT",
    "SHR_CLOSE" , "SHR_CREATE", "SHR_OPEN", "SHR_READ" , "SHR_WRITE",
      "SHR_SIZE",
    "F_ALLOC", "F_OPEN", "F_CREAT", "F_CLOSE", "F_EOF", "F_DEL",
      "F_READ", "F_WRITE",
    "ILLEGAL_SYS_ERROR" };

  private int opCode;
  private byte byteParam;
  private short wordParam;

  /**
   * Default constructor creates opCode, byteParam and wordParam with values of -1.
   */
  public Instruction()
  {
    opCode = -1;
    byteParam = -1;
    wordParam = -1;
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
    opCode = newOpCode;
    byteParam = newByteParam;
    wordParam = newWordParam;
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

    for (int index = 0; index < opCodes.length; index++)
    {
      if (opStr.compareTo(opCodes[index]) == 0)
      {
        opCode = index;
        break;
      }
    }

    try
    {
      byteParam = Byte.parseByte(byteStr);
    }
    catch (NumberFormatException nfe)
    {
      throw new java.lang.IllegalArgumentException(
      "Incorrect byte parameter: [" + byteStr + "]");
    }

    try
    {
      if ((opCode != OPCODE_CSP) &&
          (opCode != OPCODE_OPR))
      {
        wordParam = Short.parseShort(wordStr);
      }
      else if (opCode == OPCODE_CSP)
      {
        for (short index = 0; index < systemCalls.length; index++)
        {
          if (wordStr.compareTo(systemCalls[index]) == 0)
          {
            wordParam = index;
            break;
          }
        }
      }
      else if (opCode == OPCODE_OPR)
      {
        for (short index = 0; index < operators.length; index++)
        {
          if (wordStr.compareTo(operators[index]) == 0)
          {
            wordParam = index;
            break;
          }
        }
      }
    }
    catch (NumberFormatException nfe)
    {
      throw new java.lang.IllegalArgumentException(
        "Incorrect word parameter: [" + wordStr + "]");
    }
  }

  /**
   * Returns the opCode stored in the instruction.  Returns an OPCODE_ILLEGAL
   * if the opcode is wrong.
   */
  public int getOpCode()
  {
    if ((opCode >= OPCODE_LIT) && (opCode <= OPCODE_STOX))
      return opCode;
    else
      return OPCODE_ILLEGAL;
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
    //If the opcode is OPR then the wordParam defines an operator (+,-, etc)
    if (getOpCode() == Instruction.OPCODE_OPR)
    {
      if ((wordParam >= OPERATOR_RET) && (wordParam <= OPERATOR_CPY))
        return wordParam;
      else
        return OPERATOR_ILLEGAL;
    }
    //If the opcode is CSP then the wordParam defines a system call (cout, etc)
    else if (getOpCode() == Instruction.OPCODE_CSP)
    {
      if ((wordParam >= SYS_CHIN) && (wordParam <= SYS_F_WRITE))
        return wordParam;
      else
        return SYS_ILLEGAL;
    }
    //If it's any other instruction then just send it back
    return wordParam;
  }

  /**
   * Sets the new word parameter.  There is no error checking performed.
   *
   * @param newWordParamter the new value to set.
   */
  public void setWordParameter(short newWordParameter)
  {
    wordParam = newWordParameter;
  }

  /**
   * Makes a shallow copy of the instruction.
   */
  public Object clone()
  {
    Instruction newInstruction = new Instruction(this.opCode, this.byteParam,
      this.wordParam);
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
      if ((this.opCode == instr.opCode) &&
          (this.byteParam == instr.byteParam) &&
          (this.wordParam == instr.wordParam))
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

    opCodeBytes = convertShortToBytes((short) opCode);
    paramByte = byteParam;
    wordBytes = convertShortToBytes(wordParam);

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
    for(int index = 0; index < 2; index++)
    {
      newBytes[index] = (byte) (existingValue >>> (1 - index) * 8);
    }
    return newBytes;
  }

  /**
   * Returns the string value of the instruction dependant upon what the opCode
   * that is currently stored.
   */
  public String toString()
  {
    if (opCode != OPCODE_ILLEGAL)
    {
      String command = opCodes[opCode];
      if ((opCode != OPCODE_CSP) &&
          (opCode != OPCODE_OPR))
      {
        return (command + " " + byteParam + ", " + wordParam);
      }
      else if (opCode == OPCODE_CSP)
      {
        return (command + " " + byteParam + ", " + systemCalls[wordParam]);
      }
      else if (opCode == OPCODE_OPR)
      {
        return (command + " " + byteParam + ", " + operators[wordParam]);
      }
    }
    return ("ERROR");
  }
}