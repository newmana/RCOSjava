package org.rcosjava.software.filesystem.cpm14;

import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.messages.os.AddDiskRequest;
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
 * Implementation of a CPM file System.
 * <P>
 * @author Brett Carter
 * @author Andrew Newman
 * @created 28 March 1996
 */
public class CPM14FileSystem extends OSMessageHandler implements FileSystem
{
  /**
   * Mount point which seperates the mount point with the file name.
   */
  private final static String MOUNT_POINT_SEPERATOR = ":";

  /**
   * The block size of the file system in bytes.
   */
  private final static int BLOCK_SIZE = 1024;

  /**
   * The total number of blocks that makes up the disk.
   */
  private final static int TOTAL_DISK_BLOCKS = 240;

  /**
   * The total number of blocks that make up the directory.
   */
  private final static int TOTAL_DIR_BLOCKS = 2;

  /**
   * The location of the start of the directory on the disk.
   */
  private final static int DIR_BLOCK_OFFSET = 0;

  /**
   * The location of where to start writing data files.
   */
  private final static int DISK_BLOCK_OFFSET = DIR_BLOCK_OFFSET +
      TOTAL_DIR_BLOCKS;

  /**
   * The length in bytes of a directory entry.
   */
  private final static int DIR_ENTRY_SIZE = 32;

  /**
   * The total number of directory entries that the disk can support.
   */
  private final static int TOTAL_DIR_ENTRIES = (BLOCK_SIZE * TOTAL_DIR_BLOCKS)
       / DIR_ENTRY_SIZE;

  /**
   * Description of the Field
   */
  private final static int EOF = 1;

  /**
   * Description of the Field
   */
  private final static int NOT_EOF = 0;

  /**
   * Used to convert signed 8 bit numbers (byte) to integers.
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
  public CPM14FileSystem(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);
    requestTable = new IndexedList(100, 10);
    mountTable = new HashMap();
    deviceTable = new IndexedList(100, 10);
    fidTable = new IndexedList(100, 10);
  }

  /**
   * Handle a mount request.  Creates a new device and sets its status to
   * mounted.
   *
   * @param newMountPoint The name of the new mount point e.g. "C"
   * @param newDeviceName The name of the new device name e.g. "DISK1"
   */
  public void mount(String mountPoint, String deviceName)
  {
    // Note, in the simulation, the disks are initialized each
    // time the program is run. For a disk structure that
    // remained between runs, the mount would be very different.
    CPM14DeviceTableEntry device = new CPM14DeviceTableEntry(deviceName,
      BLOCK_SIZE, TOTAL_DIR_BLOCKS, TOTAL_DISK_BLOCKS, TOTAL_DIR_ENTRIES,
      DIR_ENTRY_SIZE);

    // Add device to the device table and the Mount table.
    int deviceNumber = deviceTable.add(device);
    mountTable.put(mountPoint, new Integer(deviceNumber));

    // Set status to mounted.
    device.mounted();
  }

  /**
   * Perfoms an allocation of the file. Creats an entry in the FID table
   * and inits it.
   *
   * @param requestId unique identifier for this request.
   * @param fileName the name of the file to allocate.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData allocate(int requestId, String filename)
  {
    // Get the mount point of the file.
    String mountPoint = getMountPoint(filename);

    // Assume device is mounted as the manager has passed in request.
    int deviceNumber = ((Integer) mountTable.get(mountPoint)).intValue();

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    // Check if file is already open
    if (device.isFileOpen(filename))
    {
      return (new FileSystemReturnData(requestId, -1));
    }

    // Set the file as being open.
    device.setFileOpen(filename);

    // Create the entry and init it.
    CPM14FIDTableEntry fidEntry = new CPM14FIDTableEntry(deviceNumber, filename,
      BLOCK_SIZE);
    int FID = fidTable.add(fidEntry);
    return (new FileSystemReturnData(requestId, FID));
  }

  /**
   * Sets up a directory entry for the file and sets it to a 0 length file.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData create(int requestId, int fsFileNumber)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.getDeviceNumber());

    // Check for spaces and necessary conditions
    if ((!fidEntry.hasBeenAllocated()) ||
        (diskFull(fidEntry.getDeviceNumber())))
    {
      // Return an error
      return (new FileSystemReturnData(requestId, -1));
    }

    int deviceNumber = fidEntry.getDeviceNumber();

    // Allocate the dir entry
    int dirEntry = getFreeEntry(deviceNumber);
    if (dirEntry == -1)
    {
      return (new FileSystemReturnData(requestId, -1));
    }

    // Setup the entry
    int offset = dirEntry * DIR_ENTRY_SIZE;
    device.setByteInEntry(offset, (byte) 0);
    byte[] byteFilename = convertFilename(fidEntry.getFileName());
//    System.err.println("Byte filename: " + byteFilename.length);

    for (int counter = 0; counter < 11; counter++)
    {
//      System.err.println("Dir offset: " + (offset + CPM14TableOffset.FILENAME + counter));
//      System.err.println("Byte offset: " + (counter));
      device.setByteInEntry(offset + CPM14TableOffset.FILENAME + counter,
          byteFilename[counter]);
    }

    device.setByteInEntry(offset + CPM14TableOffset.EXTENT, (byte) 0);
    device.setByteInEntry(offset + CPM14TableOffset.RESERVED, (byte) 0);
    device.setByteInEntry(offset + CPM14TableOffset.RESERVED + 1, (byte) 0);
    device.setByteInEntry(offset + CPM14TableOffset.RECORDS, (byte) 0);

    for (int counter = 0; counter < 16; counter++)
    {
      device.setByteInEntry(offset + CPM14TableOffset.DATA_BLOCKS + counter,
          (byte) 0);
    }

    // Setup the initial data.
    fidEntry.setFileNumber(dirEntry);
    fidEntry.isWriting();
    fidEntry.setCurrentPosition(0);

    return (new FileSystemReturnData(requestId, 0));
  }

  /**
   * Opens the specified file for reading.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData open(int requestId, int fsFileNumber)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.getDeviceNumber());
    int deviceNumber = fidEntry.getDeviceNumber();

    if (fidEntry.hasBeenAllocated())
    {
      int mvTheFile;

      if ((mvTheFile = getNextDirectoryEntry(1, 1)) != -1)
      {
        // init for first read.
        fidEntry.isReading();
        fidEntry.setFileNumber(mvTheFile);
        fidEntry.setCurrentPosition(0);
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
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData read(int requestId, int fsFileNumber)
  {
    // Setup the data
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.getDeviceNumber());
    int deviceNumber = fidEntry.getDeviceNumber();

    // Check the current mode.
    if (fidEntry.isBeingRead())
    {
      // The file is being read.
      if (((fidEntry.getCurrentPosition()) % BLOCK_SIZE) == 0)
      {
        // check for end of dirent. Use disk blocks.
        if (fidEntry.getCurrentDiskBlock() == device.getByteInEntry(
          (fidEntry.getFileNumber() * DIR_ENTRY_SIZE) +
           CPM14TableOffset.DATA_BLOCKS + 15))
        {
          //this.dumpToScreen("DIR",0,fidEntry.getFileNumber());
          int newEntry = getNextDirectoryEntry(fidEntry.getFileNumber(),
              deviceNumber);

          if (newEntry == -1)
          {
            return (new FileSystemReturnData(requestId, -1));
          }

          fidEntry.setFileNumber(newEntry);
          fidEntry.setCurrentDiskBlock(SVB2I & device.getByteInEntry(
              (newEntry * DIR_ENTRY_SIZE) + CPM14TableOffset.DATA_BLOCKS));
        }
        else
        {
          int newBlock = SVB2I & device.getByteInEntry(
              (fidEntry.getFileNumber() * DIR_ENTRY_SIZE) +
              CPM14TableOffset.DATA_BLOCKS +
              (fidEntry.getCurrentPosition() / BLOCK_SIZE) % 16);
          if (newBlock == 0)
          {
            // 0 is used as it is a directory block
            // using it here denotes the end of the data.
            return (new FileSystemReturnData(requestId, -1));
          }
          else
          {
            fidEntry.setCurrentDiskBlock(newBlock);
          }
        }

//        diskRequest(device.getName(), "FS_READ::GETBLOCK", "GETBLOCK",
//            fsFileNumber, requestId, -1, fidEntry.getCurrentDiskBlock(), null);
      }
      else
      {
        // No need to swap buffer. Just returnt he next character.
        int dataItem = SVB2I & fidEntry.getByteInBuffer(
            fidEntry.getCurrentPosition() % BLOCK_SIZE);
        fidEntry.incCurrentPosition();

        if (dataItem == 0x1A)
        {
          // EOF
          dataItem = -1;
          fidEntry.decCurrentPosition();
        }

        return (new FileSystemReturnData(requestId, dataItem));
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
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @param data the data to write.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData write(int requestId, int fsFileNumber, byte data)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.getDeviceNumber());

    if (fidEntry.isBeingWritten())
    {
      // Do checking for a new context
      if ((((fidEntry.getCurrentPosition()) % BLOCK_SIZE) == 0)
           && (fidEntry.getCurrentPosition() != 0))
      {
//        System.err.println("About to write Block and get new one.");
        sendDiskRequest(CPM14RequestTableEntry.FLUSH, fsFileNumber, requestId,
            data, fidEntry.getCurrentDiskBlock(), fidEntry.getBuffer());
      }
      else
      {
        if (fidEntry.getCurrentPosition() == 0)
        {
          // Setup the buffer.
          int nextBlock = getFreeBlock(fidEntry.getDeviceNumber());

          fidEntry.setCurrentDiskBlock((byte) nextBlock);
          device.setByteInEntry((fidEntry.getFileNumber() * DIR_ENTRY_SIZE)
               + CPM14TableOffset.DATA_BLOCKS, (byte) nextBlock);
        }

        fidEntry.setByteInBuffer(fidEntry.getCurrentPosition() % BLOCK_SIZE,
            (byte) data);

        if (((fidEntry.getCurrentPosition() + 1) % 128) == 0)
        {
          device.setByteInEntry((fidEntry.getFileNumber() * DIR_ENTRY_SIZE)
               + CPM14TableOffset.RECORDS,
              (byte) (((fidEntry.getCurrentPosition() + 1) %
              (16 * BLOCK_SIZE)) / 128));
        }
        fidEntry.incCurrentPosition();
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
   * Free's all disk structures associated with the specified file. Leaves the
   * file in the PID table though.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData close(int requestId, int fsFileNumber)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.getDeviceNumber());

    if (fidEntry.isBeingWritten())
    {
      // add EOF mark at the end of the file unless on a border line between the
      // next block. If no eof is encountered, and no following block is allocated,
      // Then the end of the file falls directly on the line.
      if (((fidEntry.getCurrentPosition()) % BLOCK_SIZE) != 0)
      {
        //Currentposition will already point to the place to write the  EOF
        // character
        fidEntry.setByteInBuffer((fidEntry.getCurrentPosition() % BLOCK_SIZE),
          (byte) 0x1A);
      }

      sendDiskRequest(CPM14RequestTableEntry.WRITE_BUFFER, fsFileNumber,
          requestId, -1, fidEntry.getCurrentDiskBlock(), fidEntry.getBuffer());
    }
    else
    {
      if (device.isFileOpen(fidEntry.getFileName()))
      {
        device.setFileClosed(fidEntry.getFileName());
      }

      fidTable.remove(fsFileNumber);
      fidEntry.isClosing();

      // Clean up entries in FID table.
      return (new FileSystemReturnData(requestId, 0));
    }
    return null;
  }

  /**
   * Replys to the sender of the message 1 if at end of file
   * 0 if not.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData eof(int requestId, int fsFileNumber)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.getDeviceNumber());

    int mvReturnValue = NOT_EOF;

    if (fidEntry.isBeingRead())
    {
      // Check if in the middle of a block
      if (((fidEntry.getCurrentPosition()) % BLOCK_SIZE) != 0)
      {
        // Easy, check the current character for 0x1A, EOF.
        if ((SVB2I &
            fidEntry.getByteInBuffer((fidEntry.getCurrentPosition() % BLOCK_SIZE))) == 0x1A)
        {
          mvReturnValue = EOF;
        }
      }
      else
      {
        // The position is at the very end of a data block. Check if that block
        // is the last one in the context.
        int mvDiskBlockOffset = (fidEntry.getFileNumber() * DIR_ENTRY_SIZE) +
            CPM14TableOffset.DATA_BLOCKS;

        if (fidEntry.getCurrentDiskBlock() !=
            (SVB2I & device.getByteInEntry((fidEntry.getFileNumber() * DIR_ENTRY_SIZE) +
            CPM14TableOffset.DATA_BLOCKS + 16)))
        {
          // We are not in the last one.
          int mvCurrentBlockPosition = (fidEntry.getCurrentPosition() /
              BLOCK_SIZE) % 16;

          // Check if the next block in the list == 0. If it is, this is the
          // end of the file.
          if ((SVB2I & device.getByteInEntry(
              mvDiskBlockOffset + mvCurrentBlockPosition + 1)) == 0)
          {
            mvReturnValue = EOF;
          }
        }
        else
        {
          // If there isn't an entry for the file with a higher context,
          // this be the end.
          int nextEntry = getNextDirectoryEntry(fidEntry.getFileNumber(),
            fidEntry.getDeviceNumber());

          if (nextEntry == -1)
          {
            mvReturnValue = EOF;
          }
        }
      }
    }
    return (new FileSystemReturnData(requestId, mvReturnValue));
  }

  /**
   * Free's all disk structures associated with the specified file. Leaves the
   * file in the PID table though.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData delete(int requestId, int fsFileNumber)
  {
    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    // If file isn't at the allocated state
    if (!fidEntry.hasBeenAllocated())
    {
      return (new FileSystemReturnData(requestId, -1));
    }

    int deviceNumber = fidEntry.getDeviceNumber();
    int current;
    int next;

    // eliminate all the dir entries associated with the file.
    current = getDirectoryPosition(fidEntry.getFileName());
    if (current == -1)
    {
      // Fine, no work to be done.
      return (new FileSystemReturnData(requestId, 0));
    }

    next = getNextDirectoryEntry(current, deviceNumber);
    while (next != -1)
    {
      deallocateEntry(deviceNumber, current);
      current = next;
      next = getNextDirectoryEntry(current, deviceNumber);
    }
    deallocateEntry(current, deviceNumber);

    // Clear the data items in the FID table
    fidEntry.initialize();

    return (new FileSystemReturnData(requestId, 0));
  }

  /**
   * Checks to see if the file system indicated by device number is full.
   *
   * @param deviceNumber the unique device number to check.
   * @return true if the device has no free blocks or free directory entries.
   */
  public boolean diskFull(int deviceNumber)
  {
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    return ((device.getNumberOfFreeBlocks() == 0)
         || (device.getNumberOfFreeEntries() == 0));
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
   * Removes an entry from the top level directory.
   *
   * @param deviceNumber the device to get to remove directory from.
   * @param entryNumber the entry to remove.
   */
  public int cleanDirectoryEntry(int deviceNumber, int entryNumber)
  {
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    int counter = 0;

    int entryOffset = entryNumber * DIR_ENTRY_SIZE;
    int blockNum = SVB2I & device.getByteInEntry(
      entryOffset + CPM14TableOffset.DATA_BLOCKS + counter);

    while ((counter < 16) && (blockNum > 0))
    {
//      System.out.println("Freeing block :" + blockNum);
      device.deallocateBlock(blockNum - DISK_BLOCK_OFFSET);
      device.setByteInEntry(entryOffset + CPM14TableOffset.DATA_BLOCKS +
          counter, (byte) 0);

      counter++;

      blockNum = SVB2I & device.getByteInEntry(entryOffset +
          CPM14TableOffset.DATA_BLOCKS + counter);
    }

    device.setByteInEntry(entryOffset + CPM14TableOffset.STATUS, (byte) 0xE5);
    device.deallocateEntry(entryNumber);

    return 1;
  }

  /**
   * A successful flush of data to disk.
   *
   * @param request the request from the disk manager.
   */
  public void flush(DiskRequest request)
  {
    // Get the requested data from the request table.
    int requestId = request.getRequestId();
    CPM14RequestTableEntry requestData =
              (CPM14RequestTableEntry) requestTable.getItem(requestId);

    // Get the file id from the request data.
    int fileId = requestData.getFileSystemNumber();
    CPM14FIDTableEntry fileIdEntry = (CPM14FIDTableEntry)
        fidTable.getItem(fileId);

    // Get the device from the device table based on fid entry.
    CPM14DeviceTableEntry device = (CPM14DeviceTableEntry)
        deviceTable.getItem(fileIdEntry.getDeviceNumber());

    if (request.getDiskBlock() >= 0)
    {
//      System.out.println("Interupt reveived.bout to write");

      // DEBUG

      int lastBlock = SVB2I & device.getByteInEntry(
          (fileIdEntry.getFileNumber() * DIR_ENTRY_SIZE) +
          CPM14TableOffset.DATA_BLOCKS + 15);

      //System.out.print("Cmp: "+mvLastBlock+" - "); // DEBUG
      //System.out.println("Entry :"+fidEntry.getFileNumber()); // DEBUG
      //System.out.println("entrysize : "+DIR_ENTRY_SIZE); // DEBUG
      //System.out.println("datablocks :"+ DATA_BLOCKS); // DEBUG
      //System.out.println
      //     ((fidEntry.getFileNumber()*DIR_ENTRY_SIZE) + DATA_BLOCKS + 16); // DEBUG
      //System.out.println(fidEntry.getCurrentDiskBlock()); // DEBUG

      if (fileIdEntry.getCurrentDiskBlock() == lastBlock)
      {
//        System.out.println("Setting up new DirEnt.");

        int newEntry = getFreeEntry(fileIdEntry.getDeviceNumber());

        if (newEntry == -1)
        {
          requestTable.remove(requestId);
          //return(new FileSystemReturnData(requestId, -1));
        }

        int newOffset = newEntry * DIR_ENTRY_SIZE;
        int currentOffset = fileIdEntry.getFileNumber() * DIR_ENTRY_SIZE;
        int x;

        for (x = 1; x <= 11; x++)
        {
          device.setByteInEntry(newOffset + x,
            device.getByteInEntry(currentOffset + x));
        }

        device.setByteInEntry(newOffset + 0, (byte) 0);
        device.setByteInEntry(newOffset + CPM14TableOffset.EXTENT,
            (byte) (device.getByteInEntry(currentOffset +
            CPM14TableOffset.EXTENT) + (byte) 1));
        device.setByteInEntry(currentOffset + CPM14TableOffset.RECORDS,
            (byte) 0x80);

        fileIdEntry.setFileNumber(newEntry);
//        System.out.println("Setup dirent :" + newEntry);
        // DEBUG
      }

      int mvNewBlock = getFreeBlock(fileIdEntry.getDeviceNumber());

      if (mvNewBlock >= 0)
      {
//        System.out.println("New disk block :" + mvNewBlock);

        //DEBUG
        int mvOffset = (fileIdEntry.getFileNumber() * DIR_ENTRY_SIZE) +
            CPM14TableOffset.DATA_BLOCKS;
        int mvBlockLocation = mvOffset +
            ((fileIdEntry.getCurrentPosition() / BLOCK_SIZE) % 16);

        device.setByteInEntry(mvBlockLocation, (byte) mvNewBlock);
        fileIdEntry.setCurrentDiskBlock((byte) mvNewBlock);
        fileIdEntry.setByteInBuffer(
            fileIdEntry.getCurrentPosition() % BLOCK_SIZE,
            (byte) requestData.getData());
        fileIdEntry.incCurrentPosition();
//        return(new FileSystemReturnData(requestId, 0));
//        Dump( "DIR", 0,0); // DEBUG
      }
      else
      {
//        return(new FileSystemReturnData(requestId, -1));
      }

      requestTable.remove(requestId);
    }
  }

  /**
   * Writes the buffer to the device.
   *
   * @param request the request details the request id and file and the data
   *   to write to the device.
   */
  public void writeFileBuffer(DiskRequest request)
  {
    // Get the requested data from the request table.
    int requestId = request.getRequestId();
    CPM14RequestTableEntry requestData =
        (CPM14RequestTableEntry) requestTable.getItem(requestId);

    // Get the file id from the request data.
    int fileId = requestData.getFileSystemNumber();
    CPM14FIDTableEntry fileIdEntry = (CPM14FIDTableEntry)
        fidTable.getItem(fileId);

    // Get the device from the device table based on fid entry.
    CPM14DeviceTableEntry device = (CPM14DeviceTableEntry)
        deviceTable.getItem(fileIdEntry.getDeviceNumber());

    // Get the blocks to write out to disk.
    byte[] toWrite = new byte[BLOCK_SIZE];
    for (int counter = 0; counter < BLOCK_SIZE; counter++)
    {
      toWrite[counter] = device.getByteInEntry(counter);
    }

    // Set request type to WRITE_DIR
    requestData.setRequestType(CPM14RequestTableEntry.WRITE_DIR);

    // Create a new disk request and send with a new request id set to
    request = new DiskRequest(requestId, 0, toWrite);
    AddDiskRequest message = new AddDiskRequest(this, request);
    sendMessage(message);
  }

  /**
   * Writes out the directory table to disk.
   *
   * @param request the request details the request id and file and the data
   *   to write to the device.
   */
  public void writeDirectoryBuffer(DiskRequest request)
  {
    // Get the requested data from the request table.
    int requestId = request.getRequestId();
    CPM14RequestTableEntry requestData =
        (CPM14RequestTableEntry) requestTable.getItem(requestId);

    // Get the file id from the request data.
    int fileId = requestData.getFileSystemNumber();
    CPM14FIDTableEntry fileIdEntry = (CPM14FIDTableEntry)
        fidTable.getItem(fileId);

    // Get the device from the device table based on fid entry.
    CPM14DeviceTableEntry device = (CPM14DeviceTableEntry)
        deviceTable.getItem(fileIdEntry.getDeviceNumber());

    // Get the blocks to write out to disk.
    byte[] toWrite = new byte[BLOCK_SIZE];
    for (int counter = 0; counter < BLOCK_SIZE; counter++)
    {
      toWrite[counter] = device.getByteInEntry(counter);
    }

    // Set request type to REMOVE_FILE_HANDLE
    requestData.setRequestType(CPM14RequestTableEntry.REMOVE_FILE_HANDLE);

    // Create a new disk request
    request = new DiskRequest(requestId, 1, toWrite);
    AddDiskRequest message = new AddDiskRequest(this, request);
    sendMessage(message);
  }

  /**
   * Removes the file handle from a given file.
   *
   * @param request the request details the request id and file and the data
   *   to write (if any).
   */
  public void removeFileHandle(DiskRequest request)
  {
    // Get the requested data from the request table.
    int requestId = request.getRequestId();
    CPM14RequestTableEntry requestData =
        (CPM14RequestTableEntry) requestTable.getItem(requestId);

    // Get the file id from the request data.
    int fileId = requestData.getFileSystemNumber();
    CPM14FIDTableEntry fileIdEntry = (CPM14FIDTableEntry)
        fidTable.getItem(fileId);

    // Get the device from the device table based on fid entry.
    CPM14DeviceTableEntry device = (CPM14DeviceTableEntry)
        deviceTable.getItem(fileIdEntry.getDeviceNumber());

    //ReturnValue( mvRequestData.Source, mvRequestData.FSMRequestID, 0 );

    // Set the file as closed.
    device.setFileClosed(fileIdEntry.getFileName());

    // Remove request and file id entry from tables.
    fidTable.remove(fileId);
    requestTable.remove(requestId);
  }

  public void recordSystemFile()
  {
  }

  public FileSystem requestSystemFile()
  {
    return null;
  }

  /**
   * Returns the request table.
   *
   * @return the request table.
   */
  public IndexedList getRequestTable()
  {
    return requestTable;
  }

  /**
   * Returns the mount point based on the given file name.
   *
   * @param fileName the name of file which contains the mount point e.g.
   *   "C:fred.doc".
   * @return the mount point e.g. "C".
   */
  public String getMountPoint(String fileName)
  {
    int index;
    String mountPoint;

    index = fileName.indexOf(MOUNT_POINT_SEPERATOR);
    if (index == -1)
    {
      return null;
    }
    mountPoint = fileName.substring(0, index);
    return mountPoint;
  }

  /**
   * Displays a file's entry on a devices directory as a string.
   *
   * @param deviceNumber the unique device number to check.
   * @param fsFileNumber unique identifier of the file.
   * @return a string representation of the file's directory entry.
   */
  public String dumpDirectoryEntry(int deviceNumber, int fsFileNumber)
  {
    StringBuffer data = new StringBuffer();

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    int x;
    int offset = fsFileNumber * 32;

    for (x = 0; x < 32; x++)
    {
      if (x > 0 && x < 12)
      {
        data.append((char) device.getByteInEntry(offset + x) + " ");
      }
      else
      {
        // Converts bytes to integer
        data.append((SVB2I & device.getByteInEntry(offset + x)) + " ");
      }
      if (x == 15)
      {
        data.append("\n");
      }
    }

    data.append("\n");
    return data.toString();
  }

  /**
   * Displays a file's buffer as a string.
   *
   * @param fsFileNumber unique identifier of the file.
   * @return a string representation of the file's buffer.
   */
  public String dumpBuffer(int fsFileNumber)
  {
    StringBuffer data = new StringBuffer();

    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    for (int index = 0; index < BLOCK_SIZE; index++)
    {
      if ((SVB2I & fidEntry.getByteInBuffer(index)) == 0x1A)
      {
        data.append("<EOF>");
      }
      else
      {
        data.append((char) fidEntry.getByteInBuffer(index));
      }
    }
    data.append("\n");
    return data.toString();
  }

  /**
   * Returns a file's entry id entry file.
   *
   * @param fsFileNumber unique identifier of the file.
   * @return a string representation of the file's id entry.
   */
  public String dumpFIDEntry(int fsFileNumber)
  {
    StringBuffer data = new StringBuffer();

    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(fsFileNumber);

    data.append(fidEntry.getFileName() + ",");
    data.append(fidEntry.mode);

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

    int mvFID = mvRequestData.getFileSystemNumber();

    CPM14FIDTableEntry fidEntry =
        (CPM14FIDTableEntry) fidTable.getItem(mvFID);

    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(fidEntry.getDeviceNumber());

//    else if (mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_DIR1"))
//    {
//      System.out.println("Handle Write dir1 return."); // DEBUG
//      mvRequestData.Type = "FS_CLOSE::WRITE_DIR2";
//
//      byte[] mvToWrite = new byte[BLOCK_SIZE];
//
//      int counter;
//
//      for (counter = 0; counter < BLOCK_SIZE; counter++)
//      {
//        mvToWrite[counter] = device.getByteInEntry(counter + BLOCK_SIZE);
//      }
//
//
//      DiskRequest mvNewReq = new DiskRequest(mvRequestID, 1, mvToWrite);
      //Message mvNewMessage = new Message ( id, devicedeviceName,
      //                                      "DISKREQUEST", mvNewReq);
      //SendMessage( mvNewMessage );
//    }
//    else if (mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_DIR2"))
//    {
//      System.out.println("Handle Write dir2 return."); // DEBUG
//      System.out.println("About to remove FID "+mvFID); // DEBUG
      //return(new FileSystemReturnData(requestId, 0));
//      if (device.isFileOpen(fidEntry.getFileName()))
//      {
//        device.setFileClosed(fidEntry.getFileName());
//      }
//      fidTable.remove(mvFID);
//      requestTable.remove(mvRequestID);
//    }
    if (mvRequestData.getRequestType() == mvRequestData.GET_BLOCK)
    {
      if (mvMessageData.getDiskBlock() >= 0)
      {
        int counter;

        for (counter = 0; counter < BLOCK_SIZE; counter++)
        {
          fidEntry.setByteInBuffer(counter, mvMessageData.getData()[counter]);
        }

        fidEntry.setCurrentDiskBlock((byte) mvMessageData.getDiskBlock());

        int mvReturnItem = SVB2I & fidEntry.getByteInBuffer(
            fidEntry.getCurrentPosition() % BLOCK_SIZE);

        if (mvReturnItem == 0x1A)
        {
          // EOF

          //return(new FileSystemReturnData(requestId, -1));
        }
        else
        {
          fidEntry.incCurrentPosition();
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
    else if (mvRequestData.getRequestType() == mvRequestData.FLUSH)
    {
      if (mvMessageData.getDiskBlock() >= 0)
      {
//        System.out.println("Interupt reveived.bout to write");

        // DEBUG

        int mvLastBlock = SVB2I & device.getByteInEntry(
            (fidEntry.getFileNumber() * DIR_ENTRY_SIZE) +
            CPM14TableOffset.DATA_BLOCKS + 15);

        //System.out.print("Cmp: "+mvLastBlock+" - "); // DEBUG
        //System.out.println("Entry :"+fidEntry.getFileNumber()); // DEBUG
        //System.out.println("entrysize : "+DIR_ENTRY_SIZE); // DEBUG
        //System.out.println("datablocks :"+ DATA_BLOCKS); // DEBUG
        //System.out.println
        //     ((fidEntry.getFileNumber()*DIR_ENTRY_SIZE) + DATA_BLOCKS + 16); // DEBUG


        //System.out.println(fidEntry.getCurrentDiskBlock()); // DEBUG
        if (fidEntry.getCurrentDiskBlock() == mvLastBlock)
        {
//          System.out.println("Setting up new DirEnt.");

          int mvNewEntry = getFreeEntry(fidEntry.getDeviceNumber());

          if (mvNewEntry == -1)
          {
            requestTable.remove(mvRequestID);
            //return(new FileSystemReturnData(requestId, -1));
          }

          int mvNewOffset = mvNewEntry * DIR_ENTRY_SIZE;
          int mvCurOffset = fidEntry.getFileNumber() * DIR_ENTRY_SIZE;
          int x;

          for (x = 1; x <= 11; x++)
          {
            device.setByteInEntry(mvNewOffset + x,
              device.getByteInEntry(mvCurOffset + x));
          }
          device.setByteInEntry(mvNewOffset + 0, (byte) 0);
          device.setByteInEntry(mvNewOffset + CPM14TableOffset.EXTENT,
              (byte) (device.getByteInEntry(mvCurOffset +
              CPM14TableOffset.EXTENT) + (byte) 1));
          device.setByteInEntry(mvCurOffset + CPM14TableOffset.RECORDS,
              (byte) 0x80);

          fidEntry.setFileNumber(mvNewEntry);
//          System.out.println("Setup dirent :" + mvNewEntry);
          // DEBUG
        }

        int mvNewBlock = getFreeBlock(fidEntry.getDeviceNumber());

        if (mvNewBlock >= 0)
        {
//          System.out.println("New disk block :" + mvNewBlock);

          //DEBUG

          int mvOffset = (fidEntry.getFileNumber() * DIR_ENTRY_SIZE) +
              CPM14TableOffset.DATA_BLOCKS;
          int mvBlockLocation = mvOffset +
              ((fidEntry.getCurrentPosition() / BLOCK_SIZE) % 16);

          device.setByteInEntry(mvBlockLocation, (byte) mvNewBlock);
          fidEntry.setCurrentDiskBlock((byte) mvNewBlock);
          fidEntry.setByteInBuffer(fidEntry.getCurrentPosition() % BLOCK_SIZE,
              (byte) mvRequestData.getData());
          fidEntry.incCurrentPosition();
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
  private void sendDiskRequest(int requestType, int fsFileNumber,
      int fsRequestId, int requestData, int block, byte[] data)
  {
    CPM14RequestTableEntry queueEntry = new CPM14RequestTableEntry(
        requestType, fsFileNumber, fsRequestId, requestData);
    int requestNumber = requestTable.add(queueEntry);

    // Request new block
    DiskRequest request = new DiskRequest(requestNumber, block, data);
    AddDiskRequest message = new AddDiskRequest(this, request);
    sendMessage(message);
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

  /**
   * Deallocates the disk blocks for the specified device and sets the block to
   * deleted.
   *
   * @param mvEntryNumber Description of Parameter
   * @param deviceNumber Description of Parameter
   */
  private boolean deallocateEntry(int deviceNumber, int entryNumber)
  {
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    int counter = 0;

    int mvEntryOffset = entryNumber * DIR_ENTRY_SIZE;
    int mvBlockNum = SVB2I & device.getByteInEntry(
        mvEntryOffset + CPM14TableOffset.DATA_BLOCKS + counter);

    while ((counter < 16) && (mvBlockNum > 0))
    {
//      System.out.println("Freeing block :" + mvBlockNum);
      device.deallocateBlock(mvBlockNum - DISK_BLOCK_OFFSET);
      device.setByteInEntry(mvEntryOffset + CPM14TableOffset.DATA_BLOCKS +
          counter, (byte) 0);

      counter++;

      mvBlockNum = SVB2I & device.getByteInEntry(
          mvEntryOffset + CPM14TableOffset.DATA_BLOCKS + counter);
    }

    device.setByteInEntry(mvEntryOffset + CPM14TableOffset.STATUS, (byte) 0xE5);
    device.deallocateEntry(entryNumber);

    return true;
  }

  /**
   * Returns a free directory entry for the specified device.
   *
   * @param deviceNumber the unique id of the device.
   * @return a free directory entry for the specified device.
   */
  private int getFreeEntry(int deviceNumber)
  {
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    // Loop through until we find a free entry
    int counter = 0;
    while (counter < TOTAL_DIR_ENTRIES && (device.isEntryFree(counter)))
    {
      counter++;
    }

    // If we've come to the end return -1 or return the location of the
    // newly allocated entry.
    if (counter == TOTAL_DIR_ENTRIES)
    {
      return -1;
    }
    else
    {
      device.allocateEntry(counter);
      return counter;
    }
  }

  /**
   * Return a free block number on the specified device.
   *
   * @param deviceNumber Description of Parameter
   * @return The FreeBlock value
   */
  private int getFreeBlock(int deviceNumber)
  {
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    // Loop through until we find a free block.
    int counter = 0;
    while (counter < TOTAL_DISK_BLOCKS && (device.isBlockFree(counter)))
    {
      counter++;
    }

    // If we've come to the end return -1 or return the location of the
    // newly allocated block.
    if (counter == TOTAL_DISK_BLOCKS)
    {
      return -1;
    }
    else
    {
      device.allocateBlock(counter);
      return counter + DISK_BLOCK_OFFSET;
    }
  }

  /**
   * Gets the number of the first entry for the file. Note, the filename
   * should include the mountpoint as well.
   *
   * @param filename the file name inclusive of a mount point.
   * @return the location of the file in the table entry.
   */
  private int getDirectoryPosition(String mvFilename)
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
    int counter = 0;
    int mvIndex = 0;
    int mvOffset;

    boolean mvFound = false;

    while ((counter < TOTAL_DIR_ENTRIES) && (!mvFound))
    {
      mvIndex = 0;
      mvOffset = (counter * DIR_ENTRY_SIZE) + CPM14TableOffset.FILENAME;
      while ((mvIndex < 11) &&
        (mvByteFilename[mvIndex] == device.getByteInEntry(mvOffset + mvIndex)))
      {
        mvIndex++;
      }
      mvFound = (mvIndex == 11);
      if (mvFound)
      {
        mvFound = (device.getByteInEntry(mvOffset -
          CPM14TableOffset.FILENAME + CPM14TableOffset.EXTENT) == 0);
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
   * Returns the next directory entry for the dirent on the specified device
   *
   * @param dirEntry Description of Parameter
   * @param deviceNumber Description of Parameter
   * @return The NextDirectoryEntry value
   */
  private int getNextDirectoryEntry(int dirEntry, int deviceNumber)
  {
    CPM14DeviceTableEntry device =
        (CPM14DeviceTableEntry) deviceTable.getItem(deviceNumber);

    int currentOffset = dirEntry * DIR_ENTRY_SIZE;

    // First check if this entry is totally used. If so, look for next,
    // otherwise exit.
    if ((SVB2I & device.getByteInEntry(currentOffset +
        CPM14TableOffset.RECORDS)) != 0x80)
    {
      return -1;
    }

    // Get the filename to a byte[] for the search.
    byte[] byteFilename = new byte[11];

    byte currentExtent = device.getByteInEntry(currentOffset +
        CPM14TableOffset.EXTENT);

    for (int counter = 0; counter < 11; counter++)
    {
      byteFilename[counter] = device.getByteInEntry(
        currentOffset + CPM14TableOffset.FILENAME + counter);
    }

    // Search
    int counter = 0;
    int mvIndex = 0;
    int mvOffset;
    boolean mvFound = false;

    while ((counter < TOTAL_DIR_ENTRIES) && (!mvFound))
    {
      mvIndex = 0;
      mvOffset = (counter * DIR_ENTRY_SIZE) + CPM14TableOffset.FILENAME;
      while ((mvIndex < 11) &&
        (byteFilename[mvIndex] == device.getByteInEntry(
        mvOffset + mvIndex)))
      {
        mvIndex++;
      }
      mvFound = (mvIndex == 11);
      if (mvFound)
      {
        mvFound = (device.getByteInEntry(mvOffset - CPM14TableOffset.FILENAME +
           CPM14TableOffset.EXTENT) == currentExtent + 1);
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
}
