
// ****************************************************
// FILE:        ShrmRetMessage.java
// PURPOSE:     Shared Memory (general) return structure.
// AUTHOR:      Bruce Jamieson
// HISTORY:     30/03/96        Completed.
//


package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.* ;

public class ShrmRet
{
  public int shrmID;
  public int PID;
  public short value;
  public int offset;
  public int size;

  public ShrmRet(int newID, int newPID, short newValue, int newOffset)
  {
    shrmID = newID;
    PID = newPID;
    value = newValue;
    offset = newOffset;
  }

  public ShrmRet(int newID, int newPID, int newSize)
  {
    shrmID = newID;
    PID = newPID;
    size = newSize;
  }
}
