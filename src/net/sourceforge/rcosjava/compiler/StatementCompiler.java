package net.sourceforge.rcosjava.compiler;

import net.sourceforge.rcosjava.hardware.cpu.*;
import net.sourceforge.rcosjava.compiler.symbol.*;
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
  private VariableCompiler variableCompiler = new VariableCompiler();
  private SymbolTable table;

  public StatementCompiler()
  {
    table = SymbolTable.getInstance();
  }

  /**
   * If statement
   */
  public void inAIfStatement(AIfStatement node)
  {
    System.out.println("If stmt: " + node.getCompoundStatement());

    ARelConditionalExpression expr = (ARelConditionalExpression)
        node.getConditionalExpression();

    PValue expression1 = expr.getLeft();
    PValue expression2 = expr.getRight();

    System.out.println("LH: " + expr.getLeft());
    System.out.println("LH: " + expression1.getClass().toString());
    System.out.println("RH: " + expr.getRight());
    System.out.println("RH: " + expression2.getClass().toString());

    // With the left hand load the variable or literal
    handleIdentifierLoad(expression1);

    // With the right hand load the variable or literal
    handleIdentifierLoad(expression2);
  }

  public void outAIfStatement(AIfStatement node) {

//    ArrayList tmpInstr = (ArrayList) previousInstruction.get(instructionIndex-1);
    Compiler.addInstruction(new Instruction(OpCode.JUMP_ON_CONDITION.getValue(),
      (byte) 0, (short) Compiler.getLevel()));
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

  public void inAGtRelop(AGtRelop node)
  {
    System.out.println("GT Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.GREATER_THAN.getValue()));
    System.out.println("Begin call");
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
    doLiteralLoading(stringValue);

    writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
      (byte) 0, SystemCall.STRING_OUT.getValue()));

    System.out.println("End BasePosition: " + Compiler.getLevel());
  }

  /**
   * Simple assignment statements
   * e.g. i = 0;
   */
  public void inAModifyExpressionBasicStatement(
    AModifyExpressionBasicStatement node)
  {
    try
    {
      //Need to find the way of getting the =, lt and rh sides.
      ADirectModifyExpression expr = (ADirectModifyExpression)
        node.getModifyExpression();

      String varName = expr.getVarname().toString().trim();
      String varValue = expr.getRhs().toString().trim();
      Variable newVar = table.getVariable(varName, Compiler.getLevel());
      short varPos = newVar.getOffset();
      System.out.println("In a modify statement");
      System.out.println("Varname: " + varName + " at: " +
        varPos);
      doLiteralLoading(varValue);

      // Store variable at the variables location
      writePCode(new Instruction(OpCode.STORE.getValue(), (byte) 0,
        (short) varPos));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public void writePCode(Instruction newInstruction)
  {
    Compiler.incInstructionIndex();
    Compiler.addInstruction(newInstruction);
  }

  private void handleIdentifierLoad(PValue expression)
  {
    if (expression instanceof AIdentifierValue)
    {
      handleIdentifierLoad((AIdentifierValue) expression);
    }
    else if (expression instanceof AConstantValue)
    {
      handleIdentifierLoad((AConstantValue) expression);
    }
  }

  private void handleIdentifierLoad(AIdentifierValue identifier)
  {
    try
    {
      String identifierName = identifier.toString().trim();
      System.out.println("Level: " + Compiler.getLevel());
      System.out.println("Name: [" + identifierName + "]");
      Variable newVar = table.getVariable(identifierName, Compiler.getLevel());
      short location = newVar.getOffset();
      writePCode(new Instruction(OpCode.LOAD.getValue(), (byte) 0, location));
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void handleIdentifierLoad(AConstantValue constant)
  {
    doLiteralLoading(constant.toString());
  }

  private void doLiteralLoading(String varValue)
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
      //emit store a required pos
      writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
        length));
    }
    // Do int storage
    else
    {
      System.out.println("[" + varValue + "]");
      short varIntValue = Short.parseShort(varValue.trim());
      writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
        varIntValue));
    }
  }
}