// ****************************************************
// FILE:        ShrmInitMessage.java
// PURPOSE:     Shared Memory request structure for CREATE / OPEN.
// AUTHOR:      Bruce Jamieson
// HISTORY:     30/03/96        Completed.                     
//

package MessageSystem.Messages.OS;

import MessageSystem.* ;
import java.lang.String;
import java.util.Hashtable;

public class ShrmInit
{
  public String ShrmID;
  public int PID;
  public int Size;

  public ShrmInit(String aID, int aPID, int aSize)
  {
    ShrmID = aID;
    PID = aPID;
    Size = aSize;
  }
}
