// ************************************************************************
// FILE:     SemaphoreOpenMessage.java
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

public class SemaphoreOpen extends OSMessageAdapter
{
  private String sSemaphoreID;
  private int iPID;

  public SemaphoreOpen(OSMessageHandler theSource,
    String sNewSemaphoreID, int iNewPID)
  {
    super(theSource);
    sSemaphoreID = sNewSemaphoreID;
    iPID = iNewPID;
  }
  
  public void setSempahoreID(String sNewSemaphoreID)
  {
    sSemaphoreID = sNewSemaphoreID;
  }
  
  public void setProcessID(int iNewPID)
  {
    iPID = iNewPID;
  }
  
  public void doMessage(IPC theElement)
  {
    theElement.semaphoreOpen(sSemaphoreID, iPID);
  }
}
