package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.cpu.CPUAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;

/**
 * Process is being moved from being executed on the CPU to blocked (usually
 * waiting for I/O).
 * <P>
 * <DT> <B>History:</B>
 * <DD> 24/03/96 Created </DD>
 * <DD> 01/07/96 Uses Memory </DD>
 * <DD> 03/08/97 Moved to message system </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 24th of March 1996
 * @version 1.00 $Date$
 */
public class RunningToBlocked extends UniversalMessageAdapter
{
  /**
   * The process moving from running to blocked.
   */
  private RCOSProcess process;

  /**
   * Constructor for the RunningToBlocked message.
   *
   * @param newSource who sent the message.
   * @param newProcess the process moving from running to blocked.
   */
  public RunningToBlocked(OSMessageHandler theSource, RCOSProcess newProcess)
  {
    super(theSource);
    process = newProcess;
  }

  /**
   * Calls screenReset on the CPU Animator. Resets all the pointers, stack and
   * code execution to their default values.
   *
   * @param theElement the CPU Animator to do the work on.
   */
  public void doMessage(CPUAnimator theElement)
  {
    theElement.screenReset();
  }

  /**
   * Calls runningToBlocked on the Process Scheduler.
   *
   * @param theElement the object to call.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.runningToBlocked(process);
  }

  /**
   * Calls cpuToBlocked on the ProcessSchedulerAnimator.
   *
   * @param theElement the object to call.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.cpuToBlocked(process);
  }
}
