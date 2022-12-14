package org.rcosjava.software.disk;

/**
 * This object contains the data passed between the Disk Scheduler and the File
 * System. (Both Ways, as Request and it's completion notification. <P>
 *
 * -> Disk -------- Structure holds the FSRequestID (int), the Block Number
 * (int), and a pointer to the data. If the pointer is null, it is a read
 * operation. <P>
 *
 * <-Disk ------ If the pointer is null, it was a write. if Block number is < 0,
 * an error occurred. Data is all public. Avoids having to call methods and
 * increases speed. <P>
 *
 *
 *
 * @author Andrew Newman.
 * @author Brett Carter.
 * @created 1st March, 1996
 * @version 1.00 $Date$
 */
public class DiskRequest
{
  /**
   * Description of the Field
   */
  private int requestId;
  /**
   * Description of the Field
   */
  private int diskBlock;
  /**
   * Description of the Field
   */
  private byte[] data;

  /**
   * Constructor for the DiskRequest object
   */
  public DiskRequest()
  {
    requestId = -1;
    diskBlock = -1;
    data = null;
  }

  /**
   * Constructor for the DiskRequest object
   *
   * @param newRequestId Description of Parameter
   * @param newDiskBlock Description of Parameter
   * @param newData Description of Parameter
   */
  public DiskRequest(int newRequestId, int newDiskBlock, byte[] newData)
  {
    requestId = newRequestId;
    diskBlock = newDiskBlock;
    data = newData;
  }

  /**
   * Gets the Data attribute of the DiskRequest object
   *
   * @return The Data value
   */
  public byte[] getData()
  {

    return data;
  }

  /**
   * Gets the DiskBlock attribute of the DiskRequest object
   *
   * @return The DiskBlock value
   */
  public int getDiskBlock()
  {

    return diskBlock;
  }

  /**
   * Gets the RequestId attribute of the DiskRequest object
   *
   * @return The RequestId value
   */
  public int getRequestId()
  {

    return requestId;
  }
}