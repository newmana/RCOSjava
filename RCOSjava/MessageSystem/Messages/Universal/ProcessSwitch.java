//***************************************************************************
// FILE    : ProcessSwitchMessage.java
// PACKAGE : 
// PURPOSE : 
// AUTHOR  : Andrew Newman
// MODIFIED: 
// HISTORY : 28/03/96  Created
//           
//
//***************************************************************************

package MessageSystem.Messages.Universal;

import Software.Kernel.Kernel;
import Software.Process.RCOSProcess;
import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Process.ProcessScheduler;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class ProcessSwitch extends UniversalMessageAdapter
{
  private RCOSProcess rpProcess;
  
  public ProcessSwitch(OSMessageHandler theSource, RCOSProcess rpNewProcess)
  {
    super(theSource);
    rpProcess = rpNewProcess;
  }   

  public void setProcess(RCOSProcess rpNewProcess)
  {
    rpProcess = rpNewProcess;
  }

  public void doMessage(Kernel theElement)
  {
    theElement.switchProcess(rpProcess);
    theElement.setCurrentProcessTicks();
  }
  
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.readyToCPU(rpProcess.getPID());
  }
}

