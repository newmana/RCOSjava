// ************************************************************************
// FILE:     SemaphoreSignalMessage.java
// PURPOSE:  Message sent to IPC manager for Close semaphore
//           type messages.
// AUTHOR:   Andrew Newman
// MODIFIED: 
// HISTORY:  11/08/98 Completed. AN
//
// ************************************************************************
 
package MessageSystem.Messages.OS;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.IPC.IPC;
import Software.Kernel.Kernel;

public class SemaphoreSignal extends OSMessageAdapter
{
  private int iSemaphoreID;
  private int iPID;

  public SemaphoreSignal(OSMessageHandler theSource,
    int iNewSemaphoreID, int iNewPID)
  {
    super(theSource);
    iSemaphoreID = iNewSemaphoreID;
    iPID = iNewPID;
  }
  
  public void setSemaphoreID(int iNewSemaphoreID)
  {
    iSemaphoreID = iNewSemaphoreID;
  }
  
  public void setProcessID(int iNewPID)
  {
    iPID = iNewPID;
  }
  
  public void doMessage(IPC theElement)
  {
    theElement.sempahoreSignal(iSemaphoreID, iPID);
  }
}
