package net.sourceforge.rcosjava.hardware.memory;

/**
 * Contains the total addressable memory available to the hardware sub-system.
 * <P>
 * <DT><B>Usage Example:</B><DD>
 * <CODE>
 *      MainMemory totalMemory = new MainMemory(64);
 * </CODE>
 * </DD></DT>
 * <P>
 * Creates 64 memory blocks using the default memory size (1024 bytes) or 64K.
 * <P>
 * @see net.sourceforge.rcosjava.hardware.memory.Memory
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st July, 1997
 */
public class MainMemory
{
  private int totalUnits;
  private int freeUnits;
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

    for(int count = 0; count < totalUnits; count++)
    {
      myMemory[count] = new Memory();
    }
  }

  /**
   * Given the index return the current (cloned) value of the memory.  This will
   * not return a reference to the memory just incase.  To modify it you then
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
   * @return the first index to a free memory block.
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
   * @return the number of free units available to be allocated.
   */
  public int getFreeUnits()
  {
    return freeUnits;
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
   * The tracking of a memory location is now freed up.  Sets the memory
   * to a null/unallocated value.
   *
   * @param newLocation the index to the location to free.
   */
  public void freeMemory(int location)
  {
    if (!myMemory[location].isFree())
    {
      myMemory[location].setFree();
      freeUnits++;
    }
  }
}
