package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.cpu.CPUAnimator;
import org.rcosjava.software.animator.memory.MemoryManagerAnimator;
import org.rcosjava.software.ipc.IPC;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;

/**
 * MMU is responding with requested memory.
 * <P>
 * @author Andrew Newman.
 * @created 1st of January 1998
 * @version 1.00 $Date$
 */
public class FinishedMemoryRead extends UniversalMessageAdapter
{
  /**
   * The request information (type of memory, process id, etc).
   */
  private MemoryRequest memoryRequest;

  /**
   * Constructor for the FinishedMemoryRead object
   *
   * @param theSource Description of Parameter
   * @param newRequest Description of Parameter
   */
  public FinishedMemoryRead(OSMessageHandler theSource,
      MemoryRequest newRequest)
  {
    super(theSource);
    memoryRequest = newRequest;
  }

  /**
   * If the request is of process code type it will call the Kernel's
   * setProcessCode. If it is a stack segment then it will call setProcessStack.
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
   * Depends on the type of memory but will call the appropriate method based on
   * the type of memory that was requested.
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
   * Calls the finishedReadingMemory on the IPC Manager Animator. Indicates that
   * the memory has been successfully read. The animator currently does not
   * differentiate between code and stack memory.
   *
   * @param theElement the IPC Manager Animator to do the work on.
   */
  public void doMessage(MemoryManagerAnimator theElement)
  {
    theElement.finishedReadingMemory(this.memoryRequest);
  }

  /**
   * Calls finishedSharedMemoryRead if the return memory type is Shared Memory.
   *
   * @param theElement the IPC manager to do the work on.
   */
  public void doMessage(IPC theElement)
  {
    if (memoryRequest.getMemoryType() == MemoryManager.SHARED_SEGMENT)
    {
      theElement.finishedSharedMemoryRead(memoryRequest.getMemory().
          getMemorySegment()[0]);
    }
  }

  /**
   * This message is spawned by others and is not marked for passivation.
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return false;
  }
}
