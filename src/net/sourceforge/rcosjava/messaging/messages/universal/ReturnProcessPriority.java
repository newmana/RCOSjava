package net.sourceforge.rcosjava.messaging.messages.universal;


import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Sent from Process Scheduler to return a process' priority.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 29th April 2001
 */
public class ReturnProcessPriority extends UniversalMessageAdapter
{
  private int processId;
  private int priority;

  public ReturnProcessPriority(OSMessageHandler theSource,
    int newProcessId, int newPriority)
  {
    super(theSource);
    processId = newProcessId;
    priority = newPriority;
  }

  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.returnProcessPriority(processId, priority);
  }
}

