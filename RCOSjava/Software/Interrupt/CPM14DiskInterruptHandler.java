//*************************************************************************//
// FILENAME : CPM14DiskInterruptHandler.java
// PACKAGE  : Interrupt
// PURPOSE  : 
// AUTHOR   : Brett Carter
// HISTORY  : 25/3/96 Created.
//*************************************************************************//

package Software.Interrupt;

import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.Messages.MessageAdapter;

public class CPM14DiskInterruptHandler extends InterruptHandler
{
  public CPM14DiskInterruptHandler( String id, OSOffice PO, String type,
                           String destination)
  {
    super(id, PO, type, destination);
  }


  public void handleInterrupt()
  {
    // Clean up for next interupt??

    // Send message.
//    MessageAdapter aMessage = new MessageAdapter(getID(), theDestination, "DiskRequestComplete",
//                                     null);
//    SendMessage(aMessage);
  }

}
