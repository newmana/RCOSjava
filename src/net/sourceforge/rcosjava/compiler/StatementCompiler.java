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
  private SymbolTable table;
  private Stack statementPosition;
  private boolean inAFunction = false;
  private short numberOfVariables = 3;

  public StatementCompiler()
  {
    table = SymbolTable.getInstance();
    statementPosition = new Stack();
  }

  public void inAFunctionBody(AFunctionBody node)
  {
    inAFunction = true;
    super.inAFunctionBody(node);
  }

  public void outAFunctionBody(AFunctionBody node)
  {
    inAFunction = false;
    super.outAFunctionBody(node);
  }

  public void caseAVariableDeclaration(AVariableDeclaration node)
  {
    if (!inAFunction)
    {
      numberOfVariables++;
    }
    super.caseAVariableDeclaration(node);
  }

  public short getNumberOfVariables()
  {
    return numberOfVariables;
  }

  /**
   * If-Then-Else statement
   */
  public void caseAIfThenElseStatement(AIfThenElseStatement node)
  {
    inAIfThenElseStatement(node);

    Compiler.incLevel();

    ARelConditionalExpression expr = (ARelConditionalExpression)
      node.getConditionalExpression();
    PValue expression1 = expr.getLeft();
    PValue expression2 = expr.getRight();

    // With the left hand load the variable or literal
    handleIdentifierLoad(expression1);

    // With the right hand load the variable or literal
    handleIdentifierLoad(expression2);

    expr.apply(this);
    short startPosition = Compiler.getInstructionIndex();
    node.getThenCompStmt().apply(this);
    short finishPosition = Compiler.getInstructionIndex();
    writePCode(startPosition,
      new Instruction(OpCode.JUMP_ON_CONDITION.getValue(), (byte) 0,
      (short) (finishPosition+Compiler.getLevel()+1)));

    startPosition = Compiler.getInstructionIndex();
    node.getElseCompStmt().apply(this);
    finishPosition = Compiler.getInstructionIndex();
    writePCode(startPosition,
      new Instruction(OpCode.JUMP_ON_CONDITION.getValue(), (byte) 0,
      (short) (finishPosition+Compiler.getLevel())));

    Compiler.decLevel();
    outAIfThenElseStatement(node);
  }

  /**
   * If statement
   */
  public void caseAIfStatement(AIfStatement node)
  {
    inAIfStatement(node);

    Compiler.incLevel();

    ARelConditionalExpression expr = (ARelConditionalExpression)
      node.getConditionalExpression();
    PValue expression1 = expr.getLeft();
    PValue expression2 = expr.getRight();

    // With the left hand load the variable or literal
    handleIdentifierLoad(expression1);

    // With the right hand load the variable or literal
    handleIdentifierLoad(expression2);

    expr.apply(this);
    short startPosition = Compiler.getInstructionIndex();
    node.getCompoundStatement().apply(this);
    short finishPosition = Compiler.getInstructionIndex();
    writePCode(startPosition,
      new Instruction(OpCode.JUMP_ON_CONDITION.getValue(), (byte) 0,
      (short) (finishPosition+Compiler.getLevel())));

    Compiler.decLevel();

    outAIfStatement(node);
  }

  public void caseAGteqRelop(AGteqRelop node)
  {
//    System.out.println("GTE Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.GREATER_THAN_OR_EQUAL.getValue()));
  }

  public void inAGtRelop(AGtRelop node)
  {
//    System.out.println("GT Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.GREATER_THAN.getValue()));
  }

  public void caseALteqRelop(ALteqRelop node)
  {
//    System.out.println("LTE Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.LESS_THAN_OR_EQUAL.getValue()));
  }

  public void caseALtRelop(ALtRelop node)
  {
//    System.out.println("LT Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.LESS_THAN.getValue()));
  }

  public void caseANeqRelop(ANeqRelop node)
  {
//    System.out.println("NEQ Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.NOT_EQUAL.getValue()));
  }

  public void caseAEqRelop(AEqRelop node)
  {
//    System.out.println("EQ Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.EQUAL.getValue()));
  }

  /**
   * Process simple printf literal statement.
   */
  public void caseAPrintf1RcosStatement(APrintf1RcosStatement node)
  {
    String stringValue = node.getStringLitteral().getText();
    //String name = variableCompiler.allocateVariable(1, stringValue.length());
    handleLiteralLoad(stringValue);

    writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
      (byte) 0, SystemCall.STRING_OUT.getValue()));
  }

  /**
   * Process non-literal printf statement.
   */
  public void caseAPrintf2RcosStatement(APrintf2RcosStatement node)
  {
    PPrintfControlStrings control = node.getControl();
    PValue value = node.getValue();
    printOut(control, value);
  }

  /**
   * Right hand side of a variable assignment with one statement.
   */
  public void caseAUnaryRhs(AUnaryRhs node)
  {
    node.getUnaryExpression().apply(this);
  }

  /**
   * Right hand side of a variable assignment with two statements.
   */
  public void caseABinaryRhs(ABinaryRhs node)
  {
    node.getBinaryExpression().apply(this);
  }

  /**
   * Right hand side of a variable assignment with one statement.
   */
  public void caseASimpleUnaryExpression(ASimpleUnaryExpression node)
  {
    String varValue = node.getSimpleExpression().toString().trim();
    if (node.getSimpleExpression() instanceof AConstantSimpleExpression)
    {
      handleLiteralLoad(varValue);
    }
    else if (node.getSimpleExpression() instanceof AVarnameSimpleExpression)
    {
      handleIdentifierLoad(
        ((AVarnameSimpleExpression) node.getSimpleExpression()).getVarname());
    }
  }

  /**
   * Right hand side of a variable assignment with two statements.
   */
  public void caseAIdentifierBinaryExpression(AIdentifierBinaryExpression node)
  {
    handleIdentifierLoad(node.getValue());
    handleIdentifierLoad(node.getIdentifier().toString().trim());
    node.getBinop().apply(this);
  }

  /**
   * Right hand side of a variable assignment with two statements.
   */
  public void caseAConstantBinaryExpression(AConstantBinaryExpression node)
  {
    handleLiteralLoad(node.getConstant().toString().trim());

    if (node.getValue() instanceof AIdentifierValue)
    {
      handleIdentifierLoad(node.getValue().toString().trim());
    }
    else if (node.getValue() instanceof AConstantValue)
    {
      handleLiteralLoad(node.getValue().toString().trim());
    }
    node.getBinop().apply(this);
  }

  public void caseAPlusBinop(APlusBinop node)
  {
    System.out.println("Plus Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.ADD.getValue()));
  }

  public void caseAMinusBinop(AMinusBinop node)
  {
    System.out.println("Minus Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.SUBTRACT.getValue()));
  }

  /**
   * Simple assignment statements
   * e.g. i = 0;
   */
  public void caseADirectModifyExpression(ADirectModifyExpression node)
  {
    try
    {
      String varName = node.getVarname().toString().trim();
      Symbol newVar = table.getSymbol(varName, Compiler.getLevel());

      PRhs rhs = node.getRhs();
      rhs.apply(this);

      OpCode storeOpCode = null;
      if (newVar instanceof Variable)
      {
        storeOpCode = OpCode.STORE;
      }
      else if (newVar instanceof Array)
      {
        storeOpCode = OpCode.STORE_INDEXED;
      }

      // Store variable at the variables location
      if (Compiler.getLevel() == newVar.getLevel())
      {
          writePCode(new Instruction(storeOpCode.getValue(), (byte) 0,
            newVar.getOffset()));
      }
      else
      {
        writePCode(new Instruction(storeOpCode.getValue(),
          (byte) newVar.getLevel(),
          newVar.getOffset()));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  /**
   * Identifies the function name e.g.
   * void test(void)
   * int test(int in1, int in2)
   */
  public void inAIdentifierDirectFunctionDeclarator(
    AIdentifierDirectFunctionDeclarator node)
  {
    //System.out.println("Identifier direct function declarator [" + node
    //+ "]");
  }

  /**
   * The name of the identifier.  e.g. in the cast of int global it returns
   * "global".
   */
  public void inAIdentifierValue(AIdentifierValue node)
  {
    //System.out.println("Here [" + node + "]");
  }

  /**
   * A function body.
   */
  public void caseAFunctionBody(AFunctionBody node)
  {
    Compiler.incLevel();

    inAFunctionBody(node);
    if(node.getLBrace() != null)
    {
      node.getLBrace().apply(this);
    }

    Object temp[] = node.getVariableDeclaration().toArray();
    for(int i = 0; i < temp.length; i++)
    {
      ((PVariableDeclaration) temp[i]).apply(this);
    }

    // Set the number of declared variables
    writePCode(new Instruction(OpCode.INTERVAL.getValue(), (byte) 0,
      ((short) (table.getVariableSize() + 3))));

    Object temp2[] = node.getStatement().toArray();
    for(int i = 0; i < temp2.length; i++)
    {
      ((PStatement) temp2[i]).apply(this);
    }

    if(node.getStopStatement() != null)
    {
      node.getStopStatement().apply(this);
    }
    if(node.getRBrace() != null)
    {
      node.getRBrace().apply(this);
    }
    outAFunctionBody(node);

    //System.out.println("Out of function!");
    //Modify the jump point code
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.RETURN.getValue()));
    //localVarsTable = new HashMap();

    Compiler.decLevel();
  }

  /**
   * Any variable declaration such as:
   * int global;
   * char test;
   */
  public void inAVariableDeclaration(AVariableDeclaration node)
  {
//    System.out.println("Adding: "+ node.getDeclarator().toString());
//    System.out.println("Adding: "+ node.toString());
    // This compiler understands only 16 bit int/short and chars
    if (node.getTypeSpecifier() instanceof ASignedIntTypeSpecifier ||
        node.getTypeSpecifier() instanceof AUnsignedIntTypeSpecifier ||
        node.getTypeSpecifier() instanceof ASignedShortTypeSpecifier ||
        node.getTypeSpecifier() instanceof AUnsignedShortTypeSpecifier ||
        node.getTypeSpecifier() instanceof ACharTypeSpecifier)
    {
      try
      {
        String name = node.getDeclarator().toString().trim();
        if (isArray(name))
        {
          short size = getArraySize(name);
          name = name.substring(0, name.indexOf("[")).trim();
          Array newArray = new Array(name, Compiler.getLevel(),
              Compiler.getInstructionIndex(), size);
          table.addSymbol(newArray);
        }
        else
        {
//          System.out.println("Type: " + node.getTypeSpecifier().toString());
//          System.out.println("Level: " + Compiler.getLevel());
          Variable newVar = new Variable(name,
              Compiler.getLevel(), Compiler.getInstructionIndex());
          table.addSymbol(newVar);
        }
      }
      catch (ParserException pe)
      {
        throw new RuntimeException(pe.getMessage() + " at line: " +
            pe.getStartLine() + " at position: " +
            pe.getStartPos());
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      //Do some sort of error processing.
      System.out.println("Variable type not handled!");
    }
  }

  /**
   * Returns if a declarator is a variable or not.
   *
   * @return if a declarator is a variaable or not.
   */
  private boolean isArray(String declarator)
  {
    boolean isArray = (declarator.indexOf("[") > 0) &&
      (declarator.indexOf("]") > 0);
    return isArray;
  }

  /**
   * Get the size of the array if possible
   *
   * @param declarator the declaration of the variable.
   */
  private short getArraySize(String declarator)
  {
    int arrayStartIndex = declarator.indexOf("[");
    int arrayFinishedIndex = 0;
    short arraySize = 0;
    declarator = declarator.trim();
    if (arrayStartIndex > 0)
    {
      arrayFinishedIndex = declarator.indexOf("]");
//      System.out.println("Dec: " + (arrayFinishedIndex - arrayStartIndex));
//      System.out.println("Dec: " + declarator);
      if ((arrayFinishedIndex - arrayStartIndex) > 2)
      {
        arraySize = Short.parseShort(declarator.substring(arrayStartIndex+1,
          arrayFinishedIndex).trim());
      }
    }
    return arraySize;
  }

  public void writePCode(Instruction newInstruction)
  {
    Compiler.addInstruction(newInstruction);
    Compiler.incInstructionIndex();
  }

  public void writePCode(int index, Instruction newInstruction)
  {
    Compiler.addInstruction(index, newInstruction);
    Compiler.incInstructionIndex();
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

  private void handleIdentifierLoad(PVarname identifier)
  {
    handleIdentifierLoad(identifier.toString().trim());
  }

  private void handleIdentifierLoad(AIdentifierValue identifier)
  {
    handleIdentifierLoad(identifier.toString().trim());
  }

  private void handleIdentifierLoad(String identifierName)
  {
    try
    {
      Symbol newVar = table.getSymbol(identifierName, Compiler.getLevel());

      OpCode loadOpCode = null;
      if (newVar instanceof Variable)
      {
        loadOpCode = OpCode.LOAD;
      }
      else if (newVar instanceof Array)
      {
        loadOpCode = OpCode.LOAD_INDEXED;
      }

      if (Compiler.getLevel() == newVar.getLevel())
      {
        writePCode(new Instruction(loadOpCode.getValue(), (byte) 0,
          newVar.getOffset()));
      }
      else
      {
        writePCode(new Instruction(loadOpCode.getValue(),
          (byte) newVar.getLevel(), newVar.getOffset()));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void handleIdentifierLoad(AConstantValue constant)
  {
    handleLiteralLoad(constant.toString());
  }

  private void handleLiteralLoad(String varValue)
  {
    short length = 0;

    // Do char storage
    if (varValue.indexOf("'") >= 0)
    {
      writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
        (short) varValue.charAt(1)));
    }
    // Do string storage
    else if ((varValue.indexOf("\"") >= 0))
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
      }
      //emit store a required pos
      writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
        (short) (count-1)));
    }
    // Do int storage
    else
    {
      short varIntValue = Short.parseShort(varValue.trim());
      writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
        varIntValue));
    }
  }

  private void printOut(PPrintfControlStrings control, PValue value)
  {
    System.out.println("Value: " + value);
    System.out.println("Value: " + value.getClass());
    System.out.println("Control: " + control);
    System.out.println("Control: " + control.getClass());

    if (value instanceof AConstantValue)
    {
      handleLiteralLoad(value.toString().trim());
    }
    else if (value instanceof AIdentifierValue)
    {
      handleIdentifierLoad(value.toString().trim());
    }

    if (control instanceof AIntControlPrintfControlStrings)
    {
      writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
        (byte) 0, SystemCall.NUMBER_OUT.getValue()));
    }
    else if (control instanceof AChrControlPrintfControlStrings)
    {
      writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
        (byte) 0, SystemCall.CHARACTER_OUT.getValue()));
    }
    else if (control instanceof AStrControlPrintfControlStrings)
    {
      writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
        (byte) 0, SystemCall.STRING_OUT.getValue()));
    }
  }
}