//*******************************************************************/
// FILE     : ProcessToTerminalAllocationMessage.java
// PURPOSE  : Used to assign a terminal to the next
//            waiting process.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//*******************************************************************/

package MessageSystem.Messages.Universal;

import Hardware.Memory.Memory;
import Software.Process.ProcessScheduler;
import Software.Process.RCOSProcess;
import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Terminal.TerminalManager;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class ProcessToTerminalAllocation extends UniversalMessageAdapter
{
  private String sTerminalID;
  private int iPID;

  public ProcessToTerminalAllocation(OSMessageHandler theSource, String sTerminalID, int iPID)
  {
    super(theSource);
    this.sTerminalID = sTerminalID;
    this.iPID = iPID;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.processAllocatedTerminal(iPID, sTerminalID);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.zombieToReady(iPID);
  }
}

