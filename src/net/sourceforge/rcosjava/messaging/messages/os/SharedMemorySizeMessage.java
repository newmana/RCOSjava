package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Message sent to IPC manager for the size of a shared memory structure.
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

public class SharedMemorySizeMessage extends OSMessageAdapter
{
  /**
   * The name of the new shared memory structured.
   */
  private int sharedMemoryId;

  public SharedMemorySizeMessage(OSMessageHandler theSource,
    int newSharedMemoryId)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
  }

  /**
   * Call sharedMemorySize on the IPC.
   *
   * @param theElement the IPC to call sharedMemorySize on.
   */
  public void doMessage(IPC theElement)
  {
    theElement.sharedMemorySize(sharedMemoryId);
  }
}

