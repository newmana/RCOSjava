package org.rcosjava.software.filesystem.cpm14;

import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.MessageHandler;
import org.rcosjava.software.disk.DiskRequest;
import org.rcosjava.software.filesystem.AllocationTableException;
import org.rcosjava.software.filesystem.DirectoryException;
import org.rcosjava.software.filesystem.FileSystem;
import org.rcosjava.software.filesystem.FileSystemFile;
import org.rcosjava.software.filesystem.FileSystemReturnData;
import org.rcosjava.software.util.IndexedList;

import java.util.*;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class CPM14FileSystem implements FileSystem
{
  /**
   * Description of the Field
   */
  private final static String MOUNT_POINT_SEPERATOR = ":";

  /**
   * Description of the Field
   */
  private final static int BLOCK_SIZE = 1024;

  /**
   * Description of the Field
   */
  private final static int TOTAL_DISK_BLOCKS = 240;

  /**
   * Description of the Field
   */
  private final static int TOTAL_DIR_BLOCKS = 2;

  /**
   * Description of the Field
   */
  private final static int DIR_BLOCK_OFFSET = 0;

  /**
   * Description of the Field
   */
  private final static int DISK_BLOCK_OFFSET = DIR_BLOCK_OFFSET + TOTAL_DIR_BLOCKS;

  /**
   * Description of the Field
   */
  private final static int DIR_ENTRY_SIZE = 32;

  /**
   * Description of the Field
   */
  private final static int TOTAL_DIR_ENTRIES = (BLOCK_SIZE * TOTAL_DIR_BLOCKS)
       / DIR_ENTRY_SIZE;

  // Dir Entry
  /**
   * Description of the Field
   */
  private final static int STATUS = 0;

  /**
   * Description of the Field
   */
  private final static int FILENAME = 1;

  /**
   * Description of the Field
   */
  private final static int EXTENSION = 9;

  /**
   * Description of the Field
   */
  private final static int EXTENT = 12;

  /**
   * Description of the Field
   */
  private final static int RESERVED = 13;

  /**
   * Description of the Field
   */
  private final static int RECORDS = 15;

  /**
   * Description of the Field
   */
  private final static int DATA_BLOCKS = 16;

  // File Modes
  /**
   * Description of the Field
   */
  private final static int MODELESS = -1;

  /**
   * Description of the Field
   */
  private final static int ALLOCATED = 0;

  /**
   * Description of the Field
   */
  private final static int READING = 1;

  /**
   * Description of the Field
   */
  private final static int WRITING = 2;

  /**
   * Description of the Field
   */
  private final static int CREATING = 3;

  /**
   * Description of the Field
   */
  private final static int DELETING = 4;

  /**
   * Description of the Field
   */
  private final static int CLOSING = 5;

  /**
   * Description of the Field
   */
  private final static int EOF = 1;

  /**
   * Description of the Field
   */
  private final static int NOT_EOF = 0;

  // Used to convert signed 8 bit numbers (byte) to integers.
  /**
   * Description of the Field
   */
  private final static int SVB2I = 255;

  /**
   * Description of the Field
   */
  private IndexedList requestTable;

  /**
   * Description of the Field
   */
  private HashMap mountTable;

  /**
   * Description of the Field
   */
  private IndexedList deviceTable;

  /**
   * Description of the Field
   */
  private IndexedList fidTable;

  /**
   * The identifier of the scheduler to the post office.
   */
  private final static String MESSENGING_ID = "CPM14FileSystem";

  /**
   * Constructor for the CPM14FileSystem object
   */
  public CPM14FileSystem()
  {
    requestTable = new IndexedList(100, 10);
    mountTable = new HashMap();
    deviceTable = new IndexedList(100, 10);
    fidTable = new IndexedList(100, 10);
  }

  /**
   * Gets the number of the first entry for the file. Note, the filename
   * should include the mountpoint as well.
   *
   * @param mvFilename the file name inclusive of a mount point.
   * @return The DirectoryPosition value
   */
  public int getDirectoryPosition(String mvFilename)
  {
    String mountPoint;
    Integer tmpId;
    int deviceNumber;

    // Determin the device and get a pointer to it's data.
    mountPoint = getMountPoint(mvFilename);
    tmpId = (Integer) mountTable.get(mountPoint);
    if (tmpId == null)
    {
      return -1;
    }

    deviceNumber = tmpId.intValue();
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    // Convert the filename to a byte[] for the search.
    byte[] mvByteFilename = convertFilename(mvFilename);
    // Search
    int counter = 0;
    int mvIndex = 0;
    int mvOffset;

    boolean mvFound = false;

    while ((counter < TOTAL_DIR_ENTRIES) && (!mvFound))
    {
      mvIndex = 0;
      mvOffset = (counter * DIR_ENTRY_SIZE) + FILENAME;
      while ((mvIndex < 11) &&
          (mvByteFilename[mvIndex] == device.directoryTable[mvOffset + mvIndex]))
      {
        mvIndex++;
      }
      mvFound = (mvIndex == 11);
      if (mvFound)
      {
        mvFound = (device.directoryTable[mvOffset - FILENAME + EXTENT]
             == 0);
      }
      counter++;
    }

    // Check sucess and return a value.
    if (mvFound)
    {
      counter--;
      // Counter is incremented in the loop to save cycles with
      // an if or an else.
      return counter;
    }
    else
    {
      return -1;
    }

  }

  // Return a Free Directory entry for the specified device.
  /**
   * Gets the FreeEntry attribute of the CPM14FileSystem object
   *
   * @param deviceNumber Description of Parameter
   * @return The FreeEntry value
   */
  public int getFreeEntry(int deviceNumber)
  {
    return resourceAllocator("DIR", deviceNumber, -1);
  }

  // Return a free blcok number on the specified device.
  /**
   * Gets the FreeBlock attribute of the CPM14FileSystem object
   *
   * @param deviceNumber Description of Parameter
   * @return The FreeBlock value
   */
  public int getFreeBlock(int deviceNumber)
  {
    return resourceAllocator("BLOCK", deviceNumber, -1);
  }

  // Returns the next directory entry for the dirent on the specified device
  /**
   * Gets the NextDirectoryEntry attribute of the CPM14FileSystem object
   *
   * @param mvDirent Description of Parameter
   * @param deviceNumber Description of Parameter
   * @return The NextDirectoryEntry value
   */
  public int getNextDirectoryEntry(int mvDirent, int deviceNumber)
  {
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    int mvCurrentOffset = mvDirent * DIR_ENTRY_SIZE;

    // First check if this entry is totally used. If so, look for next,
    // otherwise exit.
    if ((SVB2I & device.directoryTable[mvCurrentOffset + RECORDS]) != 0x80)
    {
      return -1;
    }

    // Get the filename to a byte[] for the search.
    byte[] mvByteFilename = new byte[11];

    byte mvCurrentExtent = device.directoryTable
        [mvCurrentOffset + EXTENT];

    int counter;

    for (counter = 0; counter < 11; counter++)
    {
      mvByteFilename[counter] = device.directoryTable
          [mvCurrentOffset + FILENAME + counter];
    }

    // Search
    counter = 0;

    int mvIndex = 0;
    int mvOffset;

    boolean mvFound = false;

    while ((counter < TOTAL_DIR_ENTRIES) && (!mvFound))
    {
      mvIndex = 0;
      mvOffset = (counter * DIR_ENTRY_SIZE) + FILENAME;
      while ((mvIndex < 11) &&
          (mvByteFilename[mvIndex] == device.directoryTable[
          mvOffset + mvIndex]))
      {
        mvIndex++;
      }
      mvFound = (mvIndex == 11);
      if (mvFound)
      {
        mvFound = (device.directoryTable[mvOffset - FILENAME + EXTENT]
             == mvCurrentExtent + 1);
      }
      counter++;
    }

    // Check sucess and return a value.
    if (mvFound)
    {
      counter--;
      // Counter is incremented in the loop to save cycles with
      // an if or an else.
      return counter;
    }
    else
    {
      return -1;
    }

  }

  /**
   * Returns the mountpoint of the specified string.
   *
   * @param mvFilename Description of Parameter
   * @return The MountPoint value
   */
  public String getMountPoint(String mvFilename)
  {
    int mvIndex;
    String mountPoint;

    mvIndex = mvFilename.indexOf(MOUNT_POINT_SEPERATOR);
    if (mvIndex == -1)
    {
      return null;
    }
    mountPoint = mvFilename.substring(0, mvIndex);
    return mountPoint;
  }

  //Handle a mount request
  /**
   * Description of the Method
   *
   * @param mountPoint Description of Parameter
   * @param deviceName Description of Parameter
   */
  public void mount(String mountPoint, String deviceName)
  {
    CPM14DeviceTableEntry device = new CPM14DeviceTableEntry();

    // Note, in the simulation, the disks are initialized each
    // time the program is run. For a disk structure that
    // remained between runs, the mount would be very different.
    device.deviceName = deviceName;
    device.directoryTable = new byte[BLOCK_SIZE * TOTAL_DIR_BLOCKS];
    device.openFileNames = new HashMap();
    device.status = 0;
    device.blockList = new boolean[TOTAL_DISK_BLOCKS];
    device.numberOfFreeBlocks = TOTAL_DISK_BLOCKS;
    device.dirEntList = new boolean[TOTAL_DIR_ENTRIES];
    device.numberOfFreeEntries = TOTAL_DIR_ENTRIES;

    // This section would start the read operations to retrieve the
    // directory blocks from the disk.  As it is, it simply sets the
    // status on all files to deleted so the system knows it can write
    // to them.
    for (int counter = 0; counter < TOTAL_DIR_ENTRIES; counter++)
    {
      device.directoryTable[(DIR_ENTRY_SIZE * counter) + STATUS] =
          (byte) 0xE5;
      device.dirEntList[counter] = false;
    }

    // Setup the free dir entry and disk block arrays.
    for (int counter = 0; counter < TOTAL_DISK_BLOCKS; counter++)
    {
      device.blockList[counter] = false;
    }

    // Add device to the device table and the Mount table.
    int deviceNumber = deviceTable.add(device);
    mountTable.put(mountPoint, new Integer(deviceNumber));

    // Set status to mounted.
    device.status = 1;
  }

  /**
   * Perfoms an allocation of the file. Creats an entry in the FID table
   * and inits it.
   *
   * @param requestId Description of Parameter
   * @param filename Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData allocate(int requestId, String filename)
  {
    String mountPoint = getMountPoint(filename);

    // Assume device is mounted as FSMan has passed in request.
    int deviceNumber = ((Integer) mountTable.get(mountPoint)).intValue();

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    // Check if file is already open
    if (device.openFileNames.containsKey(filename))
    {
      return (new FileSystemReturnData(requestId, -1));
    }

    device.openFileNames.put(filename, new Boolean(true));

    // create the entry and init it.
    CPM14FIDTableEntry fidEntry = new CPM14FIDTableEntry();

    fidEntry.Device = deviceNumber;
    fidEntry.Filename = filename;
    fidEntry.Mode = ALLOCATED;
    fidEntry.Buffer = new byte[BLOCK_SIZE];

    int FID = fidTable.add(fidEntry);

    return (new FileSystemReturnData(requestId, FID));
  }

  // Replys to the sender of the message 1 if at end of file
  // 0 if not.
  /**
   * Description of the Method
   *
   * @param requestId Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData eof(int requestId, int iFSFileNo)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(iFSFileNo);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.Device);

    int mvReturnValue = NOT_EOF;

    if (fidEntry.Mode == READING)
    {
      // Check if in the middle of a block
      if (((fidEntry.CurrentPosition) % 1024) != 0)
      {
        // Easy, check the current character for 0x1A, EOF.
        if ((SVB2I & fidEntry.Buffer[(fidEntry.CurrentPosition % 1024)]) == 0x1A)
        {
          mvReturnValue = EOF;
        }
      }
      else
      {
        // The position is at the very end of a data block. Check if that block
        // is the last one in the context.
        int mvDiskBlockOffset = (fidEntry.FileNumber * DIR_ENTRY_SIZE) +
            DATA_BLOCKS;

        if (fidEntry.CurrentDiskBlock !=
            (SVB2I & device.directoryTable[(fidEntry.FileNumber * DIR_ENTRY_SIZE) +
            DATA_BLOCKS + 16]))
        {
          // We are not in the last one.
          int mvCurrentBlockPosition = (fidEntry.CurrentPosition / 1024) % 16;

          // Check if the next block in the list == 0. If it is, this is the
          // end of the file.
          if ((SVB2I & device.directoryTable[
              mvDiskBlockOffset + mvCurrentBlockPosition + 1]) == 0)
          {
            mvReturnValue = EOF;
          }
        }
        else
        {
          // If there isn't an entry for the file with a higher context,
          // this be the end.
          if (getNextDirectoryEntry(fidEntry.FileNumber, fidEntry.Device) == -1)
          {
            mvReturnValue = EOF;
          }
        }
      }
    }
    return (new FileSystemReturnData(requestId, mvReturnValue));
  }

  // Free's all disk structures associated with the specified
  // file. Leaves the file in the PID table though.
  /**
   * Description of the Method
   *
   * @param requestId Description of Parameter
   * @param fsFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData delete(int requestId, int fsFileNumber)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    // If file isn't at the allocated state
    if (fidEntry.Mode != ALLOCATED)
    {
      return (new FileSystemReturnData(requestId, -1));
    }

    int deviceNumber = fidEntry.Device;
    int mvCurrent;
    int mvNext;

    // eliminate all the dir entries associated with the file.
    mvCurrent = getDirectoryPosition(fidEntry.Filename);
    if (mvCurrent == -1)
    {
      // Fine, no work to be done.
      return (new FileSystemReturnData(requestId, 0));
    }

    mvNext = getNextDirectoryEntry(mvCurrent, deviceNumber);
    while (mvNext != -1)
    {
      deallocateEntry(deviceNumber, mvCurrent);
      mvCurrent = mvNext;
      mvNext = getNextDirectoryEntry(mvCurrent, deviceNumber);
    }
    deallocateEntry(mvCurrent, deviceNumber);

    // Clear the data items in the FID table
    fidEntry.Mode = ALLOCATED;
    fidEntry.FileNumber = -1;
    fidEntry.CurrentPosition = -1;
    fidEntry.Buffer = null;
    fidEntry.CurrentDiskBlock = -1;

    return (new FileSystemReturnData(requestId, 0));
  }

  /**
   * Sets up a directory entry for the file and sets it to a 0 length file.
   *
   * @param requestId Description of Parameter
   * @param fsFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData create(int requestId, int fsFileNumber)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.Device);

    // Check for spaces and necessary conditions
    if ((fidEntry.Mode != ALLOCATED) ||
        (diskFull(fidEntry.Device)))
    {
      // Return an error
      return (new FileSystemReturnData(requestId, -1));
    }

    int deviceNumber = fidEntry.Device;

    // Allocate the dir entry
    int dirEntry = getFreeEntry(deviceNumber);
    if (dirEntry == -1)
    {
      return (new FileSystemReturnData(requestId, -1));
    }

    // Setup the entry
    int offset = dirEntry * DIR_ENTRY_SIZE;
    device.directoryTable[offset + STATUS] = 0;
    byte[] byteFilename = convertFilename(fidEntry.Filename);
    System.err.println("Byte filename: " + byteFilename.length);

    for (int counter = 0; counter < 11; counter++)
    {
      System.err.println("Dir offset: " + (offset + FILENAME + counter));
      System.err.println("Byte offset: " + (counter));
      device.directoryTable[offset + FILENAME + counter] =
          byteFilename[counter];
    }

    device.directoryTable[offset + EXTENT] = 0;
    device.directoryTable[offset + RESERVED] = 0;
    device.directoryTable[offset + RESERVED + 1] = 0;
    device.directoryTable[offset + RECORDS] = 0;

    for (int counter = 0; counter < 16; counter++)
    {
      device.directoryTable[offset + DATA_BLOCKS + counter] = 0;
    }

    // Setup the initial data.
    fidEntry.FileNumber = dirEntry;
    fidEntry.Mode = WRITING;
    fidEntry.CurrentPosition = 0;

    return (new FileSystemReturnData(requestId, 0));
  }

  // Closes a file and removes it from the FID table. first
  // writes the current buffer and the Dir blocks to disk.
  /**
   * Description of the Method
   *
   * @param requestId Description of Parameter
   * @param fsFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData close(int requestId, int fsFileNumber)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.Device);

    if (fidEntry.Mode == WRITING)
    {
      // add EOF mark at the end of the file unless on a border line between the
      // next block. If no eof is encountered, and no following block is allocated,
      // Then the end of the file falls directly on the line.
      if (((fidEntry.CurrentPosition) % BLOCK_SIZE) != 0)
      {
        //Currentposition will already point to the place to write the  EOF
        // character
        fidEntry.Buffer[(fidEntry.CurrentPosition % 1024)] = 0x1A;
      }

      diskRequest(device.deviceName, "FS_CLOSE::WRITE_BUFFER", "WRITING",
          fsFileNumber, requestId, -1,
          fidEntry.CurrentDiskBlock, fidEntry.Buffer);
    }
    else
    {
      if (device.openFileNames.containsKey(fidEntry.Filename))
      {
        device.openFileNames.remove(fidEntry.Filename);
      }
      fidTable.remove(fsFileNumber);
      // Clean up entries in FID table.
      return (new FileSystemReturnData(requestId, 0));
    }
    return null;
  }

  // Opens the specified file for reading.
  /**
   * Description of the Method
   *
   * @param requestId Description of Parameter
   * @param fsFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData open(int requestId, int fsFileNumber)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.Device);
    int deviceNumber = fidEntry.Device;

    if (fidEntry.Mode == ALLOCATED)
    {
      int mvTheFile;

      if ((mvTheFile = getNextDirectoryEntry(1, 1)) != -1)
      {
        // init for first read.
        fidEntry.Mode = READING;
        fidEntry.FileNumber = mvTheFile;
        fidEntry.CurrentPosition = 0;
        return (new FileSystemReturnData(requestId, 0));
      }
      else
      {
        return (new FileSystemReturnData(requestId, -1));
      }
    }
    else
    {
      return (new FileSystemReturnData(requestId, -1));
    }
  }

  /**
   * Reads from the file if it is in the right mode.
   *
   * @param requestId Description of Parameter
   * @param fsFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData read(int requestId, int fsFileNumber)
  {
    // Setup the data
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.Device);
    int deviceNumber = fidEntry.Device;
    int mvTheEntry;

    // Check the current mode.
    if (fidEntry.Mode == READING)
    {
      // The file is being read.
      if (((fidEntry.CurrentPosition) % BLOCK_SIZE) == 0)
      {
        // check for end of dirent. Use disk blocks.
        if (fidEntry.CurrentDiskBlock == device.directoryTable
            [(fidEntry.FileNumber * DIR_ENTRY_SIZE) + DATA_BLOCKS + 15])
        {
          //this.dumpToScreen("DIR",0,fidEntry.FileNumber);
          int mvNewEntry = getNextDirectoryEntry(fidEntry.FileNumber, deviceNumber);

          if (mvNewEntry == -1)
          {
            return (new FileSystemReturnData(requestId, -1));
          }
          fidEntry.FileNumber = mvNewEntry;
          fidEntry.CurrentDiskBlock = SVB2I & device.directoryTable
              [(mvNewEntry * DIR_ENTRY_SIZE) + DATA_BLOCKS];
        }
        else
        {
          int mvNewBlock;

          mvNewBlock = SVB2I & device.directoryTable[
              (fidEntry.FileNumber * DIR_ENTRY_SIZE) + DATA_BLOCKS +
              (fidEntry.CurrentPosition / BLOCK_SIZE) % 16];
          if (mvNewBlock == 0)
          {
            // 0 is used as it is a directory block
            // using it here denotes the end of the data.
            return (new FileSystemReturnData(requestId, -1));
          }
          else
          {
            fidEntry.CurrentDiskBlock = (byte) mvNewBlock;
          }
        }

        diskRequest(device.deviceName, "FS_READ::GETBLOCK", "GETBLOCK",
            fsFileNumber, requestId, -1, fidEntry.CurrentDiskBlock, null);
      }
      else
      {
        // No need to swap buffer. Just returnt he next character.
        int mvDataItem = SVB2I & fidEntry.Buffer
            [fidEntry.CurrentPosition % BLOCK_SIZE];

        fidEntry.CurrentPosition++;
        if (mvDataItem == 0x1A)
        {
          // EOF

          mvDataItem = -1;
          fidEntry.CurrentPosition--;
        }

        return (new FileSystemReturnData(requestId, mvDataItem));
      }
    }
    else
    {
      return (new FileSystemReturnData(requestId, -1));
    }
    return null;
  }

  /**
   * If the mode is correct, this starts or contines writing a file.
   *
   * @param requestId Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData write(int requestId, int iFSFileNo)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(iFSFileNo);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.Device);

    if (fidEntry.Mode == WRITING)
    {
      // Do checking for a new context
      if ((((fidEntry.CurrentPosition) % BLOCK_SIZE) == 0)
           && (fidEntry.CurrentPosition != 0))
      {

        /*        diskRequest ( devicedeviceName, "FS_WRITE::FLUSH", "WRITE",
                      iFSFileNo, requestId,
                      mvRequestData.getData(), fidEntry.CurrentDiskBlock,
                      fidEntry.Buffer);
*/
      }
      else
      {
        if (fidEntry.CurrentPosition == 0)
        {
          // Setup the buffer.
          int mvNextBlock = getFreeBlock(fidEntry.Device);

          fidEntry.CurrentDiskBlock = (byte) mvNextBlock;
          device.directoryTable[(fidEntry.FileNumber * DIR_ENTRY_SIZE)
               + DATA_BLOCKS] = (byte) mvNextBlock;
        }

        //fidEntry.Buffer[ fidEntry.CurrentPosition % BLOCK_SIZE] =
        //                (byte)mvRequestData.getData();

        if (((fidEntry.CurrentPosition + 1) % 128) == 0)
        {
          device.directoryTable[(fidEntry.FileNumber * DIR_ENTRY_SIZE)
               + RECORDS] =
              (byte) (((fidEntry.CurrentPosition + 1) % (16 * BLOCK_SIZE)) / 128);
        }
        fidEntry.CurrentPosition++;
        return (new FileSystemReturnData(requestId, 0));
      }
    }
    else
    {
      return (new FileSystemReturnData(requestId, -1));
    }
    return null;
  }

  /**
   * Checks to see if the File System indicated by MountPoint is full.
   *
   * @param deviceNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean diskFull(int deviceNumber)
  {
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    return ((device.numberOfFreeBlocks == 0)
         || (device.numberOfFreeEntries == 0));
  }

  /**
   * If the mode is correct, this starts or contines writing a file.
   *
   * @param requestId Description of Parameter
   * @param msdosFile Description of the Parameter
   * @param data Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Returned Value
   * @throws AllocationTableException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public FileSystemReturnData write(int requestId, FileSystemFile msdosFile,
      String data, String path) throws AllocationTableException,
      DirectoryException
  {
    return null;
  }

  /**
   * Description of the Method
   *
   * @param requestId Description of Parameter
   * @param msdosFile Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Returned Value
   * @throws AllocationTableException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public FileSystemReturnData read(int requestId, FileSystemFile msdosFile,
      String path) throws AllocationTableException, DirectoryException
  {
    return null;
  }

  /**
   * Reads from the file if it is in the right mode.
   *
   * @param requestId Description of Parameter
   * @param msdosFile Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Returned Value
   * @throws AllocationTableException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public FileSystemReturnData delete(int requestId, FileSystemFile msdosFile,
      String path) throws AllocationTableException, DirectoryException
  {
    return null;
  }

  /**
   * Coordinating this in the one synchronised function means that the
   * allocation of the resources will be safe.
   *
   * @param type Description of Parameter
   * @param deviceNumber Description of Parameter
   * @param Item Description of Parameter
   * @return Description of the Returned Value
   */
  public int resourceAllocator(String type, int deviceNumber, int Item)
  {
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    if (type.equalsIgnoreCase("DIR"))
    {
      int counter;

      for (counter = 0;
          ((counter < TOTAL_DIR_ENTRIES) &&
          (device.dirEntList[counter]));
          counter++)
      {
        ;
      }
      if (counter == TOTAL_DIR_ENTRIES)
      {
        return -1;
      }
      else
      {
        device.dirEntList[counter] = true;
        device.numberOfFreeEntries--;
        return counter;
      }
    }
    else if (type.equalsIgnoreCase("BLOCK"))
    {
      int counter;

      for (counter = 0;
          ((counter < TOTAL_DISK_BLOCKS) &&
          (device.blockList[counter]));
          counter++)
      {
        ;
      }
      if (counter == TOTAL_DISK_BLOCKS)
      {
        return -1;
      }
      else
      {
        device.blockList[counter] = true;
        device.numberOfFreeBlocks--;
        return counter + DISK_BLOCK_OFFSET;
      }

    }
    else if (type.equalsIgnoreCase("CLEARDIR"))
    {

      int mvEntryOffset = Item * DIR_ENTRY_SIZE;
      int counter = 0;
      int mvBlockNum;

      mvBlockNum = SVB2I & device.directoryTable
          [mvEntryOffset + DATA_BLOCKS + counter];

      while ((counter < 16) && (mvBlockNum > 0))
      {
        System.out.println("Freeing block :" + mvBlockNum);
        device.blockList[mvBlockNum - DISK_BLOCK_OFFSET] = false;
        device.directoryTable[mvEntryOffset + DATA_BLOCKS + counter] = 0;

        device.numberOfFreeBlocks++;
        counter++;
        mvBlockNum = SVB2I & device.directoryTable
            [mvEntryOffset + DATA_BLOCKS + counter];
      }
      device.directoryTable[mvEntryOffset + STATUS] = (byte) 0xE5;
      device.dirEntList[Item] = false;
      device.numberOfFreeEntries++;
      return 1;
    }
    else if (type.equalsIgnoreCase("CLEARBLOCK"))
    {

      device.blockList[Item] = false;
      device.numberOfFreeBlocks++;

      return 1;
    }
    else
    {
      return -1;
    }
  }

  /**
   * Deallocates the disk blocks for the specified device and sets the block to
   * deleted.
   *
   * @param mvEntryNumber Description of Parameter
   * @param deviceNumber Description of Parameter
   */
  public void deallocateEntry(int mvEntryNumber, int deviceNumber)
  {
    resourceAllocator("CLEARDIR", deviceNumber, mvEntryNumber);
  }

  public FileSystem requestSystemFile()
  {
    return null;
  }

  public void recordSystemFile()
  {

  }

  /**
   * Will display the specified data to the screen.
   * Operation can be "DIR" or "BUFFER". For DIR, item1 will indicate
   * the entry to dump, item2 will indicate the device. For Buffer,
   * item1 is the FID.
   *
   * @param Operation Description of Parameter
   * @param item1 Description of Parameter
   * @param item2 Description of Parameter
   */
  public String dump(String Operation, int item1, int item2)
  {
    StringBuffer data = new StringBuffer();

    if (Operation.equalsIgnoreCase("DIR"))
    {
      CPM14DeviceTableEntry device =
          (CPM14DeviceTableEntry) deviceTable.getItem(item2);

      int x;
      int Offset = item1 * 32;

      for (x = 0; x < 32; x++)
      {
        if (x > 0 && x < 12)
        {
          data.append((char) device.directoryTable[Offset + x] + " ");
        }
        else
        {
          data.append((SVB2I & device.directoryTable[Offset + x]) + " ");
        }
        if (x == 15)
        {
          data.append("\n");
        }
      }
    }
    else if (Operation.equalsIgnoreCase("BUFFER"))
    {
      CPM14FIDTableEntry fidEntry =
          (CPM14FIDTableEntry) fidTable.getItem(item1);
      int x;

      for (x = 0; x < 1024; x++)
      {
        if ((SVB2I & fidEntry.Buffer[x]) == 0x1A)
        {
          data.append("<EOF>");
        }
        else
        {
          data.append((char) fidEntry.Buffer[x]);
        }
      }
    }
    data.append("\n");
    return data.toString();
  }

  /**
   * Handle the DiskRequestComplete messages and perform the necessary
   * cary on functions.
   *
   * @param mvTheMessage Description of Parameter
   */
  private void handleReturnMessages(OSMessageAdapter mvTheMessage)
  {
    DiskRequest mvMessageData = (DiskRequest) mvTheMessage.getBody();

    int mvRequestID = mvMessageData.getRequestId();

    CPM14RequestTableEntry mvRequestData =
        (CPM14RequestTableEntry) requestTable.getItem(mvRequestID);

    int mvFID = mvRequestData.FSFileNum;

    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(mvFID);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.Device);

    if (mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_BUFFER"))
    {
      //     System.out.println("Handle Write byffer return."); // DEBUG
      mvRequestData.Type = "FS_CLOSE::WRITE_DIR1";

      byte[] mvToWrite = new byte[1024];

      int counter;

      for (counter = 0; counter < 1024; counter++)
      {
        mvToWrite[counter] = device.directoryTable[counter];
      }

      DiskRequest mvNewReq = new DiskRequest(mvRequestID, 0, mvToWrite);
      //Message mvNewMessage = new Message ( id, devicedeviceName,
      //                                      "DISKREQUEST", mvNewReq);
      //SendMessage( mvNewMessage );
    }
    else if (mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_DIR1"))
    {
//      System.out.println("Handle Write dir1 return."); // DEBUG
      mvRequestData.Type = "FS_CLOSE::WRITE_DIR2";

      byte[] mvToWrite = new byte[1024];

      int counter;

      for (counter = 0; counter < 1024; counter++)
      {
        mvToWrite[counter] = device.directoryTable[counter + 1024];
      }


      DiskRequest mvNewReq = new DiskRequest(mvRequestID, 1, mvToWrite);
      //Message mvNewMessage = new Message ( id, devicedeviceName,
      //                                      "DISKREQUEST", mvNewReq);
      //SendMessage( mvNewMessage );
    }
    else if (mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_DIR2"))
    {
//      System.out.println("Handle Write dir2 return."); // DEBUG
//      System.out.println("About to remove FID "+mvFID); // DEBUG
      //return(new FileSystemReturnData(requestId, 0));
      if (device.openFileNames.containsKey(fidEntry.Filename))
      {
        device.openFileNames.remove(fidEntry.Filename);
      }
      fidTable.remove(mvFID);
      requestTable.remove(mvRequestID);
    }
    else if (mvRequestData.Type.equalsIgnoreCase("FS_READ::GETBLOCK"))
    {
      if (mvMessageData.getDiskBlock() >= 0)
      {
        int counter;

        for (counter = 0; counter < BLOCK_SIZE; counter++)
        {
          fidEntry.Buffer[counter] = mvMessageData.getData()[counter];
        }

        fidEntry.CurrentDiskBlock = (byte) mvMessageData.getDiskBlock();

        int mvReturnItem = SVB2I & fidEntry.Buffer[
            fidEntry.CurrentPosition % BLOCK_SIZE];

        if (mvReturnItem == 0x1A)
        {
          // EOF

          //return(new FileSystemReturnData(requestId, -1));
        }
        else
        {
          fidEntry.CurrentPosition++;
          //return(new FileSystemReturnData(requestId, mvReturnItem));
        }
        requestTable.remove(mvRequestID);
      }
      else
      {
        fidTable.remove(mvFID);
        requestTable.remove(mvRequestID);
        //return(new FileSystemReturnData(requestId, -1));
      }
    }
    else if (mvRequestData.Type.equalsIgnoreCase("FS_WRITE::FLUSH"))
    {
      if (mvMessageData.getDiskBlock() >= 0)
      {
        System.out.println("Interupt reveived.bout to write");

        // DEBUG

        int mvLastBlock = SVB2I & device.directoryTable
            [(fidEntry.FileNumber * DIR_ENTRY_SIZE) + DATA_BLOCKS + 15];

        //System.out.print("Cmp: "+mvLastBlock+" - "); // DEBUG
        //System.out.println("Entry :"+fidEntry.FileNumber); // DEBUG
        //System.out.println("entrysize : "+DIR_ENTRY_SIZE); // DEBUG
        //System.out.println("datablocks :"+ DATA_BLOCKS); // DEBUG
        //System.out.println
        //     ((fidEntry.FileNumber*DIR_ENTRY_SIZE) + DATA_BLOCKS + 16); // DEBUG


        //System.out.println(fidEntry.CurrentDiskBlock); // DEBUG
        if (fidEntry.CurrentDiskBlock == mvLastBlock)
        {
          System.out.println("Setting up new DirEnt.");

          // DEBUG

          int mvNewEntry = getFreeEntry(fidEntry.Device);

          if (mvNewEntry == -1)
          {
            requestTable.remove(mvRequestID);
            //return(new FileSystemReturnData(requestId, -1));
          }

          int mvNewOffset = mvNewEntry * DIR_ENTRY_SIZE;
          int mvCurOffset = fidEntry.FileNumber * DIR_ENTRY_SIZE;
          int x;

          for (x = 1; x <= 11; x++)
          {
            device.directoryTable[mvNewOffset + x] =
                device.directoryTable[mvCurOffset + x];
          }
          device.directoryTable[mvNewOffset + 0] = 0;
          device.directoryTable[mvNewOffset + EXTENT] =
              (byte) (device.directoryTable[mvCurOffset + EXTENT] + (byte) 1);
          device.directoryTable[mvCurOffset + RECORDS] = (byte) 0x80;

          fidEntry.FileNumber = mvNewEntry;
          System.out.println("Setup dirent :" + mvNewEntry);
          // DEBUG
        }

        int mvNewBlock = getFreeBlock(fidEntry.Device);

        if (mvNewBlock >= 0)
        {
          System.out.println("New disk block :" + mvNewBlock);

          //DEBUG

          int mvOffset = (fidEntry.FileNumber * DIR_ENTRY_SIZE) + DATA_BLOCKS;
          int mvBlockLocation = mvOffset +
              ((fidEntry.CurrentPosition / BLOCK_SIZE) % 16);

          device.directoryTable[mvBlockLocation] = (byte) mvNewBlock;
          fidEntry.CurrentDiskBlock = (byte) mvNewBlock;
          fidEntry.Buffer[fidEntry.CurrentPosition % BLOCK_SIZE] =
              (byte) mvRequestData.Data;
          fidEntry.CurrentPosition++;
          //return(new FileSystemReturnData(requestId, 0));
//          Dump( "DIR", 0,0); // DEBUG
        }
        else
        {
          //return(new FileSystemReturnData(requestId, -1));
        }

        requestTable.remove(mvRequestID);

      }
      else
      {
        requestTable.remove(mvRequestID);
        //return(new FileSystemReturnData(requestId, -1));
      }
    }

    //else if ( mvRequestData.Type.equalsIgnoreCase(""))
//    System.out.println("FS:HandleReturnMessage - end"); // DEBUG

    //return null;
  }

  /**
   * Converts a string filename "mount:name.ext" to a byte array that is
   * FFFFFFFFEEE where F is the filename and E is the extention.  Spaces are
   * padded out.
   *
   * @param newFilename Description of Parameter
   * @return Description of the Returned Value
   */
  private byte[] convertFilename(String newFilename)
  {
    StringBuffer returnData = new StringBuffer("           ");
    String name = "";
    String ext = "";

    int startFilename = newFilename.indexOf(":") + 1;
    int endFilename = newFilename.indexOf(".");
    if (endFilename == -1)
    {
      endFilename = newFilename.length() - 1;
    }

    name = newFilename.substring(startFilename, endFilename);
    if (newFilename.indexOf(".") != -1)
    {
      ext = newFilename.substring(endFilename + 1, endFilename + 4);
    }

    returnData.replace(0, 7, name);
    returnData.replace(8, 11, ext);
    return returnData.toString().getBytes();
  }

  /**
   * Insert a queue Item and send a request to the disk.
   *
   * @param to Description of Parameter
   * @param from Description of Parameter
   * @param type Description of Parameter
   * @param FSFileNo Description of Parameter
   * @param FSMRequestID Description of Parameter
   * @param RequestData Description of Parameter
   * @param block Description of Parameter
   * @param data Description of Parameter
   */
  private void diskRequest(String to, String from, String type, int FSFileNo,
      int FSMRequestID, int RequestData, int block, byte[] data)
  {
    CPM14RequestTableEntry mvQueueEntry = new CPM14RequestTableEntry();

    mvQueueEntry.Source = new String(from);
    mvQueueEntry.Type = new String(type);
    mvQueueEntry.FSFileNum = FSFileNo;
    mvQueueEntry.FSMRequestID = FSMRequestID;
    mvQueueEntry.Data = RequestData;

    // Request new block
    int iReqNum = requestTable.add(mvQueueEntry);
    DiskRequest mvTheRequest = new DiskRequest(iReqNum,
        block,
        data);
    //Message mvTheReq = new Message( id,  new String(to),"DISKREQUEST", mvTheRequest);
    //SendMessage ( mvTheReq );
  }

  /**
   * Returns a ReturnValie message to the Destination basically saying it's
   * all over.
   *
   * @param mvFSMReqNo Description of Parameter
   * @param returnData Description of Parameter
   */
  private void returnValue(int mvFSMReqNo, int returnData)
  {
    FileSystemReturnData mvToReturn = new FileSystemReturnData(mvFSMReqNo,
        returnData);
    /*Message mvReturnMessage = new Message(id, mvDestination,  "RETURNVALUE",
                                           mvToReturn);
    SendMessage ( mvReturnMessage );*/
  }
}
