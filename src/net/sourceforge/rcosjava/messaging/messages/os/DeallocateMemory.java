package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Request that the pages current allocated to a certain terminal be removed.
 * Also removes any shared memory or semaphore resources in use.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th Marhc 1996
 */
public class DeallocateMemory extends OSMessageAdapter
{
  private int pid;

  public DeallocateMemory(OSMessageHandler theSource, int newPID)
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
    theElement.deallocateMemory(pid);
  }

  public void doMessage(IPC theElement)
  {
    theElement.deallocateMemory(pid);
  }
}