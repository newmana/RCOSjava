package org.rcosjava.software.util;

import java.lang.Object;

/**
 * An item for the linked list. <P>
 *
 *
 *
 * @author Brett Carter.
 * @created 26th March 1996
 * @version 1.00 $Date$
 */
class LinkedListItem
{
  /**
   * Description of the Field
   */
  public LinkedListItem prev = null;
  /**
   * Description of the Field
   */
  public LinkedListItem next = null;
  /**
   * Description of the Field
   */
  public Object data = null;

  /**
   * Description of the Method
   *
   * @param newPrev Description of Parameter
   * @param newNext Description of Parameter
   * @param newData Description of Parameter
   */
  public LinkedListItem(LinkedListItem newPrev, LinkedListItem newNext,
      Object newData)
  {
    prev = newPrev;
    next = newNext;
    data = newData;
  }

  /**
   * Description of the Method
   */
  public LinkedListItem()
  {
    prev = null;
    next = null;
    data = null;
  }
}
