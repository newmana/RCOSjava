/**
 * The P-Code Instruction class.  Contains data for a single P-Code instruction.
 * Each value is stored as an integer but a string representation can be
 * returned.  This is mainly for the user to understand.
 *
 * Usage example:
 * <CODE>
 *      Instruction newInstruction = new Instruction(0, 0, 0);
 * </CODE>
 *
 * @see Hardware.CPU
 * @version 1.00 $Date$
 * @author Andrew Newman.
 * @author David Jones.
 **/

package Hardware.CPU;

public class Instruction implements Cloneable
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
  private byte bParam;
  private short wParam;

  /**
   * Default constructor creates opCode, bParam and wParam with values of -1.
   */
  public Instruction()
  {
    opCode = -1;
    bParam = -1;
    wParam = -1;
  }

  /**
   * Construct an
   */
  public Instruction(int myOpCode, byte myBParam, short myWParam)
  {
    opCode = myOpCode;
    bParam = myBParam;
    wParam = myWParam;
  }

  //What opcode is it?
  public int getOpCode()
  {
    if ((opCode >= OPCODE_LIT) && (opCode <= OPCODE_STOX))
      return opCode;
    else
      return OPCODE_ILLEGAL;
  }

  //No error checking on Byte Parameter
  public byte getBParameter()
  {
    return bParam;
  }

  //Return a valid Word Parameter based on current opcode.
  public short getWParameter()
  {
    //If the opcode is OPR then the wParam defines an operator (+,-, etc)
    if (getOpCode() == Instruction.OPCODE_OPR)
    {
      if ((wParam >= OPERATOR_RET) && (wParam <= OPERATOR_CPY))
        return wParam;
      else
        return OPERATOR_ILLEGAL;
    }
    //If the opcode is CSP then the wParam defines a system call (cout, etc)
    else if (getOpCode() == Instruction.OPCODE_CSP)
    {
      if ((wParam >= SYS_CHIN) && (wParam <= SYS_F_WRITE))
        return wParam;
      else
        return SYS_ILLEGAL;
    }
    //If it's any other instruction then just send it back
    return wParam;
  }

  public void setWParameter(short sNewParameter)
  {
    wParam = sNewParameter;
  }

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (java.lang.CloneNotSupportedException e)
    {
    }
    return null;
  }

  public boolean equals(Object obj)
  {
    if (obj != null && (obj.getClass().equals(this.getClass())))
    {
     Instruction instr = (Instruction) obj;
      if ((this.opCode == instr.opCode) &&
          (this.bParam == instr.bParam) &&
          (this.wParam == instr.wParam))
      {
        return true;
      }
    }
    return false;
  }

  //Return String version of Current Instruction.
  public String toString()
  {
    if (opCode != OPCODE_ILLEGAL)
    {
      String command = opCodes[opCode];
      if ((opCode != OPCODE_CSP) &&
          (opCode != OPCODE_OPR))
      {
        return (command + " " + bParam + ", " + wParam);
      }
      else if (opCode == OPCODE_CSP)
      {
        return (command + " " + bParam + ", " + systemCalls[wParam]);
      }
      else if (opCode == OPCODE_OPR)
      {
        return (command + " " + bParam + ", " + operators[wParam]);
      }
    }
    return ("ERROR");
  }
}