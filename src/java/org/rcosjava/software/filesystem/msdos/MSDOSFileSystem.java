package org.rcosjava.software.filesystem.msdos;

import java.util.*;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.postoffices.MessageHandler;
import org.rcosjava.software.disk.DiskRequest;
import org.rcosjava.software.util.IndexedList;

import java.lang.Math;
import java.io.*;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSFATException;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSDirectoryException;
import org.rcosjava.software.filesystem.msdos.util.InterfaceMSDOS;
import org.rcosjava.software.filesystem.FileSystemInterface;
import org.rcosjava.software.filesystem.FileSystemData;


public class MSDOSFileSystem implements FileSystemInterface{

  // Constants
  private final static String MOUNT_POINT_SEPERATOR = ":";

    // The size of the block 4K
//  private final static int BLOCK_SIZE = 4096;

  // The total of blocks of the system
  private final static int TOTAL_DISK_BLOCKS = 240;

  // The total de Directiries of the blocks
  private final static int TOTAL_DIR_BLOCKS = 2;

  //
  private final static int DIR_BLOCK_OFFSET = 0;
  private final static int DISK_BLOCK_OFFSET = DIR_BLOCK_OFFSET + TOTAL_DIR_BLOCKS;
  private final static int DIR_ENTRY_SIZE = 32;
  private final static int TOTAL_DIR_ENTRIES = (InterfaceMSDOS.BLOCK_SIZE * TOTAL_DIR_BLOCKS)
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

  private MSDOSFAT msdosFat;
  private Directory directory;
  private Disk disk;

  /**
   * Constructor for the MSDOSFileSystem object
   *
   * @param myID Identify of the MSDOSFileSystem
   * @param myPO Description of Parameter
   */
  public MSDOSFileSystem(String myID, MessageHandler myPO){
    //super(myID, myPO);
    cvRequestTable = new IndexedList(100, 10);
    cvMountTable = new HashMap();
    cvDeviceTable = new IndexedList(100, 10);
    cvFIDTable = new IndexedList(100, 10);

    msdosFat = new MSDOSFAT();
    directory = new Directory();
    disk = new Disk();
    requestSystemFile();
  }

  /**
   * Constructor for the MSDOSFileSystem object
   *
   * @param myID Identify of the MSDOSFileSystem
   * @param myPO Description of Parameter
   */
  public MSDOSFileSystem(MSDOSFAT newMsdosFat, Directory newDirectory, Disk newDisk){
    msdosFat = newMsdosFat;
    directory = newDirectory;
    disk = newDisk;
    requestSystemFile();
  }

  // Gets the number of the first entry for the file. Note, the filename
  // should include the mountpoint as well.
  /**
   * Gets the DirectoryPosition attribute of the CPM14FileSystem object
   *
   * @param mvFilename Description of Parameter
   * @return The DirectoryPosition value
   */
  public int getDirectoryPosition(String mvFilename){
//    String mvMountPoint;
//    Integer mvTmpID;
//    int mvDeviceNumber;
//
//    // Determin the device and get a pointer to it's data.
//    mvMountPoint = getMountPoint(mvFilename);
//    mvTmpID = (Integer) cvMountTable.get(mvMountPoint);
//    if (mvTmpID == null)
//    {
//      return -1;
//    }
//
//    mvDeviceNumber = mvTmpID.intValue();
//
//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvDeviceNumber);
//
//    // Convert the filename to a byte[] for the search.
//    byte[] mvByteFilename = convertFilename(mvFilename);
//    // Search
//    int mvCounter = 0;
//    int mvIndex = 0;
//    int mvOffset;
//
//    boolean mvFound = false;
//
//    while ((mvCounter < TOTAL_DIR_ENTRIES) && (!mvFound))
//    {
//      mvIndex = 0;
//      mvOffset = (mvCounter * DIR_ENTRY_SIZE) + FILENAME;
//      while ((mvIndex < 11) &&
//          (mvByteFilename[mvIndex] == mvDevice.directoryTable[mvOffset + mvIndex]))
//      {
//        mvIndex++;
//      }
//      mvFound = (mvIndex == 11);
//      if (mvFound)
//      {
//        mvFound = (mvDevice.directoryTable[mvOffset - FILENAME + EXTENT]
//             == 0);
//      }
//      mvCounter++;
//    }
//
//    // Check sucess and return a value.
//    if (mvFound)
//    {
//      mvCounter--;
//      // Counter is incremented in the loop to save cycles with
//      // an if or an else.
//      return mvCounter;
//    }
//    else
//    {
//      return -1;
//    }
     return -1;
  }

  // Return a Free Directory entry for the specified device.
  /**
   * Gets the FreeEntry attribute of the CPM14FileSystem object
   *
   * @param mvDeviceNumber Description of Parameter
   * @return The FreeEntry value
   */
  public int getFreeEntry(int mvDeviceNumber)
  {
    return resourceAllocator("DIR", mvDeviceNumber, -1);
  }

  // Return a free blcok number on the specified device.
  /**
   * Gets the FreeBlock attribute of the CPM14FileSystem object
   *
   * @param mvDeviceNumber Description of Parameter
   * @return The FreeBlock value
   */
  public int getFreeBlock(int mvDeviceNumber){

    return resourceAllocator("BLOCK", mvDeviceNumber, -1);
  }

  // Returns the next directory entry for the dirent on the specified device
  /**
   * Gets the NextDirectoryEntry attribute of the CPM14FileSystem object
   *
   * @param mvDirent Description of Parameter
   * @param mvDeviceNumber Description of Parameter
   * @return The NextDirectoryEntry value
   */
  public int getNextDirectoryEntry(int mvDirent, int mvDeviceNumber){

//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvDeviceNumber);
//
//    int mvCurrentOffset = mvDirent * DIR_ENTRY_SIZE;
//
//    // First check if this entry is totally used. If so, look for next,
//    // otherwise exit.
//    if ((SVB2I & mvDevice.directoryTable[mvCurrentOffset + RECORDS]) != 0x80)
//    {
//      return -1;
//    }
//
//    // Get the filename to a byte[] for the search.
//    byte[] mvByteFilename = new byte[11];
//
//    byte mvCurrentExtent = mvDevice.directoryTable
//        [mvCurrentOffset + EXTENT];
//
//    int mvCounter;
//
//    for (mvCounter = 0; mvCounter < 11; mvCounter++)
//    {
//      mvByteFilename[mvCounter] = mvDevice.directoryTable
//          [mvCurrentOffset + FILENAME + mvCounter];
//    }
//
//    // Search
//    mvCounter = 0;
//
//    int mvIndex = 0;
//    int mvOffset;
//
//    boolean mvFound = false;
//
//    while ((mvCounter < TOTAL_DIR_ENTRIES) && (!mvFound))
//    {
//      mvIndex = 0;
//      mvOffset = (mvCounter * DIR_ENTRY_SIZE) + FILENAME;
//      while ((mvIndex < 11) &&
//          (mvByteFilename[mvIndex] == mvDevice.directoryTable[
//          mvOffset + mvIndex]))
//      {
//        mvIndex++;
//      }
//      mvFound = (mvIndex == 11);
//      if (mvFound)
//      {
//        mvFound = (mvDevice.directoryTable[mvOffset - FILENAME + EXTENT]
//             == mvCurrentExtent + 1);
//      }
//      mvCounter++;
//    }
//
//    // Check sucess and return a value.
//    if (mvFound)
//    {
//      mvCounter--;
//      // Counter is incremented in the loop to save cycles with
//      // an if or an else.
//      return mvCounter;
//    }
//    else
//    {
//      return -1;
//    }

    return -1;

  }

  // Returns the mountpoint of the specified string.
  /**
   * Gets the MountPoint attribute of the CPM14FileSystem object
   *
   * @param mvFilename Description of Parameter
   * @return The MountPoint value
   */
  public String getMountPoint(String mvFilename){

//    int mvIndex;
//    String mvMountPoint;
//
//    mvIndex = mvFilename.indexOf(MOUNT_POINT_SEPERATOR);
//    if (mvIndex == -1)
//    {
//      return null;
//    }
//    mvMountPoint = mvFilename.substring(0, mvIndex);
//    return mvMountPoint;
     return "";
  }

  //Handle a mount request
  /**
   * Description of the Method
   *
   * @param sMountPoint Description of Parameter
   * @param sDeviceName Description of Parameter
   */
  public void mount(String sMountPoint, String sDeviceName){

//    MSDOSFATEntry mvDevice = new MSDOSFATEntry();
//
//    // Note, in the simulation, the disks are initialized each
//    // time the program is run. For a disk structure that
//    // remained between runs, the mount would be very different.
//    mvDevice.deviceName = sDeviceName;
//    mvDevice.directoryTable = new byte[BLOCK_SIZE * TOTAL_DIR_BLOCKS];
//    mvDevice.openFileNames = new HashMap();
//    mvDevice.status = 0;
//    mvDevice.blockList = new boolean[TOTAL_DISK_BLOCKS];
//    mvDevice.numberOfFreeBlocks = TOTAL_DISK_BLOCKS;
//    mvDevice.dirEntList = new boolean[TOTAL_DIR_ENTRIES];
//    mvDevice.numberOfFreeEntries = TOTAL_DIR_ENTRIES;
//    // This section would start the read operations to retrieve the
//    // directory blocks from the disk.  As it is, it simply sets the
//    // status on all files to deleted so the system knows it can write
//    // to them.
//    for (int mvCounter = 0; mvCounter < TOTAL_DIR_ENTRIES; mvCounter++)
//    {
//      mvDevice.directoryTable[(DIR_ENTRY_SIZE * mvCounter) + STATUS] =
//          (byte) 0xE5;
//      mvDevice.dirEntList[mvCounter] = false;
//    }
//
//    // Setup the free dir entry and disk block arrays.
//    for (int mvCounter = 0; mvCounter < TOTAL_DISK_BLOCKS; mvCounter++)
//    {
//      mvDevice.blockList[mvCounter] = false;
//    }
//
//    // Add device to the device table and the Mount table.
//    int mvDeviceNumber = cvDeviceTable.add(mvDevice);
//
//    cvMountTable.put(sMountPoint, new Integer(mvDeviceNumber));
//    // Set status to mounted.
//    mvDevice.status = 1;
  }

  // Perfoms an allocation of the file. Creats an entry in the FID table
  // and inits it.
  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param sFileName Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemData allocate(int iRequestID, String sFileName){

//    String mvMountPoint = getMountPoint(sFileName);
//
//    // Assume device is mounted as FSMan has passed in request.
//    int mvDeviceNumber = ((Integer) cvMountTable.get(mvMountPoint)).intValue();
//
//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvDeviceNumber);
//
//    // Check if file is already open
//    if (mvDevice.openFileNames.containsKey(sFileName))
//    {
//      return (new FileSystemData(iRequestID, -1));
//    }
//
//    mvDevice.openFileNames.put(sFileName, new Boolean(true));
//
//    // create the entry and init it.
//    CPM14FIDTableEntry mvFIDEntry = new CPM14FIDTableEntry();
//
//    mvFIDEntry.Device = mvDeviceNumber;
//    mvFIDEntry.Filename = sFileName;
//    mvFIDEntry.Mode = ALLOCATED;
//    mvFIDEntry.Buffer = new byte[BLOCK_SIZE];
//
//    int FID = cvFIDTable.add(mvFIDEntry);
//
//    return (new FileSystemData(iRequestID, FID));
    return null;
  }

  // Replys to the sender of the message 1 if at end of file
  // 0 if not.
  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemData eof(int iRequestID, int iFSFileNo){

//    CPM14FIDTableEntry mvFIDEntry =
//        (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNo);
//
//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvFIDEntry.Device);
//
//    int mvReturnValue = NOT_EOF;
//
//    if (mvFIDEntry.Mode == READING)
//    {
//      // Check if in the middle of a block
//      if (((mvFIDEntry.CurrentPosition) % 1024) != 0)
//      {
//        // Easy, check the current character for 0x1A, EOF.
//        if ((SVB2I & mvFIDEntry.Buffer[(mvFIDEntry.CurrentPosition % 1024)]) == 0x1A)
//        {
//          mvReturnValue = EOF;
//        }
//      }
//      else
//      {
//        // The position is at the very end of a data block. Check if that block
//        // is the last one in the context.
//        int mvDiskBlockOffset = (mvFIDEntry.FileNumber * DIR_ENTRY_SIZE) +
//            DATA_BLOCKS;
//
//        if (mvFIDEntry.CurrentDiskBlock !=
//            (SVB2I & mvDevice.directoryTable[(mvFIDEntry.FileNumber * DIR_ENTRY_SIZE) +
//            DATA_BLOCKS + 16]))
//        {
//          // We are not in the last one.
//          int mvCurrentBlockPosition = (mvFIDEntry.CurrentPosition / 1024) % 16;
//
//          // Check if the next block in the list == 0. If it is, this is the
//          // end of the file.
//          if ((SVB2I & mvDevice.directoryTable[
//              mvDiskBlockOffset + mvCurrentBlockPosition + 1]) == 0)
//          {
//            mvReturnValue = EOF;
//          }
//        }
//        else
//        {
//          // If there isn't an entry for the file with a higher context,
//          // this be the end.
//          if (getNextDirectoryEntry(mvFIDEntry.FileNumber, mvFIDEntry.Device) == -1)
//          {
//            mvReturnValue = EOF;
//          }
//        }
//      }
//    }
//    return (new FileSystemData(iRequestID, mvReturnValue));
     return null;
  }

  // Free's all disk structures associated with the specified
  // file. Leaves the file in the PID table though.
  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemData delete(int iRequestID, int iFSFileNumber){

//    CPM14FIDTableEntry mvFIDEntry =
//        (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNumber);
//
//    // If file isn't at the allocated state
//    if (mvFIDEntry.Mode != ALLOCATED)
//    {
//      return (new FileSystemData(iRequestID, -1));
//    }
//
//    int mvDeviceNumber = mvFIDEntry.Device;
//    int mvCurrent;
//    int mvNext;
//
//    // eliminate all the dir entries associated with the file.
//    mvCurrent = getDirectoryPosition(mvFIDEntry.Filename);
//    if (mvCurrent == -1)
//    {
//      // Fine, no work to be done.
//      return (new FileSystemData(iRequestID, 0));
//    }
//
//    mvNext = getNextDirectoryEntry(mvCurrent, mvDeviceNumber);
//    while (mvNext != -1)
//    {
//      deallocateEntry(mvDeviceNumber, mvCurrent);
//      mvCurrent = mvNext;
//      mvNext = getNextDirectoryEntry(mvCurrent, mvDeviceNumber);
//    }
//    deallocateEntry(mvCurrent, mvDeviceNumber);
//
//    // Clear the data items in the FID table
//    mvFIDEntry.Mode = ALLOCATED;
//    mvFIDEntry.FileNumber = -1;
//    mvFIDEntry.CurrentPosition = -1;
//    mvFIDEntry.Buffer = null;
//    mvFIDEntry.CurrentDiskBlock = -1;

     int[] indexesFAT = new int[1];
    return (new FileSystemData(iRequestID, "", indexesFAT));
  }

  // Sets up a directory entry for the file and sets it
  // to a 0 length file.
  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemData create(int iRequestID, int iFSFileNumber){

//    CPM14FIDTableEntry mvFIDEntry =
//        (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNumber);
//
//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvFIDEntry.Device);
//
//    // Check for spaces and necessary conditions
//    if ((mvFIDEntry.Mode != ALLOCATED) ||
//        (diskFull(mvFIDEntry.Device)))
//    {
//      // Return an error
//      return (new FileSystemData(iRequestID, -1));
//    }
//
//    int mvDeviceNumber = mvFIDEntry.Device;
//    // Allocate the dir entry
//    int mvDirEntry = getFreeEntry(mvDeviceNumber);
//
//    if (mvDirEntry == -1)
//    {
//      return (new FileSystemData(iRequestID, -1));
//    }
//
//    // Setup the entry
//    int mvOffset = mvDirEntry * DIR_ENTRY_SIZE;
//
//    mvDevice.directoryTable[mvOffset + STATUS] = 0;
//
//    byte[] mvByteFilename = convertFilename(mvFIDEntry.Filename);
//    int mvCounter;
//
//    for (mvCounter = 0; mvCounter < 11; mvCounter++)
//    {
//      mvDevice.directoryTable[mvOffset + FILENAME + mvCounter] =
//          mvByteFilename[mvCounter];
//    }
//
//    mvDevice.directoryTable[mvOffset + EXTENT] = 0;
//    mvDevice.directoryTable[mvOffset + RESERVED] = 0;
//    mvDevice.directoryTable[mvOffset + RESERVED + 1] = 0;
//    mvDevice.directoryTable[mvOffset + RECORDS] = 0;
//
//    for (mvCounter = 0; mvCounter < 16; mvCounter++)
//    {
//      mvDevice.directoryTable[mvOffset + DATA_BLOCKS + mvCounter] = 0;
//    }
//
//    // Setup the initial data.
//    mvFIDEntry.FileNumber = mvDirEntry;
//    mvFIDEntry.Mode = WRITING;
//    mvFIDEntry.CurrentPosition = 0;

    int[] indexesFAT = new int[1];
    return (new FileSystemData(iRequestID, "", indexesFAT));
  }

  // Closes a file and removes it from the FID table. first
  // writes the current buffer and the Dir blocks to disk.
  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemData close(int iRequestID, int iFSFileNumber){

//    CPM14FIDTableEntry mvFIDEntry =
//        (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNumber);
//
//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvFIDEntry.Device);
//
//    if (mvFIDEntry.Mode == WRITING)
//    {
//      // add EOF mark at the end of the file unless on a border line between the
//      // next block. If no eof is encountered, and no following block is allocated,
//      // Then the end of the file falls directly on the line.
//      if (((mvFIDEntry.CurrentPosition) % BLOCK_SIZE) != 0)
//      {
//        //Currentposition will already point to the place to write the  EOF
//        // character
//        mvFIDEntry.Buffer[(mvFIDEntry.CurrentPosition % 1024)] = 0x1A;
//      }
//
//      diskRequest(mvDevice.deviceName, "FS_CLOSE::WRITE_BUFFER", "WRITING",
//          iFSFileNumber, iRequestID, -1,
//          mvFIDEntry.CurrentDiskBlock, mvFIDEntry.Buffer);
//    }
//    else
//    {
//      if (mvDevice.openFileNames.containsKey(mvFIDEntry.Filename))
//      {
//        mvDevice.openFileNames.remove(mvFIDEntry.Filename);
//      }
//      cvFIDTable.remove(iFSFileNumber);
//      // Clean up entries in FID table.
//      return (new FileSystemData(iRequestID, 0));
//    }
    return null;
  }

  // Opens the specified file for reading.
  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemData open(int iRequestID, int iFSFileNumber){

//    CPM14FIDTableEntry mvFIDEntry =
//        (CPM14FIDTableEntry) cvFIDTable.getItem(iFSFileNumber);
//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvFIDEntry.Device);
//    int mvDeviceNumber = mvFIDEntry.Device;
//
//    if (mvFIDEntry.Mode == ALLOCATED)
//    {
//      int mvTheFile;
//
//      if ((mvTheFile = getNextDirectoryEntry(1, 1)) != -1)
//      {
//        // init for first read.
//        mvFIDEntry.Mode = READING;
//        mvFIDEntry.FileNumber = mvTheFile;
//        mvFIDEntry.CurrentPosition = 0;
//        return (new FileSystemData(iRequestID, 0));
//      }
//      else
//      {
//        return (new FileSystemData(iRequestID, -1));
//      }
//    }
//    else
//    {
//      return (new FileSystemData(iRequestID, -1));
//    }

    // eu coloquei issooooooooooooooooo!!!!!!!!!!
    int[] indexesFAT = new int[1];
      return (new FileSystemData(iRequestID, "", indexesFAT));
  }

  // Reads from the file if it is in the right mode.
  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemData delete(int iRequestID,
                              MSDOSFile msdosFile,
                              String path)
    throws MSDOSFATException, MSDOSDirectoryException {

//    MSDOSFileSystem newMSDOSFileSystem = null;
//    newMSDOSFileSystem = requestSystemFile();

//    requestSystemFile();

//    System.out.println("FAT: "+newMSDOSFileSystem.msdosFat.toString());

    // PRIMEIRO Vai-se no  Directory pra saber ver onde estah gravado o arquivo
    // ver isso pelo nome, depois descobre-se pra onde o primeiro bloco aponta
    // depois na FAT, depois ler os bloquinhos

    int firstBlock = -7;
    String value = "";
    int[] indexesFAT;

    if(msdosFile.isA()){
        firstBlock = readSubdirectoryAtBlock(msdosFile, path);
        int[] indexes = msdosFat.getEntriesFileFromFAT(firstBlock);
        // ler e depois deleta os dados do bloco
        value = deleteDataFromBlock(indexes);
        msdosFat.removeEntriesOfFAT(indexes);
        // deleta a entrada de diretorio do (Subdiretorio dentro do bloco)
        deleteEntryFromSubdirectoryAtBlock(msdosFile, path);
        indexesFAT = indexes;
    }else{
        System.out.println("entrei no else");
        firstBlock = readSubdirectoryAtBlock(msdosFile, path);
        System.out.println("firstBlock: "+firstBlock);
        if(firstBlock!=-1){
          Directory directory = mountDirectoryFromBlock(firstBlock);
          value = directory.dir();

//          directory.removeFile(msdosFile.getNameFile());
        }else{
          value = this.directory.dir();
//          this.directory.removeFile(msdosFile.getNameFile());
        }
        indexesFAT = new int[1];
        System.out.println("firstBlock do msdosFile: "+msdosFile.getFirstBlock());
        indexesFAT[0] = firstBlock;
//        indexesFAT[0] = msdosFile.getFirstBlock();
        // ler e depois deleta os dados do bloco
        msdosFat.removeEntryFAT(firstBlock);
        deleteEntryFromSubdirectoryAtBlock(msdosFile, path);
    }

//    int[] indexesFAT;
//    System.out.println("firstBlock a ser pintado: "+firstBlock);
//    if (firstBlock!=-1){
//      indexesFAT = msdosFat.getEntriesFileFromFAT(firstBlock);
//    }else{
//      indexesFAT = new int[1];
//      indexesFAT[0] = -1;
//    }
    return (new FileSystemData(iRequestID, value, indexesFAT));

  }

  public String deleteDataFromBlock(int[] indexes) {
      String value = "";

      Block block = null;
//      for (int i = 0; i < indexes.length; i++) {
      for (int i = (indexes.length-1); i >= 0; i--) {
         block = disk.readBlock(indexes[i]);
         value += new String(block.getData());
         //primeiro ler depois remove o conteudo do disco
         disk.removeBlock(indexes[i]);
      }

      return value;

  }

  public int deleteEntryFromSubdirectoryAtBlock(MSDOSFile msdosFile, String path)
    throws MSDOSFATException, MSDOSDirectoryException{

    boolean isAbsolutePath = path.startsWith("C:/");
    MSDOSFile msdosFileAux = null;
    int firstBlock = -1;

    // Ex: C:/dany/x
    if (isAbsolutePath){
      System.out.println("path.substring(2, path.length()): "+path.substring(3, path.length()));
      StringTokenizer st = new StringTokenizer(path.substring(3, path.length()), "/");
      System.out.println("st "+st);
      int countTokens = st.countTokens();
      if (countTokens==0){
         System.out.println("lendo da raiz os arquivos");
         if(!msdosFile.getNameFile().equals("")){
           msdosFileAux = directory.readFile(msdosFile.getNameFile());
           firstBlock = msdosFileAux.getFirstBlock();
           msdosFile.setFirstBlock(msdosFileAux.getFirstBlock());
           // deletando o arquivo
           directory.removeFile(msdosFile.getNameFile());
         }else{
            firstBlock = -1;
         }

      }else{ // vai no Diretorio raiz procurar o caminho  e depois vai na FAT
             // procurar o lugar dele, depois no disco
        String aux = null;
        int initialBlock = -1;
        int[] listEntries;
        aux = st.nextToken();
        System.out.println("nome dir "+aux);
        msdosFileAux = directory.readFile(aux);
        initialBlock = msdosFileAux.getFirstBlock();
        listEntries = msdosFat.getEntriesFileFromFAT(initialBlock);
        int indice = listEntries[0];
        MSDOSFile msdosFile2 = null;

        if(listEntries.length==1){// ou ==

          Directory subDirectory = null;
          String teste = "";
          for (int i = 0; i < (countTokens-1); i++) {

//             }else{
//               // vai procurar o proximo subdiretorio nos blocos
               subDirectory = mountDirectoryFromBlock(indice);
               teste = st.nextToken();
               msdosFile2 = subDirectory.readFile(teste);
               indice = msdosFile2.getFirstBlock();
//             }
          }

         // ve se chegou no diretorio onde sera gravado o novo
         // diretorio
//         if(i==(st.countTokens()-2)){
//           recordAtBlock(indice, msdosFile);
             subDirectory = mountDirectoryFromBlock(indice);
             msdosFile2 = subDirectory.readFile(msdosFile.getNameFile());
             firstBlock = msdosFile2.getFirstBlock();
             // deletando arquivo
             subDirectory.removeFile(msdosFile.getNameFile());
//         }


        }else{
          throw new MSDOSFATException("Não há o diretório "+aux+" no sistema de arquivo");
        }

      }
    }
    return firstBlock;
  }

  // Reads from the file if it is in the right mode.
  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemData read(int iRequestID,
                              MSDOSFile msdosFile,
                              String path)
    throws MSDOSFATException, MSDOSDirectoryException {

//    MSDOSFileSystem newMSDOSFileSystem = null;
//    newMSDOSFileSystem = requestSystemFile();

//    requestSystemFile();

//    System.out.println("FAT: "+newMSDOSFileSystem.msdosFat.toString());

    // PRIMEIRO Vai-se no  Directory pra saber ver onde estah gravado o arquivo
    // ver isso pelo nome, depois descobre-se pra onde o primeiro bloco aponta
    // depois na FAT, depois ler os bloquinhos

    int firstBlock = -7;
    String value = "";
    int[] indexesFAT;

    if(msdosFile.isA()){
        firstBlock = readSubdirectoryAtBlock(msdosFile, path);
        int[] indexes = msdosFat.getEntriesFileFromFAT(firstBlock);
        value = readDataFromBlock(indexes);
        indexesFAT = indexes;
    }else{
        System.out.println("entrei no else");
        firstBlock = readSubdirectoryAtBlock(msdosFile, path);
        System.out.println("firstBlock: "+firstBlock);
        if(firstBlock!=-1){
          Directory directory = mountDirectoryFromBlock(firstBlock);
          value = directory.dir();
        }else{
          value = this.directory.dir();
        }
        indexesFAT = new int[1];
        indexesFAT[0] = firstBlock;
    }

//    int[] indexesFAT;
//    System.out.println("firstBlock a ser pintado: "+firstBlock);
//    if (firstBlock!=-1){
//      indexesFAT = msdosFat.getEntriesFileFromFAT(firstBlock);
//    }else{
//      indexesFAT = new int[1];
//      indexesFAT[0] = -1;
//    }
    return (new FileSystemData(iRequestID, value, indexesFAT));

  }

  public String readDataFromBlock(int[] indexes) {
      String value = "";

      Block block = null;
//      for (int i = 0; i < indexes.length; i++) {
      for (int i = (indexes.length-1); i >= 0; i--) {
         block = disk.readBlock(indexes[i]);
         value += new String(block.getData());
      }

      return value;

  }
  // If the mode is correct, this starts or contines writing a
  // file.
  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
//  public FileSystemData write(int iRequestID, int iFSFileNo){
  public FileSystemData write(int iRequestID,
                              MSDOSFile msdosFile,
                              String data,
                              String path)
       throws MSDOSFATException, MSDOSDirectoryException {

      // PRIMEIRO: Tratamos o relacionamento da escrita do dado na FAT

      // Checa a quantidade de entradas Livres na FAT
      int sizeFreeFAT = msdosFat.getSizeFreeFAT();
      int newPosicionFATAux1 = -1;
      int[] indexesFAT;

      // Pra garantir que tem entrada livre na FAT pra
      if (sizeFreeFAT!=0){

        // Eh um arquivo
        if (msdosFile.isA()){

           System.out.println("ARQUIVO: "+msdosFile.getNameFile());
          // Pegar entrada de bloco disponivel pra colocar os dados
          // do arquivo ou diretorio passado
          byte[] valueBytes = data.getBytes();

          int quantityBytes = valueBytes.length;

          int numberEntryFAT = (new Double(Math.ceil((double)quantityBytes/InterfaceMSDOS.BLOCK_SIZE))).intValue();

          System.out.println("numberEntryFAT: "+numberEntryFAT);

          if (numberEntryFAT <= sizeFreeFAT){
             newPosicionFATAux1 = msdosFat.getNewEntryFAT();

             // Seta o primeiro bloco do arquivo que veio da fat
             msdosFile.setFirstBlock(newPosicionFATAux1);

             // Seta as entradas correspondentes ao bloco
             // que vao ser ocupadas na FAT
             setEntriesAtFAT(msdosFile, numberEntryFAT);

             //Grava data at blocks and seta o caminho
             // dos varios blocos ocupados pelo arquivo
             writeDataAtBlock(msdosFile, data);

             // Grava no diretorio raiz ou em algum bloco
             // do disco (que representa um subdiretorio)
             // o msdosFile
             writeSubdirectoryAtBlock(msdosFile, path);

             msdosFile.setNumeberBlocksArchive(numberEntryFAT);

          // nao precisa desse else eh soh pra garantir
          }else{
              throw new MSDOSFATException("FAT is full");
          }

           indexesFAT = msdosFat.getEntriesFileFromFAT(newPosicionFATAux1);
        }else{
           // adicionar um novo diretorio
           newPosicionFATAux1 = msdosFat.getNewEntryFAT();
           msdosFile.setFirstBlock(newPosicionFATAux1);
           msdosFat.addEntriesFAT(newPosicionFATAux1, -2);

           // coloca no bloco um new de diretorio pra
           // simbolizar q ele eh um diretorio e nao um pedaco de
           // arquivo
           writeNewDirectoryAtBlock(newPosicionFATAux1);

           // coloca soh uma entrada onde deve estar o arquivo
           writeSubdirectoryAtBlock(msdosFile, path);

           indexesFAT = new int[1];
           indexesFAT[0] = newPosicionFATAux1;
           msdosFile.setNumeberBlocksArchive(1);
        }
      // nao precisa desse else eh soh pra garantir
      }else{
         throw new MSDOSFATException("FAT is full");
      }

//     int[] indexesFAT = msdosFat.getEntriesFileFromFAT(newPosicionFATAux1);
     return new FileSystemData(iRequestID, "-1", indexesFAT);
  }


  public void writeNewDirectoryAtBlock(int newPosicionFATAux)
     throws MSDOSDirectoryException {

     try{
       Directory directory = new Directory();
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       ObjectOutputStream out = new ObjectOutputStream(baos);
       out.writeObject(directory);

       byte[] byteDirectry = baos.toByteArray();

       Block block = new Block();
       block.setData(byteDirectry);
       disk.writeBlock(newPosicionFATAux, block);
     }catch(IOException ioex){
       throw new MSDOSDirectoryException("Error recording new directory");
     }
  }


  /**
   * Seta as entradas correspondentes ao bloco
   * que vao ser ocupadas na FAT
   */
  public void setEntriesAtFAT(MSDOSFile msdosFile, int numberEntryFAT)
         throws MSDOSFATException{
     int newPosicionFATAux1 = -1;
     int newPosicionFATAux2 = -1;
     int i = 0;

     newPosicionFATAux1 = msdosFile.getFirstBlock();

     System.out.println("setEntriesAtFAT: "+msdosFile.getNameFile());

     System.out.println("newPosicionFATAux1: "+newPosicionFATAux1);
     System.out.println("numberEntryFAT: "+numberEntryFAT);
     for (i = 0; i < numberEntryFAT-1; i++) {

        System.out.println("entrei no for!!!");
        newPosicionFATAux2 = msdosFat.getNewEntryFAT();
        msdosFat.addEntriesFAT(newPosicionFATAux1, newPosicionFATAux2);
        newPosicionFATAux1 = newPosicionFATAux2;
     }

     // Verifica se eh o ultimo numero a ser adicionado na FAT, para
     // aponta-lo pra -2
//     if(i==(numberEntryFAT-1)){
        System.out.println("newPosicionFATAux1: "+newPosicionFATAux1);
        msdosFat.addEntriesFAT(newPosicionFATAux1, -2);
//     }
  }

  /**
   * Write Datas at Blocks, have known the entries at FAT
   */
  public void writeDataAtBlock(MSDOSFile msdosFile, String data)
         throws MSDOSFATException{
     int newPosicionFATAux1 = -1;
     int i = 0;
     int[] entriesAtFAT;

     System.out.println("writeDataAtBlock: "+msdosFile.getNameFile());
     byte[] valueBytes = data.getBytes();

     newPosicionFATAux1 = msdosFile.getFirstBlock();
     int[] indexes = msdosFat.getEntriesFileFromFAT(newPosicionFATAux1);

     for (i = 0; i < indexes.length-1; i++) {
        System.out.println("entrei no for222");
        // Adiciona no disco realmente os dados do arquivo
        int aux = -1;
        if(i==0){
           aux = 0;
        }else{
           aux = (i*InterfaceMSDOS.BLOCK_SIZE)-1;
        }
        byte[] auxDst = new byte[InterfaceMSDOS.BLOCK_SIZE];
        System.arraycopy(valueBytes,aux,auxDst,0,InterfaceMSDOS.BLOCK_SIZE);
        Block block = new Block();
        block.setData(auxDst);
        disk.writeBlock(newPosicionFATAux1, block);
     }

     // Verifica se eh o ultimo numero pra colocar no disco nao mais pra
     // preencher o bloco todo e sim apenas ateh o fim do arquivo
//     if(i==(numberEntryFAT-1)){
        // mandando para o disco
        byte[] auxDst2 = new byte[InterfaceMSDOS.BLOCK_SIZE];
        int restante = valueBytes.length - (i*InterfaceMSDOS.BLOCK_SIZE);
        System.arraycopy(valueBytes,i*InterfaceMSDOS.BLOCK_SIZE,auxDst2,0,restante);
        Block block2 = new Block();
        System.out.println("auxDst2: "+auxDst2);
        block2.setData(auxDst2);
        System.out.println("newPosicionFATAux1: "+newPosicionFATAux1);
        disk.writeBlock(newPosicionFATAux1, block2);
//     }

   }

  public int readSubdirectoryAtBlock(MSDOSFile msdosFile, String path)
    throws MSDOSFATException, MSDOSDirectoryException{

    boolean isAbsolutePath = path.startsWith("C:/");
    MSDOSFile msdosFileAux = null;
    int firstBlock = -1;

    // Ex: C:/dany/x
    if (isAbsolutePath){
      System.out.println("path.substring(2, path.length()): "+path.substring(3, path.length()));
      StringTokenizer st = new StringTokenizer(path.substring(3, path.length()), "/");
      System.out.println("st "+st);
      int countTokens = st.countTokens();
      if (countTokens==0){
         System.out.println("lendo da raiz os arquivos");
         if(!msdosFile.getNameFile().equals("")){
           msdosFileAux = directory.readFile(msdosFile.getNameFile());
           firstBlock = msdosFileAux.getFirstBlock();
           msdosFile.setFirstBlock(msdosFileAux.getFirstBlock());
         }else{
            firstBlock = -1;
         }

      }else{ // vai no Diretorio raiz procurar o caminho  e depois vai na FAT
             // procurar o lugar dele, depois no disco
        String aux = null;
        int initialBlock = -1;
        int[] listEntries;
        aux = st.nextToken();
        System.out.println("nome dir "+aux);
        msdosFileAux = directory.readFile(aux);
        initialBlock = msdosFileAux.getFirstBlock();
        listEntries = msdosFat.getEntriesFileFromFAT(initialBlock);
        int indice = listEntries[0];
        MSDOSFile msdosFile2 = null;

        if(listEntries.length==1){// ou ==

          Directory subDirectory = null;
          String teste = "";
          for (int i = 0; i < (countTokens-1); i++) {

//             }else{
//               // vai procurar o proximo subdiretorio nos blocos
               subDirectory = mountDirectoryFromBlock(indice);
               teste = st.nextToken();
               msdosFile2 = subDirectory.readFile(teste);
               indice = msdosFile2.getFirstBlock();
//             }
          }

         // ve se chegou no diretorio onde sera gravado o novo
         // diretorio
//         if(i==(st.countTokens()-2)){
//           recordAtBlock(indice, msdosFile);
             subDirectory = mountDirectoryFromBlock(indice);
             msdosFile2 = subDirectory.readFile(msdosFile.getNameFile());
             firstBlock = msdosFile2.getFirstBlock();
//         }


        }else{
          throw new MSDOSFATException("Não há o diretório "+aux+" no sistema de arquivo");
        }

      }
    }
    return firstBlock;
  }

  /**
   *   Vai ter acesso ao path e assim vai na fat saber se vai escrever
   * no Direcoty root ou num bloco apontado pela FAT que representa na
   * verdade um subdiretorio
   *
   * PS: Vemos aqui que tanto o arquivo como um subdiretorio sao gravados da
   * mesma forma no bloco, porem os dados de um arquivo sao gravados de forma
   * diferente e isso sera feito em outro metodo
   */
  public void writeSubdirectoryAtBlock(MSDOSFile msdosFile, String path)
    throws MSDOSFATException, MSDOSDirectoryException{

    boolean isAbsolutePath = path.startsWith("C:/");

    System.out.println("writeSubdirectoryAtBlock: "+msdosFile.getNameFile());
    // Ex: C:/dany/x
    if (isAbsolutePath){
      System.out.println("path.substring(2, path.length()): "+path.substring(3, path.length()));
      StringTokenizer st = new StringTokenizer(path.substring(3, path.length()), "/");
      System.out.println("st "+st);
      int countTokens = st.countTokens();
      if (countTokens==0){
         System.out.println("adicionando na raiz o arquivo");
         directory.addNewFile(msdosFile);

      }else{ // vai no Diretorio raiz procurar o caminho  e depois vai na FAT
             // procurar o lugar dele, depois no disco pra apenas setar o bloco
             // que na verdade eh um subdiretorio com uma nova entrada de arquivo
        String aux = null;
        MSDOSFile msdosFileAux = null;
        int initialBlock = -1;
        int[] listEntries;
        aux = st.nextToken();
        msdosFileAux = directory.readFile(aux);
        initialBlock = msdosFileAux.getFirstBlock();
        System.out.println("initialBlock: "+initialBlock);
        listEntries = msdosFat.getEntriesFileFromFAT(initialBlock);
        int indice = listEntries[0];

        System.out.println("indice: "+indice);
        if(listEntries.length==1){// ou ==

          for (int i = 0; i < (countTokens-1); i++) {

//             // ve se chegou no diretorio onde sera gravado o novo
//             // diretorio
//             if(i==(st.countTokens()-2)){
//               recordAtBlock(indice, msdosFile);
//             }else{
               // vai procurar o proximo subdiretorio nos blocos
               Directory subDirectory = mountDirectoryFromBlock(indice);
               String teste = st.nextToken();
               MSDOSFile msdosFile2 = subDirectory.readFile(teste);
               indice = msdosFile2.getFirstBlock();
//             }
          }

          // ve se chegou no diretorio onde sera gravado o novo
          // diretorio
          recordAtBlock(indice, msdosFile);

        }else{
          throw new MSDOSFATException("Não há o diretório "+aux+" no sistema de arquivo");
        }

      }
    }

  }

  public void recordAtBlock(int address, MSDOSFile msdosFile)
     throws MSDOSDirectoryException{

     try{
       Block block = null;
       Directory subDirectory = null;
//       boolean flag = false;
       ByteArrayInputStream byteArrayInputStream = null;
       ObjectInputStream byteInBlock = null;

       block = disk.readBlock(address);
//       if (block != null){
//         flag = true;
         byte[] subDirectoryAux = block.getData();
         byteArrayInputStream = new ByteArrayInputStream(subDirectoryAux);
         byteInBlock = new ObjectInputStream(byteArrayInputStream);

         subDirectory = (Directory)byteInBlock.readObject();
//       }else{
//         subDirectory = new Directory();
//       }

       subDirectory.addNewFile(msdosFile);

       ByteArrayOutputStream byteArray  = new ByteArrayOutputStream();
       ObjectOutputStream byteOutBlock = new ObjectOutputStream(byteArray);
       byteOutBlock.writeObject(subDirectory);

       byteOutBlock.flush();

       block.setData(byteArray.toByteArray());
//       if(flag){
         byteArrayInputStream.close();
         byteInBlock.close();
//       }
       byteArray.close();
       byteOutBlock.close();

     }catch(StreamCorruptedException scex){

     }catch(ClassNotFoundException cnfex){

     }catch(IOException ioex){

     }
  }

  public Directory mountDirectoryFromBlock(int address){

     Directory subDirectory = null;
     try{
       Block block = disk.readBlock(address);
       byte[] subDirectoryAux = block.getData();
       ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(subDirectoryAux);

       ObjectInputStream byteInBlock = new ObjectInputStream(byteArrayInputStream);
       subDirectory = (Directory)byteInBlock.readObject();

       byteArrayInputStream.close();
       byteInBlock.close();

     }catch(ClassNotFoundException cnfex){
       System.out.println("ClassNotFoundException "+cnfex);
     }catch(IOException ioex){
       System.out.println("IOException "+ioex);
     }

     return subDirectory;
  }

  public void recordeSystemFile(){

    try{

     FileOutputStream fileOutputStream = new FileOutputStream("fileSystem.dat");
     ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
     out.writeObject(msdosFat);
     out.writeObject(directory);
     out.writeObject(disk);

     fileOutputStream.close();
     out.close();

   }catch(IOException ioex){
      System.out.println("ERRO no recorde: "+ioex.getMessage());
      ioex.printStackTrace();
   }

  }

//  public MSDOSFileSystem requestSystemFile(){
  public void requestSystemFile(){

//    MSDOSFileSystem newMSDOSFileSystem = null;
    try{
      File file = new File("/Users/andrew/java/RCOSFS/RCOSjava-0.4.1-src/dist/fileSystem.dat");

      if(file.exists()){
         ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
         msdosFat = (MSDOSFAT)in.readObject();
         directory = (Directory)in.readObject();
         disk = (Disk)in.readObject();

         this.setMsdosFat(msdosFat);
         this.setDirectory(directory);
         this.setDisk(disk);

         in.close();
      }

   }catch(ClassNotFoundException ex){
      System.out.println("ERRO em requestSystemFile: "+ex.getMessage());
      ex.printStackTrace();
   }catch(FileNotFoundException ex){
      System.out.println("ERRO em requestSystemFile: "+ex.getMessage());
      ex.printStackTrace();
   }catch(OptionalDataException ex){
      System.out.println("ERRO em requestSystemFile: "+ex.getMessage());
      ex.printStackTrace();
   }catch(IOException ex){
      System.out.println("ERRO em requestSystemFile: "+ex.getMessage());
      ex.printStackTrace();
   }
//   return newMSDOSFileSystem;
  }

  // Checks to see if the File System indicated by MountPoint is full.
  /**
   * Description of the Method
   *
   * @param mvDeviceNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean diskFull(int mvDeviceNumber)
  {
//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvDeviceNumber);
//
//    return ((mvDevice.numberOfFreeBlocks == 0)
//         || (mvDevice.numberOfFreeEntries == 0));
     return false;
  }

  // Coordinating this in the one synchronised function means that the
  // allocation of the resources will be safe.
  /**
   * Description of the Method
   *
   * @param type Description of Parameter
   * @param mvDeviceNumber Description of Parameter
   * @param Item Description of Parameter
   * @return Description of the Returned Value
   */
  public int resourceAllocator(String type, int mvDeviceNumber, int Item)
  {
//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvDeviceNumber);
//
//
//    if (type.equalsIgnoreCase("DIR"))
//    {
//      int mvCounter;
//
//      for (mvCounter = 0;
//          ((mvCounter < TOTAL_DIR_ENTRIES) &&
//          (mvDevice.dirEntList[mvCounter]));
//          mvCounter++)
//      {
//        ;
//      }
//      if (mvCounter == TOTAL_DIR_ENTRIES)
//      {
//        return -1;
//      }
//      else
//      {
//        mvDevice.dirEntList[mvCounter] = true;
//        mvDevice.numberOfFreeEntries--;
//        return mvCounter;
//      }
//    }
//    else if (type.equalsIgnoreCase("BLOCK"))
//    {
//      int mvCounter;
//
//      for (mvCounter = 0;
//          ((mvCounter < TOTAL_DISK_BLOCKS) &&
//          (mvDevice.blockList[mvCounter]));
//          mvCounter++)
//      {
//        ;
//      }
//      if (mvCounter == TOTAL_DISK_BLOCKS)
//      {
//        return -1;
//      }
//      else
//      {
//        mvDevice.blockList[mvCounter] = true;
//        mvDevice.numberOfFreeBlocks--;
//        return mvCounter + DISK_BLOCK_OFFSET;
//      }
//
//    }
//    else if (type.equalsIgnoreCase("CLEARDIR"))
//    {
//
//      int mvEntryOffset = Item * DIR_ENTRY_SIZE;
//      int mvCounter = 0;
//      int mvBlockNum;
//
//      mvBlockNum = SVB2I & mvDevice.directoryTable
//          [mvEntryOffset + DATA_BLOCKS + mvCounter];
//
//      while ((mvCounter < 16) && (mvBlockNum > 0))
//      {
//        System.out.println("Freeing block :" + mvBlockNum);
//        mvDevice.blockList[mvBlockNum - DISK_BLOCK_OFFSET] = false;
//        mvDevice.directoryTable[mvEntryOffset + DATA_BLOCKS + mvCounter] = 0;
//
//        mvDevice.numberOfFreeBlocks++;
//        mvCounter++;
//        mvBlockNum = SVB2I & mvDevice.directoryTable
//            [mvEntryOffset + DATA_BLOCKS + mvCounter];
//      }
//      mvDevice.directoryTable[mvEntryOffset + STATUS] = (byte) 0xE5;
//      mvDevice.dirEntList[Item] = false;
//      mvDevice.numberOfFreeEntries++;
//      return 1;
//    }
//    else if (type.equalsIgnoreCase("CLEARBLOCK"))
//    {
//
//      mvDevice.blockList[Item] = false;
//      mvDevice.numberOfFreeBlocks++;
//
//      return 1;
//    }
//    else
//    {
//      return -1;
//    }
    return -1;
  }

  // Deallocates the disk blocks for the specified device and sets the block to
  // deleted.
  /**
   * Description of the Method
   *
   * @param mvEntryNumber Description of Parameter
   * @param mvDeviceNumber Description of Parameter
   */
  public void deallocateEntry(int mvEntryNumber, int mvDeviceNumber)
  {
//    resourceAllocator("CLEARDIR", mvDeviceNumber, mvEntryNumber);
  }

  // Will display the specified data to the screen.
  // Operation can be "DIR" or "BUFFER". For DIR, item1 will indicate
  // the entry to dump, item2 will indicate the device. For Buffer,
  // item1 is the FID and item2 is not used.
  /**
   * Description of the Method
   *
   * @param Operation Description of Parameter
   * @param item1 Description of Parameter
   * @param item2 Description of Parameter
   */
  public void dumpToScreen(String Operation, int item1, int item2){

//    if (Operation.equalsIgnoreCase("DIR"))
//    {
//      MSDOSFATEntry mvDevice =
//          (MSDOSFATEntry) cvDeviceTable.getItem(item2);
//
//      int X;
//      int Offset = item1 * 32;
//
//      for (X = 0; X < 32; X++)
//      {
//        if (X > 0 && X < 12)
//        {
//          System.out.print((char) mvDevice.directoryTable[Offset + X]
//               + " ");
//        }
//        else
//        {
//          System.out.print((SVB2I & mvDevice.directoryTable[Offset + X]) + " ");
//        }
//        if (X == 15)
//        {
//          System.out.println("");
//        }
//      }
//    }
//    else if (Operation.equalsIgnoreCase("BUFFER"))
//    {
//      CPM14FIDTableEntry mvFIDEntry =
//          (CPM14FIDTableEntry) cvFIDTable.getItem(item1);
//      int X;
//
//      for (X = 0; X < 1024; X++)
//      {
//        if ((SVB2I & mvFIDEntry.Buffer[X]) == 0x1A)
//        {
//          System.out.print("<EOF>");
//        }
//        else
//        {
//          System.out.print((char) mvFIDEntry.Buffer[X]);
//        }
//      }
//    }
    System.out.println("dumpToScreen");
  }

  // Handle the DiskRequestComplete messages and perform the
  // necessary cary on functions.
  /**
   * Description of the Method
   *
   * @param mvTheMessage Description of Parameter
   */
  private void handleReturnMessages(OSMessageAdapter mvTheMessage){

//    DiskRequest mvMessageData = (DiskRequest) mvTheMessage.getBody();
//
//    int mvRequestID = mvMessageData.getRequestId();
//
//    CPM14RequestTableEntry mvRequestData =
//        (CPM14RequestTableEntry) cvRequestTable.getItem(mvRequestID);
//
//    int mvFID = mvRequestData.FSFileNum;
//
//    CPM14FIDTableEntry mvFIDEntry =
//        (CPM14FIDTableEntry) cvFIDTable.getItem(mvFID);
//
//    MSDOSFATEntry mvDevice =
//        (MSDOSFATEntry) cvDeviceTable.getItem(mvFIDEntry.Device);
//
//    if (mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_BUFFER"))
//    {
//      //     System.out.println("Handle Write byffer return."); // DEBUG
//      mvRequestData.Type = "FS_CLOSE::WRITE_DIR1";
//
//      byte[] mvToWrite = new byte[1024];
//
//      int mvCounter;
//
//      for (mvCounter = 0; mvCounter < 1024; mvCounter++)
//      {
//        mvToWrite[mvCounter] = mvDevice.directoryTable[mvCounter];
//      }
//
//      DiskRequest mvNewReq = new DiskRequest(mvRequestID, 0, mvToWrite);
//      //Message mvNewMessage = new Message ( id, mvDevicedeviceName,
//      //                                      "DISKREQUEST", mvNewReq);
//      //SendMessage( mvNewMessage );
//    }
//    else if (mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_DIR1"))
//    {
//////      System.out.println("Handle Write dir1 return."); // DEBUG
//      mvRequestData.Type = "FS_CLOSE::WRITE_DIR2";
//
//      byte[] mvToWrite = new byte[1024];
//
//      int mvCounter;
//
//      for (mvCounter = 0; mvCounter < 1024; mvCounter++)
//      {
//        mvToWrite[mvCounter] = mvDevice.directoryTable[mvCounter + 1024];
//      }
//
//
//      DiskRequest mvNewReq = new DiskRequest(mvRequestID, 1, mvToWrite);
//      //Message mvNewMessage = new Message ( id, mvDevicedeviceName,
//      //                                      "DISKREQUEST", mvNewReq);
//      //SendMessage( mvNewMessage );
//    }
//    else if (mvRequestData.Type.equalsIgnoreCase("FS_CLOSE::WRITE_DIR2"))
//    {
//////      System.out.println("Handle Write dir2 return."); // DEBUG
//////      System.out.println("About to remove FID "+mvFID); // DEBUG
//      //return(new FileSystemData(iRequestID, 0));
//      if (mvDevice.openFileNames.containsKey(mvFIDEntry.Filename))
//      {
//        mvDevice.openFileNames.remove(mvFIDEntry.Filename);
//      }
//      cvFIDTable.remove(mvFID);
//      cvRequestTable.remove(mvRequestID);
//    }
//    else if (mvRequestData.Type.equalsIgnoreCase("FS_READ::GETBLOCK"))
//    {
//      if (mvMessageData.getDiskBlock() >= 0)
//      {
//        int mvCounter;
//
//        for (mvCounter = 0; mvCounter < BLOCK_SIZE; mvCounter++)
//        {
//          mvFIDEntry.Buffer[mvCounter] = mvMessageData.getData()[mvCounter];
//        }
//
//        mvFIDEntry.CurrentDiskBlock = (byte) mvMessageData.getDiskBlock();
//
//        int mvReturnItem = SVB2I & mvFIDEntry.Buffer[
//            mvFIDEntry.CurrentPosition % BLOCK_SIZE];
//
//        if (mvReturnItem == 0x1A)
//        {
//          // EOF
//
//          //return(new FileSystemData(iRequestID, -1));
//        }
//        else
//        {
//          mvFIDEntry.CurrentPosition++;
//          //return(new FileSystemData(iRequestID, mvReturnItem));
//        }
//        cvRequestTable.remove(mvRequestID);
//      }
//      else
//      {
//        cvFIDTable.remove(mvFID);
//        cvRequestTable.remove(mvRequestID);
//        //return(new FileSystemData(iRequestID, -1));
//      }
//    }
//    else if (mvRequestData.Type.equalsIgnoreCase("FS_WRITE::FLUSH"))
//    {
//      if (mvMessageData.getDiskBlock() >= 0)
//      {
//        System.out.println("Interupt reveived.bout to write");
//
//        // DEBUG
//
//        int mvLastBlock = SVB2I & mvDevice.directoryTable
//            [(mvFIDEntry.FileNumber * DIR_ENTRY_SIZE) + DATA_BLOCKS + 15];
//
//        //System.out.print("Cmp: "+mvLastBlock+" - "); // DEBUG
//        //System.out.println("Entry :"+mvFIDEntry.FileNumber); // DEBUG
//        //System.out.println("entrysize : "+DIR_ENTRY_SIZE); // DEBUG
//        //System.out.println("datablocks :"+ DATA_BLOCKS); // DEBUG
//        //System.out.println
//        //     ((mvFIDEntry.FileNumber*DIR_ENTRY_SIZE) + DATA_BLOCKS + 16); // DEBUG
//
//
//        //System.out.println(mvFIDEntry.CurrentDiskBlock); // DEBUG
//        if (mvFIDEntry.CurrentDiskBlock == mvLastBlock)
//        {
//          System.out.println("Setting up new DirEnt.");
//
//          // DEBUG
//
//          int mvNewEntry = getFreeEntry(mvFIDEntry.Device);
//
//          if (mvNewEntry == -1)
//          {
//            cvRequestTable.remove(mvRequestID);
//            //return(new FileSystemData(iRequestID, -1));
//          }
//
//          int mvNewOffset = mvNewEntry * DIR_ENTRY_SIZE;
//          int mvCurOffset = mvFIDEntry.FileNumber * DIR_ENTRY_SIZE;
//          int X;
//
//          for (X = 1; X <= 11; X++)
//          {
//            mvDevice.directoryTable[mvNewOffset + X] =
//                mvDevice.directoryTable[mvCurOffset + X];
//          }
//          mvDevice.directoryTable[mvNewOffset + 0] = 0;
//          mvDevice.directoryTable[mvNewOffset + EXTENT] =
//              (byte) (mvDevice.directoryTable[mvCurOffset + EXTENT] + (byte) 1);
//          mvDevice.directoryTable[mvCurOffset + RECORDS] = (byte) 0x80;
//
//          mvFIDEntry.FileNumber = mvNewEntry;
//          System.out.println("Setup dirent :" + mvNewEntry);
//          // DEBUG
//        }
//
//        int mvNewBlock = getFreeBlock(mvFIDEntry.Device);
//
//        if (mvNewBlock >= 0)
//        {
//          System.out.println("New disk block :" + mvNewBlock);
//
//          //DEBUG
//
//          int mvOffset = (mvFIDEntry.FileNumber * DIR_ENTRY_SIZE) + DATA_BLOCKS;
//          int mvBlockLocation = mvOffset +
//              ((mvFIDEntry.CurrentPosition / BLOCK_SIZE) % 16);
//
//          mvDevice.directoryTable[mvBlockLocation] = (byte) mvNewBlock;
//          mvFIDEntry.CurrentDiskBlock = (byte) mvNewBlock;
//          mvFIDEntry.Buffer[mvFIDEntry.CurrentPosition % BLOCK_SIZE] =
//              (byte) mvRequestData.Data;
//          mvFIDEntry.CurrentPosition++;
//          //return(new FileSystemData(iRequestID, 0));
//////////////          Dump( "DIR", 0,0); // DEBUG
//        }
//        else
//        {
//          //return(new FileSystemData(iRequestID, -1));
//        }
//
//        cvRequestTable.remove(mvRequestID);
//
//      }
//      else
//      {
//        cvRequestTable.remove(mvRequestID);
//        //return(new FileSystemData(iRequestID, -1));
//      }
//    }

    //else if ( mvRequestData.Type.equalsIgnoreCase(""))
//    System.out.println("FS:HandleReturnMessage - end"); // DEBUG

    //return null;
  }

  // Converts a string filename "mount:name.ext" to a byte array
  // that is FFFFFFFFEEE where F is the filename and E is the extention.
  // spaces are padded out.
  /**
   * Description of the Method
   *
   * @param mvFilename Description of Parameter
   * @return Description of the Returned Value
   */
  private byte[] convertFilename(String mvFilename){

    byte[] mvReturnData = new byte[11];
//    StringTokenizer stTokenizer = new StringTokenizer(mvFilename, ":.");
//
//    if (stTokenizer.countTokens() == 3)
//    {
//      stTokenizer.nextToken();
//      mvReturnData = stTokenizer.nextToken().toUpperCase().getBytes();
//    }
    return mvReturnData;
  }

  // Insert a queue Item and send a request to the disk.
  /**
   * Description of the Method
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
      int FSMRequestID, int RequestData, int block, byte[] data){

//    CPM14RequestTableEntry mvQueueEntry = new CPM14RequestTableEntry();
//
//    mvQueueEntry.Source = new String(from);
//    mvQueueEntry.Type = new String(type);
//    mvQueueEntry.FSFileNum = FSFileNo;
//    mvQueueEntry.FSMRequestID = FSMRequestID;
//    mvQueueEntry.Data = RequestData;
//
//    // Request new block
//    int iReqNum = cvRequestTable.add(mvQueueEntry);
//    DiskRequest mvTheRequest = new DiskRequest(iReqNum,
//        block,
//        data);
    //Message mvTheReq = new Message( id,  new String(to),"DISKREQUEST", mvTheRequest);
    //SendMessage ( mvTheReq );
  }

  // Returns a ReturnValie message to the Destination
  // basically saying it's all over.
  /**
   * Description of the Method
   *
   * @param mvFSMReqNo Description of Parameter
   * @param mvReturnData Description of Parameter
   */
  private void returnValue(int mvFSMReqNo, int mvReturnData)
  {
//    FileSystemData mvToReturn = new FileSystemData(mvFSMReqNo,
//        mvReturnData);
    /*Message mvReturnMessage = new Message(id, mvDestination,  "RETURNVALUE",
                                           mvToReturn);
    SendMessage ( mvReturnMessage );*/
  }
  public void setDirectory(Directory directory) {
    this.directory = directory;
  }
  public void setDisk(Disk disk) {
    this.disk = disk;
  }
  public void setMsdosFat(MSDOSFAT msdosFat) {
    this.msdosFat = msdosFat;
  }
}
