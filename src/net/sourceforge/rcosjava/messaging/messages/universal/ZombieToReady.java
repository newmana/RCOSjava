package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;

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