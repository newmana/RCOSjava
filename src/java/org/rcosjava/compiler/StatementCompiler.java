package org.rcosjava.compiler;

import org.rcosjava.hardware.cpu.*;
import org.rcosjava.compiler.symbol.*;
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
  private Symbol currentSymbol = new Variable("", (short) 0, (short) 0);
  private int noLoops = 0;

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

  public void inASemcloseRcosStatement(ASemcloseRcosStatement node)
  {
    handlePValueLoad(node.getHandle());
    writeSemShrRest(node.getVarname(), null, SystemCall.SEMAPHORE_CLOSE);
  }

  public void inAShrcloseRcosStatement(AShrcloseRcosStatement node)
  {
    handlePValueLoad(node.getHandle());
    writeSemShrRest(node.getVarname(), null, SystemCall.SHARED_MEMORY_CLOSE);
  }

  public void inASemcreate1RcosStatement(ASemcreate1RcosStatement node)
  {
    handlePValueLoad(node.getHandle());
    handleLiteralLoading("0");
    writeSemShrRest(node.getVarname(), node.getHandle(),
      SystemCall.SEMAPHORE_CREATE);
  }

  public void inAShrcreateRcosStatement(AShrcreateRcosStatement node)
  {
    handlePValueLoad(node.getId());
    handlePValueLoad(node.getSize());
    writeSemShrRest(node.getVarname(), node.getSize(),
      SystemCall.SHARED_MEMORY_CREATE);
  }

  public void inASemcreate2RcosStatement(ASemcreate2RcosStatement node)
  {
    handlePValueLoad(node.getHandle());
    handlePValueLoad(node.getInitValue());
    writeSemShrRest(node.getVarname(), node.getHandle(),
      SystemCall.SEMAPHORE_CREATE);
  }

  public void inASemopenRcosStatement(ASemopenRcosStatement node)
  {
    handlePValueLoad(node.getId());
    writeSemShrRest(node.getVarname(), null, SystemCall.SEMAPHORE_OPEN);
  }

  public void inAShropenRcosStatement(AShropenRcosStatement node)
  {
    handlePValueLoad(node.getId());
    writeSemShrRest(node.getVarname(), null, SystemCall.SHARED_MEMORY_OPEN);
  }

  public void inASemsignalRcosStatement(ASemsignalRcosStatement node)
  {
    handlePValueLoad(node.getHandle());
    writeSemShrRest(node.getVarname(), null, SystemCall.SEMAPHORE_SIGNAL);
  }

  public void inASemwaitRcosStatement(ASemwaitRcosStatement node)
  {
    handlePValueLoad(node.getHandle());
    writeSemShrRest(node.getVarname(), null, SystemCall.SEMAPHORE_WAIT);
  }

  public void inAShrreadRcosStatement(AShrreadRcosStatement node)
  {
    handlePValueLoad(node.getHandle());
    handlePValueLoad(node.getOffset());
    writeSemShrRest(node.getVarname(), null, SystemCall.SHARED_MEMORY_READ);
  }

  public void inAShrwriteRcosStatement(AShrwriteRcosStatement node)
  {
    handlePValueLoad(node.getHandle());
    handlePValueLoad(node.getOffset());
    this.handleIdentifierLoad(node.getValue());
    writeSemShrRest(node.getVarname(), null, SystemCall.SHARED_MEMORY_WRITE);
  }

  public void inAShrsizeRcosStatement(AShrsizeRcosStatement node)
  {
    handlePValueLoad(node.getHandle());
    writeSemShrRest(node.getVarname(), null, SystemCall.SHARED_MEMORY_SIZE);
  }

  private void writeSemShrRest(PVarname var, PValue value, SystemCall semType)
  {
    byte byteParam = 0;

    writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
      byteParam, semType.getValue()));

    String varName = var.toString().trim();

    try
    {
      if (isArray(varName))
      {
        currentSymbol = table.getArray(varName, Compiler.getLevel());
      }
      else
      {
        currentSymbol = table.getSymbol(varName, Compiler.getLevel());
      }
    }
    catch (Exception e)
    {
    }

    currentSymbol.handleStore(this);
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
   * If statement
   */
  public void caseAIfStatement(AIfStatement node)
  {
    inAIfStatement(node);
    noLoops++;

    ARelConditionalExpression expr = (ARelConditionalExpression)
      node.getConditionalExpression();
    PValue expression1 = expr.getLeft();
    PValue expression2 = expr.getRight();

    // With the left hand load the variable or literal
    handlePValueLoad(expression1);

    // With the right hand load the variable or literal
    handlePValueLoad(expression2);

    expr.apply(this);
    short startPosition = Compiler.getInstructionIndex();
    node.getCompoundStatement().apply(this);
    short finishPosition = Compiler.getInstructionIndex();
    writePCode(startPosition,
      new Instruction(OpCode.JUMP_ON_CONDITION.getValue(), (byte) 0,
      (short) (finishPosition+2+noLoops)));

    noLoops--;
    outAIfStatement(node);
  }

  /**
   * If-Then-Else statement
   */
  public void caseAIfThenElseStatement(AIfThenElseStatement node)
  {
    inAIfThenElseStatement(node);
    noLoops++;

    ARelConditionalExpression expr = (ARelConditionalExpression)
      node.getConditionalExpression();
    PValue expression1 = expr.getLeft();
    PValue expression2 = expr.getRight();

    // With the left hand load the variable or literal
    handlePValueLoad(expression1);

    // With the right hand load the variable or literal
    handlePValueLoad(expression2);

    expr.apply(this);
    short startPosition = Compiler.getInstructionIndex();
    node.getThenCompStmt().apply(this);
    short finishPosition = Compiler.getInstructionIndex();
    writePCode(startPosition,
      new Instruction(OpCode.JUMP_ON_CONDITION.getValue(), (byte) 0,
      (short) (finishPosition+2+(noLoops*2))));

    startPosition = Compiler.getInstructionIndex();
    node.getElseCompStmt().apply(this);
    finishPosition = Compiler.getInstructionIndex();
    writePCode(startPosition,
      new Instruction(OpCode.JUMP_ON_CONDITION.getValue(), (byte) 0,
      (short) (finishPosition+1+(noLoops*2))));

    noLoops--;
    outAIfThenElseStatement(node);
  }

  /**
   * For statement
   */
  public void caseAForStatement(AForStatement node)
  {
    inAForStatement(node);
    noLoops++;

    PBasicStatement start = node.getStart();
    if (start != null)
    {
      start.apply(this);
    }

    short beforeIter = Compiler.getInstructionIndex();

    // Set-up the iterator
    PBasicStatement iter = node.getIter();
    if (iter != null)
    {
      iter.apply(this);
    }

    PConditionalExpression cond = node.getConditionalExpression();

    if (cond != null)
    {

      if (cond instanceof ARelConditionalExpression)
      {
        ARelConditionalExpression expr = (ARelConditionalExpression) cond;
        PValue expression1 = expr.getLeft();
        PValue expression2 = expr.getRight();

        // With the left hand load the variable or literal
        handlePValueLoad(expression1);

        // With the right hand load the variable or literal
        handlePValueLoad(expression2);

        expr.apply(this);
      }
      else if (cond instanceof AValueConditionalExpression)
      {
        cond.apply(this);
      }
    }

    short afterCond = Compiler.getInstructionIndex();

    // Do what is inside the for statement.
    node.getCompoundStatement().apply(this);

    short finishPosition = Compiler.getInstructionIndex();

    writePCode(afterCond,
      new Instruction(OpCode.JUMP_ON_CONDITION.getValue(), (byte) 0,
      (short) (finishPosition+3+noLoops)));

    writePCode(
      new Instruction(OpCode.JUMP.getValue(), (byte) 0,
      (short) (beforeIter+1+noLoops)));

    noLoops--;
    outAForStatement(node);
  }

  /**
   * While statement
   */
  public void caseAWhileStatement(AWhileStatement node)
  {
    inAWhileStatement(node);
    noLoops++;

    short beforeCondPosition = Compiler.getInstructionIndex();

    ARelConditionalExpression expr = (ARelConditionalExpression)
      node.getConditionalExpression();
    PValue expression1 = expr.getLeft();
    PValue expression2 = expr.getRight();

    // With the left hand load the variable or literal
    handlePValueLoad(expression1);

    // With the right hand load the variable or literal
    handlePValueLoad(expression2);

    expr.apply(this);

    short startPosition = Compiler.getInstructionIndex();

    node.getCompoundStatement().apply(this);

    // Start position -1 because we are inserting the JPC.
    writePCode(
      new Instruction(OpCode.JUMP.getValue(), (byte) 0,
      (short) (beforeCondPosition+1+noLoops)));

    short finishPosition = Compiler.getInstructionIndex();

    writePCode(startPosition,
      new Instruction(OpCode.JUMP_ON_CONDITION.getValue(), (byte) 0,
      (short) (finishPosition+2+noLoops)));

    noLoops--;
    outAWhileStatement(node);
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

  public void caseAScanf1RcosStatement(AScanf1RcosStatement node)
  {
    PScanfControlStrings control = node.getControl();
    PValue value = node.getValue();
    scanIn(control, value);
  }

  /**
   * Process simple printf literal statement.
   */
  public void caseAPrintf1RcosStatement(APrintf1RcosStatement node)
  {
    currentSymbol = new Variable("const", (short) 0, (short) 0);
    handleLiteralLoad(node.getStringLitteral());

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
   * Process print out an element in an array.
   */
  public void caseAPrintf3RcosStatement(APrintf3RcosStatement node)
  {
    PPrintfControlStrings control = node.getControl();
    PArrayref array = node.getArrayref();
    printOut(control, array);
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
    if (node.getSimpleExpression() instanceof AConstantSimpleExpression)
    {
      handleLiteralLoad((AConstantSimpleExpression) node.getSimpleExpression());
    }
    else if (node.getSimpleExpression() instanceof AVarnameSimpleExpression)
    {
      PVarname varName =
        ((AVarnameSimpleExpression) node.getSimpleExpression()).getVarname();

      if (!isArray(varName.toString()))
      {
        handleIdentifierLoad(varName);
      }
      else
      {
        handleIdentifierArrayLoad(varName.toString());
      }
    }
  }

  /**
   * Right hand side of a variable assignment with two statements.
   */
  public void caseAIdentifierBinaryExpression(AIdentifierBinaryExpression node)
  {
    handlePValueLoad(node.getValue());
    handleIdentifierLoad(node.getIdentifier());
    node.getBinop().apply(this);
  }

  /**
   * Right hand side of a variable assignment with two statements.
   */
  public void caseAConstantBinaryExpression(AConstantBinaryExpression node)
  {
    handleLiteralLoad(node.getConstant());
    handlePValueLoad(node.getValue());
    node.getBinop().apply(this);
  }

  public void caseAPlusBinop(APlusBinop node)
  {
//    System.out.println("Plus Node: " + node);
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.ADD.getValue()));
  }

  public void caseAMinusBinop(AMinusBinop node)
  {
//    System.out.println("Minus Node: " + node);
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
      short index;

//      PRhs rhs = node.getRhs();
//      rhs.apply(this);

//      System.err.println("Var name: " + varName);
//      System.err.println("Var name: " + isArray(varName));
//      System.err.println("Current symbole: " + currentSymbol);
      if (isArray(varName))
      {
        String identifierName = varName.substring(0, varName.indexOf("[")).
          trim();

        //Dodgy try and get the index as a integer else try and get it as a
        //variable.
        try
        {
          index = getArraySize(varName);
          System.out.println("Array index: " + index);
          System.out.println("Id name: [" + identifierName + "]");

          writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
            (short) index));
        }
        catch (NumberFormatException nfe)
        {
          // If it's not a number then try and load it.
          Symbol tmpSymbol = getArraySymbol(varName);
          tmpSymbol.handleLoad(this);
        }

        currentSymbol = table.getSymbol(identifierName, Compiler.getLevel());
      }
      else
      {
        currentSymbol = table.getSymbol(varName, Compiler.getLevel());
      }

      PRhs rhs = node.getRhs();
      rhs.apply(this);

      if (isArray(varName))
      {
        writePCode(new Instruction(OpCode.STORE_INDEXED.getValue(),
          (byte) 0, currentSymbol.getOffset()));
      }
      else
      {
        currentSymbol.handleStore(this);
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
    inAFunctionBody(node);
    Compiler.incLevel();

    if (node.getLBrace() != null)
    {
      node.getLBrace().apply(this);
    }

    Object temp[] = node.getVariableDeclaration().toArray();
    for (int i = 0; i < temp.length; i++)
    {
      ((PVariableDeclaration) temp[i]).apply(this);
    }

    // Set the number of declared variables
    writePCode(new Instruction(OpCode.INTERVAL.getValue(), (byte) 0,
      ((short) (table.getVariableSize() + 3))));

    Object temp2[] = node.getStatement().toArray();
    for (int i = 0; i < temp2.length; i++)
    {
      ((PStatement) temp2[i]).apply(this);
    }

    if (node.getStopStatement() != null)
    {
      node.getStopStatement().apply(this);
    }

    if (node.getRBrace() != null)
    {
      node.getRBrace().apply(this);
    }

    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.RETURN.getValue()));

    Compiler.decLevel();
    outAFunctionBody(node);
  }

  /**
   * Any variable declaration such as:
   * int global;
   * char test;
   */
  public void inAVariableDeclaration(AVariableDeclaration node)
  {
    if (node.getTypeSpecifier() instanceof ASignedIntTypeSpecifier ||
        node.getTypeSpecifier() instanceof AUnsignedIntTypeSpecifier ||
        node.getTypeSpecifier() instanceof ASignedShortTypeSpecifier ||
        node.getTypeSpecifier() instanceof AUnsignedShortTypeSpecifier ||
        node.getTypeSpecifier() instanceof ACharTypeSpecifier)
    {

      // Handle first declaration.
      doVarDeclarator(node.getDeclarator().toString().trim());

      // Handle others of the same type.
      LinkedList list = node.getAdditionalDeclarator();

      if ((list != null) && (list.size() > 0))
      {
        Iterator iter = list.iterator();
        while (iter.hasNext())
        {
          AAdditionalDeclarator dec = (AAdditionalDeclarator) iter.next();
          doVarDeclarator(dec.getDeclarator().toString().trim());
        }
      }
    }
    else
    {
      //Do some sort of error processing.
      System.err.println("Variable type not handled!");
    }
  }

  private void doVarDeclarator(String name)
  {
//    System.out.println("Adding: "+ node.getDeclarator().toString());
//    System.out.println("Adding: "+ name);
    // This compiler understands only 16 bit int/short and chars
    try
    {
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
//          System.out.println("Compiler Level: " + Compiler.getLevel());
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
  private short getArraySize(String declarator) throws NumberFormatException
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
      String declaratorStr = declarator.substring(arrayStartIndex+1,
          arrayFinishedIndex).trim();
      arraySize = Short.parseShort(declaratorStr);
    }
    return arraySize;
  }

  private Symbol getArraySymbol(String declarator)
  {
//    System.err.println("Get array symbol got: " + declarator);

    int arrayStartIndex = declarator.indexOf("[");
    int arrayFinishedIndex = 0;
    Symbol tmpSymbol = null;

    try
    {
      declarator = declarator.trim();
      if (arrayStartIndex > 0)
      {
        arrayFinishedIndex = declarator.indexOf("]");
        String declaratorStr = declarator.substring(arrayStartIndex+1,
            arrayFinishedIndex).trim();
//        System.err.println("Trying to get symbol: " + declaratorStr);
        tmpSymbol = table.getSymbol(declaratorStr, Compiler.getLevel());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return tmpSymbol;
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

  /**
   * A PValue is unknown to be either a identifier or a literal value.
   */
  private void handlePValueLoad(PValue expression)
  {
    if (expression instanceof AIdentifierValue)
    {
      handleIdentifierLoad((AIdentifierValue) expression);
    }
    else if (expression instanceof AConstantValue)
    {
      handleLiteralLoad((AConstantValue) expression);
    }
  }

  private void handleIdentifierLoad(PVarname identifier)
  {
    handleIdentifierLoading(identifier.toString().trim());
  }

  private void handleIdentifierLoad(AIdentifierValue identifier)
  {
    handleIdentifierLoading(identifier.toString().trim());
  }

  private void handleIdentifierLoad(TIdentifier identifier)
  {
    handleIdentifierLoading(identifier.toString().trim());
  }

  private void handleIdentifierLoad(PArrayref identifier)
  {
    handleIdentifierArrayLoad(identifier.toString());
  }

  private void handleIdentifierArrayLoad(String fullName)
  {
    try
    {
      if (isArray(fullName))
      {
        String identifierName = fullName.substring(0, fullName.indexOf("[")).
          trim();

        currentSymbol = table.getSymbol(identifierName, Compiler.getLevel());
        Array array = (Array) currentSymbol;
        try
        {
          short index = getArraySize(fullName);
          System.out.println("Array index: " + index);
          System.out.println("Id name: [" + identifierName + "]");
          array.handleLoad(this, index);
        }
        catch (NumberFormatException nfe)
        {
          // Dodgy if it's not a number then try and load it as a symbol.
          Symbol tmpSymbol = getArraySymbol(fullName);
          array.handleLoad(this, tmpSymbol);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void handleIdentifierLoading(String identifierName)
  {
    try
    {
      System.out.println("Id name: " + identifierName);
      currentSymbol = table.getSymbol(identifierName, Compiler.getLevel());
      currentSymbol.handleLoad(this);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void handleLiteralLoad(PConstant constant)
  {
    handleLiteralLoading(constant.toString().trim());
  }

  private void handleLiteralLoad(AConstantValue constant)
  {
    handleLiteralLoading(constant.toString().trim());
  }

  private void handleLiteralLoad(AConstantSimpleExpression literal)
  {
    handleLiteralLoading(literal.toString().trim());
  }

  private void handleLiteralLoad(PValue literal)
  {
    handleLiteralLoading(literal.toString().trim());
  }

  private void handleLiteralLoad(TStringLitteral literal)
  {
    handleLiteralLoading(literal.toString().trim());
  }

  private void handleLiteralLoading(String varValue)
  {
    short length = 0;

    // Do char storage
    if (varValue.indexOf("'") >= 0)
    {
      currentSymbol.handleCharLiteral(this, varValue);
    }
    // Do string storage
    else if ((varValue.indexOf("\"") >= 0))
    {
      currentSymbol.handleStringLiteral(this, varValue);
    }
    // Do int storage
    else
    {
      currentSymbol.handleIntLiteral(this, varValue);
    }
  }

  private void printOut(PPrintfControlStrings control, PValue value)
  {
    handlePValueLoad(value);
    printOutSysCall(control);
  }

  private void printOut(PPrintfControlStrings control, PArrayref value)
  {
    handleIdentifierLoad(value);
    printOutSysCall(control);
  }

  private void printOutSysCall(PPrintfControlStrings control)
  {
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

  private void scanIn(PScanfControlStrings control, PValue value)
  {
    if (control instanceof AIntControlScanfControlStrings)
    {
      writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
        (byte) 0, SystemCall.NUMBER_IN.getValue()));
    }
    else if (control instanceof AChrControlScanfControlStrings)
    {
      writePCode(new Instruction(OpCode.CALL_SYSTEM_PROCEDURE.getValue(),
        (byte) 0, SystemCall.CHARACTER_IN.getValue()));
    }

    try
    {
      String varName = value.toString().trim();

      if (isArray(varName))
      {
        currentSymbol = table.getArray(varName, Compiler.getLevel());
      }
      else
      {
        currentSymbol = table.getSymbol(varName, Compiler.getLevel());
      }

      currentSymbol.handleStore(this);
    }
    catch (Exception e)
    {
    }
  }
}