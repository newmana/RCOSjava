package org.rcosjava.software.interrupt;
import java.io.Serializable;
import org.rcosjava.messaging.messages.os.KeyPress;
import org.rcosjava.messaging.postoffices.os.OSOffice;

/**
 * Interrupt handler for terminal input.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 03/03/98 Tidied up and cleaned up. AN </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 29th of March 1996
 * @version 1.00 $Date$
 */
public class TerminalInterruptHandler extends InterruptHandler
{
  /**
   * Constructor for the TerminalInterruptHandler object
   *
   * @param id Description of Parameter
   * @param postOffice Description of Parameter
   * @param newType Description of Parameter
   */
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
