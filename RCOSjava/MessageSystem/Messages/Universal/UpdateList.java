//***************************************************************************
// FILE    : UpdateListMessage.java
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
import Software.Animator.Process.ProgramManagerAnimator;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;

public class UpdateList extends UniversalMessageAdapter
{
  private String sDirectory;

  public UpdateList(AnimatorMessageHandler theSource, 
    String sNewDirectory)
  {
    super(theSource);
    sDirectory = sNewDirectory;
  }
  
  public void setDirectory(String sNewDirectory)
  {
    sDirectory = sNewDirectory;
  }
  
  public void doMessage(ProgramManager theElement)
  {
    theElement.updateList(sDirectory);
  }
}

