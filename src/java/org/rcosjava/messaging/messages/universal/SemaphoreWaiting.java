package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;

/**
 * Message to IPC manager animator to indicate a process is waiting on a
 * semaphore.
 * <P>
 * @author Andrew Newman.
 * @created 17th of April 2001
 * @version 1.00 $Date$
 */
public class SemaphoreWaiting extends UniversalMessageAdapter
{
  /**
   * The name of the semaphore that the process is waiting on.
   */
  private String semaphoreId;

  /**
   * The process that is waiting on the semaphore.
   */
  private int pid;

  /**
   * The value of the semaphore.
   */
  private int value;

  /**
   * Create a new semaphore waiting object.
   *
   * @param theSource the sender of the message (usually IPC)
   * @param newSemaphoreId the name of the semaphore.
   * @param newPID the process id waiting on the semaphore.
   * @param newValue the current value of the semaphore.
   */
  public SemaphoreWaiting(OSMessageHandler theSource, String newSemaphoreId,
      int newPID, int newValue)
  {
    super(theSource);
    semaphoreId = newSemaphoreId;
    pid = newPID;
    value = newValue;
  }

  /**
   * Calls semaphoreWaiting on the IPCManagerAnimator.
   *
   * @param theElement the instance of the IPCManagerAnimator to call.
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.semaphoreWaiting(semaphoreId, pid, value);
  }
}
