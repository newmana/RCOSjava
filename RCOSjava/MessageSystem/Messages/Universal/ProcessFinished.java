//******************************************************/
// FILE     : ProcessFinishedMessage.java
// PURPOSE  : What to do when the process finishes.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Animator.Process.ProcessManagerAnimator;
import Hardware.Memory.Memory;
import Software.Kernel.Kernel;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Process.ProcessScheduler;
import Software.Process.RCOSProcess;

public class ProcessFinished extends UniversalMessageAdapter
{
  private RCOSProcess rpOldCurrent;

  public ProcessFinished(OSMessageHandler theSource, RCOSProcess oldProcess)
  {
    super(theSource);
    setProcess(oldProcess);
  }

  public void setProcess(RCOSProcess rpNewProcess)
  {
    rpOldCurrent = rpNewProcess;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.processFinished(rpOldCurrent);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.processFinished(rpOldCurrent.getPID());
  }

  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.deleteProcess(new Integer(rpOldCurrent.getPID()));
  }
}

