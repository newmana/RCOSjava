package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Message sent to IPC manager to create a new semaphore.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 08/08/98 Changed to new message system. AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @version 1.00 $Date$
 * @created 28th of March 1996
 */
public class SemaphoreCreate extends OSMessageAdapter
{
  private String semaphoreId;
  private int pid;
  private int value;

  public SemaphoreCreate(OSMessageHandler theSource, String newSemaphoreId,
    int newPID, int newValue)
  {
    super(theSource);
    semaphoreId = newSemaphoreId;
    pid = newPID;
    value = newValue;
  }

  public void setSemaphoreID(String newSemaphoreId)
  {
    semaphoreId = newSemaphoreId;
  }

  public void setProcessID(int iNewProcessID)
  {
    pid = iNewProcessID;
  }

  public void setSemaphoreValue(int newValue)
  {
    value = newValue;
  }

  public void doMessage(IPC theElement)
  {
    theElement.semaphoreCreate(semaphoreId, pid, value);
  }
}
