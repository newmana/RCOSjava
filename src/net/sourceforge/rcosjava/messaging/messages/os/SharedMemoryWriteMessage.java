package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Message sent to IPC manager to write one part to a shared memory structure.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 08/08/98 Changed to new message system. AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @version 1.00 $Date$
 * @created 30th of March 1996
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

