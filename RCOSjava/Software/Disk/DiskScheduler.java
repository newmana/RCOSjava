//*******************************************************************/
// FILENAME : DiskScheduler.java
// PURPOSE  : Simulates a piece of hardware usually in the disk 
//            controller that reads/writes from the disk. 
//            implementations.
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 25/03/96 Created.
//            08/09/98 Modified to be an interface.
//*******************************************************************/

package Software.Disk;

public interface DiskScheduler
{
  public void queueRequest(String mvSource, DiskRequest mvTheRequest);
  public void processQueue();
  public void currentRequestComplete();
  public void dump(int Block);
}
