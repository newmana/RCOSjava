//***************************************************************************
// FILE    : ReturnValueMessage.java
// PACKAGE : 
// PURPOSE : 
// AUTHOR  : Andrew Newman
// MODIFIED: 
// HISTORY : 28/03/96  Created
//           
//
//***************************************************************************

package MessageSystem.Messages.OS;

import Software.Kernel.Kernel;
import Software.Terminal.SoftwareTerminal;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class ReturnValue extends OSMessageAdapter
{
  private short sReturnValue;
  
  public ReturnValue(OSMessageHandler theSource,
    short sNewReturnValue)
  {
    super(theSource);
    setReturnValue(sNewReturnValue);
  }
  
  public void setReturnValue(short sNewReturnValue)
  {
    sReturnValue = sNewReturnValue;
  }

  public void doMessage(Kernel theElement)
  {
    theElement.returnValue(sReturnValue);
  }
}

