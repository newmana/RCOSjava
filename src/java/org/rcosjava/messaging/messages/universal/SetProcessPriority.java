package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.ProcessPriority;

/**
 * Sets the priority of a given process.
 * <P>
 * @author Andrew Newman.
 * @created 8th of May 2001
 * @version 1.00 $Date$
 */
public class SetProcessPriority extends UniversalMessageAdapter
{
  /**
   * The process id to change the priority.
   */
  private int processId;

  /**
   * The priority to change the process id to.
   */
  private ProcessPriority priority;

  /**
   * Create a new SetProcessPriority message.
   *
   * @param theSource who is sending the message.
   * @param newProcessId the process id to change the priority of.
   * @param newPriority the new priority value.
   */
  public SetProcessPriority(AnimatorMessageHandler theSource,
      int newProcessId, ProcessPriority newPriority)
  {
    super(theSource);
    processId = newProcessId;
    priority = newPriority;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.setProcessPriority(processId, priority);
  }
}

