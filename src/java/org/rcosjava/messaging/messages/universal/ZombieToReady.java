package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;

/**
 * Send when a new process has been created and is blocked waiting for a
 * terminal.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1998
 * @version 1.00 $Date$
 */
public class ZombieToReady extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  RCOSProcess zombieProcess;

  /**
   * Constructor for the ZombieToReady object
   *
   * @param newSource Description of Parameter
   * @param newZombieProcess Description of Parameter
   */
  public ZombieToReady(OSMessageHandler newSource,
      RCOSProcess newZombieProcess)
  {
    super(newSource);
    zombieProcess = newZombieProcess;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.zombieToReady(zombieProcess);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.zombieToReady(zombieProcess);
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return false;
  }
}
