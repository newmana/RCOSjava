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
  public PriorityQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  public PriorityQueue(int initialCapacity, int capacityIncrement,
    Iterator newObjects)
  {
    super(initialCapacity, capacityIncrement, newObjects);
  }

  /**
   * Priority Queue Insert.
   */
  public void insert(Object theObject)
  {
    int position = 0;

    // If it's the first element in the queue then it will have the highest priority.
    if (queueEmpty())
    {
      //System.out.println("First element in queue!! or less than or equal to first");
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
          //System.out.println("Position: " + String.valueOf(position));
          position++;
          if (position == size())
            break;
        }
        insertElementAt(theObject, position);
        //System.out.println("Element insterted into " + String.valueOf(position));
      }
    }
  }
}
