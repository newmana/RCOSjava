//******************************************************/
// FILE     : DeallocatePagesMessage.java
// PURPOSE  : 
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 24/03/98   Created
//******************************************************/

package MessageSystem.Messages.OS;

import Software.Memory.MemoryRequest;
import Software.Memory.MemoryManager;
import Software.Process.ProcessScheduler;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class DeallocatePages extends OSMessageAdapter
{
  private int iPID;

  public DeallocatePages(OSMessageHandler theSource, 
    int iNewPID)
  {
    super(theSource);
    iPID = iNewPID;
  }
  
  public void setPID(int iNewPID)
  {
    iPID = iNewPID;
  }

  public void doMessage(MemoryManager theElement)
  {
    theElement.deallocatePages(iPID);
  }
}

