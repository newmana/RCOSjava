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

package net.sourceforge.rcosjava.software.interrupt;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.messages.os.GetNewFile;
import java.io.Serializable;

public class ProgManInterruptHandler extends InterruptHandler
{
  public ProgManInterruptHandler(String myId, OSOffice postOffice,
    String newType)
  {
    super(myId, postOffice, newType);
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