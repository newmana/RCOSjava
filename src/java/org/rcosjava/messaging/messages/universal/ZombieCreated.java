package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;

/**
 * Send when the a process is first created and doesn't have a terminal
 * allocated to it.
 * <P>
 * @author Andrew Newman.
 * @created 2nd April 2001
 * @version 1.00 $Date$
 */
public class ZombieCreated extends UniversalMessageAdapter
{
  /**
   * The process that has been created.
   */
  private RCOSProcess zombieProcess;

  /**
   * Create a new zombie created process usually sent from Process Scheduler.
   *
   * @param newSource the source of the message.
   * @param newZombieProcess the new process created.
   */
  public ZombieCreated(OSMessageHandler newSource,
      RCOSProcess newZombieProcess)
  {
    super(newSource);
    zombieProcess = newZombieProcess;
  }

  /**
   * Executes the zombieCreated method.
   *
   * @param theElement the process scheduler to do work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.zombieCreated(zombieProcess);
  }

  /**
   * Executes newProcess method on process manager animator.
   *
   * @param theElement the process manager animator to do the work on.
   */
  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.newProcess(zombieProcess.getPID());
  }

  /**
   * Executes the Zombie Created method.
   *
   * @param theElement the process scheduler to do work on.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.zombieCreated(zombieProcess.getPID());
  }
}
