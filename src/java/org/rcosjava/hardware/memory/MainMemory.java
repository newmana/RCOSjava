package org.rcosjava.hardware.memory;

import java.io.*;

/**
 * Contains the total addressable memory available to the hardware sub-system.
 * <P>
 * <DT> <B>Usage Example:</B>
 * <DD> <CODE>
 *      MainMemory totalMemory = new MainMemory(64);
 * </CODE> </DD> </DT>
 * <P>
 * Creates 64 memory blocks using the default memory size (1024 bytes) or 64K.
 * <P>
 * @author Andrew Newman.
 * @created 1st July, 1997
 * @see org.rcosjava.hardware.memory.Memory
 * @version 1.00 $Date$
 */
public class MainMemory implements Serializable
{
  /**
   * Total number of units of memory to be allocated.
   */
  private int totalUnits;

  /**
   * Free number of memory able to be allocated.
   */
  private int freeUnits;

  /**
   * The array of memory that is of size total units.
   */
  private Memory[] myMemory;

  /**
   * Create a new set of memory blocks with the given size.
   *
   * @param newMaxUnits the maximum number of memory units to create.
   */
  public MainMemory(int newMaxUnits)
  {
    totalUnits = newMaxUnits;
    freeUnits = newMaxUnits;
    myMemory = new Memory[newMaxUnits];

    for (int count = 0; count < totalUnits; count++)
    {
      myMemory[count] = new Memory();
    }
  }

  /**
   * With the given location it sets the value of the memory to that current
   * value.
   *
   * @param location the location of the memory block.
   * @param newMemory the memory value to set.
   */
  public void setMemory(int location, Memory newMemory)
  {
    myMemory[location] = newMemory;
  }

  /**
   * Given the index return the current (cloned) value of the memory. This will
   * not return a reference to the memory just incase. To modify it you then
   * have to call set.
   *
   * @param location the index to the memory location.
   * @return a copy of the memory located in the given location.
   */
  public Memory getMemory(int location)
  {
    return (Memory) myMemory[location].clone();
  }

  /**
   * @return the number of free units available to be allocated.
   */
  public int getFreeUnits()
  {
    return freeUnits;
  }

  /**
   * @return the first index to a free memory block.
   * @exception NoFreeMemoryException Description of Exception
   */
  public int findFirstFree()
    throws NoFreeMemoryException
  {
    // Find first deallocated page.
    for (int count = 0; count < totalUnits; count++)
    {
      if (myMemory[count].isFree())
      {
        return count;
      }
    }
    throw new NoFreeMemoryException();
  }

  /**
   * Allocates memory location if it is free.
   *
   * @param location the index to the memory location.
   */
  public void allocateMemory(int location)
  {
    if (myMemory[location].isFree())
    {
      myMemory[location].setAllocated();
      freeUnits--;
    }
  }

  /**
   * The tracking of a memory location is now freed up. Sets the memory to a
   * null/unallocated value.
   *
   * @param location Description of Parameter
   */
  public void freeMemory(int location)
  {
    if (!myMemory[location].isFree())
    {
      myMemory[location].setFree();
      freeUnits++;
    }
  }

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
    os.writeInt(totalUnits);
    os.writeInt(freeUnits);

    for (int count = 0; count < totalUnits; count++)
    {
      os.writeObject(myMemory[count]);
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
    totalUnits = is.readInt();
    freeUnits = is.readInt();

    myMemory = new Memory[totalUnits];
    for (int count = 0; count < totalUnits; count++)
    {
      myMemory[count] = (Memory) is.readObject();
    }
  }
}
