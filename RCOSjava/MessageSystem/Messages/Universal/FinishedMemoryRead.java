//******************************************************/
// FILE     : FinishedMemoryReadMessage.java
// PACKAGE  : MessageSystem.Universal
// PURPOSE  : MMU is responding with requested memory.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 01/01/1998   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Memory.MemoryRequest;
import Software.Kernel.Kernel;
import Software.Animator.CPU.CPUAnimator;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Memory.MemoryManager;

public class FinishedMemoryRead extends UniversalMessageAdapter
{
  private MemoryRequest mrRequest;  
  
  public FinishedMemoryRead(OSMessageHandler theSource, MemoryRequest newRequest)
  {
    super(theSource);
    mrRequest = newRequest;
  }
  
  public void doMessage(Kernel theElement)
  {
    if (mrRequest.getMemoryType() == MemoryManager.CODE_SEGMENT)
    {
      theElement.setProcessCode(mrRequest.getMemory());
    }
    else if (mrRequest.getMemoryType() == MemoryManager.STACK_SEGMENT)
    {     
      theElement.setProcessStack(mrRequest.getMemory());
    }
  }
  
  public void doMessage(CPUAnimator theElement)
  {
    if (mrRequest.getMemoryType() == MemoryManager.CODE_SEGMENT)
    {
      theElement.loadCode(mrRequest.getMemory());
    }
    else if (mrRequest.getMemoryType() == MemoryManager.STACK_SEGMENT)
    {
      theElement.updateStack(mrRequest.getMemory());
    }
  }
  
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.finishedReadingMemory(this.mrRequest);
  }
}

