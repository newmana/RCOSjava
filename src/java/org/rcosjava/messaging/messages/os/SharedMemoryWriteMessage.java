package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.ipc.IPC;

/**
 * Message sent to IPC manager to write one part to a shared memory structure.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 08/08/98 Changed to new message system. AN </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @created 30th of March 1996
 * @version 1.00 $Date$
 */
public class SharedMemoryWriteMessage extends OSMessageAdapter
{
  /**
   * The shared memory id writing to the share memory structure.
   */
  private int sharedMemoryId;

  /**
   * The offset into the shared memory.
   */
  private int offset;

  /**
   * The value to write to the shared memory.
   */
  private short value;

  /**
   * The process id reading the segment.
   */
  private int PID;

  /**
   * Constructor for the SharedMemoryWriteMessage object
   *
   * @param theSource Description of Parameter
   * @param newSharedMemoryId Description of Parameter
   * @param newOffset Description of Parameter
   * @param newValue Description of Parameter
   * @param newPID Description of Parameter
   */
  public SharedMemoryWriteMessage(OSMessageHandler theSource,
      int newSharedMemoryId, int newOffset, short newValue, int newPID)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
    offset = newOffset;
    value = newValue;
    PID = newPID;
  }

  /**
   * Call sharedMemoryWrite on the IPC.
   *
   * @param theElement the IPC to call sharedMemoryWrite on.
   */
  public void doMessage(IPC theElement)
  {
    theElement.sharedMemoryWrite(sharedMemoryId, offset, value, PID);
  }
}

