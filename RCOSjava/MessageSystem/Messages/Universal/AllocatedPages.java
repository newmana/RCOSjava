package MessageSystem.Messages.Universal;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Memory.MemoryReturn;
import Software.Memory.MemoryManager;

/**
 * MMU has successfully allocated pages to a Process.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st January 1998
 */
public class AllocatedPages extends UniversalMessageAdapter
{
  private MemoryReturn memoryReturn;

  public AllocatedPages(OSMessageHandler newSource,
    MemoryReturn newMemoryReturn)
  {
    super(newSource);
    memoryReturn = newMemoryReturn;
  }

  public void setMemoryReturn(MemoryReturn newMemoryReturn)
  {
    memoryReturn = newMemoryReturn;
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.allocatedPages(memoryReturn);
  }

  public boolean undoableMessage()
  {
    return false;
  }
}