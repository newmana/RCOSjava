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
public class Compiler
{
  private static ArrayList instructions = new ArrayList();
  private static short level;
  private static short instructionIndex;
  private static SymbolTable table;

  public static void main(String args[])
  {
    System.out.println(Version.banner());

    if(args.length != 2)
    {
      System.out.println("usage:");
      System.out.println("  java Compiler sourceFile outputFile");
      System.exit(1);
    }

    try
    {
      Lexer lexer = new Lexer(new PushbackReader(new BufferedReader(
        new FileReader(args[0])), 1024));

      Parser parser = new Parser(lexer);
      Start tree = parser.parse();
      tree.apply(new FunctionCompiler());

      // Write out jump position to main function
      table = SymbolTable.getInstance();

      // Add header codes.
      addInstruction(0, new Instruction(OpCode.JUMP.getValue(), (byte) 0,
          (short) 1));
      addInstruction(1, new Instruction(OpCode.INTERVAL.getValue(), (byte) 0,
          (short) 1));

      // Print out result to file
      Iterator tmpIter = instructions.iterator();
      StringBuffer tmpBuffer = new StringBuffer();
      while (tmpIter.hasNext())
      {
        tmpBuffer.append((Instruction) tmpIter.next() + "\n");
      }

      System.out.println("Got " + tmpBuffer);
      ByteArrayInputStream input =
          new ByteArrayInputStream(tmpBuffer.toString().getBytes());
      FileOutputStream output = new FileOutputStream(args[1]);
      Pasm.compile(input, output, 0);
      output.close();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  protected static void addInstruction(int index, Instruction instruction)
  {
    instructions.add(index, instruction);
  }

  protected static void addInstruction(Instruction instruction)
  {
    instructions.add(instruction);
  }

  protected static  short getLevel()
  {
    return level;
  }

  protected static void incLevel()
  {
    level++;
  }

  protected static void decLevel()
  {
    level--;
  }

  protected static void incInstructionIndex()
  {
    instructionIndex++;
  }

  protected static short getInstructionIndex()
  {
    return instructionIndex;
  }
}