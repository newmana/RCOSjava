package net.sourceforge.rcosjava.hardware.cpu;

import java.io.Serializable;
import java.util.*;

/**
 * OpCodes are used in instructions to determine the type of instruction
 * to do.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 09/07/2001 Created
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.hardware.cpu.Instruction
 * @see net.sourceforge.rcosjava.hardware.cpu.CPU
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 9th of July 2001
 */
public class OpCode implements Serializable
{
  private static Hashtable allOpCodesByName = new Hashtable();
  private static Hashtable allOpCodesByValue = new Hashtable();

  /** Constant for LITeral call */
  public static final OpCode LITERAL = new OpCode(0x0, "LIT");
  /** Constant for OPeRation (arithmetic or logical) call */
  public static final OpCode OPERATION = new OpCode(0x1, "OPR");
  /** Constant for LOaD call */
  public static final OpCode LOAD = new OpCode(0x2, "LOD");
  /** Constant for STOre call */
  public static final OpCode STORE = new OpCode(0x3, "STO");
  /** Constant for CALL (a function) call */
  public static final OpCode CALL = new OpCode(0x4, "CAL");
  /** Constant for INTerval (?) call */
  public static final OpCode INTERVAL = new OpCode(0x5, "INT");
  /** Constant for JuMP call */
  public static final OpCode JUMP = new OpCode(0x6, "JMP");
  /** Constant for JuMP on Condition */
  public static final OpCode JUMP_ON_CONDITION = new OpCode(0x7, "JPC");
  /** Constant for Call Standard System Procedure call */
  public static final OpCode CALL_SYSTEM_PROCEDURE = new OpCode(0x8, "CSP");
  /** Constant for LOaD indeXed variable (array) call */
  public static final OpCode LOAD_INDEXED = new OpCode(0x12, "LODX");
  /** Constant for STOre indeXed variable (array) call */
  public static final OpCode STORE_INDEXED = new OpCode(0x13, "STOX");
  /** Constant for illegal call */
  public static final OpCode ILLEGAL = new OpCode(0x14, "ILLEGAL_OPCODE_ERROR");

  private int opCodeValue;
  private String opCodeName;

  /**
   * Default constructor creates opCode
   */
  protected OpCode(int newOpCodeValue, String newOpCodeName)
  {
    opCodeValue = newOpCodeValue;
    opCodeName = newOpCodeName;

    allOpCodesByName.put(opCodeName, this);
    allOpCodesByValue.put(new Integer(opCodeValue), this);
  }

  /**
   * @return ordinal value of opcode
   */
  public int getValue()
  {
    return opCodeValue;
  }

  /**
   * @return opcode string value
   */
  public String getName()
  {
    return opCodeName;
  }

  /**
   * @return the constant collection of opcodes stored by value
   */
  public static OpCode getOpCodesByValue(int opCodeValue)
  {
    OpCode tmpOpCode = (OpCode) allOpCodesByValue.get(new Integer(opCodeValue));
    return tmpOpCode;
  }

  /**
   * @return the constant collection of opcodes stored by name
   */
  public static OpCode getOpCodesByName(String opcodeString)
  {
    OpCode tmpOpCode = (OpCode) allOpCodesByName.get(opcodeString);
    return tmpOpCode;
  }

  public String toString()
  {
    return opCodeName;
  }
}