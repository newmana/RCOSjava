package org.rcosjava.software.process;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Enumeration of the priority of a process.
 * <P>
 * @author Andrew Newman.
 * @created 15th November 2002
 * @version 1.00 $Date$
 */
public class ProcessPriority implements Serializable, Comparable
{
  /**
   * The numeric value of the lowest priority that a process can have.
   */
  public static final ProcessPriority MINIMUM_PRIORITY =
      new ProcessPriority((byte) 1);

  /**
   * The numeric value of the default priority that a process can have.
   */
  public static final ProcessPriority DEFAULT_PRIORITY =
      new ProcessPriority((byte) 50);

  /**
   * The numeric value of the highest priority that a process can have.
   */
  public static final ProcessPriority MAXIMUM_PRIORITY =
      new ProcessPriority((byte) 100);

  /**
   * The numeric value of the priority.
   */
  private byte priorityValue;

  /**
   * Create a process priority.
   *
   * @param priorityValue numeric value of the priority.
   */
  public ProcessPriority(byte newPriorityValue)
  {
    priorityValue = newPriorityValue;
  }

  /**
   * Returns the value of the process priority.
   *
   * @return the value of the process priority.
   */
  public byte getPriorityValue()
  {
    return priorityValue;
  }

  /**
   * Sets the process priority value.
   *
   * @param newPriorityValue new priority value.
   */
  public void setPriorityValue(byte newPriorityValue)
  {
    priorityValue = newPriorityValue;
  }

  /**
   * Compares two process priority objects.
   *
   * @param object ProcessPriority object to compare with.
   * @return -1 if the priority of this is less than the given process, 0
   *      if it is equal and 1 if it is greater than.
   */
  public int compareTo(Object object)
  {
    ProcessPriority tmpPriority = (ProcessPriority) object;
    int result = 0;

    if (getPriorityValue() < tmpPriority.getPriorityValue())
    {
      result = -1;
    }
    else if (getPriorityValue() > tmpPriority.getPriorityValue())
    {
      result = 1;
    }

    return result;
  }
}
