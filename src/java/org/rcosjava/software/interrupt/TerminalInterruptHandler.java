package org.rcosjava.software.interrupt;

import org.rcosjava.software.terminal.SoftwareTerminal;

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
   * The handler.
   */
  private SoftwareTerminal handler;

  /**
   * Create a new interrupt handler.
   *
   * @param newHandler the handler.
   * @param newType the type of the interrupt (unique).
   */
  public TerminalInterruptHandler(SoftwareTerminal newHandler, String newType)
  {
    super(newType);
    handler = newHandler;
  }

  /**
   * Send a key press message when this event occurs.
   */
  public void handleInterrupt()
  {
    handler.keyPress();
  }
}
