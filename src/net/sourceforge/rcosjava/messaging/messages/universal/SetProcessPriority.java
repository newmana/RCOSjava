package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;

/**
 * Sets the priority of a given process.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 8th of May 2001
 */
public class SetProcessPriority extends UniversalMessageAdapter
{
  private int processId;
  private int priority;

  public SetProcessPriority(AnimatorMessageHandler theSource,
    int newProcessId, int newPriority)
  {
    super(theSource);
    processId = newProcessId;
    priority = newPriority;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.setProcessPriority(processId, priority);
  }
}


