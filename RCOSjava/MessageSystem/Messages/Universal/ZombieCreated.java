package MessageSystem.Messages.Universal;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Process.RCOSProcess;
import Software.Process.ProcessScheduler;
import Software.Animator.Process.ProcessSchedulerAnimator;

/**
 * Send when the a process is first created and doesn't have a terminal
 * allocated to it.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd April 2001
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
   * Executes the Zombie Created method.
   *
   * @param theElement the process scheduler to do work on.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.zombieCreated(zombieProcess.getPID());
  }
}