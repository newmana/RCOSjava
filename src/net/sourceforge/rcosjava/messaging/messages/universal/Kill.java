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

package net.sourceforge.rcosjava.messaging.messages.universal;


import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;

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

