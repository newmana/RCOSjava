package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;

/**
 * Message sent to IPC manager for verification of signalled semaphore.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 17th of April 2001
 */
public class SemaphoreSignalled extends UniversalMessageAdapter
{
  private String semaphoreId;
  private int pid;
  private int value;

  public SemaphoreSignalled(OSMessageHandler theSource, String newSemaphoreId,
    int newPID, int newValue)
  {
    super(theSource);
    semaphoreId = newSemaphoreId;
    pid = newPID;
    value = newValue;
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.semaphoreSignalled(semaphoreId, pid, value);
  }
}
