package org.rcosjava.messaging.messages.universal;

import org.rcosjava.hardware.cpu.Interrupt;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.process.ProcessScheduler;

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
   * Description of the Field
   */
  private int pid;

  /**
   * Constructor for the KillProcess object
   *
   * @param theSource Description of Parameter
   * @param newPID Description of Parameter
   */
  public KillProcess(OSMessageHandler theSource, int newPID)
  {
    super(theSource);
    pid = newPID;
  }

  /**
   * Sets the ProcessID attribute of the KillProcess object
   *
   * @param newPID The new ProcessID value
   */
  public void setProcessID(int newPID)
  {
    pid = newPID;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.killProcess(pid);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.killProcess(pid);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.deleteProcess(pid);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(Kernel theElement)
  {
    if (theElement.getCurrentProcessPID() == pid)
    {
      Interrupt processFinishedInterrupt = new Interrupt(-1,
          "ProcessFinished");

      theElement.generateInterrupt(processFinishedInterrupt);
      //theElement.killProcess();
    }
  }
}

