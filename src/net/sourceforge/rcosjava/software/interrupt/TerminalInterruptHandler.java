/**
 * Interrupt handler for terminal input
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 03/03/98  Tidied up and cleaned up. AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 29th of March 1996
 */
package net.sourceforge.rcosjava.software.interrupt;

import net.sourceforge.rcosjava.messaging.messages.os.KeyPress;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
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
