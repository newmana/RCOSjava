package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.memory.MemoryManager;

/**
 * MMU is responding with requested memory.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st of January 1998
 */
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

  public boolean undoableMessage()
  {
    return false;
  }
}

