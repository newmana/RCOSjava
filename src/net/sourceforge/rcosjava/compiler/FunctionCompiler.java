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

  public FunctionCompiler()
  {
    super();
  }

  /**
   * When the compiler enters a function block/body.
   */
  public void inAFunctionBody(AFunctionBody node)
  {
    System.out.println("In a function!: ");
    StatementCompiler tmpCompiler = new StatementCompiler(basePosition);
    node.apply(tmpCompiler);
    writePCode(new Instruction(OpCode.JUMP.getValue(), (byte) 0,
      tmpCompiler.getBasePosition()));
    tmpCompiler.emitInstructions();
  }

  /**
   * When the compiler leaves a function block/body.
   */
  public void outAFunctionBody(AFunctionBody node)
  {
//    System.out.println("Out of function!");
    //Modify the jump point code
    writePCode(new Instruction(OpCode.OPERATION.getValue(), (byte) 0,
      Operator.RETURN.getValue()));
    //isInFunction = false;
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