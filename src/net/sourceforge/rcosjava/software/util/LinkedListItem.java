package net.sourceforge.rcosjava.software.util;

import java.lang.Object;

/**
 * An item for the linked list.
 * <P>
 * @author Brett Carter.
 * @version 1.00 $Date$
 * @created 26th March 1996
 */
class LinkedListItem
{
  public LinkedListItem prev = null;
  public LinkedListItem next = null;
  public Object data = null;

  public void LinkedListItem(LinkedListItem newPrev, LinkedListItem newNext,
    Object newData)
  {
    prev = newPrev;
    next = newNext;
    data = newData;
  }

  public void LinkedListItem()
  {
    prev = null;
    next = null;
    data = null;
  }
}
