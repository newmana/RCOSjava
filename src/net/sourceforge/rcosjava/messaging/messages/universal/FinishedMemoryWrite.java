//******************************************************/
// FILE     : FinishedMemoryWriteMessage.java
// PACKAGE  : MessageSystem.Universal
// PURPOSE  : MMU is responding to successfully written
//            memory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/01/1998   Created
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.memory.MemoryManager;

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

  public boolean undoableMessage()
  {
    return false;
  }
}

