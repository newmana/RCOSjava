package net.sourceforge.rcosjava.software.disk;

/**
 * Simulates a piece of hardware usually in the disk controller that
 * reads/writes from the disk implementations.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @version 1.00 $Date$
 * @created 25th March, 1996
 */
public interface DiskScheduler
{
  public void queueRequest(String source, DiskRequest request);
  public void processQueue();
  public void currentRequestComplete();
  public void dump(int blockId);
}
