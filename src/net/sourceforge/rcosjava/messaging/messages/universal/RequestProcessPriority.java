package net.sourceforge.rcosjava.messaging.messages.universal;


import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Sent from Program Manager Animator to return an a processes priority.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th April 2001
 */
public class RequestProcessPriority extends UniversalMessageAdapter
{
  private int processId;

  public RequestProcessPriority(AnimatorMessageHandler theSource,
    int newProcessId)
  {
    super(theSource);
    processId = newProcessId;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.requestProcessPriority(processId);
  }
}

