// ************************************************************************
// FILE:     SemaphoreCreateMessage.java
// PURPOSE:  Message sent to IPC manager for OPEN/CREATE semaphore
//           type messages.
// AUTHOR:   Bruce Jamieson
// MODIFIED: Andrew Newman
// HISTORY:  28/03/96 Completed.
//           08/08/98 Changed to new message system. AN
//
// ************************************************************************
 
package MessageSystem.Messages.OS;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.IPC.IPC;
import Software.Kernel.Kernel;

public class SemaphoreCreate extends OSMessageAdapter
{
  private String sSemaphoreID;
  private int iPID;
  private int iValue;

  public SemaphoreCreate(OSMessageHandler theSource,
    String sNewSemaphoreID, int iNewPID, int iNewValue)
  {
    super(theSource);
    sSemaphoreID = sNewSemaphoreID;
    iPID = iNewPID;
    iValue = iNewValue;
  }
  
  public void setSemaphoreID(String sNewSemaphoreID)
  {
    sSemaphoreID = sNewSemaphoreID;
  }
  
  public void setProcessID(int iNewProcessID)
  {
    iPID = iNewProcessID;
  }
  
  public void setSemaphoreValue(int iNewValue)
  {
    iValue = iNewValue;
  }
  
  public void doMessage(IPC theElement)
  {
    theElement.semaphoreCreate(sSemaphoreID, iPID, iValue);
  }
}
