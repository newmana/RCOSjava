package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Message sent to IPC manager to read a bit of the shared memory structure.
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
public class SharedMemoryCloseMessage extends OSMessageAdapter
{
  /**
   * The id of the shared memory structure.
   */
  private int sharedMemoryId;

  /**
   * The process id closing the share memory structure.
   */
  private int PID;

  public SharedMemoryCloseMessage(OSMessageHandler theSource,
    int newSharedMemoryId, int newPID)
  {
    super(theSource);
    sharedMemoryId = newSharedMemoryId;
    PID = newPID;
  }

  /**
   * Call sharedMemorySize on the IPC.
   *
   * @param theElement the IPC to call sharedMemorySize on.
   */
  public void doMessage(IPC theElement)
  {
    theElement.sharedMemoryClose(sharedMemoryId, PID);
  }
}

