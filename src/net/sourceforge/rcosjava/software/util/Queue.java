package net.sourceforge.rcosjava.software.util;

import java.util.*;

/**
 * Based Queue interface.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd February 1996
 */
public interface Queue
{
  /**
   * Default Queue Insert.
   *
   * @param theObject the object type to store in the queue.
   */
  public void insert(Object theObject);

  /**
   * @return an element at the top of the queue, remove it and return it.
   */
  public Object retrieve();

  /**
   * @return the element currently pointed to by thePointer (removing it from
   * the queue).
   */
  public Object retrieveCurrent();

  /**
   * @return -1, 0, 1 if the first object is less than, equal to or
   * greater than the second object.
   */
  public int compare(Object first, Object second);

  /**
   * Return position of an object or -1 if not found.
   */
  public int position (Object item);

  /**
   * Return an element of the queue.
   *
   * @param index the location in the queue to find the object.
   */
  public Object peek(int index);

  /**
   * @return element at thePointer location
   */
  public Object peek();

  /**
   * return True if the queue is empty.
   */
  public boolean queueEmpty();

  /**
   * @return the size of the queue.
   */
  public int itemCount();

  /**
   * Set the pointer to anywhere in the Queue
   */
  public void goTo(int index);

  /**
   * Set the pointer to the head of the Queue
   */
  public void goToHead();

  /**
   * move to next element
   */
  public void goToNext();

  /**
   * move to previous element
   */
  public void goToPrevious();

  /**
   * Sets the pointer to the last element in the queue.
   */
  public void goToTail();

  /**
   * @return TRUE if at head of list
   */
  public boolean atHead();

  /**
   * @return return TRUE if at end of list
   */
  public boolean atTail();
}
