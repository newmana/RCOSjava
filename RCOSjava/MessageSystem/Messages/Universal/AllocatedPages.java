//******************************************************/
// FILE     : AllocatedPagesMessage.java
// PURPOSE  : MMU has successfully allocated pages to a Process.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 01/01/1998   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Memory.MemoryReturn;
import Software.Memory.MemoryManager;

public class AllocatedPages extends UniversalMessageAdapter
{
  private MemoryReturn mrReturn;

  public AllocatedPages(OSMessageHandler theSource, 
    MemoryReturn mrNewMemoryReturn)
  {
    super(theSource);
    mrReturn = mrNewMemoryReturn;
  }

  public void setMemoryReturn(MemoryReturn mrNewReturn)
  {
    mrReturn = mrNewReturn;
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.allocatedPages(mrReturn);
  }
}

