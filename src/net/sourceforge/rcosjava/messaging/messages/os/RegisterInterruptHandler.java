//***************************************************************************
// FILE    : RegisterInterruptHandlerMessage.java
// PACKAGE : MessageSystem.OS
// PURPOSE :
// AUTHOR  : Andrew Newman
// MODIFIED:
// HISTORY : 28/03/96  Created
//
//
//***************************************************************************

package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.interrupt.InterruptHandler;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import java.io.Serializable;

public class RegisterInterruptHandler extends OSMessageAdapter
{
  private InterruptHandler ihHandler;

  public RegisterInterruptHandler(InterruptHandler theSource)
  {
    super(theSource);
    ihHandler = theSource;
  }

  public void setInterruptHandler(InterruptHandler ihNewHandler)
  {
    ihHandler = ihNewHandler;
  }

  public void doMessage(Kernel theElement)
  {
    theElement.insertInterruptHandler(ihHandler);
  }
}

