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
   * Description of the Field
   */
  private RCOSProcess process;

  /**
   * Constructor for the ProcessSwitch object
   *
   * @param theSource Description of Parameter
   * @param newProcess Description of Parameter
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
    theElement.readyToCPU(process.getPID());
  }
}

