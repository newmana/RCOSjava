package org.rcosjava.software.memory.paged;

import java.io.*;

import org.rcosjava.RCOS;
import org.rcosjava.hardware.memory.MainMemory;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.hardware.memory.NoFreeMemoryException;
import org.rcosjava.software.memory.MemoryManagement;
import org.rcosjava.software.memory.MemoryOpenFailedException;
import org.rcosjava.software.memory.MemoryReturn;
import org.rcosjava.software.util.FIFOQueue;

/**
 * Provides basic Memory/Page allocation. It is very inefficent at the moment.
 * Probably should have two lists which contains used and free memory blocks.
 * Combining and splitting memory blocks means it is very slow and unrealistic.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 02/12/97 Fixed bug with deallocating memory. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson
 * @created 27th March 1996
 * @see org.rcosjava.hardware.memory.MainMemory
 * @see org.rcosjava.software.memory.MemoryManager
 * @version 1.00 $Date$
 */
public class PagedMemoryManagement implements MemoryManagement
{
  /**
   * Base page handler for the MMU
   */
  private static MainMemory myMainMemory;

  /**
   * The table of memory pages used by program and data storage.
   */
  private FIFOQueue thePageTable;

  /**
   * Create a new set of paged memory.
   *
   * @param maxPages maximum number of pages to handle.
   */
  public PagedMemoryManagement(int maxPages)
  {
    thePageTable = new FIFOQueue(10, 1);
    myMainMemory = new MainMemory(maxPages);
  }

  /**
   * Get all memory from numerous pages belonging to pid of type Type.
   *
   * @param pid the process id to find all the memory for.
   * @param type the type of memory to find (code or stack).
   * @return The AllMemory value
   */
  public Memory getAllMemory(int pid, byte type)
  {
    //Temporary page table entry (all memory blocks of a certain type for a process)
    PageTableEntry tmpEntry = null;

    //Find the page table entry for this process and type
    //Assume one per combination
    short noPages = 0;

    //Go to head of page table
    thePageTable.goToHead();
    while (!thePageTable.atTail())
    {
      tmpEntry = (PageTableEntry) thePageTable.peek();
      if ((tmpEntry.getPID() == ((byte) pid)) &&
          (tmpEntry.getType() == ((byte) type)))
      {
        break;
      }
      thePageTable.goToNext();
    }

    Memory tmpMemory = null;

    //Tempory memory block belonging to process
    if (tmpEntry != null)
    {
      short pageNo;
      int segmentSize = myMainMemory.getMemory(tmpEntry.getPages()[0]).getSegmentSize();

      tmpMemory = new Memory(segmentSize * tmpEntry.getTotalNumberOfPages());
      for (int count = 0; count < tmpEntry.getTotalNumberOfPages(); count++)
      {
        pageNo = tmpEntry.getPages()[count];
        tmpMemory.write(segmentSize * count, myMainMemory.getMemory(pageNo));
      }
    }
    return tmpMemory;
  }

  /**
   * Goes through the entire section of memory allocating a specific set of
   * memory with the same PID and Type. Size, in this case is the number of
   * pages.
   *
   * @param pid the owner (in process id) of the memory to create for.
   * @param type memory type (either code or stack).
   * @param size the number of pages to create.
   * @return Description of the Returned Value
   * @exception MemoryOpenFailedException Description of Exception
   */
  public MemoryReturn open(int pid, byte type, int size)
    throws MemoryOpenFailedException
  {
    // Allocate pages
    int count;
    int noPages;
    int freePageIndex;
    short[] pages = new short[size];

    noPages = 0;
    for (count = 0; count < size; count++)
    {
      if (myMainMemory.getFreeUnits() >= size)
      {
        try
        {
          freePageIndex = myMainMemory.findFirstFree();
          myMainMemory.allocateMemory(freePageIndex);
          pages[noPages] = (short) freePageIndex;
          noPages++;
        }
        catch (NoFreeMemoryException e)
        {
        }
      }
      else
      {
        throw new MemoryOpenFailedException();
      }
    }

    //Write allocation information to page table.
    PageTableEntry newPageTableEntry = new PageTableEntry((byte) pid,
        type, pages, (short) noPages);

    thePageTable.insert(newPageTableEntry);
    /*PageTableEntry tmpPageTable;
    thePageTable.goToHead();
    while(!thePageTable.atTail())
    {
      tmpPageTable = (PageTableEntry) thePageTable.peek();
      thePageTable.goToNext();
    }*/
    return (new MemoryReturn(pid, type, noPages, pages));
  }

  /**
   * Deallocate all memory belonging to a certain Process ID.
   *
   * @param pid the process id to deallocate the memory from.
   * @return Description of the Returned Value
   */
  public MemoryReturn close(int pid)
  {
    PageTableEntry tmpPage;

    thePageTable.goToHead();

    int totalPages = 0;
    short[] indexTotalPages = new short[20];

    while (!thePageTable.atTail())
    {
      tmpPage = (PageTableEntry) thePageTable.peek();
      if (tmpPage.getPID() == ((byte) pid))
      {
        tmpPage = (PageTableEntry) thePageTable.retrieveCurrent();
        for (int count = 0; count < tmpPage.getTotalNumberOfPages(); count++)
        {
          myMainMemory.freeMemory(tmpPage.getPages()[count]);
          indexTotalPages[totalPages] = tmpPage.getPages()[count];
          totalPages++;
        }
      }
      else
      {
        thePageTable.goToNext();
      }
    }
    return (new MemoryReturn(pid, (byte) 0, totalPages, indexTotalPages));
  }

  /**
   * Read page number Offset belonging to pid of type Type.
   *
   * @param pid Description of Parameter
   * @param type Description of Parameter
   * @param offset Description of Parameter
   * @return Description of the Returned Value
   */
  public Memory readPage(int pid, byte type, int offset)
  {
    return (readBytes(pid, type, Memory.DEFAULT_SEGMENT, offset * Memory.DEFAULT_SEGMENT));
  }

  //Read a number of bytes with given pid, Type, Size and Offset.
  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   * @param type Description of Parameter
   * @param size Description of Parameter
   * @param offset Description of Parameter
   * @return Description of the Returned Value
   */
  public Memory readBytes(int pid, byte type, int size, int offset)
  {
    Memory tmpMemory = getAllMemory(pid, type);

    if (tmpMemory != null)
    {
      return (tmpMemory.read(offset, size));
    }
    return null;
  }

  /**
   * Write page number Offset belonging to pid of type Type with Memory.
   *
   * @param pid Description of Parameter
   * @param type Description of Parameter
   * @param offset Description of Parameter
   * @param newMemory Description of Parameter
   */
  public void writePage(int pid, byte type, int offset, Memory newMemory)
  {
    writeBytes(pid, type, Memory.DEFAULT_SEGMENT, offset * Memory.DEFAULT_SEGMENT,
        newMemory);
  }

  // Write bytes given pid, Type, Size, Offset with Memory.
  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   * @param type Description of Parameter
   * @param size Description of Parameter
   * @param offset Description of Parameter
   * @param newMemory Description of Parameter
   */
  public void writeBytes(int pid, byte type, int size, int offset,
      Memory newMemory)
  {
    // Get all Memory from segments
    Memory tmpMemory = getAllMemory(pid, type);

    tmpMemory.setAllocated();
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
        if ((tmpPageTable.getPID() == ((byte) pid)) &&
            (tmpPageTable.getType() == ((byte) type)))
        {
          int count;

          for (count = 0; count < tmpPageTable.getTotalNumberOfPages(); count++)
          {
            myMainMemory.setMemory(tmpPageTable.getPages()[count],
                tmpMemory.segmentMemory(count * myMainMemory.getMemory(tmpPageTable.getPages()[count]).getSegmentSize(),
                myMainMemory.getMemory(tmpPageTable.getPages()[count]).getSegmentSize(),
                tmpMemory.isAllocated()));
          }
        }
        thePageTable.goToNext();
      }
      thePageTable.goToHead();
      while (!thePageTable.atTail())
      {
        tmpPageTable = (PageTableEntry) thePageTable.peek();
        thePageTable.goToNext();
      }
    }
  }

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
//    os.writeObject(myMainMemory);
    os.writeObject(thePageTable);
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
//    myMainMemory = (MainMemory) is.readObject();
    thePageTable = (FIFOQueue) is.readObject();
  }
}
