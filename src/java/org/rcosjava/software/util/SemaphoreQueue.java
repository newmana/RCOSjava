package org.rcosjava.software.util;
import org.rcosjava.software.ipc.Semaphore;

/**
 * For IPC to hold a queue of semaphores written because of problems with
 * HashTables.
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 31st March 1996
 * @version 1.00 $Date$
 */
public class SemaphoreQueue extends FIFOQueue
{
  /**
   * Constructor for the SemaphoreQueue object
   *
   * @param initialCapacity Description of Parameter
   * @param capacityIncrement Description of Parameter
   */
  public SemaphoreQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  /**
   * @param semaphoreId Description of Parameter
   * @return String type of Process to occur at the specified time or null if no
   *      interrupt
   */
  public synchronized Semaphore getSemaphore(int semaphoreId)
  {
    Semaphore tmp;
    String type;

    if (queueEmpty())
    {
      return null;
    }

    goToHead();

    // loop through contents of queue until
    // - we find a match, which should be removed and the process returned
    // - or we reach the end of the Q
    // - or the PID of the Processs are greater than the PID we
    //   are looking for
    do
    {
      tmp = (Semaphore) peek();
      if (tmp.getId() == semaphoreId)
      {
        tmp = (Semaphore) retrieveCurrent();
        return tmp;
      }
      goToNext();
    } while (!atTail());

    tmp = (Semaphore) peek();
    if (tmp.getId() == semaphoreId)
    {
      tmp = (Semaphore) retrieveCurrent();
      return tmp;
    }
    return null;
  }

  /**
   * isMember by numeric id (inc int value)
   *
   * @param iSempahoreID Description of Parameter
   * @return The Member value
   */
  public boolean isMember(int iSempahoreID)
  {
    return (peek(iSempahoreID) != null);
  }

  /**
   * isMember by string id (defined in program)
   *
   * @param sSempahoreID Description of Parameter
   * @return The Member value
   */
  public boolean isMember(String sSempahoreID)
  {
    return (peek(sSempahoreID) != null);
  }

  /**
   * Removes the item from the queue - added synchronization.
   *
   * @param object Description of Parameter
   * @return Description of the Returned Value
   */
  public synchronized boolean remove(Object object)
  {
    return super.remove(object);
  }

  /**
   * Peek by numeric id (inc int value)
   *
   * @param semaphoreId the id to look for
   * @return Description of the Returned Value
   */
  public synchronized Object peek(int semaphoreId)
  {
    Semaphore tmp;

    if (queueEmpty())
    {
      return null;
    }

    goToHead();

    do
    {
      tmp = (Semaphore) peek();
      if (tmp.getId() == semaphoreId)
      {
        return (Object) tmp;
      }
      goToNext();
    } while (!atTail());

    return null;
  }

  /**
   * peek by string id (defined in program)
   *
   * @param sSempahoreID Description of Parameter
   * @return Description of the Returned Value
   */
  public synchronized Semaphore peek(String sSempahoreID)
  {
    Semaphore tmp;

    if (queueEmpty())
    {
      return null;
    }

    goToHead();

    do
    {
      tmp = (Semaphore) peek();
      if (tmp.getName().compareTo(sSempahoreID) == 0)
      {
        return tmp;
      }
      goToNext();
    } while (!atTail());
    return null;
  }
}
