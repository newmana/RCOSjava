package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Message sent to IPC manager to open an existing shared memory structure.
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
public class SharedMemoryOpenMessage extends OSMessageAdapter
{
  /**
   * The name of the new shared memory structured.
   */
  private String sharedMemoryName;

  /**
   * The process ID opening the shared memory structure.
   */
  private int PID;

  public SharedMemoryOpenMessage(OSMessageHandler theSource,
    String newSharedMemoryName, int newPID)
  {
    super(theSource);
    sharedMemoryName = newSharedMemoryName;
    PID = newPID;
  }

  /**
   * Call sharedMemoryOpen on the IPC.
   *
   * @param theElement the IPC to call sharedMemoryOpen on.
   */
  public void doMessage(IPC theElement)
  {
    theElement.sharedMemoryOpen(sharedMemoryName, PID);
  }
}