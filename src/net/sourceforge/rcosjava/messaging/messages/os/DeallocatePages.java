package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Request that the pages current allocated to a certain terminal be removed.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th Marhc 1996
 */
public class DeallocatePages extends OSMessageAdapter
{
  private int pid;

  public DeallocatePages(OSMessageHandler theSource, int newPID)
  {
    super(theSource);
    pid = newPID;
  }

  public void setPID(int newPID)
  {
    pid = newPID;
  }

  public void doMessage(MemoryManager theElement)
  {
    theElement.deallocatePages(pid);
  }
}