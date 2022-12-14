package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.terminal.TerminalManager;

/**
 * Called by the Process Scheduler to indicate that the process has complete and
 * that the terminal should be released for reuse.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1998
 * @version 1.00 $Date$
 */
public class TerminalRelease extends OSMessageAdapter
{
  /**
   * Description of the Field
   */
  private String terminalId;

  /**
   * Constructor for the TerminalRelease object
   *
   * @param theSource Description of Parameter
   * @param newTerminalId Description of Parameter
   */
  public TerminalRelease(OSMessageHandler theSource,
      String newTerminalId)
  {
    super(theSource);
    terminalId = newTerminalId;
  }

  /**
   * Sets the Values attribute of the TerminalRelease object
   *
   * @param newTerminalId The new Values value
   */
  public void setValues(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  /**
   * Calls removeTerminal on the Terminal Manager with the stored terminal id.
   *
   * @param theElement the Terminal Manager to do the work on.
   */
  public void doMessage(TerminalManager theElement)
  {
    theElement.releaseTerminal(terminalId);
  }
}
