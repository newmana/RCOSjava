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
public class VariableCompiler extends DepthFirstAdapter
{
  private static short variableStackPointer = 0;
  private SymbolTable table;

  public VariableCompiler()
  {
    table = SymbolTable.getInstance();
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
    System.out.println("Adding: "+ node.getDeclarator().toString());
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
          Array newArray = new Array(name, Compiler.getLevel(),
              Compiler.getInstructionIndex(), size);
          table.addSymbol(newArray);
        }
        else
        {
          System.out.println("Type: " + node.getTypeSpecifier().toString());
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
    return ((declarator.indexOf("[") > 0) && (declarator.indexOf("]") > 0));
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
      System.out.println("Dec: " + (arrayFinishedIndex - arrayStartIndex));
      System.out.println("Dec: " + declarator);
      if ((arrayFinishedIndex - arrayStartIndex) > 2)
      {
        arraySize = Short.parseShort(declarator.substring(arrayStartIndex+1,
          arrayFinishedIndex).trim());
      }
    }
    return arraySize;
  }
}