//*****************************************************************************/
// FILE     : ChInMessage.java
// PURPOSE  :
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/98   Created
//*****************************************************************************/

package MessageSystem.Messages.OS;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Terminal.SoftwareTerminal;
import Software.Kernel.Kernel;

public class ChIn extends OSMessageAdapter
{
  private String terminalId;

  public ChIn(OSMessageHandler theSource, String newTerminalID)
  {
    super(theSource);
    setTerminalId(newTerminalID);
  }

  public void setTerminalId(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  public void doMessage (SoftwareTerminal theElement)
  {
    if (theElement.getId().compareTo(terminalId) == 0)
      theElement.chIn();
  }
}

