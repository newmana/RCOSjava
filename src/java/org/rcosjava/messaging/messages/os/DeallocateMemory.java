package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.ipc.IPC;
import org.rcosjava.software.memory.MemoryManager;

/**
 * Request that the pages current allocated to a certain terminal be removed.
 * Also removes any shared memory or semaphore resources in use.
 * <P>
 * @author Andrew Newman.
 * @created 24th Marhc 1996
 * @version 1.00 $Date$
 */
public class DeallocateMemory extends OSMessageAdapter
{
  /**
   * The process id to remove the memory from.
   */
  private int pid;

  /**
   * Constructor for the DeallocateMemory object
   *
   * @param theSource Description of Parameter
   * @param newPID Description of Parameter
   */
  public DeallocateMemory(OSMessageHandler theSource, int newPID)
  {
    super(theSource);
    pid = newPID;
  }

  /**
   * Sets the PID attribute of the DeallocateMemory object
   *
   * @param newPID The new PID value
   */
  public void setPID(int newPID)
  {
    pid = newPID;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(MemoryManager theElement)
  {
    theElement.deallocateMemory(pid);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(IPC theElement)
  {
    theElement.deallocateMemory(pid);
  }
}
