// ****************************************************
// FILE:        ShrmWriteMessage.java
// PURPOSE:     Shared Memory request structure for WRITE (shr).
// AUTHOR:	Bruce Jamieson
// HISTORY:     30/03/96 Completed.
//

package net.sourceforge.rcosjava.messaging.messages.universal;

public class ShrmWrite
{
  //Used by MMU

  public int PID;
  public int ShrmID;
  public int Offset;
  public short Byte;

  public ShrmWrite(int aid, int aShrmID, int aOffset, short aByte)
  {
    PID = aid;
    ShrmID = aShrmID;
    Offset = aOffset;
    Byte = aByte;
  }
}

