package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;

/**
 * Switch the process scheduler to use a LIFO type queue.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 14th April 2001
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
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.switchToLIFOQueue();
  }
}

