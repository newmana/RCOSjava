//******************************************************/
// FILE     : NumInMessage.java
// PURPOSE  :
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package MessageSystem.Messages.OS;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Terminal.SoftwareTerminal;
import Software.Kernel.Kernel;

public class NumIn extends OSMessageAdapter
{
  private String terminalId;

  public NumIn(OSMessageHandler theSource,
    String newTerminalId)
  {
    super(theSource);
    terminalId = newTerminalId;
  }

  public void setTerminalID(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  public void doMessage(SoftwareTerminal theElement)
  {
    if (theElement.getId().compareTo(terminalId) == 0)
      theElement.numIn();
  }
}

