package net.sourceforge.rcosjava.software.memory;

import java.io.Serializable;
import net.sourceforge.rcosjava.hardware.memory.Memory;

/**
 * Structure sent to the MMU requesting pages or byte-count memory.
 * <P>
 * HISTORY: 01/01/1998  get/set added, constructors added.<BR>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @version 1.00 $Date$
 * @created 1st March 1997
 */
public class MemoryRequest implements Serializable
{
  /**
   * The process that sent the request.
   */
  private int processId;

  /**
   * The type of memory (stack or program)
   */
  private byte memoryType;

  /**
   * How much memory was requested?
   */
  private int size;

  /**
   * The offset in a memory block (if required).
   */
  private int offset;

  /**
   * The block of memory requested.
   */
  private Memory myMemory;

  /**
   * Create a memory request without an offset (set to 0).  With no memory
   * block.
   */
  public MemoryRequest(int newProcessId, byte newMemoryType, int newSize)
  {
    processId = newProcessId;
    memoryType = newMemoryType;
    size = newSize;
    offset = 0;
  }

  /**
   * Create a memory request without an offset (set to 0).
   */
  public MemoryRequest(int newProcessId, byte newMemoryType, int newSize,
    Memory newMemory)
  {
    this(newProcessId, newMemoryType, newSize);
    myMemory = newMemory;
  }

  /**
   * Create a memory request without a memory block but with an offset.
   */
  public MemoryRequest(int newProcessId, byte newMemoryType, int newSize,
    int newOffset)
  {
    this(newProcessId, newMemoryType, newSize);
    offset = newOffset;
  }

  /**
   * Create a full memory request.
   */
  public MemoryRequest(int newProcessId, byte newMemoryType, int newSize,
    int newOffset, Memory newMemory)
  {
    this(newProcessId, newMemoryType, newSize, newOffset);
    myMemory = newMemory;
  }

  /**
   * Return the stored Process Identifier.
   */
  public int getPID()
  {
    return processId;
  }

  /**
   * Return the memory type.
   */
  public byte getMemoryType()
  {
    return memoryType;
  }

  /**
   * Set the memory type.
   */
  public void setMemoryType(byte newMemoryType)
  {
    memoryType = newMemoryType;
  }

  /**
   * Return the size of the memory block.
   */
  public int getSize()
  {
    return size;
  }

  /**
   * Set the size of the memory block.
   */
  public void setSize(int newSize)
  {
    size = newSize;
  }

  /**
   * Return the memory offset.
   */
  public int getOffset()
  {
    return offset;
  }

  /**
   * Return a copy of the memory block.
   */
  public Memory getMemory()
  {
    return (Memory) myMemory.clone();
  }

  /**
   * Set the new memory block.
   */
  public void setMemory(Memory newMemory)
  {
    myMemory = newMemory;
  }
}
