package org.rcosjava.software.filesystem.cpm14;

/**
 * Contains the data for an entry in the CPM14 FS File ID table.
 * <P>
 * @author Brett Carter (created 25 March 1996)
 * @author Andrew Newman
 */
public class CPM14FIDTableEntry
{
  /**
   * Name of the file.
   */
  public String fileName;

  /**
   * Use device number to access the device table.
   */
  public int deviceNumber;

  /**
   * Number of current directory entry in device.
   */
  public int fileNumber;

  /**
   * The position of writing/reading the file.
   */
  public int currentPosition;

  /**
   * The current disk block that is being used.
   */
  public int currentDiskBlock;

  /**
   * The current mode of the entry - reading, writing, etc.
   */
  public int mode;

  /**
   * The buffer.
   */
  public byte[] buffer;

  /**
   * Creates a new table entry.
   *
   * @param newDeviceNumber the device number allocated to this file.
   * @param newFileName the name of the file.
   * @param newMode the mode that this file is in.
   * @param blockSize the size of a block in this file system.
   */
  public CPM14FIDTableEntry(int newDeviceNumber, String newFileName,
    int blockSize)
  {
    initialize();

    fileName = newFileName;
    deviceNumber = newDeviceNumber;
    buffer = new byte[blockSize];
  }

  /**
   * Initalizes the entry.  Sets the mode to allocate but everything else is
   * set to -1 or null where appropriate.
   */
  public void initialize()
  {
    mode = CPM14Mode.ALLOCATED;
    fileNumber = -1;
    currentPosition = -1;
    buffer = null;
    currentDiskBlock = -1;
  }

  /**
   * Returns the name of the file.
   *
   * @return the name of the file.
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * Returns the device number that this file entry is attached to.
   *
   * @return the device number that this file entry is attached to.
   */
  public int getDeviceNumber()
  {
    return deviceNumber;
  }

  /**
   * Returns the file number of the file.
   *
   * @returns the file number of the file.
   */
  public int getFileNumber()
  {
    return fileNumber;
  }

  /**
   * Sets the file number of the file.
   *
   * @param newFileNumber the new file number of the file.
   */
  public void setFileNumber(int newFileNumber)
  {
    fileNumber = newFileNumber;
  }

  /**
   * Returns the current position of the entry when reading/writing.
   *
   * @return the current position of the entry when reading/writing.
   */
  public int getCurrentPosition()
  {
    return currentPosition;
  }

  /**
   * Sets the current position of the entry when reading/writing.
   *
   * @param newCurrentPosition the position in the block when reading/writing.
   */
  public void setCurrentPosition(int newCurrentPosition)
  {
    currentPosition = newCurrentPosition;
  }

  /**
   * Increments the value of the current position by one.
   */
  public void incCurrentPosition()
  {
    currentPosition++;
  }

  /**
   * Decrements the value of the current position by one.
   */
  public void decCurrentPosition()
  {
    currentPosition--;
  }

  /**
   * Returns the current disk block that the file is accessing.
   *
   * @return the current disk block that the file is accessing.
   */
  public int getCurrentDiskBlock()
  {
    return currentDiskBlock;
  }

  /**
   * Sets the current disk block that the file is accessing.
   *
   * @param newCurrentDiskBlock the disk block being accessed.
   */
  public void setCurrentDiskBlock(int newCurrentDiskBlock)
  {
    currentDiskBlock = newCurrentDiskBlock;
  }

  /**
   * Returns true if file is allocated.
   *
   * @return true if file is allocated.
   */
  public boolean hasBeenAllocated()
  {
    return mode == CPM14Mode.ALLOCATED;
  }

  /**
   * Returns true if file is being created.
   *
   * @return true if file is being created.
   */
  public boolean isBeingCreated()
  {
    return mode == CPM14Mode.CREATING;
  }

  /**
   * Sets the mode of the file to being created.
   */
  public void isCreated()
  {
    mode = CPM14Mode.CREATING;
  }

  /**
   * Returns true if file is being read.
   *
   * @return true if file is being read.
   */
  public boolean isBeingRead()
  {
    return mode == CPM14Mode.READING;
  }

  /**
   * Sets the mode of the file to being read.
   */
  public void isReading()
  {
    mode = CPM14Mode.READING;
  }

  /**
   * Returns true if file is being written.
   *
   * @return true if file is being written.
   */
  public boolean isBeingWritten()
  {
    return mode == CPM14Mode.WRITING;
  }

  /**
   * Sets the mode of the file to being written.
   */
  public void isWriting()
  {
    mode = CPM14Mode.WRITING;
  }

  /**
   * Returns true if file is being closed.
   *
   * @return true if file is being closed.
   */
  public boolean isBeingClosed()
  {
    return mode == CPM14Mode.CLOSING;
  }

  /**
   * Sets the mode of the file to being closed.
   */
  public void isClosing()
  {
    mode = CPM14Mode.CLOSING;
  }

  /**
   * Returns the byte in the buffer given at the offset.
   *
   * @param offset the index into the buffer.
   * @return the byte in the buffer given the offset.
   */
  public byte getByteInBuffer(int offset)
  {
    return buffer[offset];
  }

  /**
   * Returns the current buffer.
   *
   * @return the current buffer.
   */
  public byte[] getBuffer()
  {
    return buffer;
  }

  /**
   * Sets the byte in the buffer given at the offset.
   *
   * @param offset the index into the buffer.
   * @param value the value to put into the buffer.
   */
  public void setByteInBuffer(int offset, byte value)
  {
    buffer[offset] = value;
  }

  /**
   * Prints out the attributes of the object.
   *
   * @return the attributes of the object.
   */
  public String toString()
  {
    return "Filename: " + fileName + ", Device No: " + deviceNumber +
        ", Filenumber: " + fileNumber + ".  Buffer:" + new String(buffer) +
        " Position: " + currentPosition + ", Status: " + mode;
  }
}