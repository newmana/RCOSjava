// ****************************************************
// FILE:        ShrmInitMessage.java
// PURPOSE:     Shared Memory request structure for CREATE / OPEN.
// AUTHOR:      Bruce Jamieson
// HISTORY:     30/03/96        Completed.
//

package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.* ;

public class ShrmInit
{
  public String shrmID;
  public int PID;
  public int size;

  public ShrmInit(String newID, int newPID, int newSize)
  {
    shrmID = newID;
    PID = newPID;
    size = newSize;
  }
}
