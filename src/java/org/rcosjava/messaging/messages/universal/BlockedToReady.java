package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;

/**
 * Process moves from blocked to ready.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1998
 * @version 1.00 $Date$
 */
public class BlockedToReady extends UniversalMessageAdapter
{
  /**
   * The process moving from the blocked queue to the ready queue.
   */
  private RCOSProcess process;

  /**
   * Constructor a new BlockedToReady message.
   *
   * @param theSource the sender of the message.
   * @param newProcess the process moving from blocked to ready.
   */
  public BlockedToReady(OSMessageHandler theSource, RCOSProcess newProcess)
  {
    super(theSource);
    process = newProcess;
  }

  /**
   * Sets the process.
   *
   * @param newProcess the new process.
   */
  public void setProcess(RCOSProcess newProcess)
  {
    process = newProcess;
  }

  /**
   * Calls blockedToReady on the Process Scheduler.
   *
   * @param theElement the Process Scheduler to do the work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.blockedToReady(process);
  }

  /**
   * Calls blockedToReady on the Process Scheduler Animator.
   *
   * @param theElement the Process Scheduler Animator to do the work on.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.blockedToReady(process);
  }
}
