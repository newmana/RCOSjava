package net.sourceforge.rcosjava.software.disk;

/**
 * This object contains the data passed between the Disk Scheduler and the
 * File System. (Both Ways, as Request and it's completion notification.
 * <P>
 * -> Disk
 * --------
 * Structure holds the FSRequestID (int), the Block Number (int), and
 * a pointer to the data. If the pointer is null, it is a read
 * operation.
 * <P>
 * <-Disk
 * ------
 * If the pointer is null, it was a write. if Block number is < 0, an
 * error occurred.
 * Data is all public. Avoids having to call methods and increases
 * speed.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @version 1.00 $Date$
 * @created 1st March, 1996
 */
public class DiskRequest
{
  private int requestId;
  private int diskBlock;
  public byte[] data;

  public DiskRequest()
  {
    requestId = -1;
    diskBlock = -1;
    data = null;
  }

  public DiskRequest(int newRequestId, int newDiskBlock, byte[] newData)
  {
    requestId = newRequestId;
    diskBlock = newDiskBlock;
    data = newData;
  }

}

