//*******************************************************************/
// FILE     : TerminalnterruptHandler.java
// PURPOSE  : Interrupt handler for terminal input
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 29/03/96  Created. DJ
//            03/03/98  Tidied up and cleaned up. AN
//*******************************************************************/

package Software.Interrupt;

import MessageSystem.Messages.OS.KeyPress;
import MessageSystem.PostOffices.OS.OSOffice;
import java.io.Serializable;

public class TerminalInterruptHandler extends InterruptHandler
{
  public TerminalInterruptHandler(String sID, OSOffice postOffice,
    String newType)
  {
    super(sID, postOffice, newType);
  }

  public void handleInterrupt()
  {
    // perform any necesary clean up procedures so next
    // Interrupt of this type can occur
    // THERE IS NONE
    // send a message to the appropriate DeviceDriver
    KeyPress aMessage = new KeyPress(this, null);
    sendMessage(aMessage);
  }
}
