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
  private int iPID;

  public GetTerminal(OSMessageHandler theSource, int iProcessID)
  {
    super(theSource);
    iPID = iProcessID;
  }

  public void doMessage(TerminalManager theElement)
  {
    theElement.getTerminal(iPID);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.newProcess(iPID);
  }

  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.newProcess(new Integer(iPID));
  }
}

