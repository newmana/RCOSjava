package Hardware.Memory;

import java.io.Serializable;

/**
 * Basic Memory type - basically an array of short.  Provides utils for
 * block/single byte read/write.
 * <P>
 * HISTORY: 10/08/1999 Added segment status as a separate property.
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @version 1.00 $Date$
 * @created 1st February 1996
 */
public class Memory implements Serializable, Cloneable
{
  // Defines a segment of memory
  public final static short DEFAULT_SEGMENT = 1024;

  private short memorySegment[];
  //Segment status is either allocated or free.
  private boolean allocated;
  private int segmentSize;

   //Constructors
  public Memory()
  {
    memorySegment = new short[DEFAULT_SEGMENT];
    segmentSize = DEFAULT_SEGMENT;
    setFree();
  }

  public Memory(int iNewSegmentSize)
  {
    memorySegment = new short[iNewSegmentSize];
    segmentSize = iNewSegmentSize;
    setFree();
  }

  public Memory(String memory)
  {
    this(memory.length());
    this.write(memory.getBytes());
  }

  //Operations
  public void setFree()
  {
    allocated = false;
  }

  public boolean isFree()
  {
   return(!allocated);
  }

  public boolean isAllocated()
  {
   return(allocated);
  }

  public void setAllocated()
  {
   allocated = true;
  }

  public int getSegmentSize()
  {
   return segmentSize;
  }

  public short[] getMemorySegment()
  {
   return memorySegment;
  }

  public short getOneMemorySegment(int location)
  {
   return memorySegment[location];
  }

  // Assumes Memory segments are the same size.
  public static Memory combineMemory(Memory firstMem, Memory secondMem)
  {
   Memory newMemory = new Memory (firstMem.getSegmentSize() +
     secondMem.getSegmentSize());

   for (int count = 0; count < firstMem.getSegmentSize(); count++)
   {
     newMemory.memorySegment[count] = firstMem.memorySegment[count];
   }

   for (int count = 0; count < secondMem.getSegmentSize(); count++)
   {
     newMemory.memorySegment[count+firstMem.getSegmentSize()] =
       secondMem.memorySegment[count];
   }

   return (newMemory);
  }

  // Return a segment of memory
  public Memory segmentMemory(int start, int size, boolean isAllocated)
  {
    Memory newMemory = new Memory(size);
    for (int count = 0; count < size; count++)
    {
      newMemory.memorySegment[count] = memorySegment[count+start];
    }
    if (isAllocated)
      newMemory.setAllocated();
    return (newMemory);
  }

  public short read(int offset)
  {
    // Read a short from the segment at Offset;
    return memorySegment[offset];
  }

  public Memory read(int offset, int byteCount)
  {
    // Read a block

    Memory MemBlock = new Memory(byteCount);

    for(int count = 0; count < byteCount; count++)
    {
      MemBlock.write(count,memorySegment[offset+count]);
    }
    // No exception handling - Bounds exceptions???

    return MemBlock;
  }

  public void write(int offset, short instruction)
  {
    // Write an Instruction to location Offset
    memorySegment[offset] = instruction;
  }

  public void write(int offset, int instruction)
  {
    // Write an Instruction to location Offset
    memorySegment[offset] = (short) instruction;
  }

  public void write(byte[] myMemory)
  {
    for (int count = 0; count < myMemory.length; count++)
    {
      memorySegment[count] = (short) myMemory[count];
    }
  }

  public void write(int offset, Memory someMemory)
  {
    // Write a block
    int blockSize = someMemory.getSegmentSize();
    for(int count = 0; count < blockSize; count++)
    {
      memorySegment[offset+count] = someMemory.read(count);
    }
    // No exception handling
  }

  public Object clone()
  {
    try
    {
      Memory newObject = (Memory) super.clone();
      if (this.memorySegment != null)
        newObject.memorySegment = (short[]) this.memorySegment.clone();
      return newObject;
    }
    catch (java.lang.CloneNotSupportedException e)
    {
    }
    return null;
  }

  public boolean equals(Object obj)
  {
    if (obj != null && (obj.getClass().equals(this.getClass())))
    {
      Memory mem = (Memory) obj;
      if ((allocated == mem.allocated) && (segmentSize == mem.segmentSize))
      {
        for(int count = 0; count < getSegmentSize(); count++)
        {
         //System.out.println("Count: " + count + ", Mine: " + memorySegment[count] + ", Yours: " + mem.memorySegment[count]);
          if (memorySegment[count] != mem.memorySegment[count])
            return false;
        }
        return true;
      }
    }
    return false;
  }

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
}