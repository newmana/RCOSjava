package MessageSystem.Messages.Universal;

import Software.Terminal.TerminalManager;
import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Animator.Process.ProcessManagerAnimator;
import Software.Process.RCOSProcess;
import Software.Process.ProcessScheduler;
import MessageSystem.PostOffices.OS.OSMessageHandler;

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
   * Executes the getTerminal method passing the stored process id..
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