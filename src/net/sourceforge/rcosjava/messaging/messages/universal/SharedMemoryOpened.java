package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;

/**
 * Message sent to IPC manager animator for verification of opening a shared
 * memory block.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 17th of April 2001
 */
public class SharedMemoryOpened extends UniversalMessageAdapter
{
  /**
   * The name of the new shared memory structured.
   */
  private String sharedMemoryId;

  /**
   * The process ID creating the shared memory structure.
   */
  private int PID;

  public SharedMemoryOpened(OSMessageHandler theSource,
    String newSharedMemoryId, int newPID)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
    PID = newPID;
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.sharedMemoryOpened(sharedMemoryId, PID);
  }
}
