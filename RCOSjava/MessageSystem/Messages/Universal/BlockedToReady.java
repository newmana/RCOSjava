//******************************************************/
// FILE     : BlockedToReadyMessage.java
// PURPOSE  : Process moves from blocked to ready.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//******************************************************/

package MessageSystem.Messages.Universal;

import Hardware.Memory.Memory;
import Software.Process.ProcessScheduler;
import Software.Process.RCOSProcess;
import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Terminal.SoftwareTerminal;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class BlockedToReady extends UniversalMessageAdapter
{
  private int iPID;

  public BlockedToReady(OSMessageHandler theSource,
    int iProcessID)
  {
    super(theSource);
    iPID = iProcessID;
  }

  public void setValues(int iProcessID)
  {
    iPID = iProcessID;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.blockedToReady(iPID);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.blockedToReady(iPID);
  }
}