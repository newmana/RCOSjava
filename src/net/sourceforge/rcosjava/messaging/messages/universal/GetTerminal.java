package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Sent when the a process requires a terminal.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1996
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
   * @param newSource the source of the message.
   * @param newPID the process id that is looking for a terminal.
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
   */
  public boolean undoableMessage()
  {
    return false;
  }
}