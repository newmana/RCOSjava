//***************************************************************************
// FILE    : RunMessage.java
// PACKAGE : 
// PURPOSE : 
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

public class Run extends UniversalMessageAdapter
{
  public Run(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  public void doMessage(ProgramManager theElement)
  {
    theElement.startThread();
  }
}

