package org.rcosjava.messaging.messages.os;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;

/**
 * Requests the memory manager to allocate a page for a certain task.
 * <P>
 * @author Andrew Newman.
 * @created 24th March 1998
 * @version 1.00 $Date$
 */
public class AllocatePages extends OSMessageAdapter
{
  /**
   * The request object (type, process id, size, etc) that is sent to the memory
   * manager.
   */
  private MemoryRequest request;

  /**
   * Create a new allocate pages message.
   *
   * @param newSource the handler that sent the message.
   * @param newRequest the new memory request (type, id, size, etc).
   */
  public AllocatePages(OSMessageHandler newSource, MemoryRequest newRequest)
  {
    super(newSource);
    request = newRequest;
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
    theElement.allocatePages(request);
  }
}