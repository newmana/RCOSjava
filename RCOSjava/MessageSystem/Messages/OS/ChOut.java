//******************************************************/
// FILE     : ChOutMessage.java
// PURPOSE  : 
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 24/03/96   Created
//******************************************************/

package MessageSystem.Messages.OS;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Terminal.SoftwareTerminal;
import Software.Kernel.Kernel;

public class ChOut extends OSMessageAdapter
{
  private String sTerminalID;
  private char cCh;

  public ChOut(OSMessageHandler theSource, 
    String sNewTerminalID, char cNewCh)
  {
    super(theSource);
    sTerminalID = sNewTerminalID;
    cCh = cNewCh;
  }
  
  public void setTerminalID(String sNewTerminalID)
  {
    sTerminalID = sNewTerminalID;
  }

  public void setCharacter(char cNewCh)
  {
    cCh = cNewCh;
  }

  public void doMessage(SoftwareTerminal theElement)
  {
    if (theElement.getID().compareTo(sTerminalID) == 0)
      theElement.chOut(cCh);
  }
}

