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
  private String sTerminalID;
  private short sNum;

  public NumOut(OSMessageHandler theSource, 
    String sNewTerminalID, short sNewNum)
  {
    super(theSource);
		sTerminalID = sNewTerminalID;
    sNum = sNewNum;
  }  
  
  public void setTerminalID(String sNewTerminalID)
  {
    sTerminalID = sNewTerminalID;
  }

  public void setNewNum(short sNewNum)
  {
    sNum = sNewNum;
  }
  
  public void doMessage(SoftwareTerminal theElement)
  {
    if (theElement.getID().compareTo(sTerminalID) == 0)    
      theElement.numOut((byte) sNum);
  }
}

