package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;

/**
 * Message sent to IPC manager for verification of closed semaphore.
 * <P>
 * @author Andrew Newman.
 * @created 17th of April 2001
 * @version 1.00 $Date$
 */
public class SemaphoreClosed extends UniversalMessageAdapter
{
  /**
   * The unique id of the semaphore.
   */
  private String semaphoreId;

  /**
   * The process id of the process closing the semaphore.
   */
  private int pid;

  /**
   * The value to set the semaphore.
   */
  private int value;

  /**
   * Constructor for the SemaphoreClosed object
   *
   * @param theSource Description of Parameter
   * @param newSemaphoreId Description of Parameter
   * @param newPID Description of Parameter
   * @param newValue Description of Parameter
   */
  public SemaphoreClosed(OSMessageHandler theSource, String newSemaphoreId,
      int newPID, int newValue)
  {
    super(theSource);
    semaphoreId = newSemaphoreId;
    pid = newPID;
    value = newValue;
  }

  /**
   * Call semaphoreClosed on the IPCManagerAnimator.
   *
   * @param theElement the IPCManagerAnimator to call.
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.semaphoreClosed(semaphoreId, pid, value);
  }
}
