package MessageSystem.Messages.OS;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Terminal.TerminalManager;

/**
 * When a process is successfully attached to a terminal.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1998
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
   * @param newPID the process id that is looking for a terminal.
   */
  public AllocateTerminal(OSMessageHandler newSource,
    int newProcessId)
  {
    super(newSource);
    processId = newProcessId;
  }

  /**
   * Executes the allocateTerminal method passing the stored process id..
   *
   * @param theElement the terminal manager to do work on.
   */
  public void doMessage(TerminalManager theElement)
  {
    theElement.allocateTerminal(processId);
  }
}

