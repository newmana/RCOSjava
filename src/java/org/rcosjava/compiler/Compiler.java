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
 * Provides a compiler of a simple C like grammar with certain extensions.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th May 2001
 */
public class Compiler
{
  private static ArrayList instructions = new ArrayList();
  private static short level = 0;
  private static short instructionIndex;
  private static SymbolTable table;
  private static Lexer lexer;
  private static Parser parser;

  public static void main(String args[])
  {
    if(args.length != 2)
    {
      System.out.println("usage:");
      System.out.println("  java Compiler sourceFile outputFile");
      System.exit(1);
    }

    try
    {
      java.io.File tmpFile = new java.io.File(args[0]);

      lexer = new Lexer(new PushbackReader(new BufferedReader(
          new FileReader(args[0])), 1024));

      parser = new Parser(lexer);
      Start tree = parser.parse();

      // Does the meat.
      StatementCompiler stmtCompiler = new StatementCompiler();
      tree.apply(stmtCompiler);

      // Write out jump position to main function
      table = SymbolTable.getInstance();

      // Add header codes.
      addInstruction(0, new Instruction(OpCode.JUMP.getValue(), (byte) 0,
          stmtCompiler.mainPosition()));
      System.out.println("Main pos: " + stmtCompiler.mainPosition());

      // Print out result to file
      Iterator tmpIter = instructions.iterator();
      StringBuffer tmpBuffer = new StringBuffer();
      while (tmpIter.hasNext())
      {
        tmpBuffer.append((Instruction) tmpIter.next() + "\n");
      }

      System.out.println("Got: " + tmpBuffer.toString());

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

  /**
   * Add an instruction at the given location into the FIFO array instructions.
   *
   * @param index the position to put it in.
   * @param instruction the instruction to add.
   */
  protected static void addInstruction(int index, Instruction instruction)
  {
    instructions.add(index, instruction);
  }

  /**
   * Add an instruction into the FIFO array of instructions.
   *
   * @oaram instruction the instruction to add.
   */
  public static void addInstruction(Instruction instruction)
  {
    instructions.add(instruction);
  }

  /**
   * Returns the current level (how many nested statements i.e. curly braces)
   * we are currently in.
   *
   * @return the current level (how many nested statements i.e. curly braces)
   * we are currently in.
   */
  public static  short getLevel()
  {
    return level;
  }

  /**
   * If another nest of statements then call this to increment the level.
   */
  public static void incLevel()
  {
    level++;
  }

  /**
   * If we are out of a set of statements then call this to decrement the level.
   */
  public static void decLevel()
  {
    level--;
  }

  /**
   * Increment an internal instruction index.  This is not fixed to the array
   * of instructions.
   */
  public static void incInstructionIndex()
  {
    instructionIndex++;
  }

  /**
   * Decrement an internal instruction index.  This is not fixed to the array
   * of instructions.
   */
  public static short getInstructionIndex()
  {
    return instructionIndex;
  }

  /**
   * Returns the lexer for use by others for the current position in analysis.
   *
   * @return the lexer for use by others for the current position in analysis.
   */
  public static Lexer getLexer()
  {
    return lexer;
  }
}