package net.sourceforge.rcosjava.software.disk;

/**
 * Holds the disk request and the object that sent it.  TODO refactor.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @version 1.00 $Date$
 * @created 10th September, 1999
 */
public class DiskQueueItem
{
  private String cvSource;
  private DiskRequest cvTheRequest;

  public DiskQueueItem(String mvSource, DiskRequest mvTheDiskRequest)
  {
    cvSource = new String( mvSource);
    cvTheRequest = mvTheDiskRequest;
  }

  public DiskQueueItem()
  {
    cvSource = null;
    cvTheRequest = null;
  }

  public String getSource()
  {
    return cvSource;
  }

  public DiskRequest getDiskRequest()
  {
    return cvTheRequest;
  }
}

