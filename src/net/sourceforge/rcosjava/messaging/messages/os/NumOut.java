//******************************************************/
// FILE     : NumOutMessage.java
// PURPOSE  :
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.software.kernel.Kernel;

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

