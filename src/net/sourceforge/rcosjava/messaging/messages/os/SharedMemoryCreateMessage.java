package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Message sent to IPC manager to create a new shared memory structure.
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
public class SharedMemoryCreateMessage extends OSMessageAdapter
{
  /**
   * The name of the new shared memory structured.
   */
  private String sharedMemoryName;

  /**
   * The process ID creating the shared memory structure.
   */
  private int PID;

  /**
   * The size of the shared memory structured to create.
   */
  private int size;

  public SharedMemoryCreateMessage(OSMessageHandler theSource,
    String newSharedMemoryName, int newPID, int newSize)
  {
    super(theSource);
    sharedMemoryName = newSharedMemoryName;
    PID = newPID;
    size = newSize;
  }

  /**
   * Call sharedMemoryCreate on the IPC.
   *
   * @param theElement the IPC to call sharedMemoryCreate on.
   */
  public void doMessage(IPC theElement)
  {
    theElement.sharedMemoryCreate(sharedMemoryName, PID, size);
  }
}
