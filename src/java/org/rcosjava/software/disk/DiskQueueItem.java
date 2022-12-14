package org.rcosjava.software.disk;

/**
 * Holds the disk request and the object that sent it.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @created 10th September, 1999
 * @version 1.00 $Date$
 */
public class DiskQueueItem
{
  /**
   * Description of the Field
   */
  private String cvSource;

  /**
   * Description of the Field
   */
  private DiskRequest cvTheRequest;

  /**
   * Constructor for the DiskQueueItem object
   *
   * @param mvSource Description of Parameter
   * @param mvTheDiskRequest Description of Parameter
   */
  public DiskQueueItem(String mvSource, DiskRequest mvTheDiskRequest)
  {
    cvSource = new String(mvSource);
    cvTheRequest = mvTheDiskRequest;
  }

  /**
   * Constructor for the DiskQueueItem object
   */
  public DiskQueueItem()
  {
    cvSource = null;
    cvTheRequest = null;
  }

  /**
   * Gets the Source attribute of the DiskQueueItem object
   *
   * @return The Source value
   */
  public String getSource()
  {
    return cvSource;
  }

  /**
   * Gets the DiskRequest attribute of the DiskQueueItem object
   *
   * @return The DiskRequest value
   */
  public DiskRequest getDiskRequest()
  {
    return cvTheRequest;
  }
}

