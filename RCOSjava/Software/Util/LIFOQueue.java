//**************************************************************************
//FILE    : LIFOQueue.java
//PACKAGE : Util
//PURPOSE : Implements Last In First Out type of queue.
//AUTHOR  : Andrew Newman
//MODIFIED:
//HISTORY : 02/02/96  First created.
//
//**************************************************************************

package Software.Util;

import java.util.*;

public class LIFOQueue extends Queue
{
  //Class constructor.

  public LIFOQueue()
  {
    super();
  }
 
  //Class constructor.

  public LIFOQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }
 
  // LIFO Queue Insert.

  public void insert(Object theObject)
  {
    insertElementAt(theObject, itemCount());
  }
}

