package org.rcosjava.messaging.messages.os;

import org.rcosjava.software.disk.DiskManager;
import org.rcosjava.software.disk.DiskRequest;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Adds to the disk request to the disk manager.
 * <P>
 * @author Andrew Newman
 * @created 28 March 1996
 */
public class AddDiskRequest extends OSMessageAdapter
{
  /**
   * A request to add.
   */
  private DiskRequest request;

  /**
   * Create a new AddDiskRequest message.
   *
   * @param theSource the messaging handler that is sending the message.
   * @param newRequest the request to add.
   */
  public AddDiskRequest(OSMessageHandler theSource, DiskRequest newRequest)
  {
    super(theSource);
    request = newRequest;
  }

  /**
   * Returns the disk request.
   *
   * @return the disk request.
   */
  public DiskRequest getDiskRequest()
  {
    return request;
  }

  /**
   * Calls addInterrupt on the CPU via the Kernel.
   *
   * @param theElement the Kernel object to call.
   */
  public void doMessage(DiskManager theElement)
  {
    theElement.addDiskRequest(getSource().getId(), request);
  }
}
