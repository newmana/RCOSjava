package net.sourceforge.rcosjava.software.util;

import java.util.*;

/**
 * Implements a First In First Out type of queue.  The same as queue.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd February 1996
 */
public class FIFOQueue extends BaseQueue
{
  public FIFOQueue()
  {
    super();
  }

  public FIFOQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  public FIFOQueue(int initialCapacity, int capacityIncrement,
    Iterator newObjects)
  {
    super(initialCapacity, capacityIncrement, newObjects);
  }
}

