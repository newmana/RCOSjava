package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.process.RCOSProcess;

/**
 * Called by the Process Scheduler when a process has stopped executing and a
 * new process is moved from the Ready Queue to be executed.
 * <P>
 * @author Andrew Newman.
 * @created 28th March 1998
 * @version 1.00 $Date$
 */
public class ProcessSwitch extends UniversalMessageAdapter
{
  /**
   * The process being switched.
   */
  private RCOSProcess process;

  /**
   * Construct a new ProcessSwitch message.
   *
   * @param theSource the sender of the message.
   * @param newProcess the process being switched.
   */
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
    theElement.readyToCPU(process);
  }
}

