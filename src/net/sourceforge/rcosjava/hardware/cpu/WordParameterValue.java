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
public class WordParameterValue extends WordParameter
{
  private short wordParameterValue;

  public WordParameterValue(short newWordParameterValue)
  {
    wordParameterValue = newWordParameterValue;
  }

  /**
   * @return the value of the word parameter
   */
  public short getValue()
  {
    return wordParameterValue;
  }

  public String toString()
  {
    return Short.toString(wordParameterValue);
  }
}