package org.rcosjava.software.interrupt;

import java.io.Serializable;
import org.rcosjava.software.kernel.Kernel;

/**
 * Interrupt handler for a processes time of the CPU expiring.
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 29th of March 1996
 * @version 1.00 $Date$
 */
public class TimerInterruptHandler extends InterruptHandler
{
  /**
   * Handler object.
   */
  private Kernel handler;

  /**
   * Create a new interrupt handler.
   *
   * @param newHandler the handler.
   * @param newType the type of the interrupt (unique).
   */
  public TimerInterruptHandler(Kernel newHandler, String newType)
  {
    super(newType);
    handler = newHandler;
  }

  /**
   * Send a quantum expired message.
   */
  public void handleInterrupt()
  {
    handler.handleTimer();
  }
}
