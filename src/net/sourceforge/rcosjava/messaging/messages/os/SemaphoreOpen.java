package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Message sent to the IPC from the Kernel to open an existing semaphore.
 * Proceeds the create Semaphore function.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th of March 1996
 */
public class SemaphoreOpen extends OSMessageAdapter
{
  private String semaphoreId;
  private int pid;

  public SemaphoreOpen(OSMessageHandler theSource, String newSemaphoreId,
    int newPID)
  {
    super(theSource);
    semaphoreId = newSemaphoreId;
    pid = newPID;
  }

  public void setSempahoreID(String newSemaphoreId)
  {
    semaphoreId = newSemaphoreId;
  }

  public void setProcessID(int newPID)
  {
    pid = newPID;
  }

  public void doMessage(IPC theElement)
  {
    theElement.semaphoreOpen(semaphoreId, pid);
  }
}
