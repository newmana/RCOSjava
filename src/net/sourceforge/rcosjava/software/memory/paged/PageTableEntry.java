package net.sourceforge.rcosjava.software.memory.paged;

/**
 * Table which contains type and PID of all memory.  For fast look-up.
 * <P>
 * @see net.sourceforge.rcosjava.software.memory.paged.PagedMemoryManagement
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 27th March 1996
 */
public class PageTableEntry
{
  private byte type;
  private byte pid;
  private short[] pages;
  private short noPages;

  public PageTableEntry()
  {
    type = -1;
    pid = -1;
  }

  public PageTableEntry(byte newPID, byte newType, short[] newPages,
    short newNoPages)
  {
    pid = newPID;
    type = newType;
    pages = newPages;
    noPages = newNoPages;
  }

  public byte getType()
  {
    return type;
  }

  public byte getPID()
  {
    return pid;
  }

  public short[] getPages()
  {
    return pages;
  }

  public short getTotalNumberOfPages()
  {
    return noPages;
  }
}

