package net.sourceforge.rcosjava.software.util;

import java.util.*;

/**
 * Implements Last In First Out type of queue.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd February 1996
 */
public class LIFOQueue extends BaseQueue
{
  public LIFOQueue()
  {
    super();
  }

  public LIFOQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  public void insert(Object theObject)
  {
    insertElementAt(theObject, itemCount());
  }

  public LIFOQueue(int initialCapacity, int capacityIncrement,
    Iterator newObjects)
  {
    super(initialCapacity, capacityIncrement, newObjects);
  }
}
