// *************************************************************************
// FILE     : CPM14FileSystem
// PACKAGE  : FileSystem.CPM14
// PURPOSE  : Handle a CPM file System.
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// DATE     : 28/3/96   Created.
//            01/01/98  Rewrote convertFilename to use 1.1 AN.
//            08/08/98  Added new messaging system support.
//                      Removed from being a message handler.
//                      All requests taken through FileSystemManager. AN
// *************************************************************************

package net.sourceforge.rcosjava.software.filesystem.cpm14;

import java.util.*;
import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.software.disk.DiskRequest;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.software.filesystem.FileSystemReturnData;
import net.sourceforge.rcosjava.software.filesystem.FileSystem;
import net.sourceforge.rcosjava.software.util.IndexedList;

public class CPM14FileSystem implements FileSystem
{
  // Constants
  private final static String MOUNT_POINT_SEPERATOR = ":";
  private final static int BLOCK_SIZE = 1024;
  private final static int TOTAL_DISK_BLOCKS = 240;
  private final static int TOTAL_DIR_BLOCKS = 2;
  private final static int DIR_BLOCK_OFFSET = 0;
  private final static int DISK_BLOCK_OFFSET = DIR_BLOCK_OFFSET + TOTAL_DIR_BLOCKS;
  private final static int DIR_ENTRY_SIZE = 32;
  private final static int TOTAL_DIR_ENTRIES = (BLOCK_SIZE * TOTAL_DIR_BLOCKS)
                                           / DIR_ENTRY_SIZE;
  // Dir Entry
  private final static int STATUS = 0;
  private final static int FILENAME = 1;
  private final static int EXTENSION = 9;
  private final static int EXTENT = 12;
  private final static int RESERVED = 13;
  private final static int RECORDS = 15;
  private final static int DATA_BLOCKS = 16;

  // File Modes
  private final static int MODELESS = -1;
  private final static int ALLOCATED = 0;
  private final static int READING = 1;
  private final static int WRITING = 2;
  private final static int CREATING = 3;
  private final static int DELETING = 4;
  private final static int CLOSING = 5;

  private final static int EOF = 1;
  private final static int NOT_EOF = 0;

  // Used to convert signed 8 bit numbers (byte) to integers.
  private final static int SVB2I = 255;

  private IndexedList cvRequestTable;
  private HashMap cvMountTable;
  private IndexedList cvDeviceTable;
  private IndexedList cvFIDTable;

  public CPM14FileSystem(String myID, MessageHandler myPO)
  {
    //super(myID, myPO);
    cvRequestTable = new IndexedList(100, 10);
    cvMountTable = new HashMap();
    cvDeviceTable = new IndexedList(100,10);
    cvFIDTable = new IndexedList(100, 10);
  }

  //Handle a mount request
  public void mount(String sMountPoint, String sDeviceName)
  {
    CPM14DeviceTableEntry mvDevice = new CPM14DeviceTableEntry();
    // Note, in the simulation, the disks are initialized each
    // time the program is run. For a disk structure that
    // remained between runs, the mount would be very different.
    mvDevice.deviceName = sDeviceName;
    mvDevice.directoryTable = new byte[BLOCK_SIZE * TOTAL_DIR_BLOCKS];
    mvDevice.openFileNames = new HashMap();
    mvDevice.status = 0;
    mvDevice.blockList = new boolean[TOTAL_DISK_BLOCKS];
    mvDevice.numberOfFreeBlocks = TOTAL_DISK_BLOCKS;
    mvDevice.dirEntList = new boolean[TOTAL_DIR_ENTRIES];
    mvDevice.numberOfFreeEntries = TOTAL_DIR_ENTRIES;
    // This section would start the read operations to retrieve the
    // directory blocks from the disk.  As it is, it simply sets the
    // status on all files to deleted so the system knows it can write
    // to them.
    for (int mvCounter = 0; mvCounter < TOTAL_DIR_ENTRIES; mvCounter++)
    {
      mvDevice.directoryTable[(DIR_ENTRY_SIZE * mvCounter) + STATUS] =
        (byte) 0xE5;
      mvDevice.dirEntList[mvCounter] = false;
    }

    // Setup the free dir entry and disk block arrays.
    for (int mvCounter = 0; mvCounter < TOTAL_DISK_BLOCKS; mvCounter++)
    {
      mvDevice.blockList[mvCounter] = false;
    }

    // Add device to the device table and the Mount table.
    int mvDeviceNumber = cvDeviceTable.add(mvDevice);
    cvMountTable.put(sMountPoint, new Integer(mvDeviceNumber));
    // Set status to mounted.
    mvDevice.status = 1;
  }

  // Perfoms an allocation of the file. Creats an entry in the FID table
  // and inits it.
  public FileSystemReturnData allocate(int iRequestID, String sFileName)
  {
    String mvMountPoint = getMountPoint(sFileName);

    // Assume device is mounted as FSMan has passed in request.
    int mvDeviceNumber = ((Integer)cvMountTable.get(mvMountPoint)).intValue();

    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem(mvDeviceNumber);

    // Check if file is already open
    if (mvDevice.openFileNames.containsKey(sFileName))
    {
      return(new FileSystemReturnData(iRequestID, -1));
    }

    mvDevice.openFileNames.put(sFileName, new Boolean(true));
    // create the entry and init it.
    CPM14FIDTableEntry mvFIDEntry = new CPM14FIDTableEntry();
    mvFIDEntry.Device = mvDeviceNumber;
    mvFIDEntry.Filename = sFileName;
    mvFIDEntry.Mode = ALLOCATED;
    mvFIDEntry.Buffer = new byte[BLOCK_SIZE];
    int FID = cvFIDTable.add(mvFIDEntry);
    return(new FileSystemReturnData(iRequestID, FID));
  }

  // Replys to the sender of the message 1 if at end of file
  // 0 if not.
  public FileSystemReturnData eof(int iRequestID, int iFSFileNo)
  {
    CPM14FIDTableEntry mvFIDEntry =
        (CPM14FIDTableEntry)cvFIDTable.getItem(iFSFileNo);

    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry)cvDeviceTable.getItem( mvFIDEntry.Device);

    int mvReturnValue = NOT_EOF;
    if (mvFIDEntry.Mode == READING)
    {
      // Check if in the middle of a block
      if (((mvFIDEntry.CurrentPosition) % 1024) != 0)
      {
        // Easy, check the current character for 0x1A, EOF.
        if ((SVB2I & mvFIDEntry.Buffer[(mvFIDEntry.CurrentPosition % 1024)]) == 0x1A)
        {
          mvReturnValue = EOF;
        }
      }
      else
      {
        // The position is at the very end of a data block. Check if that block
        // is the last one in the context.
        int mvDiskBlockOffset = (mvFIDEntry.FileNumber * DIR_ENTRY_SIZE) +
                                               DATA_BLOCKS;

        if ( mvFIDEntry.CurrentDiskBlock !=
          (SVB2I&mvDevice.directoryTable[(mvFIDEntry.FileNumber * DIR_ENTRY_SIZE) +
                                    DATA_BLOCKS + 16 ]))
        {
          // We are not in the last one.
          int mvCurrentBlockPosition = (mvFIDEntry.CurrentPosition / 1024) % 16;

          // Check if the next block in the list == 0. If it is, this is the
          // end of the file.
          if ( (SVB2I & mvDevice.directoryTable[
                        mvDiskBlockOffset+mvCurrentBlockPosition+1])  == 0)
          {
            mvReturnValue = EOF;
          }
        }
        else
        {
          // If there isn't an entry for the file with a higher context,
          // this be the end.
          if (getNextDirectoryEntry(mvFIDEntry.FileNumber, mvFIDEntry.Device) == -1)
          {
            mvReturnValue = EOF;
          }
        }
      }
    }
    return(new FileSystemReturnData(iRequestID, mvReturnValue));
  }

  // Free's all disk structures associated with the specified
  // file. Leaves the file in the PID table though.
  public FileSystemReturnData delete(int iRequestID, int iFSFileNumber)
  {
    CPM14FIDTableEntry mvFIDEntry =
           (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNumber);

    // If file isn't at the allocated state
    if (mvFIDEntry.Mode != ALLOCATED)
    {
      return(new FileSystemReturnData(iRequestID, -1));
    }

    int mvDeviceNumber = mvFIDEntry.Device;
    int mvCurrent, mvNext;

    // eliminate all the dir entries associated with the file.
    mvCurrent = getDirectoryPosition(mvFIDEntry.Filename);
    if (mvCurrent == -1)
    {
      // Fine, no work to be done.
      return(new FileSystemReturnData(iRequestID, 0));
    }

    mvNext = getNextDirectoryEntry(mvCurrent, mvDeviceNumber);
    while (mvNext != -1)
    {
      deallocateEntry(mvDeviceNumber, mvCurrent);
      mvCurrent = mvNext;
      mvNext = getNextDirectoryEntry(mvCurrent, mvDeviceNumber);
    }
    deallocateEntry(mvCurrent, mvDeviceNumber);

    // Clear the data items in the FID table
    mvFIDEntry.Mode = ALLOCATED;
    mvFIDEntry.FileNumber = -1;
    mvFIDEntry.CurrentPosition = -1;
    mvFIDEntry.Buffer = null;
    mvFIDEntry.CurrentDiskBlock = -1;

    return(new FileSystemReturnData(iRequestID, 0));
  }

  // Sets up a directory entry for the file and sets it
  // to a 0 length file.
  public FileSystemReturnData create(int iRequestID, int iFSFileNumber)
  {
    CPM14FIDTableEntry mvFIDEntry =
               (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNumber);

    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem(mvFIDEntry.Device);

    // Check for spaces and necessary conditions
    if ((mvFIDEntry.Mode != ALLOCATED)  ||
        (diskFull(mvFIDEntry.Device)))
    {
      // Return an error
      return(new FileSystemReturnData(iRequestID, -1));
    }

    int mvDeviceNumber = mvFIDEntry.Device;
    // Allocate the dir entry
    int mvDirEntry = getFreeEntry(mvDeviceNumber);
    if (mvDirEntry == -1)
    {
      return(new FileSystemReturnData(iRequestID, -1));
    }

    // Setup the entry
    int mvOffset = mvDirEntry * DIR_ENTRY_SIZE;
    mvDevice.directoryTable[mvOffset + STATUS] = 0;

    byte[] mvByteFilename = convertFilename(mvFIDEntry.Filename);
    int mvCounter;
    for (mvCounter = 0; mvCounter < 11; mvCounter++)
    {
      mvDevice.directoryTable[mvOffset + FILENAME + mvCounter] =
                     mvByteFilename[mvCounter];
    }

    mvDevice.directoryTable[mvOffset + EXTENT] = 0;
    mvDevice.directoryTable[mvOffset + RESERVED] = 0;
    mvDevice.directoryTable[mvOffset + RESERVED + 1] = 0;
    mvDevice.directoryTable[mvOffset + RECORDS] = 0;

    for (mvCounter = 0; mvCounter < 16; mvCounter++)
    {
      mvDevice.directoryTable[mvOffset + DATA_BLOCKS + mvCounter] = 0;
    }

    // Setup the initial data.
    mvFIDEntry.FileNumber = mvDirEntry;
    mvFIDEntry.Mode = WRITING;
    mvFIDEntry.CurrentPosition = 0;

    return(new FileSystemReturnData(iRequestID, 0));
  }

  // Closes a file and removes it from the FID table. first
  // writes the current buffer and the Dir blocks to disk.
  public FileSystemReturnData close(int iRequestID, int iFSFileNumber)
  {
    CPM14FIDTableEntry mvFIDEntry =
              (CPM14FIDTableEntry)cvFIDTable.getItem(iFSFileNumber);

    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem(mvFIDEntry.Device);

    if (mvFIDEntry.Mode == WRITING)
    {
      // add EOF mark at the end of the file unless on a border line between the
      // next block. If no eof is encountered, and no following block is allocated,
      // Then the end of the file falls directly on the line.
      if (((mvFIDEntry.CurrentPosition) % BLOCK_SIZE) != 0)
      {
        //Currentposition will already point to the place to write the  EOF
        // character
        mvFIDEntry.Buffer[(mvFIDEntry.CurrentPosition%1024)] = 0x1A;
      }

      diskRequest(mvDevice.deviceName,"FS_CLOSE::WRITE_BUFFER", "WRITING",
        iFSFileNumber, iRequestID, -1,
        mvFIDEntry.CurrentDiskBlock, mvFIDEntry.Buffer);
    }
    else
    {
      if (mvDevice.openFileNames.containsKey(mvFIDEntry.Filename))
      {
        mvDevice.openFileNames.remove(mvFIDEntry.Filename);
      }
      cvFIDTable.remove(iFSFileNumber);
      // Clean up entries in FID table.
      return(new FileSystemReturnData(iRequestID, 0));
    }
		return null;
  }

  // Opens the specified file for reading.
  public FileSystemReturnData open(int iRequestID, int iFSFileNumber)
  {
    CPM14FIDTableEntry mvFIDEntry =
              (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNumber);
    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem(mvFIDEntry.Device);
    int mvDeviceNumber = mvFIDEntry.Device;

    if (mvFIDEntry.Mode == ALLOCATED)
    {
      int mvTheFile;
      if ((mvTheFile = getNextDirectoryEntry(1,1)) != -1)
      {
        // init for first read.
        mvFIDEntry.Mode = READING;
        mvFIDEntry.FileNumber = mvTheFile;
        mvFIDEntry.CurrentPosition = 0;
        return(new FileSystemReturnData(iRequestID, 0));
      }
      else
      {
        return(new FileSystemReturnData(iRequestID, -1));
      }
    }
    else
    {
      return(new FileSystemReturnData(iRequestID, -1));
    }
  }

  // Reads from the file if it is in the right mode.
  public FileSystemReturnData read(int iRequestID, int iFSFileNumber)
  {
    // Setup the data
    CPM14FIDTableEntry mvFIDEntry =
              (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNumber);
    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem (mvFIDEntry.Device);
    int mvDeviceNumber = mvFIDEntry.Device;
    int mvTheEntry;

    // Check the current mode.
    if (mvFIDEntry.Mode == READING)
    {
      // The file is being read.
      if (((mvFIDEntry.CurrentPosition) % BLOCK_SIZE) == 0)
      {
        // check for end of dirent. Use disk blocks.
        if (mvFIDEntry.CurrentDiskBlock == mvDevice.directoryTable
						[(mvFIDEntry.FileNumber*DIR_ENTRY_SIZE) + DATA_BLOCKS + 15])
        {
          //this.dumpToScreen("DIR",0,mvFIDEntry.FileNumber);
          int mvNewEntry = getNextDirectoryEntry(mvFIDEntry.FileNumber,mvDeviceNumber);
          if ( mvNewEntry == -1)
          {
            return(new FileSystemReturnData(iRequestID, -1));
          }
          mvFIDEntry.FileNumber = mvNewEntry;
          mvFIDEntry.CurrentDiskBlock = SVB2I&mvDevice.directoryTable
                      [ (mvNewEntry * DIR_ENTRY_SIZE) + DATA_BLOCKS];
        }
        else
        {
          int mvNewBlock;
          mvNewBlock = SVB2I&mvDevice.directoryTable[
               (mvFIDEntry.FileNumber * DIR_ENTRY_SIZE) + DATA_BLOCKS +
               (mvFIDEntry.CurrentPosition/BLOCK_SIZE) % 16 ];
          if (mvNewBlock == 0) // 0 is used as it is a directory block
          {                    // using it here denotes the end of the data.
            return(new FileSystemReturnData(iRequestID, -1));
          }
          else
          {
            mvFIDEntry.CurrentDiskBlock = (byte)mvNewBlock;
          }
        }

        diskRequest(mvDevice.deviceName, "FS_READ::GETBLOCK", "GETBLOCK",
          iFSFileNumber, iRequestID, -1, mvFIDEntry.CurrentDiskBlock, null);
      }
      else
      {
        // No need to swap buffer. Just returnt he next character.
        int mvDataItem = SVB2I&mvFIDEntry.Buffer
                           [mvFIDEntry.CurrentPosition % BLOCK_SIZE];
        mvFIDEntry.CurrentPosition ++;
        if ( mvDataItem == 0x1A) // EOF
        {
          mvDataItem = -1;
          mvFIDEntry.CurrentPosition--;
        }

        return(new FileSystemReturnData(iRequestID, mvDataItem));
      }
    }
    else
    {
      return(new FileSystemReturnData(iRequestID, -1));
    }
    return null;
  }

  // If the mode is correct, this starts or contines writing a
  // file.
  public FileSystemReturnData write(int iRequestID, int iFSFileNo)
  {
    CPM14FIDTableEntry mvFIDEntry =
            (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNo);

    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem(mvFIDEntry.Device);

    if (mvFIDEntry.Mode == WRITING )
    {
      // Do checking for a new context

      if ( (((mvFIDEntry.CurrentPosition) % BLOCK_SIZE ) == 0)
           && ( mvFIDEntry.CurrentPosition != 0))
      {

/*        diskRequest ( mvDevicedeviceName, "FS_WRITE::FLUSH", "WRITE",
                      iFSFileNo, iRequestID,
                      mvRequestData.getData(), mvFIDEntry.CurrentDiskBlock,
                      mvFIDEntry.Buffer);
*/
      }
      else
      {
        if ( mvFIDEntry.CurrentPosition == 0)
        {
          // Setup the buffer.
          int mvNextBlock = getFreeBlock ( mvFIDEntry.Device );
          mvFIDEntry.CurrentDiskBlock = (byte)mvNextBlock;
          mvDevice.directoryTable[ (mvFIDEntry.FileNumber * DIR_ENTRY_SIZE)
                                + DATA_BLOCKS ] = (byte)mvNextBlock;
        }


        //mvFIDEntry.Buffer[ mvFIDEntry.CurrentPosition % BLOCK_SIZE] =
         //                (byte)mvRequestData.getData();

        if ( ((mvFIDEntry.CurrentPosition + 1)% 128) == 0)
        {
          mvDevice.directoryTable[ (mvFIDEntry.FileNumber * DIR_ENTRY_SIZE)
                      + RECORDS] =
                  (byte)(((mvFIDEntry.CurrentPosition + 1)% (16*BLOCK_SIZE)) / 128);
        }
        mvFIDEntry.CurrentPosition++;
				return(new FileSystemReturnData(iRequestID, 0));
      }
    }
    else
    {
      return(new FileSystemReturnData(iRequestID, -1));
    }
    return null;
  }

  // Handle the DiskRequestComplete messages and perform the
  // necessary cary on functions.
  private void handleReturnMessages(OSMessageAdapter mvTheMessage)
  {
    DiskRequest mvMessageData = (DiskRequest) mvTheMessage.getBody();

    int mvRequestID = mvMessageData.getRequestId();

    CPM14RequestTableEntry mvRequestData =
      (CPM14RequestTableEntry) cvRequestTable.getItem( mvRequestID );

    int mvFID = mvRequestData.FSFileNum;

    CPM14FIDTableEntry mvFIDEntry =
      (CPM14FIDTableEntry)cvFIDTable.getItem( mvFID );

    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem (mvFIDEntry.Device);

    if ( mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_BUFFER"))
    {
 //     System.out.println("Handle Write byffer return."); // DEBUG
      mvRequestData.Type = "FS_CLOSE::WRITE_DIR1";
      byte[] mvToWrite = new byte[1024];

      int mvCounter;
      for ( mvCounter = 0; mvCounter < 1024; mvCounter++)
      {
        mvToWrite[mvCounter] = mvDevice.directoryTable[mvCounter];
      }

      DiskRequest mvNewReq = new DiskRequest(mvRequestID, 0, mvToWrite);

      //Message mvNewMessage = new Message ( id, mvDevicedeviceName,
      //                                      "DISKREQUEST", mvNewReq);
      //SendMessage( mvNewMessage );
    }
    else if ( mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_DIR1"))
    {
//      System.out.println("Handle Write dir1 return."); // DEBUG
      mvRequestData.Type = "FS_CLOSE::WRITE_DIR2";
      byte[] mvToWrite = new byte[1024];

      int mvCounter;
      for ( mvCounter = 0; mvCounter < 1024; mvCounter++)
      {
        mvToWrite[mvCounter] = mvDevice.directoryTable[mvCounter+1024];
      }


      DiskRequest mvNewReq = new DiskRequest(mvRequestID, 1, mvToWrite);

      //Message mvNewMessage = new Message ( id, mvDevicedeviceName,
      //                                      "DISKREQUEST", mvNewReq);
      //SendMessage( mvNewMessage );
    }
    else if ( mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_DIR2"))
    {
//      System.out.println("Handle Write dir2 return."); // DEBUG
//      System.out.println("About to remove FID "+mvFID); // DEBUG
      //return(new FileSystemReturnData(iRequestID, 0));
      if ( mvDevice.openFileNames.containsKey( mvFIDEntry.Filename ))
      {
        mvDevice.openFileNames.remove(mvFIDEntry.Filename);
      }
      cvFIDTable.remove(mvFID);
      cvRequestTable.remove(mvRequestID);
    }
    else if ( mvRequestData.Type.equalsIgnoreCase("FS_READ::GETBLOCK"))
    {
      if ( mvMessageData.getDiskBlock() >= 0)
      {
        int mvCounter;
        for ( mvCounter = 0; mvCounter < BLOCK_SIZE; mvCounter++)
        {
          mvFIDEntry.Buffer[mvCounter] = mvMessageData.getData()[mvCounter];
        }

        mvFIDEntry.CurrentDiskBlock = (byte) mvMessageData.getDiskBlock();
        int mvReturnItem = SVB2I&mvFIDEntry.Buffer[
                    mvFIDEntry.CurrentPosition % BLOCK_SIZE];
        if (mvReturnItem == 0x1A)    // EOF
        {
		      //return(new FileSystemReturnData(iRequestID, -1));
        }
        else
        {
          mvFIDEntry.CurrentPosition++;
		      //return(new FileSystemReturnData(iRequestID, mvReturnItem));
        }
        cvRequestTable.remove(mvRequestID);
      }
      else
      {
        cvFIDTable.remove(mvFID);
        cvRequestTable.remove(mvRequestID);
	      //return(new FileSystemReturnData(iRequestID, -1));
      }
    }
    else if ( mvRequestData.Type.equalsIgnoreCase("FS_WRITE::FLUSH"))
    {
      if ( mvMessageData.getDiskBlock() >= 0)
      {
        System.out.println("Interupt reveived.bout to write"); // DEBUG
        int mvLastBlock = SVB2I&mvDevice.directoryTable
               [(mvFIDEntry.FileNumber*DIR_ENTRY_SIZE) + DATA_BLOCKS + 15];
        //System.out.print("Cmp: "+mvLastBlock+" - "); // DEBUG
        //System.out.println("Entry :"+mvFIDEntry.FileNumber); // DEBUG
        //System.out.println("entrysize : "+DIR_ENTRY_SIZE); // DEBUG
        //System.out.println("datablocks :"+ DATA_BLOCKS); // DEBUG
        //System.out.println
        //     ((mvFIDEntry.FileNumber*DIR_ENTRY_SIZE) + DATA_BLOCKS + 16); // DEBUG


        //System.out.println(mvFIDEntry.CurrentDiskBlock); // DEBUG
        if ( mvFIDEntry.CurrentDiskBlock == mvLastBlock )
        {
          System.out.println("Setting up new DirEnt."); // DEBUG
          int mvNewEntry = getFreeEntry(mvFIDEntry.Device);
          if ( mvNewEntry == -1)
          {
            cvRequestTable.remove(mvRequestID);
			      //return(new FileSystemReturnData(iRequestID, -1));
          }
          int mvNewOffset = mvNewEntry * DIR_ENTRY_SIZE;
          int mvCurOffset = mvFIDEntry.FileNumber * DIR_ENTRY_SIZE;
          int X;
          for (X=1; X<=11; X++)
          {
            mvDevice.directoryTable[ mvNewOffset + X] =
                 mvDevice.directoryTable[ mvCurOffset + X];
          }
          mvDevice.directoryTable[ mvNewOffset + 0] = 0;
          mvDevice.directoryTable[ mvNewOffset + EXTENT] =
                (byte)(mvDevice.directoryTable[ mvCurOffset + EXTENT] + (byte)1);
          mvDevice.directoryTable[mvCurOffset + RECORDS] = (byte)0x80;

          mvFIDEntry.FileNumber = mvNewEntry;
          System.out.println("Setup dirent :"+mvNewEntry); // DEBUG
        }
        int mvNewBlock = getFreeBlock(mvFIDEntry.Device);
        if ( mvNewBlock >= 0 )
        {
          System.out.println("New disk block :"+mvNewBlock); //DEBUG
          int mvOffset = (mvFIDEntry.FileNumber*DIR_ENTRY_SIZE) + DATA_BLOCKS;
          int mvBlockLocation = mvOffset +
                 ( (mvFIDEntry.CurrentPosition / BLOCK_SIZE) % 16 );
          mvDevice.directoryTable[ mvBlockLocation] = (byte) mvNewBlock;
          mvFIDEntry.CurrentDiskBlock = (byte)mvNewBlock;
          mvFIDEntry.Buffer[ mvFIDEntry.CurrentPosition % BLOCK_SIZE] =
                         (byte)mvRequestData.Data;
          mvFIDEntry.CurrentPosition++;
					//return(new FileSystemReturnData(iRequestID, 0));
//          Dump( "DIR", 0,0); // DEBUG
        }
        else
        {
		      //return(new FileSystemReturnData(iRequestID, -1));
        }

        cvRequestTable.remove(mvRequestID);

      }
      else
      {
        cvRequestTable.remove(mvRequestID);
 	      //return(new FileSystemReturnData(iRequestID, -1));
      }
    }

    //else if ( mvRequestData.Type.equalsIgnoreCase(""))
//    System.out.println("FS:HandleReturnMessage - end"); // DEBUG

		//return null;
  }

  // Converts a string filename "mount:name.ext" to a byte array
  // that is FFFFFFFFEEE where F is the filename and E is the extention.
  // spaces are padded out.
  private byte[] convertFilename( String mvFilename )
  {
    byte[] mvReturnData = new byte[11];
    StringTokenizer stTokenizer = new StringTokenizer(mvFilename, ":.");

    if (stTokenizer.countTokens() == 3)
    {
      stTokenizer.nextToken();
      mvReturnData = stTokenizer.nextToken().toUpperCase().getBytes();
    }
    return mvReturnData;
  }

  // Gets the number of the first entry for the file. Note, the filename
  // should include the mountpoint as well.
  public int getDirectoryPosition(String mvFilename)
  {
    String mvMountPoint;
    Integer mvTmpID;
    int mvDeviceNumber;

    // Determin the device and get a pointer to it's data.
    mvMountPoint = getMountPoint (mvFilename);
    mvTmpID = (Integer)cvMountTable.get(mvMountPoint);
    if ( mvTmpID == null )
    {
      return -1;
    }

    mvDeviceNumber = mvTmpID.intValue();
    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem(mvDeviceNumber);

    // Convert the filename to a byte[] for the search.
    byte[] mvByteFilename = convertFilename(mvFilename);
    // Search
    int mvCounter = 0;
    int mvIndex = 0;
    int mvOffset;

    boolean mvFound = false;
    while ( (mvCounter < TOTAL_DIR_ENTRIES) && ( ! mvFound ))
    {
      mvIndex = 0;
      mvOffset = (mvCounter * DIR_ENTRY_SIZE) + FILENAME;
      while((mvIndex < 11) &&
        (mvByteFilename[mvIndex] == mvDevice.directoryTable[mvOffset + mvIndex]))
      {
        mvIndex++;
      }
      mvFound = (mvIndex == 11);
      if (mvFound)
      {
        mvFound = ( mvDevice.directoryTable[ mvOffset - FILENAME + EXTENT]
                       == 0);
      }
      mvCounter++;
    }


    // Check sucess and return a value.
    if ( mvFound )
    {
      mvCounter--; // Counter is incremented in the loop to save cycles with
                   // an if or an else.
      return mvCounter;
    }
    else
    {
      return -1;
    }

  }

  // Checks to see if the File System indicated by MountPoint is full.
  public boolean diskFull(int mvDeviceNumber)
  {
    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem(mvDeviceNumber);
    return ((mvDevice.numberOfFreeBlocks == 0)
        ||  (mvDevice.numberOfFreeEntries == 0));
  }

  // Coordinating this in the one synchronised function means that the
  // allocation of the resources will be safe.
  public int resourceAllocator( String type, int mvDeviceNumber, int Item)
  {
     CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem(mvDeviceNumber);


    if ( type.equalsIgnoreCase("DIR"))
    {
      int mvCounter;
      for ( mvCounter = 0;
            (( mvCounter < TOTAL_DIR_ENTRIES) &&
             ( mvDevice.dirEntList[mvCounter]));
            mvCounter++);
      if ( mvCounter == TOTAL_DIR_ENTRIES)
      {
        return -1;
      }
      else
      {
        mvDevice.dirEntList[mvCounter] = true;
        mvDevice.numberOfFreeEntries--;
        return mvCounter;
      }
    }
    else if ( type.equalsIgnoreCase("BLOCK"))
    {
      int mvCounter;
      for ( mvCounter = 0;
            (( mvCounter < TOTAL_DISK_BLOCKS) &&
             ( mvDevice.blockList[mvCounter]));
            mvCounter++);
      if ( mvCounter == TOTAL_DISK_BLOCKS)
      {
        return -1;
      }
      else
      {
        mvDevice.blockList[mvCounter] = true;
        mvDevice.numberOfFreeBlocks--;
        return mvCounter + DISK_BLOCK_OFFSET;
      }

    }
    else if ( type.equalsIgnoreCase("CLEARDIR"))
    {

      int mvEntryOffset = Item * DIR_ENTRY_SIZE;
      int mvCounter = 0;
      int mvBlockNum;

      mvBlockNum = SVB2I&mvDevice.directoryTable
                               [ mvEntryOffset + DATA_BLOCKS + mvCounter ];

      while ((mvCounter < 16) && (mvBlockNum > 0 ))
      {
        System.out.println("Freeing block :"+mvBlockNum);
        mvDevice.blockList[mvBlockNum - DISK_BLOCK_OFFSET] = false;
        mvDevice.directoryTable[ mvEntryOffset + DATA_BLOCKS + mvCounter ] = 0;

        mvDevice.numberOfFreeBlocks++;
        mvCounter++;
        mvBlockNum = SVB2I&mvDevice.directoryTable
                             [ mvEntryOffset + DATA_BLOCKS + mvCounter ];
      }
      mvDevice.directoryTable[ mvEntryOffset + STATUS ] = (byte)0xE5;
      mvDevice.dirEntList[ Item ] = false;
      mvDevice.numberOfFreeEntries++;
      return 1;
    }
    else if ( type.equalsIgnoreCase("CLEARBLOCK"))
    {

      mvDevice.blockList[Item] = false;
      mvDevice.numberOfFreeBlocks++;

      return 1;
    }
    else
    {
      return -1;
    }
  }

  // Return a Free Directory entry for the specified device.
  public int getFreeEntry(int mvDeviceNumber)
  {
    return resourceAllocator( "DIR", mvDeviceNumber, -1 );
  }

  // Return a free blcok number on the specified device.
  public int getFreeBlock ( int mvDeviceNumber )
  {
    return resourceAllocator( "BLOCK", mvDeviceNumber, -1 );
  }

  // Returns the next directory entry for the dirent on the specified device
  public int getNextDirectoryEntry(int mvDirent, int mvDeviceNumber)
  {
    CPM14DeviceTableEntry mvDevice =
        (CPM14DeviceTableEntry) cvDeviceTable.getItem (mvDeviceNumber);

    int mvCurrentOffset = mvDirent * DIR_ENTRY_SIZE;

    // First check if this entry is totally used. If so, look for next,
    // otherwise exit.
    if ( (SVB2I&mvDevice.directoryTable[mvCurrentOffset+RECORDS]) != 0x80)
    {
      return -1;
    }

    // Get the filename to a byte[] for the search.
    byte[] mvByteFilename = new byte[11];

    byte mvCurrentExtent = mvDevice.directoryTable
                             [mvCurrentOffset + EXTENT];

    int mvCounter;
    for ( mvCounter = 0; mvCounter < 11; mvCounter++)
    {
      mvByteFilename[mvCounter] = mvDevice.directoryTable
                      [ mvCurrentOffset + FILENAME + mvCounter];
    }

    // Search
    mvCounter = 0;
    int mvIndex = 0;
    int mvOffset;

    boolean mvFound = false;
    while ( (mvCounter < TOTAL_DIR_ENTRIES) && ( ! mvFound ))
    {
      mvIndex = 0;
      mvOffset = (mvCounter * DIR_ENTRY_SIZE) + FILENAME;
      while ( (mvIndex < 11) &&
              (mvByteFilename[mvIndex] == mvDevice.directoryTable[
                                                     mvOffset + mvIndex]))
      {
        mvIndex++;
      }
      mvFound = (mvIndex == 11);
      if ( mvFound )
      {
        mvFound = ( mvDevice.directoryTable[ mvOffset - FILENAME + EXTENT]
                       == mvCurrentExtent + 1);
      }
      mvCounter++;
    }


    // Check sucess and return a value.
    if ( mvFound )
    {
      mvCounter--; // Counter is incremented in the loop to save cycles with
                   // an if or an else.
      return mvCounter;
    }
    else
    {
      return -1;
    }

  }

  // Deallocates the disk blocks for the specified device and sets the block to
  // deleted.
  public void deallocateEntry(int mvEntryNumber, int mvDeviceNumber)
  {
    resourceAllocator("CLEARDIR", mvDeviceNumber, mvEntryNumber);
  }

  // Returns the mountpoint of the specified string.
  public String getMountPoint( String mvFilename )
  {
    int mvIndex;
    String mvMountPoint;
    mvIndex = mvFilename.indexOf (MOUNT_POINT_SEPERATOR);
    if ( mvIndex == -1 )
    {
      return null;
    }
    mvMountPoint = mvFilename.substring (0, mvIndex);
    return mvMountPoint;
  }

  // Insert a queue Item and send a request to the disk.
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
    int iReqNum = cvRequestTable.add(mvQueueEntry);
    DiskRequest mvTheRequest = new DiskRequest(iReqNum,
                                               block,
                                               data);
    //Message mvTheReq = new Message( id,  new String(to),"DISKREQUEST", mvTheRequest);
    //SendMessage ( mvTheReq );
  }

  // Returns a ReturnValie message to the Destination
  // basically saying it's all over.
  private void returnValue(int mvFSMReqNo, int mvReturnData)
  {
    FileSystemReturnData mvToReturn = new FileSystemReturnData(mvFSMReqNo,
                                               mvReturnData);

    /*Message mvReturnMessage = new Message(id, mvDestination,  "RETURNVALUE",
                                           mvToReturn);
    SendMessage ( mvReturnMessage );*/
  }

  // Will display the specified data to the screen.
  // Operation can be "DIR" or "BUFFER". For DIR, item1 will indicate
  // the entry to dump, item2 will indicate the device. For Buffer,
  // item1 is the FID and item2 is not used.
  public void dumpToScreen(String Operation, int item1, int item2)
  {
    if (Operation.equalsIgnoreCase("DIR"))
    {
      CPM14DeviceTableEntry mvDevice=
         (CPM14DeviceTableEntry)cvDeviceTable.getItem(item2);

      int X;
      int Offset = item1 * 32;

      for ( X = 0; X<32; X++)
      {
        if ( X >0 && X < 12)
        {
          System.out.print( (char)mvDevice.directoryTable[ Offset + X ]
                                +" ");
        }
        else
        {
          System.out.print ((SVB2I&mvDevice.directoryTable[ Offset + X])+" ");
        }
        if ( X == 15 )
        {
          System.out.println("");
        }
      }
    }
    else if (Operation.equalsIgnoreCase("BUFFER"))
    {
      CPM14FIDTableEntry mvFIDEntry =
        (CPM14FIDTableEntry) cvFIDTable.getItem(item1);
      int X;
      for ( X = 0; X < 1024; X++)
      {
        if ( (SVB2I&mvFIDEntry.Buffer[X]) == 0x1A)
        {
          System.out.print("<EOF>");
        }
        else
        {
          System.out.print( (char) mvFIDEntry.Buffer[X] );
        }
      }
    }
    System.out.println("");
  }
}
