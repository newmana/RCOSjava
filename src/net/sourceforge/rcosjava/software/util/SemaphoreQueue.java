package net.sourceforge.rcosjava.software.util;

import net.sourceforge.rcosjava.software.ipc.Semaphore;

/**
 * For IPC to hold a queue of semaphores written because of problems with HashTables
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 31st March 1996
 */
public class SemaphoreQueue extends FIFOQueue
{
  public SemaphoreQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  /**
   * @return String type of Process to occur at the specified time or null if
   * no interrupt
   */
  public synchronized Semaphore getSemaphore(int semaphoreId)
  {
    Semaphore tmp;
    String type;

    if (queueEmpty())
      return null;

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
   */
  public boolean isMember(int iSempahoreID)
  {
    return (peek(iSempahoreID) != null);
  }

  public synchronized boolean remove(Object object)
  {
    return super.remove(object);
  }

  /**
   * isMember by string id (defined in program)
   */
  public boolean isMember(String sSempahoreID)
  {
    return (peek(sSempahoreID) != null);
  }

  /**
   * Peek by numeric id (inc int value)
   *
   * @param semaphoreId the id to look for
   */
  public synchronized Object peek(int semaphoreId)
  {
    Semaphore tmp;

    if (queueEmpty())
      return null;

    goToHead();

    do
    {
      tmp = (Semaphore) peek();
      //System.out.println("tmpSID = " + tmp.getId());
      //System.out.println("mySID = " + semaphoreId);
      if (tmp.getId() == semaphoreId)
        return (Object) tmp;
      goToNext();
    } while (!atTail());

    return null;
  }

  /**
   * peek by string id (defined in program)
   */
  public synchronized Semaphore peek(String sSempahoreID)
  {
    Semaphore tmp;

    if (queueEmpty())
      return null;

    goToHead();

    do
    {
      tmp = (Semaphore)peek();
      if (tmp.getName().compareTo(sSempahoreID) == 0)
        return tmp;
      goToNext();
    } while (!atTail());
    return null;
  }
}