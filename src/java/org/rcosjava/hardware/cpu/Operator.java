package org.rcosjava.hardware.cpu;

import java.io.Serializable;
import java.util.*;

/**
 * Operators are used in arithmetic or logical opcodes.
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
public class Operator extends WordParameter
{
  /**
   * The name of the operator. The location in the list reflects its value.
   */
  private static HashMap allOperatorsByName = new HashMap();

  /**
   * The value of the operator. The location in the list reflects its name.
   */
  private static HashMap allOperatorsByValue = new HashMap();

  /**
   * Operation constant (for OPCODE_OPR) RETurn
   */
  public final static Operator RETURN = new Operator((short) 0, "_RET")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().setStackPointer((short) (cpu.getContext().getBasePointer() - 1));
      cpu.getContext().setBasePointer((short)
          (cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 2)));
      cpu.getContext().setProgramCounter((short)
          (cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 3)));
      //codeToExecute = !(cpu.getContext().getBasePointer() <= 0);

      cpu.setProcessFinished(cpu.getContext().getBasePointer() <= 0);
      cpu.setCodeToExecute(!cpu.processFinished());
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) NEGative
   */
  public final static Operator NEGATIVE = new Operator((short) 1, "_NEG")
  {
    void execute(CPU cpu)
    {
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) (0 - cpu.getProcessStack().read(cpu.getContext().getStackPointer())));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) ADDition
   */
  public final static Operator ADD = new Operator((short) 2, "_ADD")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) +
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };


  /**
   * Operation constant (for OPCODE_OPR) SUBtraction
   */
  public final static Operator SUBTRACT = new Operator((short) 3, "_SUB")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) -
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) MULtiply
   */
  public final static Operator MULTIPLY = new Operator((short) 4, "_MUL")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) *
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) DIVision
   */
  public final static Operator DIVIDE = new Operator((short) 5, "_DIV")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) /
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) LOW bit
   */
  public final static Operator LOW_BIT = new Operator((short) 6, "_LOW")
  {
    void execute(CPU cpu)
    {
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) & 1));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) MODulus
   */
  public final static Operator MODULUS = new Operator((short) 7, "_MOD")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) %
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) EQual test
   */
  public final static Operator EQUAL = new Operator((short) 8, "_EQ")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      if (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) ==
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1))
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 1);
      }
      else
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 0);
      }
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) Not Equal test
   */
  public final static Operator NOT_EQUAL = new Operator((short) 9, "_NE")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      if (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) !=
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1))
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 1);
      }
      else
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 0);
      }
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) Less Than test
   */
  public final static Operator LESS_THAN = new Operator((short) 10, "_LT")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();

      if (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) <
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1))
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 1);
      }
      else
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 0);
      }
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) Greater than or Equal test
   */
  public final static Operator GREATER_THAN_OR_EQUAL = new Operator((short) 11, "_GE")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      if (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) >=
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1))
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 1);
      }
      else
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 0);
      }
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) Greater Than test
   */
  public final static Operator GREATER_THAN = new Operator((short) 12, "_GT")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      if (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) >
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1))
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 1);
      }
      else
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 0);
      }
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) Less than or Equal test
   */
  public final static Operator LESS_THAN_OR_EQUAL = new Operator((short) 13, "_LE")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      if (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) <=
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1))
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 1);
      }
      else
      {
        cpu.getProcessStack().write(cpu.getContext().getStackPointer(), 0);
      }
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) logical OR
   */
  public final static Operator OR = new Operator((short) 14, "_OR")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) |
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) logical AND
   */
  public final static Operator AND = new Operator((short) 15, "_AND")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) &
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) logical XOR
   */
  public final static Operator XOR = new Operator((short) 16, "_XOR")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) ^
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) logical NOT
   */
  public final static Operator NOT = new Operator((short) 17, "_NOT")
  {
    void execute(CPU cpu)
    {
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) ~cpu.getProcessStack().read(cpu.getContext().getStackPointer()));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) SHift Left operation
   */
  public final static Operator SHIFT_LEFT = new Operator((short) 18, "_SHL")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) <<
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) SHift Right operation
   */
  public final static Operator SHIFT_RIGHT = new Operator((short) 19, "_SHR")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().decStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (cpu.getProcessStack().read(cpu.getContext().getStackPointer()) >>
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() + 1)));
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) INCrement
   */
  public final static Operator INCREMENT = new Operator((short) 20, "_INC")
  {
    void execute(CPU cpu)
    {
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) cpu.getProcessStack().read(cpu.getContext().getStackPointer()) + 1);
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) DECrement
   */
  public final static Operator DECREMENT = new Operator((short) 21, "_DEC")
  {
    void execute(CPU cpu)
    {
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          (short) cpu.getProcessStack().read(cpu.getContext().getStackPointer()) - 1);
    }
  };

  /**
   * Operation constant (for OPCODE_OPR) CoPY
   */
  public final static Operator COPY = new Operator((short) 22, "_CPY")
  {
    void execute(CPU cpu)
    {
      cpu.getContext().incStackPointer();
      cpu.getProcessStack().write(cpu.getContext().getStackPointer(),
          cpu.getProcessStack().read(cpu.getContext().getStackPointer() - 1));
    }
  };

  public static final Operator OPERATIONS[] =
  {
    Operator.RETURN, Operator.NEGATIVE, Operator.ADD, Operator.SUBTRACT,
    Operator.MULTIPLY, Operator.DIVIDE, Operator.LOW_BIT, Operator.MODULUS,
    Operator.EQUAL, Operator.NOT_EQUAL, Operator.LESS_THAN,
    Operator.GREATER_THAN_OR_EQUAL, Operator.GREATER_THAN,
    Operator.LESS_THAN_OR_EQUAL, Operator.OR, Operator.AND, Operator.XOR,
    Operator.NOT, Operator.SHIFT_LEFT, Operator.SHIFT_RIGHT,
    Operator.INCREMENT, Operator.DECREMENT, Operator.COPY
  };

  /**
   * The internal value of the operator. Current valid between 0 and 22.
   */
  private short operatorValue;

  /**
   * The internal string represenation of the operator.
   */
  private String operatorName;

  /**
   * Default constructor creates operator
   *
   * @param newOperatorValue Description of Parameter
   * @param newOperatorName Description of Parameter
   */
  protected Operator(short newOperatorValue, String newOperatorName)
  {
    operatorValue = newOperatorValue;
    operatorName = newOperatorName;
    allOperatorsByName.put(operatorName, this);
    allOperatorsByValue.put(new Short(operatorValue), this);
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
   * @param operatorValue Description of Parameter
   * @return the constant collection of operators stored by value
   */
  public static Operator getOperatorsByValue(short operatorValue)
  {
    Operator tmpOperator = (Operator)
        allOperatorsByValue.get(new Short(operatorValue));

    return tmpOperator;
  }

  /**
   * @param operatorString Description of Parameter
   * @return the constant collection of operators stored by name
   */
  public static Operator getOperatorsByName(String operatorString)
  {
    Operator tmpOperator = (Operator) allOperatorsByName.get(operatorString);

    return tmpOperator;
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
   * @return the string value of the operator.
   */
  public String toString()
  {
    return operatorName;
  }
}
