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
   * Description of the Field
   */
  private String semaphoreId;
  /**
   * Description of the Field
   */
  private int pid;
  /**
   * Description of the Field
   */
  private int value;

  /**
   * Constructor for the SemaphoreWaiting object
   *
   * @param theSource Description of Parameter
   * @param newSemaphoreId Description of Parameter
   * @param newPID Description of Parameter
   * @param newValue Description of Parameter
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
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.semaphoreWaiting(semaphoreId, pid, value);
  }
}
