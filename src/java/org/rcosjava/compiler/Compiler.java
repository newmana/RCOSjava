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
  private static short instructionIndex = 0;
  private static SymbolTable table;
  private static Lexer lexer;
  private static Parser parser;

  public static void main(String args[])
  {
    if(args.length != 2)
    {
      System.out.println("usage:");
      System.out.println("  Compiler sourceFile outputFile");
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

      // Add jump to main function after the first INT (global vars).
      addInstruction(1, OpCode.OPERATION.getValue(), (byte) 0,
        Operator.RETURN.getValue());
      addInstruction(1, OpCode.CALL.getValue(), (byte) 0,
        (short) (stmtCompiler.mainPosition()+1));

      // Print out result to file
      Iterator tmpIter = instructions.iterator();
      StringBuffer tmpBuffer = new StringBuffer();
      while (tmpIter.hasNext())
      {
        tmpBuffer.append((String) tmpIter.next() + "\n");
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
   * @param instruction the index to the instruction to add.
   * @param byteParam the byte value of the instruction.
   * @param wordParam the word value of the instruction.
   */
  protected static void addInstruction(int index, int instruction,
      byte byteParam, short wordParam)
  {
    Instruction tmpInstruction = Instruction.INSTRUCTIONS[instruction];
    tmpInstruction.setByteParameter(byteParam);
    tmpInstruction.setWordParameter(wordParam);
    instructions.add(index, tmpInstruction.toString());
  }

  /**
   * Add an instruction into the FIFO array of instructions.
   *
   * @oaram instruction the instruction to add.
   * @param byteParam the byte value of the instruction.
   * @param wordParam the word value of the instruction.
   */
  public static void addInstruction(int instruction, byte byteParam,
      short wordParam)
  {
    Instruction tmpInstruction = Instruction.INSTRUCTIONS[instruction];
    tmpInstruction.setByteParameter(byteParam);
    tmpInstruction.setWordParameter(wordParam);
    instructions.add(tmpInstruction.toString());
  }

  /**
   * Returns the current level (how many nested statements i.e. curly braces)
   * we are currently in.
   *
   * @return the current level (how many nested statements i.e. curly braces)
   * we are currently in.
   */
  public static short getLevel()
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
   * Gets the instruction index.  This is not fixed to the array
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