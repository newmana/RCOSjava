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
      decompile(theFileName);
    else if (theSwitch.compareToIgnoreCase("-c") == 0)
      compile(theFileName);
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
  public static void decompile(String theFileName)
  {
    try
    {
      DataInputStream inputStream = new DataInputStream(
        new FileInputStream(theFileName));

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
        Instruction theInstruction = new Instruction((
          pcodeInstruction[0] & 0xff),
          (pcodeInstruction[4]),
          (short) ((pcodeInstruction[5] << 8) + (pcodeInstruction[6])) );

        System.out.println(format.format(offset) + ": " + theInstruction);
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
   * stdout.
   *
   * @param theFileName the filename to compile into pcode.
   */
  public static void compile(String theFileName)
  {
    String instructionLine = new String();

    try
    {
      BufferedReader inputStream = new BufferedReader(
        new InputStreamReader(new FileInputStream(theFileName)));

      byte pcodeInstruction[] = new byte[8];
      instructionLine = inputStream.readLine();

      while (instructionLine != null)
      {
        Instruction theInstruction = new Instruction(instructionLine);
        System.out.write(theInstruction.toByte(),0,8);
        instructionLine = inputStream.readLine();
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