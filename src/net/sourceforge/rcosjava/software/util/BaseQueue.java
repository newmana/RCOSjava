package net.sourceforge.rcosjava.software.util;

import java.util.*;

/**
 * Implements base Queue type (FIFO).
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd February 1996
 */
public class BaseQueue extends Vector implements Queue
{
  /**
   * This is an integer that points to an element in the queue.
   */
  public int thePointer;

  /**
   * Default constructor.  Cannot construct a BaseQueue from outside this
   * package must be a subclass like FIFO, LIFO, etc.
   */
  protected BaseQueue()
  {
    super();
    thePointer = 0;
  }

  /**
   * Constructor of the queue.  Cannot construct a BaseQueue from outside this
   * package must be a subclass like FIFO, LIFO, etc.
   *
   * @param initialCapacity initial size of queue.
   * @param initialIncrement the increment size in which to increase the queue
   * when it becomes full.
   */
  protected BaseQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
    thePointer = 0;
  }

  /**
   * Create a queue with a given set of objects.  Cannot construct a BaseQueue
   * from outside this package must be a subclass like FIFO, LIFO, etc.
   *
   * @param initialCapacity initial size of queue.
   * @param initialIncrement the increment size in which to increase the queue
   * when it becomes full.
   * @param objectIterator
   */
  protected BaseQueue(int initialCapacity, int capacityIncrement,
    Iterator newObjects)
  {
    this(initialCapacity, capacityIncrement);
    while (newObjects.hasNext())
    {
      insert(newObjects.next());
    }
  }

  public void insert(Object theObject)
  {
    insertElementAt(theObject, 0);
  }

  public Object retrieve()
  {
    if (queueEmpty()) return null;

    Object theObject = elementAt(itemCount()-1);

    removeElementAt(itemCount()-1);
    return theObject;
  }

  public Object retrieveCurrent()
  {
    if (queueEmpty()) return null;

    Object theObject = elementAt(thePointer);

    removeElementAt(thePointer);
    return theObject;
  }

  public int compare(Object first, Object second)
  {
    String firstString = first.toString();
    String secondString = second.toString();
    return (firstString.compareTo(secondString));
  }

  public int position (Object item)
  {
    int iWhere, iCurrentPos;
    iWhere = 0;
    iCurrentPos = thePointer;
    goToHead();
    while (!atTail())
    {
      if (compare(item,retrieveCurrent()) == 0)
      {
        iWhere = thePointer;
        thePointer = iCurrentPos;
        return iWhere;
      }
      goToNext();
    }
    return -1;
  }

  public Object peek(int index)
  {
    if (queueEmpty())
      return null;
    return elementAt(index);
  }

  public Object peek()
  {
    if (queueEmpty())
      return null;
    return elementAt(thePointer);
  }

  public boolean queueEmpty()
  {
    return (this.size() == 0);
  }

  public int itemCount()
  {
    return (this.size());
  }

  public void goTo(int index)
  {
    if ((index <= this.size()-1) && (index >= 0))
    {
      thePointer = index;
    }
  }

  public void goToHead()
  {
    thePointer = 0;
  }

  public void goToNext()
  {
    if (!atTail())
      thePointer++;
  }

  public void goToPrevious()
  {
    if (thePointer != 0)
      thePointer--;
  }

  public void goToTail()
  {
    thePointer = this.size()-1;
  }

  public boolean atHead()
  {
    return (thePointer == 0);
  }

  public boolean atTail()
  {
    return (thePointer == this.size());
  }
}
