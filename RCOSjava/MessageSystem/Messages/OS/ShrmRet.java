
// ****************************************************
// FILE:        ShrmRetMessage.java
// PURPOSE:     Shared Memory (general) return structure.         
// AUTHOR:      Bruce Jamieson
// HISTORY:     30/03/96        Completed.
//
 

package MessageSystem.Messages.OS;

import MessageSystem.* ;
import java.lang.String;
import java.util.Hashtable;

public class ShrmRet
{
  public int ShrmID;
  public int PID;
  public int Offset;
  public short Byte;
  public int Size;


  public ShrmRet(int aID, int aPID, short aByte, int aOffset)
  {
    ShrmID = aID;
    PID = aPID;
    Byte = aByte;
    Offset = aOffset;    
  }

  public ShrmRet(int aID, int aPID, int aSize)
  {
    ShrmID = aID;
    PID = aPID;
    Size = aSize;
  }
 


}
