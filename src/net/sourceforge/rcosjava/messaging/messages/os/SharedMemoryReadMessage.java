package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Message sent to IPC manager to read one part of a shared memory structure.
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

  public SharedMemoryReadMessage(OSMessageHandler theSource,
    int newSharedMemoryId, int newOffset)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
    offset = newOffset;
  }

  /**
   * Call sharedMemoryRead on the IPC.
   *
   * @param theElement the IPC to call sharedMemoryRead on.
   */
  public void doMessage(IPC theElement)
  {
    theElement.sharedMemoryRead(sharedMemoryId, offset);
  }
}

