package org.rcosjava.software.filesystem.cpm14;

import java.util.*;

/**
 * Contains the data for an entry in the CPM14 FS Mount table.
 * <P>
 * @author Brett Carter (created 25 March 1996)
 * @author Andrew Newman
 */
class CPM14DeviceTableEntry
{
  /**
   * The name of the device such as "DISK1".
   */
  private String deviceName;

  /**
   * The data structure containing the bytes for the directory table.
   */
  private byte[] directoryTable;

  /**
   * A list of all the open files.
   */
  private ArrayList openFileNames;

  /**
   * Status is either 0 or 1 indicating mounted or not.
   */
  private int status;

  /**
   * Indicates if a block of data has been allocated or not.
   */
  private boolean[] blockList;

  /**
   * The total number of free blocks.
   */
  private int numberOfFreeBlocks;

  /**
   * Indicates if a directory entry has been allocated or not.
   */
  private boolean[] directoryEntriesList;

  /**
   * The total number of free directory entries.
   */
  private int numberOfFreeEntries;

  /**
   * The size (in bytes) of a single directory entry.
   */
  private int directoryEntrySize;

  /**
   * Creates a new device table entry.
   *
   * @param newDeviceName the new name of the device.
   * @param blockSize the size (in bytes) of each block on the disk.
   * @param totalNumberOfDirectoryBlocks the number of blocks allocated to
   *   storing the directory.
   * @param totalNumberOfDiskBlocks the number of blocks available on the disk.
   * @param totalNumberOfDirectoryEntries the maximum number of directory
   *   entries.
   */
  public CPM14DeviceTableEntry(String newDeviceName, int blockSize,
    int totalNumberOfDirectoryBlocks, int totalNumberOfDiskBlocks,
    int totalNumberOfDirectoryEntries, int directorySize)
  {
    deviceName = newDeviceName;
    directoryTable = new byte[blockSize * totalNumberOfDirectoryBlocks];
    openFileNames = new ArrayList();
    status = 0;
    blockList = new boolean[totalNumberOfDiskBlocks];
    numberOfFreeBlocks = totalNumberOfDiskBlocks;
    directoryEntriesList = new boolean[totalNumberOfDirectoryEntries];
    numberOfFreeEntries = totalNumberOfDirectoryEntries;
  }

  /**
   * Initializes the table entry file.  Sets all entries as being deleted and
   * initializes the free entry and disk entry arrays.
   */
  public void initialize()
  {
    // This section would start the read operations to retrieve the
    // directory blocks from the disk.  As it is, it simply sets the
    // status on all files to deleted so the system knows it can write
    // to them.
    for (int counter = 0; counter < directoryEntriesList.length; counter++)
    {
      directoryTable[(directoryEntrySize * counter) + CPM14TableOffset.STATUS] =
          (byte) 0xE5;
      directoryEntriesList[counter] = false;
    }

    // Setup the free dir entry and disk block arrays.
    for (int counter = 0; counter < blockList.length; counter++)
    {
      blockList[counter] = false;
    }
  }

  /**
   * Returns the name of the device.
   *
   * @return the name of the device.
   */
  public String getName()
  {
    return deviceName;
  }

  /**
   * Returns the byte in the entry table given the offset.
   *
   * @param offset the index into the entry table.
   * @return the byte in the entry table given the offset.
   */
  public byte getByteInEntry(int offset)
  {
    return directoryTable[offset];
  }

  /**
   * Sets a byte in the entry table give the offset.
   *
   * @param offset the index into the entry table.
   * @param value the value to set the offset.
   */
  public void setByteInEntry(int offset, byte value)
  {
    directoryTable[offset] = value;
  }

  /**
   * Returns true if a given entry free.
   *
   * @param offset the entry to check to see if it's available.
   * @return true if a given entry if free.
   */
  public boolean isEntryFree(int offset)
  {
    return directoryEntriesList[offset];
  }

  /**
   * Allocates a given directory entry.
   *
   * @param offset the entry to allocate.
   */
  public void allocateEntry(int offset)
  {
    directoryEntriesList[offset] = true;
    numberOfFreeEntries--;
  }

  /**
   * Deallocates a given directory entry.
   *
   * @param offset the entry to deallocate.
   */
  public void deallocateEntry(int offset)
  {
    directoryEntriesList[offset] = false;
    numberOfFreeEntries++;
  }

  /**
   * Returns true if a block is allocated.
   *
   * @param offset the entry to check to see if it's allocated.
   * @return true if the block is allocated.
   */
  public boolean isBlockAllocated(int offset)
  {
    return blockList[offset];
  }

  /**
   * Allocates a given block.
   *
   * @param offset the block to allocate.
   */
  public void allocateBlock(int offset)
  {
    blockList[offset] = true;
    numberOfFreeBlocks--;
  }

  /**
   * Deallocates a given block.
   *
   * @param offset the block to allocate.
   */
  public void deallocateBlock(int offset)
  {
    blockList[offset] = false;
    numberOfFreeBlocks++;
  }

  /**
   * Returns true if the file name given is currently open.
   *
   * @param fileName the name of the file.
   * @return true if the file name given is currently open.
   */
  public boolean isFileOpen(String fileName)
  {
    boolean isOpen = false;

    if (openFileNames.contains(fileName))
    {
      isOpen = true;
    }

    return isOpen;
  }

  /**
   * Add a file to the list of open files.
   *
   * @param fileName the name of the file to add.
   */
  public void setFileOpen(String fileName)
  {
    openFileNames.add(fileName);
  }

  /**
   * Removes a file from the list of open files.
   *
   * @param fileName the name of the file to remove.
   */
  public void setFileClosed(String fileName)
  {
    openFileNames.remove(fileName);
  }

  /**
   * Returns the number of free blocks.
   *
   * @return the number of free blocks.
   */
  public int getNumberOfFreeBlocks()
  {
    return numberOfFreeBlocks;
  }

  /**
   * Returns the number of free directory entries.
   *
   * @return the number of free directory entries.
   */
  public int getNumberOfFreeEntries()
  {
    return numberOfFreeEntries;
  }

  /**
   * Indicate that the device is mounted.
   */
  public void mounted()
  {
    status = 1;
  }

  /**
   * Indicate that the device is unmounted.
   */
  public void unmounted()
  {
    status = 0;
  }

  /**
   * Returns if the device is mounted.
   *
   * @return if the device is mounted.
   */
  public boolean isMounted()
  {
    return status == 1;
  }

  /**
   * Prints out the attributes of the object.
   *
   * @return the attributes of the object.
   */
  public String toString()
  {
    return "Device Name: " + deviceName + ", Directory Table: " +
        new String(directoryTable);
  }
}