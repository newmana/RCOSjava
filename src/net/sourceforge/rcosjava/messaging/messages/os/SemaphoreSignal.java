package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Message sent to IPC manager for signal semaphore type messages.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 11th of August 1998
 */
public class SemaphoreSignal extends OSMessageAdapter
{
  private int semaphoreId;
  private int pid;

  public SemaphoreSignal(OSMessageHandler theSource,
    int newSemaphoreId, int newPID)
  {
    super(theSource);
    semaphoreId = newSemaphoreId;
    pid = newPID;
  }

  public void setSemaphoreID(int newSemaphoreId)
  {
    semaphoreId = newSemaphoreId;
  }

  public void setProcessID(int newPID)
  {
    pid = newPID;
  }

  public void doMessage(IPC theElement)
  {
    theElement.sempahoreSignal(semaphoreId, pid);
  }
}
