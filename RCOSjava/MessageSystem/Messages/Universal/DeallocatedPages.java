//******************************************************/
// FILE     : DeallocatedPagesMessage.java
// PURPOSE  : MMU has successfully allocated pages to a Process.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 01/01/1998   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import MessageSystem.PostOffices.OS.OSMessageHandler;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Memory.MemoryReturn;
import MessageSystem.PostOffices.MessageHandler;
import Software.Memory.MemoryManager;

public class DeallocatedPages extends UniversalMessageAdapter
{
  private MemoryReturn mrReturn;

  public DeallocatedPages(OSMessageHandler theSource, 
		MemoryReturn newReturn)
  {
    super(theSource);
    
		mrReturn = newReturn;
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.deallocatedPages(this.mrReturn);
  }
}

