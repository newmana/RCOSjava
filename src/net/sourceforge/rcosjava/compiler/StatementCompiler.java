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
  private Stack statementPosition;

  public StatementCompiler()
  {
    table = SymbolTable.getInstance();
    statementPosition = new Stack();
  }

  /**
   * If statement
   */
  public void inAIfStatement(AIfStatement node)
  {
    ARelConditionalExpression expr = (ARelConditionalExpression)
        node.getConditionalExpression();

    PValue expression1 = expr.getLeft();
    PValue expression2 = expr.getRight();

    // With the left hand load the variable or literal
    handleIdentifierLoad(expression1);

    // With the right hand load the variable or literal
    handleIdentifierLoad(expression2);

    // Record where the if statement finished so we can put the jump in if it
    // fails.
    statementPosition.push(new Integer(Compiler.getInstructionIndex()));
  }

  public void outAIfStatement(AIfStatement node) {

//    ArrayList tmpInstr = (ArrayList) previousInstruction.get(instructionIndex-1);
    int position = ((Integer) statementPosition.pop()).intValue();
    Compiler.incInstructionIndex();
    Compiler.addInstruction(position+1,
      new Instruction(OpCode.JUMP_ON_CONDITION.getValue(),
      (byte) 0, (short) (Compiler.getInstructionIndex()+2)));
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
    String stringValue = node.getStringLitteral().getText();
    //String name = variableCompiler.allocateVariable(1, stringValue.length());
    doLiteralLoading(stringValue);

    writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
      (byte) 0, SystemCall.STRING_OUT.getValue()));
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
      int count = 1;
      while(count < varStrLength)
      {
        // Check if it's a \n otherwise ignore
        if ((varValue.charAt(count) == '\\') &&
            (varValue.charAt(count + 1) == 'n'))
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
        (short) (length+1)));
    }
    // Do int storage
    else
    {
      short varIntValue = Short.parseShort(varValue.trim());
      writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
        varIntValue));
    }
  }
}