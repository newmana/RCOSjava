package org.rcosjava.software.animator.ipc;

import java.io.*;
import java.util.*;

/**
 * Represents a semaphore.
 * <P>
 * @author Andrew Newman
 * @created 28th April 2002
 * @version 1.00 $Date$
 */
public class Semaphore implements Serializable
{
  /**
   * The name of the semaphore.
   */
  private String name;

  /**
   * The process the created it.
   */
  private int creatorId;

  /**
   * The attached processes.
   */
  private ArrayList attachedProcesses = new ArrayList();

  /**
   * The waiting processes.
   */
  private ArrayList waitingProcesses = new ArrayList();

  /**
   * Value
   */
  private int value;

  /**
   * Create a new semaphore with the given creator process id and value.
   *
   * @param newName the name of the semaphore.
   * @param newCreatorId the id of the process creating the semaphore.
   * @param newValue the initial value of the semaphore.
   */
  public Semaphore(String newName, int newCreatorId, int newValue)
  {
    name = newName;
    creatorId = newCreatorId;
    value = newValue;
  }

  /**
   * Returns the name of the semaphore.
   *
   * @return the name of the semaphore.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the PID of the process that created the object.
   *
   * @return the PID of the process that created the object.
   */
  public int getCreatorId()
  {
    return creatorId;
  }

  /**
   * Attaches a process to the semaphore.
   *
   * @param newProcessId the id of the process to add to the semaphore.
   */
  public void attachProcess(int newProcessId)
  {
    attachedProcesses.add(new Integer(newProcessId));
  }

  /**
   * Remove a process from the semaphore.
   *
   * @param existingProcessId the id of the process to remove from the semaphore.
   */
  public void removeProcess(int existingProcessId)
  {
    attachedProcesses.remove(new Integer(existingProcessId));
  }

  /**
   * Adds a waiting process to the semaphore.
   *
   * @param newProcessId the id of the process to wait on the semaphore.
   */
  public void addWaitingProcess(int newProcessId)
  {
    waitingProcesses.add(new Integer(newProcessId));
  }

  /**
   * Remove a process waiting on the semaphore.
   *
   * @param existingProcessId the id of the process to remove from waiting for
   *     the semaphore.
   */
  public void removeWaitingProcess(int existingProcessId)
  {
    waitingProcesses.remove(new Integer(existingProcessId));
  }

  /**
   * Sets the value of the semaphore.
   *
   * @param newValue the new value to set the semaphore to.
   */
  public void setValue(int newValue)
  {
    value = newValue;
  }

  /**
   * Returns the value of the semaphore.
   *
   * @return the value of the semaphore.
   */
  public int getValue()
  {
    return value;
  }
}
