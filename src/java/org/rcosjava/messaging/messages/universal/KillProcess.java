package org.rcosjava.messaging.messages.universal;

import org.rcosjava.hardware.cpu.Interrupt;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.interrupt.ProcessFinishedInterruptHandler;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;

/**
 * Sent by the user (control-C on the terminal) or by the process manager to
 * kill a given process with a given process Id. Currently, no security is in
 * place - everyone is root!
 * <P>
 * @author Andrew Newman.
 * @created 24th of March 1996
 * @version 1.00 $Date$
 */
public class KillProcess extends UniversalMessageAdapter
{
  /**
   * The process to kill.
   */
  private RCOSProcess process;

  /**
   * Construct a new KillProcess message.
   *
   * @param theSource the sender of the message.
   * @param newProcess the process to kill.
   */
  public KillProcess(OSMessageHandler theSource, RCOSProcess newProcess)
  {
    super(theSource);
    process = newProcess;
  }

  /**
   * Sets a new process to kill.
   *
   * @param newProcess a new process to kill.
   */
  public void setProcess(RCOSProcess newProcess)
  {
    process = newProcess;
  }

  /**
   * Call kill process on the process scheduler.
   *
   * @param theElement the Process Scheduler to call kill on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.killProcess(process);
  }

  /**
   * Call kill process on the process scheduler animator.
   *
   * @param theElement the Process Scheduler Animator to call kill on.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.killProcess(process);
  }

  /**
   * Call deleteProcess on the process manager animator.
   *
   * @param theElement the Process Manager Animator to call delete on.
   */
  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.deleteProcess(process);
  }

  /**
   * Generate an interrupt that the process has finished if it's currently
   * running in the Kernel.
   *
   * @param theElement the Kernel to create the process finished interrupt.
   */
  public void doMessage(Kernel theElement)
  {
    if (theElement.getCurrentProcessPID() == process.getPID())
    {
      Interrupt processFinishedInterrupt = new Interrupt(-1,
          ProcessFinishedInterruptHandler.myType);

      theElement.handleInterrupt(processFinishedInterrupt);
      //theElement.killProcess();
    }
  }
}