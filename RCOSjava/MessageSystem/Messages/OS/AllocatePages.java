//******************************************************/
// FILE     : AllocatePagesMessage.java
// PURPOSE  : 
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 24/03/98   Created
//******************************************************/

package MessageSystem.Messages.OS;

import Software.Memory.MemoryRequest;
import Software.Memory.MemoryManager;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Process.ProcessScheduler;

public class AllocatePages extends OSMessageAdapter
{
  private MemoryRequest mrRequest;

  public AllocatePages(OSMessageHandler theSource, 
    MemoryRequest mrNewRequest)
  {
    super(theSource);
    mrRequest = mrNewRequest;
  }
  
  public void setMemoryRequest(MemoryRequest mrNewRequest)
  {
    mrRequest = mrNewRequest;
  }

  public void doMessage(MemoryManager theElement)
  {
    theElement.allocatePages(mrRequest);
  }
}

