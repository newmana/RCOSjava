package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;

/**
 * Write a series of bytes of shared memory.
 * <P>
 * @author Andrew Newman.
 * @created 3rd August 1997
 * @version 1.00 $Date$
 */
public class SharedMemoryWrite extends UniversalMessageAdapter
{
  /**
   * The memory request that is used by the receiving objects which holds such
   * things as the type, process id, and other defining qualities of the memory
   * to write to.
   */
  private MemoryRequest request;
  private String shmId;

  /**
   * Create a new write bytes message from a given source with a given memory
   * request item to store the type, process id and the new memory to write.
   *
   * @param theSource the sender of the message.
   * @param newRequest the object holding the details of the memory to write.
   */
  public SharedMemoryWrite(OSMessageHandler theSource, String newShmId,
      MemoryRequest newRequest)
  {
    super(theSource);
    shmId = newShmId;
    request = newRequest;
  }

  /**
   * Calls writingMemory on the receiver.
   *
   * @param theElement the object being used to do the method.
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.writingMemory(request);
  }

  /**
   * Calls writingBytes on the receiver.
   *
   * @param theElement the object being used to do the method.
   */
  public void doMessage(MemoryManager theElement)
  {
    theElement.sharedMemoryWrite(request, shmId);
  }
}

