package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;

/**
 * What to do when the process finishes.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1998
 * @version 1.00 $Date$
 */
public class ProcessFinished extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private RCOSProcess finishedProcess;

  /**
   * Constructor for the ProcessFinished object
   *
   * @param theSource Description of Parameter
   * @param newFinishedProcess Description of Parameter
   */
  public ProcessFinished(OSMessageHandler theSource,
      RCOSProcess newFinishedProcess)
  {
    super(theSource);
    finishedProcess = newFinishedProcess;
  }

  /**
   * Sets the Process attribute of the ProcessFinished object
   *
   * @param newFinishedProcess The new Process value
   */
  public void setProcess(RCOSProcess newFinishedProcess)
  {
    finishedProcess = newFinishedProcess;
  }

  /**
   * Calls finished process on the Process Scheduler releasing the terminal and
   * deallocating the memory.
   *
   * @param theElement the Process Scheduler to do the work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.processFinished(finishedProcess);
  }

  /**
   * Indicate to the Animator that the process is finished. Calls
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
   * be displayed any more as running. Calls deleteProcess on the Animator.
   *
   * @param theElement the Process Manager Animator to do the work on.
   */
  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.deleteProcess(new Integer(finishedProcess.getPID()));
  }
}

