package org.rcosjava.software.disk;

/**
 * Simulates a piece of hardware usually in the disk controller that
 * reads/writes from the disk implementations.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @created 25th March, 1996
 * @version 1.00 $Date$
 */
public interface DiskScheduler
{
  /**
   * Description of the Method
   *
   * @param source Description of Parameter
   * @param request Description of Parameter
   */
  public void queueRequest(String source, DiskRequest request);

  /**
   * Description of the Method
   */
  public void processQueue();

  /**
   * Description of the Method
   */
  public void currentRequestComplete();

  /**
   * Description of the Method
   *
   * @param blockId Description of Parameter
   */
  public void dump(int blockId);
}
