//**************************************************************************
//FILE    : FIFOQueue.java
//PACKAGE : Util
//PURPOSE : Implements First In First Out type of queue.
//AUTHOR  : Andrew Newman
//MODIFIED:
//HISTORY : 02/02/96  First created.
//
//**************************************************************************

package Software.Util;

import java.util.*;

public class FIFOQueue extends Queue
{
  // Class constructor.

  public FIFOQueue()
  {
    super();
  }

  // Class constructor.

  public FIFOQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }
}

