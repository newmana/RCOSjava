package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Process moves from blocked to ready.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st January 1998
 */
public class BlockedToReady extends UniversalMessageAdapter
{
  private int pid;

  public BlockedToReady(OSMessageHandler theSource,
    int processId)
  {
    super(theSource);
    pid = processId;
  }

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