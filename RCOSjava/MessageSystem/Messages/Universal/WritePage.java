//******************************************************/
// FILE     : WritePageMessage.java
// PURPOSE  : Write a page of memory.
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

public class WritePage extends UniversalMessageAdapter
{
  private MemoryRequest mrRequest;

  public WritePage(OSMessageHandler theSource,
    MemoryRequest mrNewRequest)
  {
    super(theSource);
    mrRequest = mrNewRequest;
  }

  public void setMemoryRequest(MemoryRequest mrNewRequest)
  {
    mrRequest = mrNewRequest;
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.writingMemory(mrRequest);
  }

  public void doMessage(MemoryManager theElement)
  {
    theElement.writePage(mrRequest.getPID(), mrRequest.getMemoryType(),
      mrRequest.getOffset(), mrRequest.getMemory());
    FinishedMemoryWrite aMessage = new FinishedMemoryWrite(theElement, mrRequest);
    theElement.sendMessage(aMessage);
  }
}

