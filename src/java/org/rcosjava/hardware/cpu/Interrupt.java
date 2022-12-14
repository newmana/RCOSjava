package org.rcosjava.hardware.cpu;

import java.io.Serializable;

/**
 * Used by CPU to maintain a queue of Interrupts and when they should occur.
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 28th March 1997
 * @see org.rcosjava.hardware.cpu.CPU
 * @version 1.00 $Date$
 */
public class Interrupt implements Serializable
{
  /**
   * The CPU tick at which the interupt occurred.
   */
  private int time;

  /**
   * The string representation of the interrupt.
   */
  private String type;

  /**
   * Create a new interrupt based on the given tick and type.
   *
   * @param newTime the time in CPU ticks in which the interrupt is to occur or
   *      ocurred.
   * @param newType the string representation of the interrupt.
   */
  public Interrupt(int newTime, String newType)
  {
    time = newTime;
    type = newType;
  }

  /**
   * Simple set method to set the value of time.
   *
   * @param newTime The new Time value
   */
  public void setTime(int newTime)
  {
    time = newTime;
  }

  /**
   * Simple set method to set the value of type.
   *
   * @param newType The new Type value
   */
  public void setType(String newType)
  {
    type = newType;
  }

  /**
   * Return the time. No checking is assumed about what the current tick of the
   * CPU is.
   *
   * @return The Time value
   */
  public int getTime()
  {
    return time;
  }

  /**
   * Returned the stored string identication of the interrupt.
   *
   * @return The Type value
   */
  public String getType()
  {
    return type;
  }
}
