package net.sourceforge.rcosjava.software.util;

import java.util.*;

/**
 * Implements Last In First Out type of queue by inserting the object at the
 * end of the queue (and removing them from the end of the queue).  The rest of
 * the queue's implementation relies on BaseQueue implementing the FIFO queue
 * as expected.
 * <P>
 * @see net.sourceforge.rcosjava.software.util.LIFOQueue
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd February 1996
 */
public class LIFOQueue extends BaseQueue
{
  /**
   * Default constructor.
   */
  public LIFOQueue()
  {
    super();
  }

  /**
   * Constructor of the queue.
   *
   * @param initialCapacity initial size of queue.
   * @param initialIncrement the increment size in which to increase the queue
   * when it becomes full.
   */
  public LIFOQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  /**
   * Create a queue with a given set of objects.
   *
   * @param initialCapacity initial size of queue.
   * @param initialIncrement the increment size in which to increase the queue
   * when it becomes full.
   * @param objectIterator
   */
  public LIFOQueue(int initialCapacity, int capacityIncrement,
    Iterator newObjects)
  {
    super(initialCapacity, capacityIncrement, newObjects);
  }

  /**
   * This will insert a new element into the queue at the end of the queue.
   *
   * @param the object to insert into the queue.
   */
  public void insert(Object theObject)
  {
    add(itemCount(), theObject);
  }
}
