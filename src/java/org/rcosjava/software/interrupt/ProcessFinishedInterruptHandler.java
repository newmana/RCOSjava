package org.rcosjava.software.interrupt;

import org.rcosjava.software.kernel.Kernel;

/**
 * Interrupt handler for when a process has finished executing
 * <P>
 * @author Andrew Newman.
 * @created 29th March 2003
 * @version 1.00 $Date$
 */
public class ProcessFinishedInterruptHandler extends InterruptHandler
{
  /**
   * The handler.
   */
  private Kernel handler;

  /**
   * Create a new interrupt handler.
   *
   * @param newHandler the handler.
   * @param newType the type of the interrupt (unique).
   */
  public ProcessFinishedInterruptHandler(Kernel newHandler, String newType)
  {
    super(newType);
    handler = newHandler;
  }

  /**
   * Send a key press message when this event occurs.
   */
  public void handleInterrupt()
  {
    handler.processFinished();
  }
}
