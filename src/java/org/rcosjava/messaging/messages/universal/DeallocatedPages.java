package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.memory.MemoryManagerAnimator;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * MMU has successfully allocated pages to a Process.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1998
 * @version 1.00 $Date$
 */
public class DeallocatedPages extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private MemoryReturn returnedMemory;

  /**
   * Constructor for the DeallocatedPages object
   *
   * @param theSource Description of Parameter
   * @param newReturn Description of Parameter
   */
  public DeallocatedPages(OSMessageHandler theSource,
      MemoryReturn newReturn)
  {
    super(theSource);
    returnedMemory = newReturn;
  }

  /**
   * Calls deallocatedPages to indicate that the memory was successfully freed
   * by the Memory Manager.
   *
   * @param theElement the IPC Manager Animator to do the work on.
   */
  public void doMessage(MemoryManagerAnimator theElement)
  {
    theElement.deallocatedPages(returnedMemory);
  }
}

