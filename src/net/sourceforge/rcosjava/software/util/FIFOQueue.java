package net.sourceforge.rcosjava.software.util;

import java.util.*;

/**
 * Implements a First In First Out type of queue.  The same as the base queue
 * but is publicly accessible.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd February 1996
 */
public class FIFOQueue extends BaseQueue
{
  /**
   * Default constructor.
   */
  public FIFOQueue()
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
  public FIFOQueue(int initialCapacity, int capacityIncrement)
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
   public FIFOQueue(int initialCapacity, int capacityIncrement,
    Iterator newObjects)
  {
    super(initialCapacity, capacityIncrement, newObjects);
  }
}

