package net.sourceforge.rcosjava.compiler;

import net.sourceforge.rcosjava.hardware.cpu.Instruction;
import java.io.*;
import java.net.*;

/**
 * Provides a simple compiler/decompiler of Pcode.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
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
      eof = inputStream.read(pcodeInstruction);

      System.out.println("Begin dissassembly:");
      while (eof != -1)
      {
        Instruction theInstruction = new Instruction((
          pcodeInstruction[0] & 0xff),
          (pcodeInstruction[4]),
          (short) ((pcodeInstruction[5] << 8) + (pcodeInstruction[6])) );
        System.out.println(theInstruction);
        eof = inputStream.read(pcodeInstruction);
      }
    }
    catch (java.io.IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  public static void compile(String theFileName)
  {
  }
}