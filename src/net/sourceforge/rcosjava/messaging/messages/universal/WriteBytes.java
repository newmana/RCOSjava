//******************************************************/
// FILE     : WriteBytesMessage.java
// PURPOSE  : Write a series of bytes of memory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 03/08/97   Created
//
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class WriteBytes extends UniversalMessageAdapter
{
  private MemoryRequest request;

  public WriteBytes(OSMessageHandler theSource,
    MemoryRequest newRequest)
  {
    super(theSource);
    request = newRequest;
  }

  public void setMemoryRequest(MemoryRequest newRequest)
  {
    request = newRequest;
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.writingMemory(request);
  }

  public void doMessage(MemoryManager theElement)
  {
    theElement.writeBytes(request);
  }
}

