package net.sourceforge.rcosjava.software.util;

import java.util.Iterator;

/**
 * Implements priority queue (based on compareTo value).
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd February 1996
 */
public class PriorityQueue extends BaseQueue
{
  /**
   * Default constructor.
   */
  public PriorityQueue()
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
  public PriorityQueue(int initialCapacity, int capacityIncrement)
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
  public PriorityQueue(int initialCapacity, int capacityIncrement,
    Iterator newObjects)
  {
    super(initialCapacity, capacityIncrement, newObjects);
  }

  /**
   * Insert the element into the queue based on it's comparison with the other
   * elements in the queue.  It will create a queue that is ascending is
   * value.  Therefore, greater priority will be taken off the queue first
   * (because elements are removed at the end).
   *
   * @param theObject the object to insert into the queue.
   */
  public void insert(Object theObject)
  {
    int position = 0;

    //If it's the first element in the queue then it will have the highest
    //priority.
    if (queueEmpty())
    {
      insertElementAt(theObject, 0);
    }
    else
    {
      if (compare(theObject, elementAt(position)) == 0)
        insertElementAt(theObject, 0);
      else
      {
        while (compare(theObject, elementAt(position)) > 0)
        {
          System.out.println("Position: " + String.valueOf(position));
          position++;
          if (position == size())
            break;
        }
        insertElementAt(theObject, position);
        System.out.println("Element insterted into " + String.valueOf(position));
      }
    }
  }
}
