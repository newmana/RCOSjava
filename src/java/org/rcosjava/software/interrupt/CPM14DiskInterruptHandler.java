package org.rcosjava.software.interrupt;

import java.io.Serializable;
import org.rcosjava.software.disk.cpm14.CPM14DiskScheduler;

/**
 * Interrupt for CPM14 Disk.
 * <P>
 * @author Andrew Newman
 * @author Brett Carter
 * @created 25 March 1996
 * @version 1.00 $Date$
 */
public class CPM14DiskInterruptHandler extends InterruptHandler
{
  /**
   * Call this when the interrupt occurs.
   */
  private CPM14DiskScheduler handler;

  /**
   * Create a new interrupt handler.
   *
   * @param newHandler the handler.
   * @param newType the type of the interrupt (unique).
   */
  public CPM14DiskInterruptHandler(CPM14DiskScheduler newHandler, String newType)
  {
    super(newType);
    handler = newHandler;
  }

  /**
   * Description of the Method
   */
  public void handleInterrupt()
  {
    // Clean up for next interupt??

    // Send message.
//    MessageAdapter aMessage = new MessageAdapter(getId(), theDestination, "DiskRequestComplete",
//                                     null);
//    SendMessage(aMessage);
  }

}
