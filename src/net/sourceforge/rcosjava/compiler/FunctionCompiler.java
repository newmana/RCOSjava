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
  // This will eventually be split into two.
  private int basePosition = 0;
  private ArrayList instructions = new ArrayList();
  private boolean isInFunction;

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
    System.out.println("In a function!: ");
    isInFunction = true;

    StatementCompiler statementCompiler = new StatementCompiler(basePosition);
    node.apply(statementCompiler);

    // Write out jump position to main function
//    writePCode(new Instruction(OpCode.JUMP.getValue(), (byte) 0,
//      statementCompiler.getBasePosition()));
    writePCode(new Instruction(OpCode.JUMP.getValue(), (byte) 0, (short) 1));

    // Write out stack pointer size for allocated variables
    int stackPointer = variableCompiler.getVariableStackPointer();
    if (stackPointer == 0)
    {
      stackPointer = 1;
    }
    writePCode(new Instruction(OpCode.INTERVAL.getValue(), (byte) 0,
        variableCompiler.getVariableStackPointer()));

    statementCompiler.emitInstructions();
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
    isInFunction = false;
    //localVarsTable = new HashMap();
  }

  public void writePCode(Instruction newInstruction)
  {
    basePosition++;
//    instructions.add(newInstruction);
    System.out.println(newInstruction);
  }

  public void emitInstructions()
  {
    Iterator tmpIter = instructions.iterator();
    while (tmpIter.hasNext())
    {
      System.out.println((Instruction) tmpIter.next());
    }
  }
}