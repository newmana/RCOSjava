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
public class FunctionCompiler extends DepthFirstAdapter
{
  private VariableCompiler variableCompiler = new
      VariableCompiler();

  public FunctionCompiler()
  {
    super();
  }

  /**
   * Any variable declaration such as:
   * int global;
   * char test;
   */
  public void inAVariableDeclaration(AVariableDeclaration node)
  {
    node.apply(variableCompiler);
  }

  /**
   * When the compiler enters a function block/body.
   */
  public void inAFunctionBody(AFunctionBody node)
  {
    StatementCompiler statementCompiler = new StatementCompiler();
    node.apply(statementCompiler);
  }

  /**
   * When the compiler leaves a function block/body.
   */
  public void outAFunctionBody(AFunctionBody node)
  {
    //System.out.println("Out of function!");
    //Modify the jump point code
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.RETURN.getValue()));
    //localVarsTable = new HashMap();
  }

  public void writePCode(Instruction newInstruction)
  {
    Compiler.incInstructionIndex();
    Compiler.addInstruction(newInstruction);
  }
}