package net.sourceforge.rcosjava.software.util;

import java.util.*;

/**
 * Implements priority queue (based on compareTo value).
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 09/05/2001 Changed insert to use comparable objects instead of string
 * vales.
 * </DD></DT>
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
   * (because elements are removed at the end).  Requires that the process
   * extends java.lang.Comparable so that compareTo may-be called.
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
      if (theObject instanceof java.lang.Comparable)
      {
        Comparable compObject = (Comparable) theObject;
        if (compObject.compareTo(elementAt(position)) == 0)
          insertElementAt(theObject, 0);
        else
        {
          while (compObject.compareTo(elementAt(position)) > 0)
          {
            position++;
            if (position == size())
              break;
          }
          insertElementAt(theObject, position);
        }
      }
    }
  }
}
