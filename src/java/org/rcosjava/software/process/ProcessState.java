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
public class ProcessState implements Serializable, Comparable
{
  /**
   * The numeric value of a process that is in a zombie state.
   */
  public final static ProcessState ZOMBIE = new ProcessState((byte) 1,
      "Zombie");

  /**
   * The numeric value of a process that is in a ready state.
   */
  public final static ProcessState READY = new ProcessState((byte) 2, "Ready");

  /**
   * The numeric value of a process that is in a running state.
   */
  public final static ProcessState RUNNING = new ProcessState((byte) 3,
      "Running");

  /**
   * The numeric value of a process that is in a blocked state.
   */
  public final static ProcessState BLOCKED = new ProcessState((byte) 4,
      "Blocked");

  /**
   * The numeric value of the priority.
   */
  private byte stateValue;

  /**
   * Literal value.
   */
  private String stateLiteralValue;

  /**
   * Create a process priority.
   *
   * @param priorityValue numeric value of the priority.
   */
  private ProcessState(byte newStateValue, String newStateLiteralValue)
  {
    stateValue = newStateValue;
    stateLiteralValue = newStateLiteralValue;
  }

  /**
   * Returns the ordinal value of the process state.
   *
   * @return the ordinal value of the process state.
   */
  protected byte getStateValue()
  {
    return stateValue;
  }

  /**
   * Returns the literal value of the process state.
   *
   * @return the literal value of the process state.
   */
  public String toString()
  {
    return stateLiteralValue;
  }

  /**
   * Compares two process state objects.
   *
   * @param object ProcessState object to compare with.
   * @return -1 if the priority of this is less than the given process, 0
   *      if it is equal and 1 if it is greater than.
   */
  public int compareTo(Object object)
  {
    ProcessState tmpProcess = (ProcessState) object;
    int result = 0;

    if (getStateValue() < tmpProcess.getStateValue())
    {
      result = -1;
    }
    else if (getStateValue() > tmpProcess.getStateValue())
    {
      result = 1;
    }

    return result;
  }
}
