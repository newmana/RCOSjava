package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.process.ProcessScheduler;

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
   * Description of the Field
   */
  private int pid;

  /**
   * Constructor for the BlockedToReady object
   *
   * @param theSource Description of Parameter
   * @param processId Description of Parameter
   */
  public BlockedToReady(OSMessageHandler theSource,
      int processId)
  {
    super(theSource);
    pid = processId;
  }

  /**
   * Sets the Values attribute of the BlockedToReady object
   *
   * @param processId The new Values value
   */
  public void setValues(int processId)
  {
    pid = processId;
  }

  /**
   * Calls blockedToReady on the Process Scheduler.
   *
   * @param theElement the Process Scheduler to do the work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.blockedToReady(pid);
  }

  /**
   * Calls blockedToReady on the Process Scheduler Animator.
   *
   * @param theElement the Process Scheduler Animator to do the work on.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.blockedToReady(pid);
  }
}
