
//*************************************************************************/
// FILE     : PagedMemoryManagement.java
// PURPOSE  : Provides basic Memory/Page allocation.
//            It is very inefficent at the moment.
//            Probably should have two lists which
//            contains used and free memory blocks.
//            Combining and splitting memory blocks means it is
//            very slow and unrealistic.
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 27/03/96   Last Modified.
//            02/12/97   Fixed bug with deallocating memory.
//
// *************************************************************************/

package Software.Memory.Paged;

import java.lang.Integer;
import Hardware.Memory.MainMemory;
import Hardware.Memory.Memory;
import Software.Memory.MemoryReturn;
import Software.Memory.MemoryManagement;
import Software.Util.FIFOQueue;

public class PagedMemoryManagement implements MemoryManagement
{
  // Base page handler for the MMU
  private MainMemory myMainMemory;
  private FIFOQueue thePageTable;

  public PagedMemoryManagement(int MaxPages)
  {
    thePageTable = new FIFOQueue(10,1);
    myMainMemory = new MainMemory(MaxPages);
  }

  //Goes through the entire section of memory allocating
  //a specific set of memory with the same PID and
  //Type.  Size, in this case is the number of pages.
  public MemoryReturn open(int PID, int type, int size)
  {
    // Allocate pages
    int count, noPages;
    short[] pages = new short[size];
    noPages = 0;
    for (count = 0; count < size; count++)
    {
      if (myMainMemory.getFreeUnits() != 0)
      {
        int freePageIndex = myMainMemory.findFirstFree();
        if (freePageIndex >= 0)
        {
          myMainMemory.allocateMemory(freePageIndex);
          pages[noPages] = (short) freePageIndex;
          noPages++;
        }
      }
    }
    //Write allocation information to page table.
    PageTableEntry newPageTableEntry = new PageTableEntry((byte) PID,
      (byte) type, pages,(short) noPages);
    thePageTable.insert(newPageTableEntry);
    /*PageTableEntry tmpPageTable;
    thePageTable.goToHead();
    while(!thePageTable.atTail())
    {
      tmpPageTable = (PageTableEntry) thePageTable.peek();
      thePageTable.goToNext();
    }*/
    return (new MemoryReturn(PID, (byte) type, noPages, pages));
  }

  // Deallocate based on Process ID.
  public MemoryReturn close(int PID)
  {
    PageTableEntry tmpPage;

    thePageTable.goToHead();
    int totalPages=0;
    short[] indexTotalPages = new short[20];

    while (!thePageTable.atTail())
    {
      tmpPage = (PageTableEntry) thePageTable.peek();
      if (tmpPage.getPID() == ((byte) PID))
      {
        tmpPage = (PageTableEntry) thePageTable.retrieveCurrent();
        for(int count = 0; count < tmpPage.getTotalNumberOfPages(); count++)
        {
          myMainMemory.freeMemory(tmpPage.getPages()[count]);
          for (int count2 = 1; count2 < Memory.DEFAULT_SEGMENT-1; count2++)
            myMainMemory.allocateMemory(tmpPage.getPages()[count]);
          indexTotalPages[totalPages] = tmpPage.getPages()[count];
          totalPages++;
        }
      }
      else
      {
        thePageTable.goToNext();
      }
    }
    return (new MemoryReturn(PID, (byte) 0, totalPages, indexTotalPages));
  }

  // Get all memory from numerous pages belonging to PID of type Type.
  public Memory getAllMemory(int PID, int type, int size)
  {
    PageTableEntry tmpPageTable;
    int count;
    thePageTable.goToHead();
    Memory tmpMemory = new Memory(size);
    while (!thePageTable.atTail())
    {
      tmpPageTable = (PageTableEntry) thePageTable.peek();
      thePageTable.goToNext();
    }
    thePageTable.goToHead();
    tmpMemory = new Memory(size);
    while (!thePageTable.atTail())
    {
      tmpPageTable = (PageTableEntry) thePageTable.peek();
      if ((tmpPageTable.getPID() == ((byte) PID)) &&
          (tmpPageTable.getType() == ((byte) type)))
      {
        tmpMemory = myMainMemory.getMemory(tmpPageTable.getPages()[0]);
        for(count = 1; count < tmpPageTable.getTotalNumberOfPages(); count++)
        {
          tmpMemory = Memory.combineMemory(tmpMemory,
                      myMainMemory.getMemory(tmpPageTable.getPages()[count]));
        }
        return (tmpMemory);
      }
      thePageTable.goToNext();
    }
    return null;
  }

  // Read page number Offset belonging to PID of type Type.
  public Memory readPage(int PID, int type, int offset)
  {
    return (readBytes(PID, type, Memory.DEFAULT_SEGMENT, offset*Memory.DEFAULT_SEGMENT));
  }

  //Read a number of bytes with given PID, Type, Size and Offset.
  public Memory readBytes(int PID, int type, int size, int offset)
  {
    Memory tmpMemory = getAllMemory(PID, type, size);
    if (tmpMemory != null)
    {
      return (tmpMemory.read(offset, size));
    }
    return null;
  }

  // Write page number Offset belonging to PID of type Type with Memory.
  public void writePage(int PID, int type, int offset, Memory newMemory)
  {
    writeBytes(PID, type, Memory.DEFAULT_SEGMENT, offset*Memory.DEFAULT_SEGMENT,
      newMemory);
  }

  // Write bytes given PID, Type, Size, Offset with Memory.
  public void writeBytes(int PID, int type, int size, int offset, Memory newMemory)
  {
    // Get all Memory from segments
    Memory tmpMemory = getAllMemory(PID, type, size);
    if (tmpMemory != null)
    {
      // Write new values to one large block of memory
      tmpMemory.write(offset, newMemory);
      // Write memory into segments
      PageTableEntry tmpPageTable;
      thePageTable.goToHead();
      while (!thePageTable.atTail())
      {
        tmpPageTable = (PageTableEntry) thePageTable.peek();
        if ((tmpPageTable.getPID() == ((byte) PID)) &&
            (tmpPageTable.getType() == ((byte) type)))
        {
          int count;
          for (count = 0; count < tmpPageTable.getTotalNumberOfPages(); count++)
          {
            myMainMemory.setMemory(tmpPageTable.getPages()[count],
              tmpMemory.segmentMemory(count*myMainMemory.getMemory(tmpPageTable.getPages()[count]).getSegmentSize(),
                myMainMemory.getMemory(tmpPageTable.getPages()[count]).getSegmentSize(),
                tmpMemory.isAllocated()));
          }
        }
        thePageTable.goToNext();
      }
      thePageTable.goToHead();
      while(!thePageTable.atTail())
      {
        tmpPageTable = (PageTableEntry) thePageTable.peek();
        thePageTable.goToNext();
      }
    }
  }
}
