// ****************************************************
// FILE:        ShrmSizeMessage.java
// PURPOSE:     Shared Memory request structure for SIZE (shr).
// AUTHOR:      Bruce Jamieson
// HISTORY:     30/03/96        Completed.
//

package net.sourceforge.rcosjava.messaging.messages.os;

public class ShrmSize
{
  //Used by MMU

  public int PID;
  public int ShrmID;

  public ShrmSize(int aid, int aShrmID)
  {
    PID = aid;
    ShrmID = aShrmID;
  }

}

