package org.rcosjava.software.memory.paged;

/**
 * Table which contains type and PID of all memory. For fast look-up.
 * <P>
 * @author Andrew Newman.
 * @created 27th March 1996
 * @see org.rcosjava.software.memory.paged.PagedMemoryManagement
 * @version 1.00 $Date$
 */
public class PageTableEntry
{
  /**
   * The type of memory (current 1 and 2 for stack and program).
   */
  private byte type;

  /**
   * The process id that uses the memory.
   */
  private byte pid;

  /**
   * The array of indexed pages that are being used.
   */
  private short[] pages;

  /**
   * The number of total pages (really the length of the pages array).
   */
  private short noPages;

  /**
   * Initializes the page table. Type and pid are set to non-valid values.
   */
  public PageTableEntry()
  {
    type = -1;
    pid = -1;
  }

  /**
   * Create a new page table entry.
   *
   * @param newPID the process to own this group of pages.
   * @param newType the type of memory owned by the particular user.
   * @param newPages the array of indices to the memory blocks.
   * @param newNoPages Description of Parameter
   */
  public PageTableEntry(byte newPID, byte newType, short[] newPages,
      short newNoPages)
  {
    pid = newPID;
    type = newType;
    pages = newPages;
    noPages = newNoPages;
  }

  /**
   * @return the type (currently 1 or 2).
   */
  public byte getType()
  {
    return type;
  }

  /**
   * @return the process id.
   */
  public byte getPID()
  {
    return pid;
  }

  /**
   * @return the index of pages.
   */
  public short[] getPages()
  {
    return pages;
  }

  /**
   * @return the number of pages allocated.
   */
  public short getTotalNumberOfPages()
  {
    return noPages;
  }
}

