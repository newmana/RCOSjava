package org.rcosjava.software.animator.memory;

import java.io.Serializable;

/**
 * Represents the current state of a memory object.
 * <P>
 * @author Andrew Newman
 * @created 28th April 2002
 * @version 1.00 $Date$
 */
public class MemoryState implements Serializable
{
  /**
   * Memory type
   */
  private byte memoryType;

  /**
   * Process ID.
   */
  private int PID;

  /**
   * Allocated memory or not.
   */
  private boolean allocated;

  /**
   * Is being read.
   */
  private boolean beingRead;

  /**
   * Is being written
   */
  private boolean beingWritten;

  /**
   * Create a new memory state.
   *
   * @param newMemoryType memory type.
   * @param newPID process id.
   * @param newAllocated allocated memory.
   */
  public MemoryState(byte newMemoryType, int newPID, boolean newAllocated)
  {
    memoryType = newMemoryType;
    PID = newPID;
    allocated = newAllocated;
  }

  /**
   * Returns memory type.
   *
   * @return memory type.
   */
  public byte getMemoryType()
  {
    return memoryType;
  }

  /**
   * Sets the memory type.
   *
   * @param newMemoryType memory type.
   */
  public void setMemoryType(byte newMemoryType)
  {
    memoryType = newMemoryType;
  }

  /**
   * Returns process id.
   *
   * @param return process id.
   */
  public int getPID()
  {
    return PID;
  }

  /**
   * Sets a new PID.
   *
   * @param newPID new PID.
   */
  public void setPID(int newPID)
  {
    PID = newPID;
  }

  /**
   * Sets it allocated.
   */
  public void allocated()
  {
    allocated = true;
  }

  /**
   * Sets unallocated.
   */
  public void unallocated()
  {
    allocated = false;
  }

  /**
   * Sets it being read.
   */
  public void beingRead()
  {
    beingRead = true;
  }

  /**
   * Sets not being read.
   */
  public void finishedBeingRead()
  {
    beingRead = false;
  }

  /**
   * Sets it being written.
   */
  public void beingWritten()
  {
    beingWritten = true;
  }

  /**
   * Sets not being written.
   */
  public void finishedBeingWritten()
  {
    beingWritten = false;
  }

  /**
   * Returns true if allocated.
   *
   * @return true if allocated.
   */
  public boolean isAllocated()
  {
    return allocated;
  }

  /**
   * Returns true if being read.
   *
   * @return true if being read.
   */
  public boolean isBeingRead()
  {
    return beingRead;
  }

  /**
   * Returns true if being written.
   *
   * @return true if being written.
   */
  public boolean isBeingWritten()
  {
    return beingWritten;
  }
}
