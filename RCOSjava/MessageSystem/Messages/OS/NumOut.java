//******************************************************/
// FILE     : NumOutMessage.java
// PURPOSE  :
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package MessageSystem.Messages.OS;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Terminal.SoftwareTerminal;
import Software.Kernel.Kernel;

public class NumOut extends OSMessageAdapter
{
  private String terminalId;
  private short num;

  public NumOut(OSMessageHandler theSource,
    String newTerminalId, short newNum)
  {
    super(theSource);
    terminalId = newTerminalId;
    num = newNum;
  }

  public void setTerminalID(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  public void setNewNum(short newNum)
  {
    num = newNum;
  }

  public void doMessage(SoftwareTerminal theElement)
  {
    if (theElement.getId().compareTo(terminalId) == 0)
      theElement.numOut((byte) num);
  }
}

