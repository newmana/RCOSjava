//**************************************************************************/
// FILE     : ProgManInterruptHandler.java
// PACKAGE  : Interrupt
// PURPOSE  : Interrupt handler for program manager
// AUTHOR   :	David Jones
// MODIFIED : Andrew Newman
// HISTORY  :	23/03/95	Created
//            1/7/98    Should've been calling new message not NewProcess. 
//                      
//
//**************************************************************************/

package Software.Interrupt;

import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.Messages.OS.GetNewFile;

public class ProgManInterruptHandler extends InterruptHandler
{
  public ProgManInterruptHandler(String myId, OSOffice PostOffice, 
                                 String type, String destination)
  {
    super(myId, PostOffice, type, destination);
  }

  public void handleInterrupt()
  {
    // perform any necesary clean up procedures so next
    // Interrupt of this type can occur
    // THERE IS NONE
    // send a message to the appropriate DeviceDriver
    GetNewFile aMessage = new GetNewFile(this);
    sendMessage(aMessage);
  }
}
 