package net.sourceforge.rcosjava.hardware.cpu;

import java.io.Serializable;
import java.util.*;

/**
 * Operators are used in arithmetic or logical opcodes.
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
public class Operator extends WordParameter
{
  private static Hashtable allOperatorsByName = new Hashtable();
  private static Hashtable allOperatorsByValue = new Hashtable();

  /** Operation constant (for OPCODE_OPR) RETurn */
  public static final Operator RETURN = new Operator((short) 0,"_RET");
  /** Operation constant (for OPCODE_OPR) NEGative */
  public static final Operator NEGATIVE = new Operator((short) 1, "_NEG");
  /** Operation constant (for OPCODE_OPR) ADDition */
  public static final Operator ADD = new Operator((short) 2, "_ADD");
  /** Operation constant (for OPCODE_OPR) SUBtraction */
  public static final Operator SUBTRACT = new Operator((short) 3, "_SUB");
  /** Operation constant (for OPCODE_OPR) MULtiply */
  public static final Operator MULTIPLY = new Operator((short) 4, "_MUL");
  /** Operation constant (for OPCODE_OPR) DIVision */
  public static final Operator DIVIDE = new Operator((short) 5, "_DIV");
  /** Operation constant (for OPCODE_OPR) LOW bit*/
  public static final Operator LOW_BIT = new Operator((short) 6, "_LOW");
  /** Operation constant (for OPCODE_OPR) MODulus */
  public static final Operator MODULUS = new Operator((short) 7, "_MOD");
  /** Operation constant (for OPCODE_OPR) EQual test */
  public static final Operator EQUAL = new Operator((short) 8, "_EQ");
  /** Operation constant (for OPCODE_OPR) Not Equal test */
  public static final Operator NOT_EQUAL = new Operator((short) 9, "_NE");
  /** Operation constant (for OPCODE_OPR) Less Than test */
  public static final Operator LESS_THAN =  new Operator((short) 10, "_LT");
  /** Operation constant (for OPCODE_OPR) Greater than or Equal test */
  public static final Operator GREATER_THAN_OR_EQUAL = new Operator((short) 11, "_GE");
  /** Operation constant (for OPCODE_OPR) Greater Than test */
  public static final Operator GREATER_THAN = new Operator((short) 12, "_GT");
  /** Operation constant (for OPCODE_OPR) Less than or Equal test */
  public static final Operator LESS_THAN_OR_EQUAL = new Operator((short) 13, "_LE");
  /** Operation constant (for OPCODE_OPR) logical OR */
  public static final Operator OR = new Operator((short) 14, "_OR");
  /** Operation constant (for OPCODE_OPR) logical AND */
  public static final Operator AND = new Operator((short) 15, "_AND");
  /** Operation constant (for OPCODE_OPR) logical XOR */
  public static final Operator XOR = new Operator((short) 16, "_XOR");
  /** Operation constant (for OPCODE_OPR) logical NOT */
  public static final Operator NOT = new Operator((short) 17, "_NOT");
  /** Operation constant (for OPCODE_OPR) SHift Left operation */
  public static final Operator SHIFT_LEFT = new Operator((short) 18, "_SHL");
  /** Operation constant (for OPCODE_OPR) SHift Right operation */
  public static final Operator SHIFT_RIGHT = new Operator((short) 19, "_SHR");
  /** Operation constant (for OPCODE_OPR) INCrement */
  public static final Operator INCREMENT = new Operator((short) 20, "_INC");
  /** Operation constant (for OPCODE_OPR) DECrement */
  public static final Operator DECREMENT = new Operator((short) 21, "_DEC");
  /** Operation constant (for OPCODE_OPR) CoPY */
  public static final Operator COPY = new Operator((short) 22, "_CPY");

  /**
   * The internal value of the operator.  Current valid between 0 and 22.
   */
  private short operatorValue;

  /**
   * The internal string represenation of the operator.
   */
  private String operatorName;

  /**
   * Default constructor creates operator
   */
  protected Operator(short newOperatorValue, String newOperatorName)
  {
    operatorValue = newOperatorValue;
    operatorName = newOperatorName;
    allOperatorsByName.put(operatorName, this);
    allOperatorsByValue.put(new Short(operatorValue), this);
  }

  /**
   * @return ordinal value of opcode
   */
  public short getValue()
  {
    return operatorValue;
  }

  /**
   * @return opcode string value
   */
  public String getName()
  {
    return operatorName;
  }

  /**
   * @return the constant collection of operators stored by value
   */
  public static Operator getOperatorsByValue(short operatorValue)
  {
    Operator tmpOperator = (Operator)
      allOperatorsByValue.get(new Short(operatorValue));
    return tmpOperator;
  }

  /**
   * @return the constant collection of operators stored by name
   */
  public static Operator getOperatorsByName(String operatorString)
  {
    Operator tmpOperator = (Operator) allOperatorsByName.get(operatorString);
    return tmpOperator;
  }

  /**
   * @return the string value of the operator.
   */
  public String toString()
  {
    return operatorName;
  }
}