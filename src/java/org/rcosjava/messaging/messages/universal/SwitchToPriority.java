package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.process.ProcessScheduler;

/**
 * Switch the process scheduler to use a Priority type queue.
 * <P>
 * @author Andrew Newman.
 * @created 14th April 2001
 * @version 1.00 $Date$
 */
public class SwitchToPriority extends UniversalMessageAdapter
{
  /**
   * Create a switch message from the Animator to the process scheduler.
   *
   * @param theSource the sender of the message
   */
  public SwitchToPriority(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Executes process scheduler's swithToPriorityQueue.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.switchToPriorityQueue();
  }
}