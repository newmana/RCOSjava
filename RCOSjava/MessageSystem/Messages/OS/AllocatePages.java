package MessageSystem.Messages.OS;

import Software.Memory.MemoryRequest;
import Software.Memory.MemoryManager;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Process.ProcessScheduler;

/**
 * Requests the memory manager to allocate a page for a certain task.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1998
 */
public class AllocatePages extends OSMessageAdapter
{
  /**
   * The request object (type, process id, size, etc) that is sent to the
   * memory manager.
   */
  private MemoryRequest request;

  /**
   * @param source the sender of the message it is usually the process
   * scheduler.
   * @param the memory request to be sent to the memory manager.
   */
  public AllocatePages(OSMessageHandler newSource,
    MemoryRequest newRequest)
  {
    super(newSource);
    request = newRequest;
  }

  /**
   * Sets a new memory request object in case several allocatations are
   * required the object can be reused.
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

