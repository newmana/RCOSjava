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
      // Try and retrieve the next process in the ZombieQ          
      RCOSProcess rpNewProc = (RCOSProcess) 
        theElement.removeFromZombieCreatedQ(iPID);
        
      // If a process is retrieve allocate terminal and move
      // to Ready Q.
      if (rpNewProc != null)
      {
        rpNewProc.setTerminalID(sTerminalID);
        rpNewProc.setStatus(RCOSProcess.READY); 
        theElement.insertIntoReadyQ(rpNewProc);
      }
      
      // If there is no current process running schedule
      // process to be run.
      if (theElement.getCurrentProcess() == null)
        theElement.schedule();
  }
  
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.zombieToReady(iPID);
  }
}

