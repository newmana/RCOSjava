//******************************************************/
// FILE     : WriteBytesMessage.java
// PURPOSE  : Write a series of bytes of memory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 03/08/97   Created
//
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Memory.MemoryRequest;
import Software.Animator.IPC.IPCManagerAnimator;
import MessageSystem.Messages.Universal.FinishedMemoryRead;
import MessageSystem.Messages.Universal.FinishedMemoryWrite;
import Software.Memory.MemoryManager;
import Software.Kernel.Kernel;
import Software.Process.ProcessScheduler;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class WriteBytes extends UniversalMessageAdapter
{
  private MemoryRequest mrRequest;

  public WriteBytes(OSMessageHandler theSource,
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
    theElement.writeBytes(mrRequest.getPID(), mrRequest.getMemoryType(),
      mrRequest.getSize(),
      mrRequest.getOffset(), mrRequest.getMemory());
    FinishedMemoryWrite aMessage = new FinishedMemoryWrite(theElement, mrRequest);
    theElement.sendMessage(aMessage);
  }
}

