//***************************************************************************
// FILE    : KeyMessage.java
// PACKAGE : 
// PURPOSE : 
// AUTHOR  : Andrew Newman
// MODIFIED: 
// HISTORY : 28/03/96  Created
//           
//
//***************************************************************************

package MessageSystem.Messages.OS;

import Hardware.CPU.Interrupt;
import Software.Interrupt.TerminalInterruptHandler;
import Software.Terminal.SoftwareTerminal;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class KeyPress extends OSMessageAdapter
{
  private Interrupt intInterrupt;
  
  public KeyPress(OSMessageHandler theSource, 
    Interrupt intNewInterrupt)
  {
    super(theSource);
    intInterrupt = intNewInterrupt;
  }   

  private void setInterrupt(Interrupt intNewInterrupt)
  {
    intInterrupt = intNewInterrupt;
  }
  
  public void doMessage(SoftwareTerminal theElement)
  {
    theElement.keyPress();
  }
}

