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
  private String sTerminalID;

  public ChIn(OSMessageHandler theSource, String sNewTerminalID)
  {
    super(theSource);
    setTerminalID(sNewTerminalID);
  }

  public void setTerminalID(String sNewTerminalID)
  {
    sTerminalID = sNewTerminalID;
  }

  public void doMessage (SoftwareTerminal theElement)
  {
    if (theElement.getID().compareTo(sTerminalID) == 0)
      theElement.chIn();
  }
}

