//******************************************************/
// FILE     : DeallocatePagesMessage.java
// PURPOSE  :
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/98   Created
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.os;


import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class DeallocatePages extends OSMessageAdapter
{
  private int iPID;

  public DeallocatePages(OSMessageHandler theSource,
    int iNewPID)
  {
    super(theSource);
    iPID = iNewPID;
  }

  public void setPID(int iNewPID)
  {
    iPID = iNewPID;
  }

  public void doMessage(MemoryManager theElement)
  {
    theElement.deallocatePages(iPID);
  }
}

