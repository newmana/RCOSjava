package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;

/**
 * Called when a process has been allocated a terminal.
 * <P>
 * @author Andrew Newman.
 * @created 28th March 1998
 * @version 1.00 $Date$
 */
public class ProcessToTerminalAllocation extends UniversalMessageAdapter
{
  /**
   * The id allocated to the terminal.
   */
  private String terminalID;

  /**
   * The process being allocated the terminal.
   */
  private RCOSProcess process;

  /**
   * Construct a new ProcessToTerminalAllocation message.
   *
   * @param theSource the sender of the message.
   * @param newTerminalID the terminal that's been allocated to the process.
   * @param newProcess the process that's been allocated a terminal.
   */
  public ProcessToTerminalAllocation(OSMessageHandler theSource,
      String newTerminalID, RCOSProcess newProcess)
  {
    super(theSource);
    terminalID = newTerminalID;
    process = newProcess;
  }

  /**
   * Call processAllocatedTerminal on the process scheduler.
   *
   * @param theElement the Process Scheduler to call.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.processAllocatedTerminal(process, terminalID);
  }

  /**
   * Call zombieToReady on the process scheduler animator.
   *
   * @param theElement the Process Scheduler Animator to call.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.zombieToReady(process);
  }
}