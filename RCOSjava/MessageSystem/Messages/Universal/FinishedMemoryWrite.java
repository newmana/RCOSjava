//******************************************************/
// FILE     : FinishedMemoryWriteMessage.java
// PACKAGE  : MessageSystem.Universal
// PURPOSE  : MMU is responding to successfully written 
//            memory.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 01/01/1998   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Hardware.Memory.Memory;
import Software.Memory.MemoryRequest;
import Software.Kernel.Kernel;
import Software.Animator.CPU.CPUAnimator;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Memory.MemoryManager;

public class FinishedMemoryWrite extends UniversalMessageAdapter
{
  private MemoryRequest mrRequest;  
  
  public FinishedMemoryWrite(OSMessageHandler theSource, MemoryRequest newRequest)
  {
    super(theSource);
    mrRequest = newRequest;
  }
  
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.finishedWritingMemory(this.mrRequest);
  }
}

