package org.rcosjava.software.animator.ipc;

import java.util.*;
import org.rcosjava.software.util.LIFOQueue;

/**
 * Provides a way to display the list of processes using the currently selected
 * shared memory segment or semaphore.
 * <P>
 * @author Andrew Newman.
 * @created 1st March 2001
 * @version 1.00 $Date$
 */
public class SemaphoreSharedMemoryGraphic
{
  /**
   * The processes connected to the semaphore or shared memory segment.
   */
  private LIFOQueue attachedProcesses;

  /**
   * The value of the shared memory or semaphore segment.
   */
  private Object value;

  /**
   * Create a new memory graphic with the given process id and value.
   *
   * @param newProcess the process that is creating this semaphore or shared
   *      memory.
   * @param newValue the value of the semaphore or shared memory.
   */
  public SemaphoreSharedMemoryGraphic(int newProcess, Object newValue)
  {
    attachedProcesses = new LIFOQueue(10, 1);
    attachedProcesses.add(new Integer(newProcess));
    value = newValue;
  }

  /**
   * Sets a new value to the segment.
   *
   * @param newValue the value of the new segment.
   */
  public void setValue(Object newValue)
  {
    value = newValue;
  }

  /**
   * Returns the queue to go through the processes attached.
   *
   * @return the queue to go through the processes attached.
   */
  public LIFOQueue getAttachedProcesses()
  {
    return attachedProcesses;
  }

  /**
   * Returns the value of the segment.
   *
   * @return the value of the segment.
   */
  public Object getValue()
  {
    return value;
  }

  /**
   * Remove the first process from the queue.
   *
   * @return the Id of the first process.
   */
  public int removeFirstProcess()
  {
    attachedProcesses.goToHead();
    return ((Integer) attachedProcesses.retrieveCurrent()).intValue();
  }

  /**
   * Adds a new process to the queue.
   *
   * @param newProcess the process id to add to the queue.
   */
  public void addProcess(int newProcess)
  {
    attachedProcesses.insert(new Integer(newProcess));
  }

  /**
   * Removes an old process from the queue.
   *
   * @param oldProcess the process id to remove from the queue.
   */
  public void removeProcess(int oldProcess)
  {
    attachedProcesses.remove(new Integer(oldProcess));
  }

  /**
   * Returns the total number of attached processes.
   *
   * @return the total number of attached processes.
   */
  public int attachedProcesses()
  {
    return attachedProcesses.size();
  }
}
