//***************************************************************************
// FILE    : GetTerminalMessage.java
// PACKAGE : Terminal
// PURPOSE : 
// AUTHOR  : Andrew Newman
// MODIFIED: 
// HISTORY : 28/03/96  Created
//           
//
//***************************************************************************

package MessageSystem.Messages.Universal;

import Software.Terminal.TerminalManager;
import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Animator.Process.ProcessManagerAnimator;
import Software.Process.RCOSProcess;
import Software.Process.ProcessScheduler;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class GetTerminal extends UniversalMessageAdapter
{
  private int iPID;

  public GetTerminal(OSMessageHandler theSource, int iProcessID)
  {
    super(theSource);
    iPID = iProcessID;
  }
  
  public void doMessage(TerminalManager theElement)
  {
    theElement.getTerminal(iPID);
  }
  
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.newProcess(iPID);
  }
  
  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.newProcess(new Integer(iPID));
  }
}

