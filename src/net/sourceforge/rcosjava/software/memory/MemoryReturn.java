package net.sourceforge.rcosjava.software.memory;

import java.io.Serializable;

/**
 * Structure sent to everyone after memory request.
 * <P>
 *
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 30th March 1996
 */
public class MemoryReturn implements Serializable
{
  /**
   * The process that sent the request.
   */
  private int processId;

    /**
   * The type of memory (stack or program)
   */
  private byte memoryType;

  /**
   * How much memory was returned?
   */
  private int size;

  /**
   * The array of indexed pages that are being used.
   */
  private short[] pages;

  /**
   * Create a new memory return object.
   *
   * @param newPID the process to own this group of pages.
   * @param newType the type of memory owned by the particular user.
   * @param newSize the size in bytes of the memory.
   * @param newPages index of pages allocated.
   */
  public MemoryReturn(int newPID, byte newType, int newSize, short[] newPages)
  {
    processId = newPID;
    memoryType = newType;
    size = newSize;
    pages = newPages;
  }

  /**
   * @return the owner of the memory's process id.
   */
  public int getPID()
  {
    return processId;
  }

  /**
   * @return stored memory type.
   */
  public byte getMemoryType()
  {
    return memoryType;
  }

  /**
   * Allocate a new type without having to recreate the object.
   *
   * @param newType the type of memory.
   */
  public void setMemoryType(byte newType)
  {
    memoryType = newType;
  }

  /**
   * @return the size of the memory that's been allocated.
   */
  public int getSize()
  {
    return size;
  }

  /**
   * Set a new value for the size of the memory returned.
   *
   * @param set the size of the memory returned.
   */
  public void setSize(int newSize)
  {
    size = newSize;
  }

  /**
   * @return the array of the index of pages.
   */
  public short[] getPages()
  {
    return pages;
  }

  /**
   * Tries to return the value (the absolute location of the memory block)
   * that have been allocated.
   *
   * @param index the look up to the array of pages.
   * @return the index of the page.
   * @throws IllegalArgumentException if the index is out of range.
   */
  public short getPage(int index) throws IllegalArgumentException
  {
    if (index >= pages.length)
    {
      throw new java.lang.IllegalArgumentException("Index too large");
    }
    return pages[index];
  }

  /**
   * Replace the current array of page indexes.
   *
   * @param newPages the new value to set it.
   */
  public void setPages(short[] newPages)
  {
    pages = newPages;
  }
}
