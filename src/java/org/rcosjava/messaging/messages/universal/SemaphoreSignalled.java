package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;

/**
 * Message sent to IPC manager animator for verification of signalled semaphore.
 * <P>
 * @author Andrew Newman.
 * @created 17th of April 2001
 * @version 1.00 $Date$
 */
public class SemaphoreSignalled extends UniversalMessageAdapter
{
  /**
   * The unique semaphore id.
   */
  private String semaphoreId;

  /**
   * The PID of the process signalling.
   */
  private int pid;

  /**
   * The value of the semaphore.
   */
  private int value;

  /**
   * The process that has been signalled.
   */
  private int signalledPID;

  /**
   * Create a new semaphore signalled message.
   *
   * @param theSource the objec that sent the message.
   * @param newSemaphoreId the unique id of the process.
   * @param newPID the process signalling.
   * @param newValue the value of the semaphore.
   * @param newSignalledPID the process signalled.
   */
  public SemaphoreSignalled(OSMessageHandler theSource, String newSemaphoreId,
      int newPID, int newValue, int newSignalledPID)
  {
    super(theSource);
    semaphoreId = newSemaphoreId;
    pid = newPID;
    value = newValue;
    signalledPID = newSignalledPID;
  }

  /**
   * Calls semaphoreSingalled on the IPC manager animator.
   *
   * @param theElement the animator to call.
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
    theElement.semaphoreSignalled(semaphoreId, pid, value, signalledPID);
  }
}
