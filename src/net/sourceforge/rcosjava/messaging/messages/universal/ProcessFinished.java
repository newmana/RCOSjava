package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.RCOSProcess;

/**
 * What to do when the process finishes.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st January 1998
 */
public class ProcessFinished extends UniversalMessageAdapter
{
  private RCOSProcess finishedProcess;

  public ProcessFinished(OSMessageHandler theSource,
    RCOSProcess newFinishedProcess)
  {
    super(theSource);
    finishedProcess = newFinishedProcess;
  }

  public void setProcess(RCOSProcess newFinishedProcess)
  {
    finishedProcess = newFinishedProcess;
  }

  /**
   * Calls finished process on the Process Scheduler releasing the terminal
   * and deallocating the memory.
   *
   * @param theElement the Process Scheduler to do the work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.processFinished(finishedProcess);
  }

  /**
   * Indicate to the Animator that the process is finished.  Calls
   * processFinished on the Process Scheduler Animator.
   *
   * @param theElement the Process Scheduler Animator to do the work on.
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.processFinished(finishedProcess.getPID());
  }

  /**
   * Indicate to the Animator that the process is finished and that it shouldn't
   * be displayed any more as running.  Calls deleteProcess on the Animator.
   *
   * @param theElement the Process Manager Animator to do the work on.
   */
  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.deleteProcess(new Integer(finishedProcess.getPID()));
  }
}

