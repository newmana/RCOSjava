package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;

/**
 * Requests the memory manager to allocate a shared memory page for a certain
 * task.
 * <P>
 * @author Andrew Newman.
 * @created 24th March 1998
 * @version 1.00 $Date$
 */
public class AllocateSharedMemoryPages extends OSMessageAdapter
{
  /**
   * The request object (type, process id, size, etc) that is sent to the memory
   * manager.
   */
  private MemoryRequest request;
  private String shmName;
  private int size;

  /**
   * @param newSource Description of Parameter
   * @param newRequest Description of Parameter
   */
  public AllocateSharedMemoryPages(OSMessageHandler newSource,
      MemoryRequest newRequest, String newShmName, int newSize)
  {
    super(newSource);
    request = newRequest;
    shmName = newShmName;
    size = newSize;
  }

  /**
   * Sets a new memory request object in case several allocatations are required
   * the object can be reused.
   *
   * @param newRequest the memory request to be sent to the memory manager.
   */
  public void setMemoryRequest(MemoryRequest newRequest)
  {
    request = newRequest;
  }

  /**
   * Called by the memory and calls allocatePages.
   *
   * @param theElement the memory manager that is receiving the message.
   */
  public void doMessage(MemoryManager theElement)
  {
    theElement.allocateSharedMemoryPages(request, shmName, size);
  }
}

