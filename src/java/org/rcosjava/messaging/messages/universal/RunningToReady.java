package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.cpu.CPUAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;

/**
 * Process is being moved from being executed on the CPU to ready.
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
public class RunningToReady extends UniversalMessageAdapter
{
  /**
   * The process moving from running to ready.
   */
  private RCOSProcess process;

  /**
   * Constructor for the RunningToReady message.
   *
   * @param newSource who sent the message.
   * @param newProcess the process moving from running to blocked.
   */
  public RunningToReady(OSMessageHandler theSource, RCOSProcess newProcess)
  {
    super(theSource);
    process = newProcess;
  }

  /**
   * Calls runningToReady on the Process Scheduler.
   *
   * @param theElement the object to call.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.runningToReady(process);
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
   * Calls cpuToReady on the ProcessSchedulerAnimator.
   *
   * @param theElement the object to call.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.cpuToReady(process);
  }
}
