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
  private String terminalId;
  private char ch;

  public ChOut(OSMessageHandler theSource,
    String newTerminalId, char newCh)
  {
    super(theSource);
    terminalId = newTerminalId;
    ch = newCh;
  }

  public void setTerminalID(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  public void setCharacter(char newCh)
  {
    ch = newCh;
  }

  public void doMessage(SoftwareTerminal theElement)
  {
    if (theElement.getId().compareTo(terminalId) == 0)
      theElement.chOut(ch);
  }
}

