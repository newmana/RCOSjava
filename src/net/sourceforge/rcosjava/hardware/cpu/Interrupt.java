package net.sourceforge.rcosjava.hardware.cpu;

import java.io.Serializable;

/**
 * Used by CPU to maintain a queue of Interrupts and when they should occur.
 * <P>
 * @see net.sourceforge.rcosjava.hardware.cpu.CPU
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 28th March 1997
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
   *   ocurred.
   * @param newType the string representation of the interrupt.
   */
  public Interrupt(int newTime, String newType)
  {
    time = newTime;
    type = newType;
  }

  /**
   * Return the time.  No checking is assumed about what the current tick of
   * the CPU is.
   */
  public int getTime()
  {
    return time;
  }

  /**
   * Returned the stored string identication of the interrupt.
   */
  public String getType()
  {
    return type;
  }

  /**
   * Simple set method to set the value of time.
   */
  public void setTime(int newTime)
  {
    time = newTime;
  }

  /**
   * Simple set method to set the value of type.
   */
  public void setType(String newType)
  {
    type = newType;
  }
}