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
import java.io.Serializable;

public class CPM14DiskInterruptHandler extends InterruptHandler
  implements Serializable
{
  public CPM14DiskInterruptHandler(String id, OSOffice postOffice,
    String newType)
  {
    super(id, postOffice, newType);
  }


  public void handleInterrupt()
  {
    // Clean up for next interupt??

    // Send message.
//    MessageAdapter aMessage = new MessageAdapter(getId(), theDestination, "DiskRequestComplete",
//                                     null);
//    SendMessage(aMessage);
  }

}
