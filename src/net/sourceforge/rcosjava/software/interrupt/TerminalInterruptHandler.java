
package net.sourceforge.rcosjava.software.interrupt;

import net.sourceforge.rcosjava.messaging.messages.os.KeyPress;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import java.io.Serializable;

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
public class TerminalInterruptHandler extends InterruptHandler
{
  public TerminalInterruptHandler(String id, OSOffice postOffice,
    String newType)
  {
    super(id, postOffice, newType);
  }

  /**
   * Send a key press message when this event occurs.
   */
  public void handleInterrupt()
  {
    KeyPress message = new KeyPress(this, null);
    sendMessage(message);
  }
}
