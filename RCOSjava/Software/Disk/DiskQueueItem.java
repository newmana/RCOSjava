//*************************************************************************//
// FILENAME : DiskQueueItem.java
// PACKAGE  : Disk
// PURPOSE  : 
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 25/3/96 Created.
//            08/08/98 Moved package added get etc. AN
//*************************************************************************//

package Software.Disk;

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

