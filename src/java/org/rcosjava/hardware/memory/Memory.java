package org.rcosjava.hardware.memory;

import java.io.*;

/**
 * Basic Memory type - basically an array of short. Provides utils for
 * block/single byte read/write.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 10/08/1999 Added segment status as a separate property. </DD> </DD>
 * 19/5/2001 Fixed it so that setFree creates a new memory segment. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @created 1st February 1996
 * @version 1.00 $Date$
 */
public class Memory implements Serializable, Cloneable
{
  /**
   * Defines the size of a a segment of memory.
   */
  public final static short DEFAULT_SEGMENT = 1024;

  /**
   * The segment size of memory.
   */
  private short memorySegment[];

  /**
   * Segment status is either allocated or free.
   */
  private boolean allocated = false;

  /**
   * The size of the segment in bytes.
   */
  private int segmentSize;

  /**
   * Create a memory class with the size of DEFAULT_SEGMENT. By default is set
   * to be free.
   */
  public Memory()
  {
    memorySegment = new short[DEFAULT_SEGMENT];
    segmentSize = DEFAULT_SEGMENT;
    setFree();
  }

  /**
   * Create a memory class with the given segment size.
   *
   * @param newSegmentSize the number of bytes per memory segment that a block
   *      is to store.
   */
  public Memory(int newSegmentSize)
  {
    memorySegment = new short[newSegmentSize];
    segmentSize = newSegmentSize;
    setFree();
  }

  /**
   * Create a memory class with the pre-exisiting memory. Writes the memory to
   * this new memory block.
   *
   * @param memory the string representation of bytes of a memory block.
   */
  public Memory(String memory)
  {
    this(memory.length());
    this.write(memory.getBytes());
  }

  /**
   * Allows two memory blocks to be combined into one. The total size will be
   * combined segment sizes of the first and the second memory segment.
   *
   * @param firstMem the first memory segment.
   * @param secondMem the second memory segment.
   * @return the combined memory segment.
   */
  public static Memory combineMemory(Memory firstMem, Memory secondMem)
  {
    Memory newMemory = new Memory(firstMem.getSegmentSize() +
        secondMem.getSegmentSize());

    for (int count = 0; count < firstMem.getSegmentSize(); count++)
    {
      newMemory.memorySegment[count] = firstMem.memorySegment[count];
    }

    for (int count = 0; count < secondMem.getSegmentSize(); count++)
    {
      newMemory.memorySegment[count + firstMem.getSegmentSize()] =
          secondMem.memorySegment[count];
    }

    return (newMemory);
  }

  /**
   * Sets the memory to be unallocated and resets the memory segment to its null
   * value.
   */
  public void setFree()
  {
    allocated = false;
    memorySegment = new short[segmentSize];
  }

  /**
   * Sets the memory is allocated.
   */
  public void setAllocated()
  {
    allocated = true;
  }

  /**
   * @return whether the memory block is unallocated/free.
   */
  public boolean isFree()
  {
    return (!allocated);
  }

  /**
   * @return whether the memory block is allocated/not free.
   */
  public boolean isAllocated()
  {
    return (allocated);
  }

  /**
   * @return the segments size in bytes that the memory block can hold.
   */
  public int getSegmentSize()
  {
    return segmentSize;
  }

  /**
   * @return the stored memory segment (not a copy).
   */
  public short[] getMemorySegment()
  {
    return memorySegment;
  }

  /**
   * Returns the single segment in the memory block.
   *
   * @param location Description of Parameter
   * @return the one segment in the memory block.
   */
  public short getOneMemorySegment(int location)
  {
    return memorySegment[location];
  }

  /**
   * Creates a new memory segment based on start offset, length or size and
   * whether it should be set to be allocated.
   *
   * @param start offset in the memory segment to start from.
   * @param size the length or total size of the segment to return.
   * @param isAllocated whether the memory segment is configure to be allocated
   *      or not.
   * @return the newly create memory segment.
   */
  public Memory segmentMemory(int start, int size, boolean isAllocated)
  {
    Memory newMemory = new Memory(size);

    for (int count = 0; count < size; count++)
    {
      newMemory.memorySegment[count] = memorySegment[count + start];
    }
    if (isAllocated)
    {
      newMemory.setAllocated();
    }
    return (newMemory);
  }

  /**
   * Read a short from the segment at Offset.
   *
   * @param offset (index) to use to return the memory.
   * @return the value located in the offset.
   */
  public short read(int offset)
  {
    return memorySegment[offset];
  }

  /**
   * Read a block of memory beginning from the offset location to the total
   * number of bytes.
   *
   * @param offset where to start reading from in the memory segment.
   * @param byteCount the number of bytes to read from the memory segment.
   * @return the new created memory segment which is the size of the given
   *      byteCount.
   */
  public Memory read(int offset, int byteCount)
  {
    // Read a block
    Memory MemBlock = new Memory(byteCount);

    for (int count = 0; count < byteCount; count++)
    {
      MemBlock.write(count, memorySegment[offset + count]);
    }
    // No exception handling - Bounds exceptions???
    return MemBlock;
  }

  /**
   * Write a value to the memory segment give an offset from which to use as an
   * index.
   *
   * @param offset the index or offset to write to the memory segment. Assumed
   *      to be less than the maximum number. Otherwise will cause a OutOfBounds
   *      exception.
   * @param instruction the instruction or value to set to the memory location.
   */
  public void write(int offset, short instruction)
  {
    // Write an Instruction to location Offset
    memorySegment[offset] = instruction;
  }

  /**
   * The integer version of
   *
   * @param offset Description of Parameter
   * @param instruction Description of Parameter
   * @see #write(int, short). Will convert the int to a short.
   */
  public void write(int offset, int instruction)
  {
    // Write an Instruction to location Offset
    memorySegment[offset] = (short) instruction;
  }

  /**
   * Writes a block of bytes (converting them to shorts) to this memory segment.
   *
   * @param myMemory the memory values to set to the memory segment assumed to
   *      be the same size as the memory segment.
   */
  public void write(byte[] myMemory)
  {
    for (int count = 0; count < myMemory.length; count++)
    {
      memorySegment[count] = (short) myMemory[count];
    }
  }

  /**
   * Writes a block of shorts to this memory segment.
   *
   * @param myMemory the memory values to set to the memory segment assumed to
   *      be the same size as the memory segment.
   */
  public void write(short[] myMemory)
  {
    for (int count = 0; count < myMemory.length; count++)
    {
      memorySegment[count] = myMemory[count];
    }
  }

  /**
   * Writes a memory block. Assumes that the offset plus the length of the new
   * memory block does not exceed the maximum size of this memory segment. No
   * checking is done.
   *
   * @param offset the offset of the memory writing to begin at.
   * @param someMemory the memory to write to the segment.
   */
  public void write(int offset, Memory someMemory)
  {
    // Write a block
    int blockSize = someMemory.getSegmentSize();

    for (int count = 0; count < blockSize; count++)
    {
      memorySegment[offset + count] = someMemory.read(count);
    }
    // No exception handling
  }

  /**
   * Create a duplicate memory.
   *
   * @return Description of the Returned Value
   */
  public Object clone()
  {
    try
    {
      Memory newObject = (Memory) super.clone();

      if (this.memorySegment != null)
      {
        newObject.memorySegment = (short[]) this.memorySegment.clone();
      }
      return newObject;
    }
    catch (java.lang.CloneNotSupportedException e)
    {
    }
    return null;
  }

  /**
   * Tests equality of the memory segment based on if it is allocated, the
   * segment size and the values stored in the segment.
   *
   * @param obj Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean equals(Object obj)
  {
    if (obj != null && (obj.getClass().equals(this.getClass())))
    {
      Memory mem = (Memory) obj;

      if ((allocated == mem.allocated) && (segmentSize == mem.segmentSize))
      {
        for (int count = 0; count < getSegmentSize(); count++)
        {
          if (memorySegment[count] != mem.memorySegment[count])
          {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the string representation of the memory block which is simply
   * appending the short values to a string.
   *
   * @return the string representation of the memory block which is simply
   *      appending the short values to a string.
   */
  public String toString()
  {
    StringBuffer toReturn = null;

    if (memorySegment != null)
    {
      toReturn = new StringBuffer();
      for (int i = 0; i < segmentSize; i++)
      {
        toReturn.append(memorySegment[i]);
      }
    }
    return toReturn.toString();
  }
  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
    os.writeInt(segmentSize);
    os.writeBoolean(allocated);
    for (int index = 0; index < memorySegment.length; index++)
    {
      os.writeShort(memorySegment[index]);
    }
  }

  /**
   * Handle deserialization of the contents.  Ensures non-serializable
   * components correctly created.
   *
   * @param is stream that is being read.
   */
  private void readObject(ObjectInputStream is) throws IOException,
      ClassNotFoundException
  {
    segmentSize = is.readInt();
    allocated = is.readBoolean();

    memorySegment = new short[segmentSize];

    for (int index = 0; index < segmentSize; index++)
    {
      memorySegment[index] = is.readShort();
    }
  }
}