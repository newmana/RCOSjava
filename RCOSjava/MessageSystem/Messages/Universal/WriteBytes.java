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
import Software.Memory.MemoryManager;
import MessageSystem.PostOffices.OS.OSMessageHandler;

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

