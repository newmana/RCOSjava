//******************************************************/
// FILE     : ChOutMessage.java
// PURPOSE  :
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.os;


import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.software.kernel.Kernel;

public class ChOut extends OSMessageAdapter
{
  private String terminalId;
  private char ch;

  public ChOut(OSMessageHandler newSource,
    String newTerminalId, char newCh)
  {
    super(newSource);
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

