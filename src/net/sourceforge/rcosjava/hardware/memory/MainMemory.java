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
 * @see Hardware.memory.Memory
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st July, 1997
 */
public class MainMemory
{
  private int totalUnits;
  private int freeUnits;
  private Memory[] myMemory;

  public MainMemory(int iMaxUnits)
  {
    totalUnits = iMaxUnits;
    freeUnits = iMaxUnits;
    myMemory = new Memory[iMaxUnits];

    for(int count = 0; count < totalUnits; count++)
    {
      myMemory[count] = new Memory();
    }
  }

  public Memory getMemory(int iLocation)
  {
    return (Memory) myMemory[iLocation].clone();
  }

  public void setMemory(int iLocation, Memory mNewMemory)
  {
    myMemory[iLocation] = mNewMemory;
  }

  public int findFirstFree()
    throws NoFreeMemoryException
  {
    // Find first deallocated page.
    int count;
    for (count = 0; count < totalUnits; count++)
    {
      if (myMemory[count].isFree())
      {
        return count;
      }
    }
    throw new NoFreeMemoryException();
  }

  public int getFreeUnits()
  {
    return freeUnits;
  }

  public void allocateMemory(int iLocation)
  {
    if (myMemory[iLocation].isFree())
    {
      myMemory[iLocation].setAllocated();
      freeUnits--;
    }
  }

  public void freeMemory(int iLocation)
  {
    if (!myMemory[iLocation].isFree())
    {
      myMemory[iLocation].setFree();
      freeUnits++;
    }
  }
}
