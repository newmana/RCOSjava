//******************************************************/
// FILE     : WritePageMessage.java
// PURPOSE  : Write a page of memory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 03/08/97   Created
//
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

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

