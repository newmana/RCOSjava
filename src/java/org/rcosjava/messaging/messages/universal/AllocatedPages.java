package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.memory.MemoryManagerAnimator;
import org.rcosjava.software.ipc.IPC;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * MMU has successfully allocated pages to a Process.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1998
 * @version 1.00 $Date$
 */
public class AllocatedPages extends UniversalMessageAdapter
{
  /**
   * The result information (type of memory, process id, etc).
   */
  private MemoryReturn memoryReturn;

  /**
   * Constructor for the AllocatedPages object
   *
   * @param newSource who sent the message.
   * @param newMemoryReturn the result of the allocation of memory.
   */
  public AllocatedPages(OSMessageHandler newSource,
      MemoryReturn newMemoryReturn)
  {
    super(newSource);
    memoryReturn = newMemoryReturn;
  }

  /**
   * Sets the MemoryReturn attribute of the AllocatedPages object
   *
   * @param newMemoryReturn The new MemoryReturn value
   */
  public void setMemoryReturn(MemoryReturn newMemoryReturn)
  {
    memoryReturn = newMemoryReturn;
  }

  /**
   * Calls allocatedPages on the Memory Manager Animator to indicate that certain
   * pages have been allocated.
   *
   * @param theElement the Memory Manager Animator to do the work on.
   */
  public void doMessage(MemoryManagerAnimator theElement)
  {
    theElement.allocatedPages(memoryReturn);
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
