// ****************************************************
// FILE:        ShrmReadMessage.java
// PURPOSE:     Shared Memory request structure for READ.
// AUTHOR:      Bruce Jamieson
// HISTORY:     30/03/96        Completed.
//

package net.sourceforge.rcosjava.messaging.messages.os;

public class ShrmRead
{
  //Used by MMU

  public int PID;
  public int ShrmID;
  public int Offset;

  public ShrmRead(int aid, int aShrmID, int aOffset)
  {
    PID = aid;
    ShrmID = aShrmID;
    Offset = aOffset;
  }
}

