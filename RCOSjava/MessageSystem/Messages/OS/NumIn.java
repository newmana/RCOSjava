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
  private String sTerminalID;

  public NumIn(OSMessageHandler theSource, 
    String sNewTerminalID)
  {
    super(theSource);
    sTerminalID = sNewTerminalID;
  }  
  
  public void setTerminalID(String sNewTerminalID)
  {
    sTerminalID = sNewTerminalID;
  }
  
  public void doMessage(SoftwareTerminal theElement)
  {
    if (theElement.getID().compareTo(sTerminalID) == 0)    
      theElement.numIn();
  }
}

