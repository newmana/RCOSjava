//***************************************************************************
// FILE    : Kill.java
// PACKAGE : 
// PURPOSE : Sent from Program Manager Animator to kill a selected
//           process running.  Not to be confused with (although
//           linked to) KillProcessMessage.
// AUTHOR  : Andrew Newman
// MODIFIED: 
// HISTORY : 28/03/96  Created
//           
//
//***************************************************************************

package MessageSystem.Messages.Universal;

import Software.Process.ProgramManager;
import Software.Animator.Process.ProcessManagerAnimator;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;

public class Kill extends UniversalMessageAdapter
{
  private int iPID;

  public Kill(AnimatorMessageHandler theSource, int iNewPID)
  {
    super(theSource);
    iPID = iNewPID;
  }
  
  private void setProcessID(int iNewPID)
  {
    iPID = iNewPID;
  }
  
  public void doMessage(ProgramManager theElement)
  {
    theElement.killProgram(iPID);
  }
}

