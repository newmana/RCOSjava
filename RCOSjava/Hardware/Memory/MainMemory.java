// ****************************************************/
// FILE     : MainMemory.java
// PURPOSE  : Contains total addressable memory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/07/97  Created
// ****************************************************/

package Hardware.Memory;

import java.lang.Integer;

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
    return -1;
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
