//******************************************************/
// FILE     : ReadPageMessage.java
// PURPOSE  : Read a page of memory.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 03/08/97   Created
//          
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Memory.MemoryRequest;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Memory.MemoryManager;
import Software.Kernel.Kernel;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class ReadPage extends UniversalMessageAdapter
{
  private MemoryRequest mrRequest;  
  
  public ReadPage(OSMessageHandler theSource, 
    MemoryRequest mrNewRequest)
  {
    super(theSource);
    mrRequest = mrNewRequest;
  }
  
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.readingMemory(mrRequest);
  }
  
  public void doMessage(MemoryManager theElement)
  {
    mrRequest.setMemory(theElement.readPage(mrRequest.getPID(), 
      mrRequest.getMemoryType(), mrRequest.getOffset()));
    FinishedMemoryRead aMessage = new FinishedMemoryRead(theElement, mrRequest);
    theElement.sendMessage(aMessage);
  }
}

