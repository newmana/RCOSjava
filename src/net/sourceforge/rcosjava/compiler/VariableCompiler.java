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
public class VariableCompiler extends DepthFirstAdapter
{
  private static boolean isInFunction;
  private static short variableStackPointer = 0;
  private static int basePosition = 0;
  private static HashMap globalVarsTable = new HashMap();
  private HashMap localVarsTable = new HashMap();
  private static int anonymousVariableCounter = 0;

  public VariableCompiler()
  {
    isInFunction = false;
  }

  public VariableCompiler(boolean newIsInFunction)
  {
    isInFunction = newIsInFunction;
  }

  public static void isInFunction()
  {
    isInFunction = true;
  }

  public static void isOutFunction()
  {
    isInFunction = false;
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
    System.out.println("In var declaration");
    // This compiler understands only 16 bit int/short and chars
    if (node.getTypeSpecifier() instanceof ASignedIntTypeSpecifier ||
      node.getTypeSpecifier() instanceof AUnsignedIntTypeSpecifier ||
      node.getTypeSpecifier() instanceof ASignedShortTypeSpecifier ||
      node.getTypeSpecifier() instanceof AUnsignedShortTypeSpecifier ||
      node.getTypeSpecifier() instanceof ACharTypeSpecifier)
    {
//      allocateVariable(node.getDeclarator().toString(), 2,
//        getArraySize(node.getDeclarator().toString()));
//    }
//    else if ()
//    {
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

  public String allocateVariable(int noBits, int size)
  {
    String name = "anonymous" + anonymousVariableCounter++;
    allocateVariable(name, noBits, size);
    return name;
  }

  /**
   * Allocate the variables.  Used for initial jump once compiled and for
   * referral later on.  Store for both global and local variables.  Locals will
   * be cleaned up by deallocateVariable.
   */
  public void allocateVariable(String name, int noBits, int size)
  {
    name = name.trim();
    if (isInFunction)
    {
      System.out.println("Allocating local: " + name + " position:" +
        variableStackPointer);
      localVarsTable.put(name, new Short(variableStackPointer));
    }
    else
    {
      System.out.println("Allocating global: " + name + " position:" +
        variableStackPointer);
      globalVarsTable.put(name, new Short(variableStackPointer));
    }
    variableStackPointer += noBits * size;
  }

  public static short getVariableStackPointer()
  {
    return variableStackPointer;
  }

  public short getVariableLocation(String name)
  {
    short location = -1;
    name = name.trim();
    if (localVarsTable.containsKey(name))
    {
      location = ((Short) localVarsTable.get(name)).shortValue();
    }
    else if (globalVarsTable.containsKey(name))
    {
      location = ((Short) globalVarsTable.get(name)).shortValue();
    }
    return location;
  }
}