package MessageSystem.Messages.Universal;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Process.RCOSProcess;
import Software.Process.ProcessScheduler;
import Software.Animator.Process.ProcessSchedulerAnimator;

/**
 * Send when a new process has been created and is blocked waiting for a
 * terminal.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st January 1998
 */
public class ZombieToReady extends UniversalMessageAdapter
{
  RCOSProcess zombieProcess;

  public ZombieToReady(OSMessageHandler newSource,
    RCOSProcess newZombieProcess)
  {
    super(newSource);
    zombieProcess = newZombieProcess;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.zombieToReady(zombieProcess);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.zombieToReady(zombieProcess.getPID());
  }

  public boolean undoableMessage()
  {
    return false;
  }
}