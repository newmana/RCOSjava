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
   * The identifier of this interrupt.
   */
  public static final String myType = "CodeFinished";

  /**
   * The handler.
   */
  private Kernel handler;

  /**
   * Create a new interrupt handler.
   *
   * @param newHandler the handler.
   */
  public ProcessFinishedInterruptHandler(Kernel newHandler)
  {
    super(myType);
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
