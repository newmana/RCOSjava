//***************************************************************************
// FILE    : TerminalReleaseMessage.java
// PACKAGE : Terminal
// PURPOSE : 
// AUTHOR  : Andrew Newman
// MODIFIED: 
// HISTORY : 28/03/96  Created
//           
//
//***************************************************************************

package MessageSystem.Messages.OS;

import Software.Terminal.TerminalManager;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class TerminalRelease extends OSMessageAdapter
{
  private String sTerminalID;

  public TerminalRelease(OSMessageHandler theSource, 
    String sNewTerminalID)
  {
    super(theSource);
    sTerminalID = sNewTerminalID;
  }
  
  public void setValues(String sNewTerminalID)
  {
    sTerminalID = sNewTerminalID;
  }
  
  public void doMessage(TerminalManager theElement)
  {
    theElement.removeTerminal(sTerminalID);
  }
}

