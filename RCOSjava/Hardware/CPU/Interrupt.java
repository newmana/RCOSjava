//*******************************************************************/
// FILE     : Interrupt.java
// PURPOSE  : Used by CPU to maintain a queue of Interrupts and
//            when they should occur.
//
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// VERSION  : 1.00
// HISTORY  : 23/03/96  Created
//            24/09/98  Made private and added get/set methods.
//*******************************************************************/

package Hardware.CPU;

import java.io.Serializable;

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