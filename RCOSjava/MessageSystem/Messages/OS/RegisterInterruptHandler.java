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

package MessageSystem.Messages.OS;

import Software.Interrupt.InterruptHandler;
import Software.Kernel.Kernel;
import Software.Process.ProgramManager;
import Software.Terminal.SoftwareTerminal;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class RegisterInterruptHandler extends OSMessageAdapter
{
  private InterruptHandler ihHandler;

  public RegisterInterruptHandler(OSMessageHandler theSource, 
    InterruptHandler ihNewHandler)
  {
    super(theSource);
    ihHandler = ihNewHandler;
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

