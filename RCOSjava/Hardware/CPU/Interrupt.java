package Hardware.CPU;

import java.io.Serializable;

/**
 * Used by CPU to maintain a queue of Interrupts and when they should occur.
 *
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 28th March 1997
 */
public class Interrupt implements Serializable
{
  private int iTime;
  private String sType;

  public Interrupt(int aTime, String aType)
  {
    iTime = aTime;
    sType = aType;
  }

  public int getTime()
  {
    return iTime;
  }

  public String getType()
  {
    return sType;
  }

  public void setTime(int aTime)
  {
    iTime = aTime;
  }

  public void setType(String aType)
  {
    sType = aType;
  }
}