package org.rcosjava.software.util;

import java.util.*;

/**
 * Implements a based queue object. The default implementation is of a FIFO
 * (First In, First Out) algorithm. It achieves this by inserting objects at the
 * start of the Vector (which it extends) and removes objects from the end of
 * the queue.
 * <P>
 * @author Andrew Newman.
 * @created 2nd February 1996
 * @version 1.00 $Date$
 */
public class BaseQueue extends ArrayList implements Queue
{
  /**
   * This is an integer that points to an element in the queue.
   */
  private int pointer;

  /**
   * Default constructor. Cannot construct a BaseQueue from outside this package
   * must be a subclass like FIFO, LIFO, etc.
   */
  protected BaseQueue()
  {
    super();
    pointer = 0;
  }

  /**
   * Constructor of the queue. Cannot construct a BaseQueue from outside this
   * package must be a subclass like FIFO, LIFO, etc.
   *
   * @param initialCapacity initial size of queue.
   * @param capacityIncrement Description of Parameter
   */
  protected BaseQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity);
    pointer = 0;
  }

  /**
   * Create a queue with a given set of objects. Cannot construct a BaseQueue
   * from outside this package must be a subclass like FIFO, LIFO, etc.
   *
   * @param initialCapacity initial size of queue.
   * @param capacityIncrement Description of Parameter
   * @param newObjects Description of Parameter
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

  /**
   * Will set the pointer to the give value.
   *
   * @param newPointer the new value of the pointer to set as long as it's not
   *      greater than the current size of the queue or less than zero.
   */
  public void setPointer(int newPointer)
  {
    if (newPointer > size() && newPointer >= 0)
    {
      pointer = newPointer;
    }
  }

  /**
   * @return the current location of the index.
   */
  public int getPointer()
  {
    return pointer;
  }

  /**
   * Inserts an object to the first element of the queue.
   *
   * @param theObject the object to insert in.
   */
  public void insert(Object theObject)
  {
    add(0, theObject);
  }

  /**
   * Removes the element end of the queue if the queue is not null. If the queue
   * is empty it will return null.
   *
   * @return Description of the Returned Value
   */
  public Object retrieve()
  {
    if (queueEmpty())
    {
      return null;
    }

    Object theObject = get(itemCount() - 1);

    remove(itemCount() - 1);
    return theObject;
  }

  /**
   * Removes the element at the current location of the pointer in the queue. If
   * the queue is empty it will return null.
   *
   * @return Description of the Returned Value
   */
  public Object retrieveCurrent()
  {
    if (queueEmpty())
    {
      return null;
    }

    Object theObject = get(pointer);

    remove(pointer);
    return theObject;
  }

  /**
   * Does a simple string based comparison on two objects.
   *
   * @param first Description of Parameter
   * @param second Description of Parameter
   * @return the results of a compareTo on the string values of the object.
   */
  public int compare(Object first, Object second)
  {
    String firstString = first.toString();
    String secondString = second.toString();

    return (firstString.compareTo(secondString));
  }

  /**
   * Based on the object given it will return the index of that object within
   * the object's queue.
   *
   * @param item the item to find in the queue.
   * @return the integer value of the position of the object if it's found
   *      otherwise it will be -1.
   */
  public int position(Object item)
  {
    int where;
    int currentPos;

    where = 0;
    currentPos = pointer;
    goToHead();
    while (!atTail())
    {
      if (compare(item, retrieveCurrent()) == 0)
      {
        where = pointer;
        pointer = currentPos;
        return where;
      }
      goToNext();
    }
    return -1;
  }

  /**
   * This is a non-distructive way to determine what object is at a certain
   * index location.
   *
   * @param index the position of the element to return the object
   * @return the object found at the location - null if the queue is empty.
   */
  public Object peek(int index)
  {
    if (queueEmpty())
    {
      return null;
    }
    return get(index);
  }

  /**
   * This is a non-distructive way to determine what object is at the current
   * location of the index.
   *
   * @return the object found at the current location - null if the queue is
   *      empty.
   */
  public Object peek()
  {
    if (queueEmpty())
    {
      return null;
    }
    return get(pointer);
  }

  /**
   * @return if the size of the queue is zero it will be true.
   */
  public boolean queueEmpty()
  {
    return (size() == 0);
  }

  /**
   * @return the number of elements located in the queue.
   */
  public int itemCount()
  {
    return size();
  }

  /**
   * Sets the index of the queue to the given value if it is not bigger than the
   * size of the queue and is greater than or equal to zero.
   *
   * @param index the index to set the pointer value to.
   */
  public void goTo(int index)
  {
    if ((index <= size() - 1) && (index >= 0))
    {
      pointer = index;
    }
  }

  /**
   * Sets the pointer to the top of the queue.
   */
  public void goToHead()
  {
    pointer = 0;
  }

  /**
   * If the index is not at the end of the queue it will increment the index's
   * location.
   */
  public void goToNext()
  {
    if (!atTail())
    {
      pointer++;
    }
  }

  /**
   * If the index is not at the start of the queue it will decrement the
   * pointers location.
   */
  public void goToPrevious()
  {
    if (pointer != 0)
    {
      pointer--;
    }
  }

  /**
   * Will set the pointer to the end of the queue.
   */
  public void goToTail()
  {
    pointer = size() - 1;
  }

  /**
   * Will set the index to the start of the queue.
   *
   * @return Description of the Returned Value
   */
  public boolean atHead()
  {
    return (pointer == 0);
  }

  /**
   * @return true if the index is currently at the last item.
   */
  public boolean atTail()
  {
    return (pointer == size());
  }
}
