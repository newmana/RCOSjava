package net.sourceforge.rcosjava.software.util;

import java.util.*;

/**
 * Implements base Queue type (FIFO).
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd February 1996
 */
public class Queue extends Vector
{
  /**
   * This is an integer that points to an element in the queue.
   */
  public int thePointer;

  /**
   * Default constructor.
   */
  public Queue()
  {
    super();
    thePointer = 0;
  }

  /**
   * Constructor of the queue.
   *
   * @param initialCapacity initial size of queue.
   * @param initialIncrement the increment size in which to increase the queue
   * when it becomes full.
   */
  public Queue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
    thePointer = 0;
  }

  /**
   * Default Queue Insert - FIFO Type.
   *
   * @param theObject the object type to store in the queue.
   */
  public void insert(Object theObject)
  {
    insertElementAt(theObject, 0);
  }

  /**
   * @return an element at the top of the queue, remove it and return it.
   */
  public Object retrieve()
  {
    if (queueEmpty()) return null;

    Object theObject = elementAt(itemCount()-1);

    removeElementAt(itemCount()-1);
    return theObject;
  }

  /**
   * @return the element currently pointed to by thePointer (removing it from
   * the queue).
   */
  public Object retrieveCurrent()
  {
    if (queueEmpty()) return null;

    Object theObject = elementAt(thePointer);

    removeElementAt(thePointer);
    return theObject;
  }

  /**
   * @return -1, 0, 1 if the first object is less than, equal to or
   * greater than the second object.
   */
  public int compare (Object first, Object second)
  {
    String firstString = first.toString();
    String secondString = second.toString();
    return (firstString.compareTo(secondString));
  }

  /**
   * Return position of an object or -1 if not found.
   */
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

  /**
   * Return an element of the queue.
   *
   * @param index the location in the queue to find the object.
   */
  public Object peek(int index)
  {
    if (queueEmpty())
      return null;
    return elementAt(index);
  }

  /**
   * @return element at thePointer location
   */
  public Object peek()
  {
    if (queueEmpty())
      return null;
    return elementAt(thePointer);
  }

  /**
   * return True if the queue is empty.
   */
  public boolean queueEmpty()
  {
    return (this.size() == 0);
  }

  /**
   * @return the size of the queue.
   */
  public int itemCount()
  {
    return (this.size());
  }

  /**
   * Set the pointer to anywhere in the Queue
   */
  public void goTo(int index)
  {
    if ((index <= this.size()-1) && (index >= 0))
    {
      thePointer = index;
    }
  }

  /**
   * Set the pointer to the head of the Queue
   */
  public void goToHead()
  {
    thePointer = 0;
  }

  /**
   * move to next element
   */
  public void goToNext()
  {
    if (!atTail())
      thePointer++;
  }

  /**
   * move to previous element
   */
  public void goToPrevious()
  {
    if (thePointer != 0)
      thePointer--;
  }

  /**
   * Sets the pointer to the last element in the queue.
   */
  public void goToTail()
  {
    thePointer = this.size()-1;
  }

  /**
   * @return TRUE if at head of list
   */
  public boolean atHead()
  {
    return (thePointer == 0);
  }

  /**
   * @return return TRUE if at end of list
   */
  public boolean atTail()
  {
    return (thePointer == this.size());
  }
}
