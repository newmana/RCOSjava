package org.rcosjava.software.animator.ipc;

import java.util.*;
import org.rcosjava.software.util.FIFOQueue;

/**
 * Shows the current value of the semaphore together with the processes
 * currently using it.
 *
 * @author Andrew Newman.
 * @created 1st March 2001
 * @version 1.00 $Date$
 */
public class SemaphoreGraphic
{
  /**
   * The list of process attached to an individual piece of memory.
   */
  private FIFOQueue attachedProcesses;

  /**
   * The current value of the semaphore.
   */
  private int value;

  /**
   * Constructor for the SemaphoreGraphic object
   *
   * @param newProcess Description of Parameter
   * @param newValue Description of Parameter
   */
  public SemaphoreGraphic(int newProcess, int newValue)
  {
    attachedProcesses = new FIFOQueue(10, 1);
    attachedProcesses.add(new Integer(newProcess));
    value = newValue;
  }

  /**
   * Sets the Value attribute of the SemaphoreGraphic object
   *
   * @param newValue The new Value value
   */
  public void setValue(int newValue)
  {
    value = newValue;
  }

  /**
   * Gets the AttachedProcesses attribute of the SemaphoreGraphic object
   *
   * @return The AttachedProcesses value
   */
  public Iterator getAttachedProcesses()
  {
    return attachedProcesses.iterator();
  }

  /**
   * Gets the Value attribute of the SemaphoreGraphic object
   *
   * @return The Value value
   */
  public int getValue()
  {
    return value;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public int removeFirstProcess()
  {
    attachedProcesses.goToHead();
    return ((Integer) attachedProcesses.retrieveCurrent()).intValue();
  }

  /**
   * Adds a feature to the Process attribute of the SemaphoreGraphic object
   *
   * @param newProcess The feature to be added to the Process attribute
   */
  public void addProcess(int newProcess)
  {
    attachedProcesses.insert(new Integer(newProcess));
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public int attachedProcesses()
  {
    return attachedProcesses.size();
  }
}
