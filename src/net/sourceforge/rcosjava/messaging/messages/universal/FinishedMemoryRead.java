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
  private MemoryRequest memoryRequest;

  public FinishedMemoryRead(OSMessageHandler theSource,
    MemoryRequest newRequest)
  {
    super(theSource);
    memoryRequest = newRequest;
  }

  /**
   * If the request is of process code type it will call the Kernel's
   * setProcessCode.  If it is a stack segment then it will call
   * setProcessStack.
   *
   * @param theElement the Kernel to do the work on.
   */
  public void doMessage(Kernel theElement)
  {
    if (memoryRequest.getMemoryType() == MemoryManager.CODE_SEGMENT)
    {
      theElement.setProcessCode(memoryRequest.getMemory());
    }
    else if (memoryRequest.getMemoryType() == MemoryManager.STACK_SEGMENT)
    {
      theElement.setProcessStack(memoryRequest.getMemory());
    }
  }

  /**
   * Depends on the type of memory but will call the appropriate method based
   * on the type of memory that was requested.
   *
   * @param theElement the CPU Animator to do the work on.
   */
  public void doMessage(CPUAnimator theElement)
  {
    if (memoryRequest.getMemoryType() == MemoryManager.CODE_SEGMENT)
    {
      theElement.loadCode(memoryRequest.getMemory());
    }
    else if (memoryRequest.getMemoryType() == MemoryManager.STACK_SEGMENT)
    {
      theElement.updateStack(memoryRequest.getMemory());
    }
  }

  /**
   * Calls teh finishedReadingMemory on the IPC Manager Animator.  Indicates
   * that the memory has been successfully read.  The animator currently does
   * not differentiate between code and stack memory.
   *
   * @param theElement the IPC Manager Animator to do the work on.
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.finishedReadingMemory(this.memoryRequest);
  }

  /**
   * This message is spawned by others and is not marked for passivation.
   */
  public boolean undoableMessage()
  {
    return false;
  }
}