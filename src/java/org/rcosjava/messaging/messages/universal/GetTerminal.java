package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.terminal.TerminalManager;

/**
 * Sent when the a process requires a terminal.
 * <P>
 * @author Andrew Newman.
 * @created 28th March 1996
 * @version 1.00 $Date$
 */
public class GetTerminal extends UniversalMessageAdapter
{
  /**
   * The process id that is looking for a terminal.
   */
  private int PID;

  /**
   * Create a new get terminal message.
   *
   * @param newPID the process id that is looking for a terminal.
   * @param theSource Description of Parameter
   */
  public GetTerminal(OSMessageHandler theSource, int newPID)
  {
    super(theSource);
    PID = newPID;
  }

  /**
   * Executes the getTerminal method passing the stored process id.
   *
   * @param theElement the terminal manager to do work on.
   */
  public void doMessage(TerminalManager theElement)
  {
    theElement.getTerminal(PID);
  }

  /**
   * Set to false so it is not recorded.
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return false;
  }
}
