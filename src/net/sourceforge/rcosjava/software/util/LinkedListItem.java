//**************************************************************************
//FILE    : LinkedListItem.java
//PACKAGE : Util
//PURPOSE : Item for a linked list.
//AUTHOR  : Brett Carter
//MODIFIED:
//HISTORY : 26/3/96 Created.
//
//**************************************************************************

package net.sourceforge.rcosjava.software.util;

import java.lang.Object;

class LinkedListItem
{
  public LinkedListItem prev = null;
  public LinkedListItem next = null;
  public Object data = null;

  public void LinkedListItem( LinkedListItem aPrev,
                              LinkedListItem aNext, Object aData )
  {
    prev = aPrev;
    next = aNext;
    data = aData;
  }

  public void LinkedListItem()
  {
    prev = null;
    next = null;
    data = null;
  }
}
