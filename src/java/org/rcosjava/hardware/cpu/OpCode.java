package org.rcosjava.hardware.cpu;

import java.io.Serializable;
import java.util.*;

/**
 * OpCodes are used in instructions to determine the type of instruction to do.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 09/07/2001 Created </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 9th of July 2001
 * @see org.rcosjava.hardware.cpu.Instruction
 * @see org.rcosjava.hardware.cpu.CPU
 * @version 1.00 $Date$
 */
public class OpCode implements Serializable
{
  /**
   * The name of opcodes. The location in the list reflects its value.
   */
  private static HashMap allOpCodesByName = new HashMap();

  /**
   * The value of opcodes. The location in the list reflects its name.
   */
  private static HashMap allOpCodesByValue = new HashMap();

  /**
   * Constant for LITeral call
   */
  public final static OpCode LITERAL = new OpCode(0x0, "LIT");

  /**
   * Constant for OPeRation (arithmetic or logical) call
   */
  public final static OpCode OPERATION = new OpCode(0x1, "OPR");

  /**
   * Constant for LOaD call
   */
  public final static OpCode LOAD = new OpCode(0x2, "LOD");

  /**
   * Constant for STOre call
   */
  public final static OpCode STORE = new OpCode(0x3, "STO");

  /**
   * Constant for CALL (a function) call
   */
  public final static OpCode CALL = new OpCode(0x4, "CAL");

  /**
   * Constant for INcrement T-Register (Stack Pointer) call
   */
  public final static OpCode INCREMENT_T_REGISTER = new OpCode(0x5, "INT");

  /**
   * Constant for JuMP call
   */
  public final static OpCode JUMP = new OpCode(0x6, "JMP");

  /**
   * Constant for JuMP on Condition
   */
  public final static OpCode JUMP_ON_CONDITION = new OpCode(0x7, "JPC");

  /**
   * Constant for Call Standard System Procedure call
   */
  public final static OpCode CALL_SYSTEM_PROCEDURE = new OpCode(0x8, "CSP");

  /**
   * Constant for LOaD indeXed variable (array) call
   */
  public final static OpCode LOAD_INDEXED = new OpCode(0x12, "LODX");

  /**
   * Constant for STOre indeXed variable (array) call
   */
  public final static OpCode STORE_INDEXED = new OpCode(0x13, "STOX");

  /**
   * Constant for illegal call
   */
  public final static OpCode ILLEGAL = new OpCode(0x14, "ILLEGAL_OPCODE_ERROR");

  /**
   * Constant for maximum number of op codes.
   */
  public final static int MAX_OP_CODES = 20;

  /**
   * Internal integer value of the op-code (currently valid from 0-8 and 18, 19
   * and 20).
   */
  private int opCodeValue;

  /**
   * The internal string value of the op-code.
   */
  private String opCodeName;

  /**
   * Default constructor creates new opcode.
   *
   * @param newOpCodeValue simple integer representation of opcode.
   * @param newOpCodeName simple string represetation of opcode.
   */
  protected OpCode(int newOpCodeValue, String newOpCodeName)
  {
    opCodeValue = newOpCodeValue;
    opCodeName = newOpCodeName;

    allOpCodesByName.put(opCodeName, this);
    allOpCodesByValue.put(new Integer(opCodeValue), this);
  }

  /**
   * Returns the opcode from the given integer value.  Null if not found.
   *
   * @param opCodeValue the integer value to get the opcode.
   * @return the opcode from the given integer value.
   */
  public static OpCode getOpCodesByValue(int opCodeValue)
  {
    if (allOpCodesByValue.containsKey(new Integer(opCodeValue)))
    {
      return (OpCode) allOpCodesByValue.get(new Integer(opCodeValue));
    }
    return null;
  }

  /**
   * Returns the opcode based on the given name.  Null if not found.
   *
   * @param opcodeString the string of the opcode.
   * @return the opcode based on the given name.
   */
  public static OpCode getOpCodesByName(String opCodeString)
  {
    if (allOpCodesByName.containsKey(opCodeString))
    {
      return (OpCode) allOpCodesByName.get(opCodeString);
    }
    return null;
  }

  /**
   * Returns the ordinal value of the opcode.
   *
   * @return the ordinal value of the opcode.
   */
  public int getValue()
  {
    return opCodeValue;
  }

  /**
   * Returns the string value of the opcode.
   *
   * @return the string value of the opcode.
   */
  public String getName()
  {
    return opCodeName;
  }

  /**
   * Returns the string value of the opcode.
   *
   * @return the string value of the opcode.
   */
  public String toString()
  {
    return opCodeName;
  }
}
