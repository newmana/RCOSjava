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

package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

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

