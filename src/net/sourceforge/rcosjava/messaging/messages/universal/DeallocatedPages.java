//******************************************************/
// FILE     : DeallocatedPagesMessage.java
// PURPOSE  : MMU has successfully allocated pages to a Process.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/01/1998   Created
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.software.memory.MemoryManager;

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

