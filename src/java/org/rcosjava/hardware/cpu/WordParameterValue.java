package org.rcosjava.hardware.cpu;

import java.io.Serializable;

/**
 * This is a simple interface to group both Operator and SystemCall together as
 * one type.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 09/07/2001 Created </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 9th of July 2001
 * @see org.rcosjava.hardware.cpu.Instruction
 * @see org.rcosjava.hardware.cpu.CPU
 * @version 1.00 $Date$
 */
public class WordParameterValue extends WordParameter
{
  /**
   * Description of the Field
   */
  private short wordParameterValue;

  /**
   * Constructor for the WordParameterValue object
   *
   * @param newWordParameterValue Description of Parameter
   */
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

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public String toString()
  {
    return Short.toString(wordParameterValue);
  }
}
