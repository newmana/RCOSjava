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
public class WordParameter implements Serializable
{
  /**
   * Description of the Field
   */
  public final static WordParameter ILLEGAL = new WordParameter();

  /**
   * Gets the Value attribute of the WordParameter object
   *
   * @return The Value value
   */
  public short getValue()
  {
    return -1;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public String toString()
  {
    return "ILLEGAL_WORD_PARAMETER";
  }
}
