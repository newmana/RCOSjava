//***************************************************************************
// FILE    : ReturnValueMessage.java
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
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class ReturnValue extends OSMessageAdapter
{
  private short sReturnValue;

  public ReturnValue(OSMessageHandler theSource,
    short sNewReturnValue)
  {
    super(theSource);
    setReturnValue(sNewReturnValue);
  }

  public void setReturnValue(short sNewReturnValue)
  {
    sReturnValue = sNewReturnValue;
  }

  public void doMessage(Kernel theElement)
  {
    theElement.returnValue(sReturnValue);
  }
}

