package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.ipc.IPC;

/**
 * Message sent to IPC manager to read one part of a shared memory structure.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 08/08/98 Changed to new message system. AN </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @created 30th of March 1996
 * @version 1.00 $Date$
 */
public class SharedMemoryReadMessage extends OSMessageAdapter
{
  /**
   * The shared memory id reading to the share memory structure.
   */
  private int sharedMemoryId;

  /**
   * The offset into the shared memory.
   */
  private int offset;

  /**
   * The process id reading the segment.
   */
  private int PID;

  /**
   * Constructor for the SharedMemoryReadMessage object
   *
   * @param theSource Description of Parameter
   * @param newSharedMemoryId Description of Parameter
   * @param newOffset Description of Parameter
   * @param newPID Description of Parameter
   */
  public SharedMemoryReadMessage(OSMessageHandler theSource,
      int newSharedMemoryId, int newOffset, int newPID)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
    offset = newOffset;
    PID = newPID;
  }

  /**
   * Call sharedMemoryRead on the IPC.
   *
   * @param theElement the IPC to call sharedMemoryRead on.
   */
  public void doMessage(IPC theElement)
  {
    theElement.sharedMemoryRead(sharedMemoryId, offset, PID);
  }
}

