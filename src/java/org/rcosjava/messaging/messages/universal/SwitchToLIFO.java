package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.process.ProcessScheduler;

/**
 * Switch the process scheduler to use a LIFO type queue.
 * <P>
 * @author Andrew Newman.
 * @created 14th April 2001
 * @version 1.00 $Date$
 */
public class SwitchToLIFO extends UniversalMessageAdapter
{
  /**
   * Create a switch message from the Animator to the process scheduler.
   *
   * @param theSource the sender of the message
   */
  public SwitchToLIFO(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Executes process scheduler's swithToLIFOQueue.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.switchToLIFOQueue();
  }
}