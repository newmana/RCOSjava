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

public class Compiler extends DepthFirstAdapter
{
  public static void main(String args[])
  {
    System.out.println(Version.banner());

    if(args.length != 1)
    {
      System.out.println("usage:");
      System.out.println("  java Compiler filename");
      System.exit(1);
    }

    try
    {
      Lexer lexer = new Lexer(new PushbackReader(new BufferedReader(
        new FileReader(args[0])), 1024));

      Parser parser = new Parser(lexer);

      Node ast = parser.parse();

      ast.apply(new Compiler());

      System.out.println(ast);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

// This will eventually be split into two.

  private int variableStackPosition = 0;
  private int basePosition = 0;
  private int functionPosition = 0;
  private Hashtable globalVarsTable = new Hashtable();
  private Hashtable localVarsTable = new Hashtable();
  boolean isInFunction = false;

  public Compiler()
  {
    super();
  }

  /**
   * Identifies the function name e.g.
   * void test(void)
   * int test(int in1, int in2)
   */
  public void inAIdentifierDirectFunctionDeclarator(AIdentifierDirectFunctionDeclarator node)
  {
    //System.out.println("Identifier direct function declarator [" + node + "]");
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
   * Right hand side of a variable assignment.
   */
  public void caseAUnaryRhs(AUnaryRhs node)
  {
    //System.out.println("Here: " + node + "");
  }

  /**
   * Any variable declaration such as:
   * int global;
   * char test;
   */
  public void inAVariableDeclaration(AVariableDeclaration node)
  {
    // This compiler understands only 16 bit int/short and chars
    if (node.getTypeSpecifier() instanceof ASignedIntTypeSpecifier ||
      node.getTypeSpecifier() instanceof AUnsignedIntTypeSpecifier ||
      node.getTypeSpecifier() instanceof ASignedShortTypeSpecifier ||
      node.getTypeSpecifier() instanceof AUnsignedShortTypeSpecifier)
    {
      allocateVariable(node.getDeclarator().toString(), 2,
        getArraySize(node.getDeclarator().toString()));
    }
    else if (node.getTypeSpecifier() instanceof ACharTypeSpecifier)
    {
      allocateVariable(node.getDeclarator().toString(), 1,
        getArraySize(node.getDeclarator().toString()));
    }
    else
    {
      //Do some sort of error processing.
      System.out.println("Variable type not handled!");
    }
  }

  /**
   * If statement
   */
  public void inAIfStatement(AIfStatement node)
  {
    System.out.println("If stmt: " + node.getCompoundStatement());

    ARelConditionalExpression expr = (ARelConditionalExpression) node.getConditionalExpression();
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

  /**
   * Simple assignment statements
   */
  public void inAModifyExpressionBasicStatement(AModifyExpressionBasicStatement node)
  {
    //Need to find the way of getting the =, lt and rh sides.
    ADirectModifyExpression expr = (ADirectModifyExpression) node.getModifyExpression();

    String varName = expr.getVarname().toString().trim();
    String varValue = expr.getRhs().toString().trim();
    System.out.println("Varname: " + varName + " at: " + getVariableLocation(varName));

    // Do string storage
    if ((varValue.indexOf("'") >= 0) || (varValue.indexOf("\"") >= 0))
    {
      int varStrLength = varValue.length()-2;
      //emit each element in the string
      for (int count = 0; count < varStrLength; count++)
      {
        writePCode(new Instruction(OpCode.LITERAL.getValue(), (byte) 0,
          (short) varValue.charAt(count)));
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
      (short) getVariableLocation(varName)));
  }

  /**
   * Get the size of the array if possible
   *
   * @param declarator the declaration of the variable.
   */
  private int getArraySize(String declarator)
  {
    int arrayIndex = declarator.indexOf("[");
    int arraySize = 1;
    if (arrayIndex > 0)
    {
      arraySize = Integer.parseInt(declarator.substring(arrayIndex+1,
        declarator.indexOf("]")).trim());
    }
    return arraySize;
  }

  /**
   * Allocate the variables.  Used for initial jump once compiled and for
   * referral later on.  Store for both global and local variables.  Locals will
   * be cleaned up by deallocateVariable.
   */
  private void allocateVariable(String name, int noBits, int size)
  {
    name = name.trim();
    if (isInFunction)
    {
      System.out.println("Allocating local: " + name + " position:" + variableStackPosition);
      localVarsTable.put(name, new Integer(variableStackPosition));
    }
    else
    {
      System.out.println("Allocating global: " + name + " position:" + variableStackPosition);
      globalVarsTable.put(name, new Integer(variableStackPosition));
    }
    variableStackPosition += noBits * size;
  }

  private int getVariableLocation(String name)
  {
    int location = -1;
    name = name.trim();
    if (localVarsTable.containsKey(name))
    {
      location = ((Integer) localVarsTable.get(name)).intValue();
    }
    else if (globalVarsTable.containsKey(name))
    {
      location = ((Integer) globalVarsTable.get(name)).intValue();
    }
    return location;
  }

  /**
   * When the compiler enters a function block/body.
   */
  public void inAFunctionBody(AFunctionBody node)
  {
    System.out.println("In a function!");
    isInFunction = true;
  }

  /**
   * When the compiler leaves a function block/body.
   */
  public void outAFunctionBody(AFunctionBody node)
  {
    System.out.println("Out of function!");
    isInFunction = false;
    localVarsTable = new Hashtable();
  }

  public void writePCode(Instruction newInstruction)
  {
    System.out.println("Instr: " + newInstruction);
  }
}