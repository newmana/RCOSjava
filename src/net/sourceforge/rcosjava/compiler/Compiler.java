package net.sourceforge.rcosjava.compiler;

import net.sourceforge.rcosjava.hardware.cpu.Instruction;
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
      System.out.println(e);
    }
  }

// This will eventually be split into two.

  private int variableStackPosition = 0;
  private int basePosition = 0;
  private int functionPosition = 0;
  private Hashtable table = new Hashtable();

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
      System.out.println("Is a 16 bit int: " + node.getDeclarator());
      allocateVariable(node.getDeclarator().toString(), 2,
        getArraySize(node.getDeclarator().toString()));
    }
    else if (node.getTypeSpecifier() instanceof ACharTypeSpecifier)
    {
      System.out.println("Is a 8 bit char: " + node.getDeclarator());
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

  /**
   * Allocate the variables.  Used for initial jump once compiled and for
   * referral later on.
   */
  private void allocateVariable(String name, int noBits, int size)
  {
    System.out.println("Allocating to: " + name + " position:" + variableStackPosition);
    table.put(name.toUpperCase(), new Integer(variableStackPosition));
    variableStackPosition += noBits * size;
  }

  /**
   * When the compiler enters a function block/body.
   */
  public void inAFunctionBody(AFunctionBody node)
  {
    System.out.println("In main body!");
  }

  /**
   * When the compiler leaves a function block/body.
   */
  public void outAFunctionBody(AFunctionBody node)
  {
    System.out.println("Out a main body!");
  }
}