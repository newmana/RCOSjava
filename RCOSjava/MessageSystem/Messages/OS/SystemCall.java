//***************************************************************************
// FILE    : SystemCallMessage.java
// PACKAGE :
// PURPOSE :
// AUTHOR  : Andrew Newman
// MODIFIED:
// HISTORY : 28/03/96  Created
//
//
//***************************************************************************

package MessageSystem.Messages.OS;

import Software.Kernel.Kernel;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class SystemCall extends OSMessageAdapter
{
  public SystemCall(OSMessageHandler theElement)
  {
    super(theElement);
  }

  public void doMessage(Kernel theElement)
  {
    try
    {
      theElement.handleSystemCall();
    }
    catch (Exception e)
    {
      System.err.println("System call error: " + e);
      e.printStackTrace();
    }
  }
}

