package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Called by the Process Scheduler when a process has stopped executing and
 * a new process is moved from the Ready Queue to be executed.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1998
 */
public class ProcessSwitch extends UniversalMessageAdapter
{
  private RCOSProcess process;

  public ProcessSwitch(OSMessageHandler theSource, RCOSProcess newProcess)
  {
    super(theSource);
    process = newProcess;
  }

  /**
   * Set a new value for the process.
   *
   * @param newProcess the new value of the process.
   */
  public void setProcess(RCOSProcess newProcess)
  {
    process = newProcess;
  }

  /**
   * Call the Kernel's switchProcess with the stored process and set the current
   * process ticks.
   *
   * @param theElement the kernel to do the work on.
   */
  public void doMessage(Kernel theElement)
  {
    theElement.switchProcess(process);
    theElement.setCurrentProcessTicks();
  }

  /**
   * Call the scheduler's readyToCPU to display the CPU moving from the ready
   * queue to the CPU queue.
   *
   * @param theElement the process scheduler animator to do the work on.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.readyToCPU(process.getPID());
  }
}

