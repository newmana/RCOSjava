//*******************************************************************/
// FILE     : Instruction.java
// PURPOSE  : P-Code Instruction class
//            Class Instruction contains data for a single P-Code
//            instruction
//
// AUTHOR   : David Jones (d.jones@cqu.edu.au)
// MODIFIED : Andrew Newman
// VERSION  : 1.00
// HISTORY  : 28/01/96  Created by removing class from CPU.java
//                      reorganised classes and methods mainly to
//                      remove large unnecessary switch statements.
//            23/03/96  Moved into CPU Package. AN
//            11/08/98  Reduce the use of String constants and used int
//                      values instead.  AN
//
//*******************************************************************/

package Hardware.CPU;

public class Instruction implements Cloneable
{
  // constants for opCodes
  public static final int OPCODE_LIT = 0x0;
  public static final int OPCODE_OPR = 0x1;
  public static final int OPCODE_LOD = 0x2;
  public static final int OPCODE_STO = 0x3;
  public static final int OPCODE_CAL = 0x4;
  public static final int OPCODE_INT = 0x5;
  public static final int OPCODE_JMP = 0x6;
  public static final int OPCODE_JPC = 0x7;
  public static final int OPCODE_CSP = 0x8;
  public static final int OPCODE_LODX = 0x12;
  public static final int OPCODE_STOX = 0x13;
  public static final int OPCODE_ILLEGAL = 0x14;

  private static final String opCodes[] = { "LIT", "OPR", "LOD", "STO",
    "CAL", "INT", "JMP", "JPC", "CSP", "", "", "", "", "", "", "", "", "",
    "LODX", "STOX", "ILLEGAL_OP_ERROR" };

  // constants for operators
  public static final int OPERATOR_RET = 0;
  public static final int OPERATOR_NEG = 1;
  public static final int OPERATOR_ADD = 2;
  public static final int OPERATOR_SUB = 3;
  public static final int OPERATOR_MUL = 4;
  public static final int OPERATOR_DIV = 5;
  public static final int OPERATOR_LOW = 6;
  public static final int OPERATOR_MOD = 7;
  public static final int OPERATOR_EQ = 8;
  public static final int OPERATOR_NE = 9;
  public static final int OPERATOR_LT = 10;
  public static final int OPERATOR_GE = 11;
  public static final int OPERATOR_GT = 12;
  public static final int OPERATOR_LE = 13;
  public static final int OPERATOR_OR = 14;
  public static final int OPERATOR_AND = 15;
  public static final int OPERATOR_XOR = 16;
  public static final int OPERATOR_NOT = 17;
  public static final int OPERATOR_SHL = 18;
  public static final int OPERATOR_SHR = 19;
  public static final int OPERATOR_INC = 20;
  public static final int OPERATOR_DEC = 21;
  public static final int OPERATOR_CPY = 22;
  public static final int OPERATOR_ILLEGAL = 23;

  private static final String operators[] =
       {  "_RET", "_NEG", "_ADD", "_SUB", "_MUL", "_DIV",
          "_LOW", "_MOD", "_EQ", "_NE", "_LT", "_GE",
          "_GT", "_LE", "_OR", "_AND", "_XOR", "_NOT",
          "_SHL", "_SHR", "_INC", "_DEC", "_CPY",
          "ILLEGAL_OPR_ARG"
       };

  // constants for System Calls
  public static final int SYS_CHIN = 0;
  public static final int SYS_CHOUT = 1;
  public static final int SYS_NUMIN = 2;
  public static final int SYS_NUMOUT = 3;
  public static final int SYS_HEXIN = 4;
  public static final int SYS_HEXOUT = 5;
  public static final int SYS_EXEC = 6;
  public static final int SYS_FORK = 7;
  public static final int SYS_STROUT = 8;
  public static final int SYS_SEM_CLOSE = 9;
  public static final int SYS_SEM_CREATE = 10;
  public static final int SYS_SEM_OPEN = 11;
  public static final int SYS_SEM_SIGNAL = 12;
  public static final int SYS_SEM_WAIT = 13;
  public static final int SYS_SHR_CLOSE = 14;
  public static final int SYS_SHR_CREATE = 15;
  public static final int SYS_SHR_OPEN = 16;
  public static final int SYS_SHR_READ = 17;
  public static final int SYS_SHR_WRITE = 18;
  public static final int SYS_SHR_SIZE = 19;
  public static final int SYS_F_ALLOC = 20;
  public static final int SYS_F_OPEN = 21;
  public static final int SYS_F_CREAT = 22;
  public static final int SYS_F_CLOSE = 23;
  public static final int SYS_F_EOF = 24;
  public static final int SYS_F_DEL = 25;
  public static final int SYS_F_READ = 26;
  public static final int SYS_F_WRITE = 27;
  public static final int SYS_ILLEGAL = 28;

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

  public Instruction()
  {
    opCode = -1;
    bParam = -1;
    wParam = -1;
  }

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