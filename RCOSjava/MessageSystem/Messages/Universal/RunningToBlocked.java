//*******************************************************************/
// FILE     : RunningToBlockedMessage.java
// PURPOSE  : Process moves from running (CPU) to
//            blocked.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//*******************************************************************/

package MessageSystem.Messages.Universal;

import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Animator.Process.ProcessSchedulerFrame;
import Software.Process.ProcessScheduler;
import Software.Process.RCOSProcess;
import Software.Kernel.Kernel;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class RunningToBlocked extends UniversalMessageAdapter
{
  private RCOSProcess myProcess;

  public RunningToBlocked(OSMessageHandler theSource, RCOSProcess newProcess)
  {
    super(theSource);
    myProcess = newProcess;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.runningToBlocked(myProcess);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.cpuToBlocked(myProcess.getPID());
  }
}

