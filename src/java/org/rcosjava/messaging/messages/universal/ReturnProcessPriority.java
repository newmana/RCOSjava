package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;

/**
 * Sent from Process Scheduler to return a process' priority.
 * <P>
 * @author Andrew Newman.
 * @created 29th April 2001
 * @version 1.00 $Date$
 */
public class ReturnProcessPriority extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private int processId;
  /**
   * Description of the Field
   */
  private int priority;

  /**
   * Constructor for the ReturnProcessPriority object
   *
   * @param theSource Description of Parameter
   * @param newProcessId Description of Parameter
   * @param newPriority Description of Parameter
   */
  public ReturnProcessPriority(OSMessageHandler theSource,
      int newProcessId, int newPriority)
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
  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.returnProcessPriority(processId, priority);
  }
}

