package net.sourceforge.rcosjava.hardware.cpu;

import java.io.Serializable;

/**
 * This is a simple interface to group both Operator and SystemCall together
 * as one type.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 09/07/2001 Created
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.hardware.cpu.Instruction
 * @see net.sourceforge.rcosjava.hardware.cpu.CPU
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 9th of July 2001
 */
public class WordParameter implements Serializable
{
  public static final WordParameter ILLEGAL = new WordParameter();

  public short getValue()
  {
    return -1;
  }

  public String toString()
  {
    return "ILLEGAL_WORD_PARAMETER";
  }
}