package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.software.memory.MemoryManager;

/**
 * MMU has successfully allocated pages to a Process.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st January 1998
 */
public class DeallocatedPages extends UniversalMessageAdapter
{
  private MemoryReturn returnedMemory;

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
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.deallocatedPages(this.returnedMemory);
  }
}

