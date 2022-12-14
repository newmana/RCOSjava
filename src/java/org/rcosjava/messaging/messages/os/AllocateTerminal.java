package org.rcosjava.messaging.messages.os;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.terminal.TerminalManager;

/**
 * When a process is successfully attached to a terminal.
 * <P>
 * @author Andrew Newman.
 * @created 24th March 1998
 * @version 1.00 $Date$
 */
public class AllocateTerminal extends OSMessageAdapter
{
  /**
   * The process id to allocate the terminal with.
   */
  private int processId;

  /**
   * Create a new allocate terminal message.
   *
   * @param newSource the source of the message.
   * @param newProcessId Description of Parameter
   */
  public AllocateTerminal(OSMessageHandler newSource, int newProcessId)
  {
    super(newSource);
    processId = newProcessId;
  }

  /**
   * Executes the allocateTerminal method passing the stored process id.
   *
   * @param theElement the terminal manager to do work on.
   */
  public void doMessage(TerminalManager theElement)
  {
    theElement.allocateTerminal(processId);
  }
}