//******************************************************/
// FILE     : ReadPageMessage.java
// PURPOSE  : Read a page of memory.
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

