//***************************************************************************
// FILE    : GetNewFileMessage.java
// PACKAGE : 
// PURPOSE : 
// AUTHOR  : Andrew Newman
// MODIFIED: 
// HISTORY : 28/03/96  Created
//           
//
//***************************************************************************

package MessageSystem.Messages.OS;

import Software.Process.ProgramManager;
import MessageSystem.Messages.Universal.NewProcess;
import Software.Interrupt.ProgManInterruptHandler;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class GetNewFile extends OSMessageAdapter
{
  public GetNewFile(OSMessageHandler theSource)
  {
    super(theSource);
  }
  
  public void doMessage(ProgramManager theElement)
  {
    theElement.startThread();
  }
}

