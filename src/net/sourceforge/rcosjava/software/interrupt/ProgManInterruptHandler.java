package net.sourceforge.rcosjava.software.interrupt;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.messages.os.GetNewFile;
import java.io.Serializable;

/**
 * Interrupt handler for program manager
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 1/7/1998 Should've been calling new message not NewProcess.
 * </DD></DT>
 * <P>
 * @author Andrew Newman
 * @author David Jones
 * @created 23 March 1996
 * @version 1.00 $Date$
 */
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
    GetNewFile message = new GetNewFile(this);
    sendMessage(message);
  }
}
