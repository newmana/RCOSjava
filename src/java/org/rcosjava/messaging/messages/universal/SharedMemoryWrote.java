package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;
import org.rcosjava.software.ipc.SharedMemory;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * Message sent to IPC manager animator for verification of writing a shared
 * memory block.
 * <P>
 * @author Andrew Newman.
 * @created 17th of April 2001
 * @version 1.00 $Date$
 */
public class SharedMemoryWrote extends UniversalMessageAdapter
{
  /**
   * The id of the shared memory written.
   */
  private String sharedMemoryId;

  /**
   * The current value of the memory.
   */
  private Memory memory;

  /**
   * Constructor for the SharedMemoryWrote object
   *
   * @param theSource Description of Parameter
   * @param newSharedMemoryId Description of Parameter
   * @param newMemory Description of Parameter
   */
  public SharedMemoryWrote(OSMessageHandler theSource,
      String newSharedMemoryId, Memory newMemory)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
    memory = newMemory;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.sharedMemoryWrote(sharedMemoryId, memory);
  }
}
