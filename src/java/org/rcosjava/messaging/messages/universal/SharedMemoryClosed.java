package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;

/**
 * Message sent to IPC manager animator for verification of closed a shared
 * memory block.
 * <P>
 * @author Andrew Newman.
 * @created 17th of April 2001
 * @version 1.00 $Date$
 */
public class SharedMemoryClosed extends UniversalMessageAdapter
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
   * Constructor for the SharedMemoryClosed object
   *
   * @param theSource Description of Parameter
   * @param newSharedMemoryId Description of Parameter
   * @param newPID Description of Parameter
   */
  public SharedMemoryClosed(OSMessageHandler theSource,
      String newSharedMemoryId, int newPID)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
    PID = newPID;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.sharedMemoryClosed(sharedMemoryId, PID);
  }
}
