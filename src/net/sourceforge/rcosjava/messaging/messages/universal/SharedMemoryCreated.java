package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;

/**
 * Message sent to IPC manager animator for verification of created shared
 * memory block.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 17th of April 2001
 */
public class SharedMemoryCreated extends UniversalMessageAdapter
{
  /**
   * The name of the new shared memory structured.
   */
  private String sharedMemoryId;

  /**
   * The process ID creating the shared memory structure.
   */
  private int PID;

  /**
   * The size of the shared memory structured to create.
   */
  private int size;

  public SharedMemoryCreated(OSMessageHandler theSource,
    String newSharedMemoryId, int newPID, int newSize)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
    PID = newPID;
    size = newSize;
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.semaphoreCreated(sharedMemoryId, PID, size);
  }
}
