package net.sourceforge.rcosjava.compiler;

import net.sourceforge.rcosjava.hardware.cpu.*;
import java.io.*;
import java.net.*;
import java.util.*;

import org.sablecc.simplec.analysis.*;
import org.sablecc.simplec.node.*;
import org.sablecc.simplec.lexer.*;
import org.sablecc.simplec.parser.*;
import org.sablecc.simplec.tool.Version;

/**
 * Provides a compiler of a simple C like grammar with certain extensions..
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public class StatementCompiler extends DepthFirstAdapter
{
// This will eventually be split into two.
  private int basePosition = 0;
  private int functionPosition = 0;
  private boolean isInFunction = false;
  private ArrayList instructions = new ArrayList();
  private VariableCompiler variableCompiler = new VariableCompiler(false);

  public StatementCompiler(int newBasePosition)
  {
    basePosition = newBasePosition;
  }

  /**
   * If statement
   */
  public void inAIfStatement(AIfStatement node)
  {
    System.out.println("If stmt: " + node.getCompoundStatement());

    ARelConditionalExpression expr = (ARelConditionalExpression)
        node.getConditionalExpression();
    System.out.println("LH: " + expr.getLeft());
    System.out.println("RH: " + expr.getRight());
  }

  public void inAValueConditionalExpression(AValueConditionalExpression node)
  {
    System.out.println("Identifier: " + node);
  }

  public void caseAGteqRelop(AGteqRelop node)
  {
    System.out.println("GTE Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.GREATER_THAN_OR_EQUAL.getValue()));
  }

  public void caseAGtRelop(AGtRelop node)
  {
    System.out.println("GT Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.GREATER_THAN.getValue()));
  }

  public void caseALteqRelop(ALteqRelop node)
  {
    System.out.println("LTE Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.LESS_THAN_OR_EQUAL.getValue()));
  }

  public void caseALtRelop(ALtRelop node)
  {
    System.out.println("LT Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.LESS_THAN.getValue()));
  }

  public void caseAEqRelop(AEqRelop node)
  {
    System.out.println("EQ Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.EQUAL.getValue()));
  }

  public void inAPrintf1RcosStatement(APrintf1RcosStatement node)
  {
    System.out.println("Str contstant: " + node);
    System.out.println("Literal: " + node.getStringLitteral());

    String stringValue = node.getStringLitteral().getText();
    //String name = variableCompiler.allocateVariable(1, stringValue.length());
    doVariableLoading(stringValue);

    writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
      (byte) 0, SystemCall.STRING_OUT.getValue()));
  }

  /**
   * Simple assignment statements
   */
  public void inAModifyExpressionBasicStatement(
    AModifyExpressionBasicStatement node)
  {
    //Need to find the way of getting the =, lt and rh sides.
    ADirectModifyExpression expr = (ADirectModifyExpression)
      node.getModifyExpression();

    String varName = expr.getVarname().toString().trim();
    String varValue = expr.getRhs().toString().trim();
    System.out.println("In a modify statement");
    System.out.println("Varname: " + varName + " at: " +
      variableCompiler.getVariableLocation(varName));

    //doVariableLoading(varName, varValue);
  }

  public void writePCode(Instruction newInstruction)
  {
    basePosition++;
    instructions.add(newInstruction);
  }

  public void emitInstructions()
  {
    Iterator tmpIter = instructions.iterator();
    while (tmpIter.hasNext())
    {
      System.out.println((Instruction) tmpIter.next());
    }
  }

  public short getBasePosition()
  {
    return (short) basePosition;
  }

  private void doVariableLoading(String varValue)
  {
    short length = 0;

    // Do string storage
    if ((varValue.indexOf("'") >= 0) || (varValue.indexOf("\"") >= 0))
    {
      int varStrLength = varValue.length()-1;
      //emit each element in the string
      int count = 0;
      while(count < varStrLength)
      {
        // Check if it's a \n otherwise ignore
        if ((varValue.charAt(count) == '\\') &&
            (varValue.charAt(count+1) == 'n'))
        {
          writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
            (short) 13));
          writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
            (short) 10));
          count = count + 2;
        }
        else
        {
          writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
            (short) varValue.charAt(count)));
          count++;
        }
        length++;
      }
    }
    // Do int storage
    else
    {
      System.out.println("[" + varValue + "]");
      short varIntValue = Short.parseShort(varValue);
      writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
        varIntValue));
    }

    //emit store a required pos
    writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
      length));
  }
}