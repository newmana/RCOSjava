package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.memory.MemoryManagerAnimator;
import org.rcosjava.software.ipc.IPC;
import org.rcosjava.software.memory.MemoryReturn;
import org.rcosjava.software.memory.MemoryManager;

/**
 * MMU has successfully allocated shared memory pages to a process.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1998
 * @version 1.00 $Date$
 */
public class AllocatedSharedMemoryPages extends UniversalMessageAdapter
{
  private String sharedMemoryName;
  private int shmSize;
  private MemoryReturn memoryReturn;
  private Memory memory;

  /**
   * Constructor for the AllocatedPages object
   *
   * @param newSource Description of Parameter
   * @param newMemoryReturn Description of Parameter
   */
  public AllocatedSharedMemoryPages(OSMessageHandler newSource,
      String newSharedMemoryName, int newShmSize, MemoryReturn newMemoryReturn,
      Memory newMemory)
  {
    super(newSource);
    sharedMemoryName = newSharedMemoryName;
    shmSize = newShmSize;
    memoryReturn = newMemoryReturn;
    memory = newMemory;
  }

  public void doMessage(MemoryManagerAnimator theElement)
  {
    theElement.allocatedPages(memoryReturn);
  }

  public void doMessage(IPC theElement)
  {
    theElement.allocatedSharedMemoryPages(sharedMemoryName, shmSize,
        memoryReturn, memory);
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
