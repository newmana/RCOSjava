//*******************************************************************/
// FILE     : KillProcessMessage.java
// PURPOSE  : Kill a process.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//*******************************************************************/

package MessageSystem.Messages.Universal;

import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Animator.Process.ProcessManagerAnimator;
import Hardware.Memory.Memory;
import Software.Kernel.Kernel;
import Software.Process.ProcessScheduler;
import Software.Process.ProgramManager;
import Software.Process.RCOSProcess;
import Software.Terminal.SoftwareTerminal;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class KillProcess  extends UniversalMessageAdapter
{
  private int iPID;
  private boolean bHandleInterrupt = false;

  public KillProcess(OSMessageHandler theSource, int iNewPID, boolean bNewHandleInterrupt)
  {
    super(theSource);
    iPID = iNewPID;
    bHandleInterrupt = bNewHandleInterrupt;
  }

  public void setProcessID(int iNewPID)
  {
    iPID = iNewPID;
  }

  public void doMessage(ProcessScheduler theElement)
  {
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
     theElement.killProcess(iPID);
  }

  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.deleteProcess(new Integer(iPID));
  }

  public void doMessage(Kernel theElement)
  {
    if (bHandleInterrupt)
      theElement.handleProcessFinishedInterrupt();
  }
}

