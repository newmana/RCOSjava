package org.rcosjava.software.interrupt;

import java.io.Serializable;
import org.rcosjava.messaging.messages.os.GetNewFile;
import org.rcosjava.messaging.postoffices.os.OSOffice;

/**
 * Interrupt handler for program manager.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 1/7/1998 Should've been calling new message not NewProcess. </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @author David Jones
 * @created 23 March 1996
 * @version 1.00 $Date$
 */
public class ProgManInterruptHandler extends InterruptHandler
{
  /**
   * Create a new interrupt handler.
   *
   * @param newHandler the handler.
   * @param newType the type of the interrupt (unique).
   */
  public ProgManInterruptHandler(Object newHandler, String newType)
  {
    super(newType);
  }

  /**
   * Description of the Method
   */
  public void handleInterrupt()
  {
  }
}
