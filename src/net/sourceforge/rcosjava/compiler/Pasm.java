package net.sourceforge.rcosjava.compiler;

import net.sourceforge.rcosjava.hardware.cpu.Instruction;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;

/**
 * Provides a simple compiler/decompiler of pcode.  Prints out pcodes or
 * parsers pcodes (to produce the binary format), for example:<BR/>
 * 00000: JMP 0, 38<BR/>
 * 00001: JMP 0, 2<BR/>
 * <P/>
 * <DT><B>Usage Example:</B><DD>
 * <CODE>
 *      pasm -d tmp.pcd > result.pcode
 *      pasm -c result.pcode > tmp.pcd
 * </CODE>
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 25th May 2001
 */
public class Pasm
{
  /**
   * The main method.  Expects a switch of either -d or -c for decompile and
   * compile followed by a filename.
   */
  public static void main(String args[])
  {
    if (args.length != 2)
      printUsageMessage();

    String theSwitch = args[0];
    String theFileName = args[1];

    if (theSwitch.compareToIgnoreCase("-d") == 0)
      decompileFileToStdOut(theFileName, true);
    else if (theSwitch.compareToIgnoreCase("-c") == 0)
      compileFileToStdOut(theFileName, true);
    else
      printUsageMessage();
  }

  /**
   * Print out the error message to the user.
   */
  public static void printUsageMessage()
  {
    System.err.println(" Correct usage is : Pasm -d/-c filename");
    System.err.println(" Where:");
    System.err.println("     -d decompile file (convert from pcode to text)");
    System.err.println("     -c compile file (convert from text to pcode)");
    System.exit(0);
  }

  /**
   * Takes a file, reads instruction by instruction and prints it out to
   * standard output.
   */
  public static void decompileFileToStdOut(String theFileName,
      boolean printOffset)
  {
    try
    {
      FileInputStream input = new FileInputStream(theFileName);
      OutputStream output = System.out;
      decompile(input, output, printOffset);
    }
    catch (java.io.IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  public static void decompile(InputStream input, OutputStream output,
      boolean printOffset)
  {
    try
    {
      DataInputStream inputStream = new DataInputStream(input);

      byte pcodeInstruction[] = new byte[8];
      int eof;
      int offset = 0;
      eof = inputStream.read(pcodeInstruction);

      DecimalFormat format = new DecimalFormat();
      format.setMaximumIntegerDigits(5);
      format.setMinimumIntegerDigits(5);
      format.setGroupingUsed(false);

      while (eof != -1)
      {
        byte instr1 = pcodeInstruction[5];
        byte instr2 = pcodeInstruction[6];
        short loc = (short) ((256*(instr1 & 255)) + (instr2 & 255));
        Instruction theInstruction = new Instruction((
          pcodeInstruction[0] & 0xff), (pcodeInstruction[4]), loc);

        if (printOffset)
        {
          String tmpOffsetStr = format.format(offset) + ": ";
          output.write(tmpOffsetStr.getBytes());
        }
        String instruction = theInstruction + "\n";
        output.write(instruction.getBytes());
        offset++;
        eof = inputStream.read(pcodeInstruction);
      }
    }
    catch (java.io.IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  /**
   * Takes a file consisting of the pcode mnemonics.  Displays the result to
   * stdout.  The mnemonics have an offset of 7 (the first 7 being offset).
   * see decompileFileToStdOut method.
   *
   * @param theFileName the filename to compile into pcode.
   */
  public static void compileFileToStdOut(String theFileName, boolean hasOffset)
  {
    try
    {
      FileInputStream input = new FileInputStream(theFileName);
      OutputStream output = System.out;
      if (hasOffset)
      {
        compile(input, output, 7);
      }
      else
      {
        compile(input, output, 0);
      }
    }
    catch (java.io.IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  /**
   * Compile the given input stream to produce assembly instructions.
   *
   * @param input the input stream consisting of the pcode mnemonics
   * @param output the output stream to write the pcode binary
   * @param inputLineOffset the offset to start parsing for the pcodes.
   */
  public static void compile(InputStream input, OutputStream output,
      int inputLineOffset)
  {
    String instructionLine = new String();
    try
    {
      BufferedReader inputBuffer =
          new BufferedReader(new InputStreamReader(input));
      byte pcodeInstruction[] = new byte[8];
      instructionLine = inputBuffer.readLine();

      while (instructionLine != null)
      {
        instructionLine = instructionLine.substring(inputLineOffset);
        Instruction theInstruction = new Instruction(instructionLine);
        output.write(theInstruction.toByte(), 0, 8);
        instructionLine = inputBuffer.readLine();
      }
    }
    catch (java.lang.StringIndexOutOfBoundsException sioobe)
    {
      System.out.println("Line: " + instructionLine);
      sioobe.printStackTrace();
    }
    catch (java.io.IOException ioe)
    {
      ioe.printStackTrace();
    }
  }
}