package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalOff;

/**
 * Called by the Process Scheduler to indicate that the process has complete and
 * that the terminal should be released for reuse.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st January 1998
 */
public class TerminalRelease extends OSMessageAdapter
{
  private String terminalId;

  public TerminalRelease(OSMessageHandler theSource,
    String newTerminalId)
  {
    super(theSource);
    terminalId = newTerminalId;
  }

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
