package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.ipc.SharedMemory;

/**
 * Message sent to IPC manager animator for verification of writing a shared
 * memory block.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 17th of April 2001
 */
public class SharedMemoryWrote extends UniversalMessageAdapter
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
  private SharedMemory memory;

  public SharedMemoryWrote(OSMessageHandler theSource,
    String newSharedMemoryId, int newPID, SharedMemory newMemory)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
    PID = newPID;
    memory = newMemory;
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.sharedMemoryWrote(sharedMemoryId, PID, memory);
  }
}
