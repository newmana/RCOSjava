package net.sourceforge.rcosjava.software.util;

/**
 * A simple implementation of a linked list.
 * <P>
 * @author Brett Carter.
 * @version 1.00 $Date$
 * @created 26th March 1996
 */
public class LinkedList
{
  LinkedListItem head;
  LinkedListItem tail;

  LinkedListItem current;
  int count;

  public void LinkedList()
  {
    head = null;
    tail = null;
    current = null;
    count = 0;
  }

  /**
   * @return the first item in the list.
   */
  public Object getFirst()
  {
    if (head != null)
    {
      current = head;
      return current.data;
    }
    else
    {
      return null;
    }
  }

  /**
   * @return the data in the last item in the list
   */
  public Object getLast()
  {
    if (tail != null)
    {
      current = tail;
      return tail.data;
    }
    else
    {
      return null;
    }
  }

  /**
   * @return the data for the next item in the list.
   */
  public Object getNext()
  {
    if ((current == null) || (current.next == null))
    {
      return null;
    }
    else
    {
      current = current.next;
      return current.data;
    }
  }

  /**
   * @return the next item in the list
   */
  public Object getPrev()
  {
    if ((current == null) || (current.prev == null))
    {
      return null;
    }
    else
    {
      current = current.prev;
      return current.data;
    }
  }

  /**
   * @return the data for the current position in the list
   */
  public Object getCurrent()
  {
    if (current != null)
    {
      return current.data;
    }
    else
    {
      return null;
    }
  }

  /**
   * @return the number of items contained in the list.
   */
  public int itemCount()
  {
    return count;
  }

  /**
   * Add anitem containing the specified data after the current item. If no
   * current item is selected, the end of the list is used.
   */
  public void addAfter( Object theData )
  {
    LinkedListItem newitem = new LinkedListItem();

    if (head == null)
    {
      newitem.next = null;
      newitem.prev = null;
      newitem.data = theData;

      head = newitem;
      tail = newitem;
      current = head;
    }
    else
    {
      if (current == null)
      {
        current = tail;
      }

      newitem.data = theData;
      newitem.prev = current;
      newitem.next = current.next;

      if (current.next != null)
      {
        current.next.prev = newitem;
      }

      current.next = newitem;

      if (tail == current)
      {
        tail = newitem;
      }
      current = newitem;
    }
    count++;
  }

  /**
   * Add the specified data top the list before the current position. If no
   * current position is selected, the start of the list is used.
   */
  public void addBefore(Object theData)
  {
    LinkedListItem newitem = new LinkedListItem();

    if (head == null)
    {
      newitem.next = null;
      newitem.prev = null;
      newitem.data = theData;

      head = newitem;
      tail = newitem;
      current = head;
    }

    else
    {
      if (current == null)
      {
        current = head;
      }

      newitem.data = theData;
      newitem.prev = current.prev;
      newitem.next = current;

      if (current.prev != null)
      {
        current.prev.next = newitem;
      }

      current.prev = newitem;

      if (head == current)
      {
        head = newitem;
      }
      current = newitem;
    }
    count++;
  }

  /**
   * @returm removes the current item from the list. If no curent item
   * is selected, -1 is returnd. Otherwise, 0 is returned.
   */
  public int remove()
  {
    if (current == null)
    {
      return -1;
    }
    else
    {
      LinkedListItem tmp = null;

      if ((current.prev == null) && (current.next == null))
      {
        head = null;
        tail = null;
        tmp = null;
      }

      else if ((current.prev != null) && (current.next != null))
      {
        current.prev.next = current.next;
        current.next.prev = current.prev;
        tmp = current.prev;
      }

      else if (current == head)
      {
        current.next.prev = null;
        head = current.next;
        tmp = head;
      }

      else if (current == tail)
      {
        current.prev.next = null;
        tail = current.prev;
        tmp = tail;
      }

      // Disconnect all from current and Garbage Collection should pick it
      // up.
      current.prev = null;
      current.next = null;
      current.data = null;
      current = tmp;

      count--;
      return 0;
    }
  }
}
