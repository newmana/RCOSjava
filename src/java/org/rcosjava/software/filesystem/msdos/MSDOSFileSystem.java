package org.rcosjava.software.filesystem.msdos;

import java.io.*;
import java.util.*;

import org.rcosjava.hardware.disk.Disk;
import org.rcosjava.hardware.disk.SimpleDisk;
import org.rcosjava.hardware.disk.msdos.MSDOSFloppy;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.postoffices.MessageHandler;
import org.rcosjava.software.disk.DiskRequest;
import org.rcosjava.software.util.IndexedList;
import org.rcosjava.software.filesystem.AllocationTableException;
import org.rcosjava.software.filesystem.DirectoryException;
import org.rcosjava.software.filesystem.FileSystem;
import org.rcosjava.software.filesystem.FileSystemFile;
import org.rcosjava.software.filesystem.msdos.MSDOSFATException;
import org.rcosjava.software.filesystem.msdos.MSDOSDirectoryException;
import org.rcosjava.software.filesystem.FileSystemReturnData;
import org.rcosjava.software.util.Horario;

/**
 * Description of the Class
 *
 * @author andrew
 * @created July 27, 2003
 */
public class MSDOSFileSystem implements FileSystem
{
  /**
   * Seperator for the mount point. i.e. c:
   *
   * @author andrew
   * @since July 27, 2003
   */
  private final static String MOUNT_POINT_SEPERATOR = ":";

  /**
   * The size of the block 4K
   *
   * @author andrew
   * @since July 27, 2003
   */
  private final static int BLOCK_SIZE = 4096;

  /**
   * The total of blocks of the system
   *
   * @author andrew
   * @since July 27, 2003
   */
  private final static int TOTAL_DISK_BLOCKS = 100;

  /**
   * Total number of blocks used to hold the directory information.
   *
   * @author andrew
   * @since July 27, 2003
   */
  private final static int TOTAL_DIR_BLOCKS = 2;

  /**
   * Where to start when place the directory block
   *
   * @author andrew
   * @since July 27, 2003
   */
  private final static int DIR_BLOCK_OFFSET = 0;

  /**
   * Where to start placing the disk data.
   *
   * @author andrew
   * @since July 27, 2003
   */
  private final static int DISK_BLOCK_OFFSET = DIR_BLOCK_OFFSET + TOTAL_DIR_BLOCKS;

  /**
   * Total size in bytes to store the directory entry.
   *
   * @author andrew
   * @since July 27, 2003
   */
  private final static int DIR_ENTRY_SIZE = 32;

  /**
   * The total number of directory entries.
   *
   * @author andrew
   * @since July 27, 2003
   */
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

  /**
   * Used to convert signed 8 bit numbers (byte) to integers.
   *
   * @author andrew
   * @since July 27, 2003
   */
  private final static int SVB2I = 255;

  private IndexedList cvRequestTable;
  private HashMap cvMountTable;
  private IndexedList cvDeviceTable;
  private IndexedList cvFIDTable;

  private MSDOSFAT msdosFat;
  private MSDOSDirectory directory;
  private Disk disk;

  /**
   * Constructor for the MSDOSFileSystem object
   *
   * @param myID Identify of the MSDOSFileSystem
   * @param myPO Description of Parameter
   */
  public MSDOSFileSystem(String myID, MessageHandler myPO)
  {
    //super(myID, myPO);
    cvRequestTable = new IndexedList(100, 10);
    cvMountTable = new HashMap();
    cvDeviceTable = new IndexedList(100, 10);
    cvFIDTable = new IndexedList(100, 10);

    // File allocation table used to store items - pass in total number of disk
    // blocks.
    msdosFat = new MSDOSFAT(TOTAL_DISK_BLOCKS);

    // Directory file system.
    directory = new MSDOSDirectory();

    // The actual storage of the files.
    disk = new MSDOSFloppy(10, 10, BLOCK_SIZE);

    // Load a file image.
//    requestSystemFile();
  }

  /**
   * Constructor for the MSDOSFileSystem object
   *
   * @param newMsdosFat Description of the Parameter
   * @param newDirectory Description of the Parameter
   * @param newDisk Description of the Parameter
   */
  public MSDOSFileSystem(MSDOSFAT newMsdosFat, MSDOSDirectory newDirectory,
      Disk newDisk)
  {
    msdosFat = newMsdosFat;
    directory = newDirectory;
    disk = newDisk;
//    requestSystemFile();
  }

  // Gets the number of the first entry for the file. Note, the filename
  // should include the mountpoint as well.
  /**
   * Gets the DirectoryPosition attribute of the CPM14FileSystem object
   *
   * @param mvFilename Description of Parameter
   * @return The DirectoryPosition value
   */
  public int getDirectoryPosition(String mvFilename)
  {
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
  public int getFreeBlock(int mvDeviceNumber)
  {

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
  public int getNextDirectoryEntry(int mvDirent, int mvDeviceNumber)
  {

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
  public String getMountPoint(String mvFilename)
  {

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
  public void mount(String sMountPoint, String sDeviceName)
  {

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
  public FileSystemReturnData allocate(int iRequestID, String sFileName)
  {
    return null;
  }

  /**
   * Replys to the sender of the message 1 if at end of file 0 if not.
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData eof(int iRequestID, int iFSFileNo)
  {
    return null;
  }

  /**
   * Free's all disk structures associated with the specified file. Leaves the
   * file in the PID table though.
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData delete(int iRequestID, int iFSFileNumber)
  {
    return (new FileSystemReturnData(iRequestID, 0));
  }

  /**
   * Sets up a directory entry for the file and sets it to a 0 length file.
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData create(int iRequestID, int iFSFileNumber)
  {
    return (new FileSystemReturnData(iRequestID, 0));
  }

  public FileSystemFile create(String newNameFile, String newExtension, boolean newA,
      boolean newD, boolean newV, boolean newS, boolean newH,
      String newLastModificationDate, Horario newLastModificationHour,
      int newFirstBlock, long newSizeMSDOSFile, int newNumeberBlocksArchive)
  {
    return new MSDOSFile(newNameFile, newExtension, newA, newD, newV, newS,
        newH, newLastModificationDate, newLastModificationHour, newFirstBlock,
        newSizeMSDOSFile, newNumeberBlocksArchive);
  }

  /**
   * Closes a file and removes it from the FID table. first writes the current
   * buffer and the Dir blocks to disk.
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData close(int iRequestID, int iFSFileNumber)
  {
    return null;
  }

  /**
   * Opens the specified file for reading.
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData open(int iRequestID, int iFSFileNumber)
  {

//    return (new FileSystemReturnData(iRequestID, "", indexesFAT));
    return (new FileSystemReturnData(iRequestID, 0));
  }

  /**
   * Reads from the file if it is in the right mode.
   *
   * @param iRequestID Description of Parameter
   * @param msdosFile Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Returned Value
   * @throws AllocationTableException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public FileSystemReturnData delete(int iRequestID, FileSystemFile msdosFile,
      String path) throws AllocationTableException, DirectoryException
  {
    // PRIMEIRO Vai-se no  Directory pra saber ver onde estah gravado o arquivo
    // ver isso pelo nome, depois descobre-se pra onde o primeiro bloco aponta
    // depois na FAT, depois ler os bloquinhos
    int firstBlock = -7;
    String value = "";
    int[] indexesFAT;

    MSDOSFile tmpFile = (MSDOSFile) msdosFile;

    if (tmpFile.isA())
    {
      firstBlock = readSubdirectoryAtBlock(tmpFile, path);
      int[] indexes = msdosFat.getEntriesFileFromFAT(firstBlock);

      // ler e depois deleta os dados do bloco
      value = deleteDataFromBlock(indexes);
      msdosFat.removeEntriesOfFAT(indexes);

      // deleta a entrada de diretorio do (Subdiretorio dentro do bloco)
      deleteEntryFromSubdirectoryAtBlock(tmpFile, path);
      indexesFAT = indexes;
    }
    else
    {
      System.out.println("entrei no else");
      firstBlock = readSubdirectoryAtBlock(tmpFile, path);
      System.out.println("firstBlock: " + firstBlock);

      if (firstBlock != -1)
      {
        MSDOSDirectory directory = mountDirectoryFromBlock(firstBlock);
        value = directory.dir();
//          directory.removeFile(msdosFile.getNameFile());
      }
      else
      {
        value = this.directory.dir();
//          this.directory.removeFile(msdosFile.getNameFile());
      }
      indexesFAT = new int[1];
      System.out.println("firstBlock do msdosFile: " + tmpFile.getFirstBlock());
      indexesFAT[0] = firstBlock;
//        indexesFAT[0] = msdosFile.getFirstBlock();

      // ler e depois deleta os dados do bloco
      msdosFat.removeEntryFAT(firstBlock);
      deleteEntryFromSubdirectoryAtBlock(tmpFile, path);
    }

//    return (new FileSystemData(iRequestID, value, indexesFAT));
    return (new FileSystemReturnData(iRequestID, 0));
  }

  /**
   * Description of the Method
   *
   * @param indexes Description of the Parameter
   * @return Description of the Return Value
   */
  public String deleteDataFromBlock(int[] indexes)
  {
    StringBuffer block = null;
    byte[] buffer;

    for (int i = (indexes.length - 1); i >= 0; i--)
    {
      buffer = disk.readSector(indexes[i]);
      block.append(buffer);

      //primeiro ler depois remove o conteudo do disco
      disk.writeSector(indexes[i], new byte[BLOCK_SIZE]);
    }

    return block.toString();
  }

  /**
   * Description of the Method
   *
   * @param msdosFile Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Return Value
   * @throws MSDOSFATException Description of the Exception
   * @throws MSDOSDirectoryException Description of the Exception
   * @throws AllocationTableException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public int deleteEntryFromSubdirectoryAtBlock(MSDOSFile msdosFile, String path)
       throws AllocationTableException, DirectoryException
  {
    boolean isAbsolutePath = path.startsWith("C:/");
    MSDOSFile msdosFileAux = null;
    int firstBlock = -1;

// Ex: C:/dany/x
    if (isAbsolutePath)
    {
      System.out.println("path.substring(2, path.length()): " + path.substring(3, path.length()));
      StringTokenizer st = new StringTokenizer(path.substring(3, path.length()), "/");
      System.out.println("st " + st);
      int countTokens = st.countTokens();
      if (countTokens == 0)
      {
        System.out.println("lendo da raiz os arquivos");
        if (!msdosFile.getNameFile().equals(""))
        {
          msdosFileAux = directory.readFile(msdosFile.getNameFile());
          firstBlock = msdosFileAux.getFirstBlock();
          msdosFile.setFirstBlock(msdosFileAux.getFirstBlock());
// deletando o arquivo
          directory.removeFile(msdosFile.getNameFile());
        }
        else
        {
          firstBlock = -1;
        }

      }
      else
      {
        // vai no Diretorio raiz procurar o caminho  e depois vai na FAT
        // procurar o lugar dele, depois no disco
        String aux = null;
        int initialBlock = -1;
        int[] listEntries;
        aux = st.nextToken();
        System.out.println("nome dir " + aux);
        msdosFileAux = directory.readFile(aux);
        initialBlock = msdosFileAux.getFirstBlock();
        listEntries = msdosFat.getEntriesFileFromFAT(initialBlock);
        int indice = listEntries[0];
        MSDOSFile msdosFile2 = null;

        if (listEntries.length == 1)
        {
          MSDOSDirectory subDirectory = null;
          String teste = "";
          for (int i = 0; i < (countTokens - 1); i++)
          {
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
        }
        else
        {
          throw new MSDOSFATException("Não há o diretório " + aux + " no sistema de arquivo");
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
   * @param msdosFile Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Returned Value
   * @throws AllocationTableException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public FileSystemReturnData read(int iRequestID, FileSystemFile msdosFile,
      String path) throws AllocationTableException, DirectoryException
  {
    // PRIMEIRO Vai-se no  Directory pra saber ver onde estah gravado o arquivo
    // ver isso pelo nome, depois descobre-se pra onde o primeiro bloco aponta
    // depois na FAT, depois ler os bloquinhos

    int firstBlock = -7;
    String value = "";
    int[] indexesFAT;

    MSDOSFile tmpFile = (MSDOSFile) msdosFile;

    if (tmpFile.isA())
    {
      firstBlock = readSubdirectoryAtBlock(tmpFile, path);
      int[] indexes = msdosFat.getEntriesFileFromFAT(firstBlock);
      value = readDataFromBlock(indexes);
      indexesFAT = indexes;
    }
    else
    {
      System.out.println("entrei no else");
      firstBlock = readSubdirectoryAtBlock(tmpFile, path);
      System.out.println("firstBlock: " + firstBlock);
      if (firstBlock != -1)
      {
        MSDOSDirectory directory = mountDirectoryFromBlock(firstBlock);
        value = directory.dir();
      }
      else
      {
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
    return (new FileSystemReturnData(iRequestID, 0));
//    return (new FileSystemReturnData(iRequestID, value, indexesFAT));
  }

  /**
   * Description of the Method
   *
   * @param indexes Description of the Parameter
   * @return Description of the Return Value
   */
  public String readDataFromBlock(int[] indexes)
  {
    StringBuffer block = null;
    byte[] buffer;

    for (int i = (indexes.length - 1); i >= 0; i--)
    {
      buffer = disk.readSector(indexes[i]);
      block.append(buffer);
    }

    return block.toString();
  }

  /**
   * If the mode is correct, this starts or contines writing a file.
   *
   * @param iRequestID Description of Parameter
   * @param msdosFile Description of the Parameter
   * @param data Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Returned Value
   * @throws AllocationTableException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public FileSystemReturnData write(int iRequestID, FileSystemFile msdosFile,
      String data, String path) throws AllocationTableException,
      DirectoryException
  {
    // PRIMEIRO: Tratamos o relacionamento da escrita do dado na FAT

    // Checa a quantidade de entradas Livres na FAT
    int sizeFreeFAT = msdosFat.getSizeFreeFAT();
    int newPosicionFATAux1 = -1;
    int[] indexesFAT;

    // Pra garantir que tem entrada livre na FAT pra
    if (sizeFreeFAT != 0)
    {
      MSDOSFile tmpFile = (MSDOSFile) msdosFile;

      // Eh um arquivo
      if (tmpFile.isA())
      {
        System.out.println("ARQUIVO: " + tmpFile.getNameFile());

        // Pegar entrada de bloco disponivel pra colocar os dados
        // do arquivo ou diretorio passado
        byte[] valueBytes = data.getBytes();

        int quantityBytes = valueBytes.length;

        int numberEntryFAT = (new Double(Math.ceil((double) quantityBytes / BLOCK_SIZE))).intValue();

        System.out.println("numberEntryFAT: " + numberEntryFAT);

        if (numberEntryFAT <= sizeFreeFAT)
        {
          newPosicionFATAux1 = msdosFat.getNewEntryFAT();

          // Seta o primeiro bloco do arquivo que veio da fat
          msdosFile.setFirstBlock(newPosicionFATAux1);

          // Seta as entradas correspondentes ao bloco
          // que vao ser ocupadas na FAT
          setEntriesAtFAT(tmpFile, numberEntryFAT);

          //Grava data at blocks and seta o caminho
          // dos varios blocos ocupados pelo arquivo
          writeDataAtBlock(tmpFile, data);

          // Grava no diretorio raiz ou em algum bloco
          // do disco (que representa um subdiretorio)
          // o msdosFile
          writeSubdirectoryAtBlock(tmpFile, path);

          msdosFile.setNumeberBlocksArchive(numberEntryFAT);

          // nao precisa desse else eh soh pra garantir
        }
        else
        {
          throw new MSDOSFATException("FAT is full");
        }

        indexesFAT = msdosFat.getEntriesFileFromFAT(newPosicionFATAux1);
      }
      else
      {
        // adicionar um novo diretorio
        newPosicionFATAux1 = msdosFat.getNewEntryFAT();
        msdosFile.setFirstBlock(newPosicionFATAux1);
        msdosFat.addEntriesFAT(newPosicionFATAux1, -2);

        // coloca no bloco um new de diretorio pra
        // simbolizar q ele eh um diretorio e nao um pedaco de
        // arquivo
        writeNewDirectoryAtBlock(newPosicionFATAux1);

        // coloca soh uma entrada onde deve estar o arquivo
        writeSubdirectoryAtBlock(tmpFile, path);

        indexesFAT = new int[1];
        indexesFAT[0] = newPosicionFATAux1;
        msdosFile.setNumeberBlocksArchive(1);
      }
      // nao precisa desse else eh soh pra garantir
    }
    else
    {
      throw new MSDOSFATException("FAT is full");
    }

//     int[] indexesFAT = msdosFat.getEntriesFileFromFAT(newPosicionFATAux1);
//    return new FileSystemData(iRequestID, "-1", indexesFAT);
    return new FileSystemReturnData(iRequestID, -1);
  }


  /**
   * Description of the Method
   *
   * @param newPosicionFATAux Description of the Parameter
   * @throws MSDOSDirectoryException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public void writeNewDirectoryAtBlock(int newPosicionFATAux)
       throws DirectoryException
  {
    try
    {
      MSDOSDirectory directory = new MSDOSDirectory();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(baos);
      out.writeObject(directory);

      byte[] byteDirectory = baos.toByteArray();
      disk.writeSector(newPosicionFATAux, byteDirectory);
    }
    catch (IOException ioex)
    {
      throw new MSDOSDirectoryException("Error recording new directory");
    }
  }


  /**
   * Seta as entradas correspondentes ao bloco que vao ser ocupadas na FAT
   *
   * @param msdosFile The new entriesAtFAT value
   * @param numberEntryFAT The new entriesAtFAT value
   * @throws MSDOSFATException Description of the Exception
   * @throws AllocationTableException Description of the Exception
   */
  public void setEntriesAtFAT(MSDOSFile msdosFile, int numberEntryFAT)
       throws AllocationTableException
  {
    int newPosicionFATAux1 = -1;
    int newPosicionFATAux2 = -1;
    int i = 0;

    newPosicionFATAux1 = msdosFile.getFirstBlock();

    System.out.println("setEntriesAtFAT: " + msdosFile.getNameFile());

    System.out.println("newPosicionFATAux1: " + newPosicionFATAux1);
    System.out.println("numberEntryFAT: " + numberEntryFAT);
    for (i = 0; i < numberEntryFAT - 1; i++)
    {

      System.out.println("entrei no for!!!");
      newPosicionFATAux2 = msdosFat.getNewEntryFAT();
      msdosFat.addEntriesFAT(newPosicionFATAux1, newPosicionFATAux2);
      newPosicionFATAux1 = newPosicionFATAux2;
    }

    // Verifica se eh o ultimo numero a ser adicionado na FAT, para
    // aponta-lo pra -2
//     if(i==(numberEntryFAT-1)){
    System.out.println("newPosicionFATAux1: " + newPosicionFATAux1);
    msdosFat.addEntriesFAT(newPosicionFATAux1, -2);
//     }
  }

  /**
   * Write Datas at Blocks, have known the entries at FAT
   *
   * @param msdosFile Description of the Parameter
   * @param data Description of the Parameter
   * @throws MSDOSFATException Description of the Exception
   * @throws AllocationTableException Description of the Exception
   */
  public void writeDataAtBlock(MSDOSFile msdosFile, String data)
       throws AllocationTableException
  {
    int newPosicionFATAux1 = -1;
    int i = 0;
    int[] entriesAtFAT;

    System.out.println("writeDataAtBlock: " + msdosFile.getNameFile());
    byte[] valueBytes = data.getBytes();

    newPosicionFATAux1 = msdosFile.getFirstBlock();
    int[] indexes = msdosFat.getEntriesFileFromFAT(newPosicionFATAux1);

    for (i = 0; i < indexes.length - 1; i++)
    {
      System.out.println("entrei no for222");
      // Adiciona no disco realmente os dados do arquivo
      int aux = -1;
      if (i == 0)
      {
        aux = 0;
      }
      else
      {
        aux = (i * BLOCK_SIZE) - 1;
      }
      byte[] auxDst = new byte[BLOCK_SIZE];
      System.arraycopy(valueBytes, aux, auxDst, 0, BLOCK_SIZE);
      disk.writeSector(newPosicionFATAux1, auxDst);
    }

    // Verifica se eh o ultimo numero pra colocar no disco nao mais pra
    // preencher o bloco todo e sim apenas ateh o fim do arquivo
//     if(i==(numberEntryFAT-1)){
    // mandando para o disco
    byte[] auxDst2 = new byte[BLOCK_SIZE];
    int restante = valueBytes.length - (i * BLOCK_SIZE);
    System.arraycopy(valueBytes, i * BLOCK_SIZE, auxDst2, 0, restante);
    System.out.println("auxDst2: " + auxDst2);
    System.out.println("newPosicionFATAux1: " + newPosicionFATAux1);
    disk.writeSector(newPosicionFATAux1, auxDst2);
//     }

  }

  /**
   * Description of the Method
   *
   * @param msdosFile Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Return Value
   * @throws MSDOSFATException Description of the Exception
   * @throws MSDOSDirectoryException Description of the Exception
   * @throws AllocationTableException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public int readSubdirectoryAtBlock(MSDOSFile msdosFile, String path)
       throws AllocationTableException, DirectoryException
  {

    boolean isAbsolutePath = path.startsWith("C:/");
    MSDOSFile msdosFileAux = null;
    int firstBlock = -1;

    // Ex: C:/dany/x
    if (isAbsolutePath)
    {
      System.out.println("path.substring(2, path.length()): " + path.substring(3, path.length()));
      StringTokenizer st = new StringTokenizer(path.substring(3, path.length()), "/");
      System.out.println("st " + st);
      int countTokens = st.countTokens();
      if (countTokens == 0)
      {
        System.out.println("lendo da raiz os arquivos");
        if (!msdosFile.getNameFile().equals(""))
        {
          msdosFileAux = directory.readFile(msdosFile.getNameFile());
          firstBlock = msdosFileAux.getFirstBlock();
          msdosFile.setFirstBlock(msdosFileAux.getFirstBlock());
        }
        else
        {
          firstBlock = -1;
        }

      }
      else
      {
        // vai no Diretorio raiz procurar o caminho  e depois vai na FAT
        // procurar o lugar dele, depois no disco
        String aux = null;
        int initialBlock = -1;
        int[] listEntries;
        aux = st.nextToken();
        System.out.println("nome dir " + aux);
        msdosFileAux = directory.readFile(aux);
        initialBlock = msdosFileAux.getFirstBlock();
        listEntries = msdosFat.getEntriesFileFromFAT(initialBlock);
        int indice = listEntries[0];
        MSDOSFile msdosFile2 = null;

        if (listEntries.length == 1)
        {
          // ou ==

          MSDOSDirectory subDirectory = null;
          String teste = "";
          for (int i = 0; i < (countTokens - 1); i++)
          {

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


        }
        else
        {
          throw new MSDOSFATException("Não há o diretório " + aux + " no sistema de arquivo");
        }

      }
    }
    return firstBlock;
  }

  /**
   * Vai ter acesso ao path e assim vai na fat saber se vai escrever no Direcoty
   * root ou num bloco apontado pela FAT que representa na verdade um
   * subdiretorio PS: Vemos aqui que tanto o arquivo como um subdiretorio sao
   * gravados da mesma forma no bloco, porem os dados de um arquivo sao gravados
   * de forma diferente e isso sera feito em outro metodo
   *
   * @param msdosFile Description of the Parameter
   * @param path Description of the Parameter
   * @throws AllocationTableException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public void writeSubdirectoryAtBlock(MSDOSFile msdosFile, String path)
       throws AllocationTableException, DirectoryException
  {

    boolean isAbsolutePath = path.startsWith("C:/");

    System.out.println("writeSubdirectoryAtBlock: " + msdosFile.getNameFile());
    // Ex: C:/dany/x
    if (isAbsolutePath)
    {
      System.out.println("path.substring(2, path.length()): " + path.substring(3, path.length()));
      StringTokenizer st = new StringTokenizer(path.substring(3, path.length()), "/");
      System.out.println("st " + st);
      int countTokens = st.countTokens();
      if (countTokens == 0)
      {
        System.out.println("adicionando na raiz o arquivo");
        directory.addNewFile(msdosFile);

      }
      else
      {
        // vai no Diretorio raiz procurar o caminho  e depois vai na FAT
        // procurar o lugar dele, depois no disco pra apenas setar o bloco
        // que na verdade eh um subdiretorio com uma nova entrada de arquivo
        String aux = null;
        MSDOSFile msdosFileAux = null;
        int initialBlock = -1;
        int[] listEntries;
        aux = st.nextToken();
        msdosFileAux = directory.readFile(aux);
        initialBlock = msdosFileAux.getFirstBlock();
        System.out.println("initialBlock: " + initialBlock);
        listEntries = msdosFat.getEntriesFileFromFAT(initialBlock);
        int indice = listEntries[0];

        System.out.println("indice: " + indice);
        if (listEntries.length == 1)
        {
          // ou ==

          for (int i = 0; i < (countTokens - 1); i++)
          {

//             // ve se chegou no diretorio onde sera gravado o novo
//             // diretorio
//             if(i==(st.countTokens()-2)){
//               recordAtBlock(indice, msdosFile);
//             }else{
            // vai procurar o proximo subdiretorio nos blocos
            MSDOSDirectory subDirectory = mountDirectoryFromBlock(indice);
            String teste = st.nextToken();
            MSDOSFile msdosFile2 = subDirectory.readFile(teste);
            indice = msdosFile2.getFirstBlock();
//             }
          }

          // ve se chegou no diretorio onde sera gravado o novo
          // diretorio
          recordAtBlock(indice, msdosFile);

        }
        else
        {
          throw new MSDOSFATException("Não há o diretório " + aux + " no sistema de arquivo");
        }

      }
    }

  }

  /**
   * Description of the Method
   *
   * @param address Description of the Parameter
   * @param msdosFile Description of the Parameter
   * @throws MSDOSDirectoryException Description of the Exception
   * @throws DirectoryException Description of the Exception
   */
  public void recordAtBlock(int address, MSDOSFile msdosFile)
       throws DirectoryException
  {
    try
    {
      MSDOSDirectory subDirectory = null;
//       boolean flag = false;
      ByteArrayInputStream byteArrayInputStream = null;
      ObjectInputStream byteInBlock = null;

      byte[] subDirectoryAux = disk.readSector(address);
//       if (block != null){
//         flag = true;
      byteArrayInputStream = new ByteArrayInputStream(subDirectoryAux);
      byteInBlock = new ObjectInputStream(byteArrayInputStream);

      subDirectory = (MSDOSDirectory) byteInBlock.readObject();
//       }else{
//         subDirectory = new Directory();
//       }

      subDirectory.addNewFile(msdosFile);

      ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
      ObjectOutputStream byteOutBlock = new ObjectOutputStream(byteArray);
      byteOutBlock.writeObject(subDirectory);

      byteOutBlock.flush();

//       block.setData(byteArray.toByteArray());
//       if(flag){
      byteArrayInputStream.close();
      byteInBlock.close();
//       }
      byteArray.close();
      byteOutBlock.close();

    }
    catch (StreamCorruptedException scex)
    {

    }
    catch (ClassNotFoundException cnfex)
    {

    }
    catch (IOException ioex)
    {

    }
  }

  /**
   * Description of the Method
   *
   * @param address Description of the Parameter
   * @return Description of the Return Value
   */
  public MSDOSDirectory mountDirectoryFromBlock(int address)
  {
    MSDOSDirectory subDirectory = null;
    try
    {
      byte[] subDirectoryAux = disk.readSector(address);
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(subDirectoryAux);

      ObjectInputStream byteInBlock = new ObjectInputStream(byteArrayInputStream);
      subDirectory = (MSDOSDirectory) byteInBlock.readObject();

      byteArrayInputStream.close();
      byteInBlock.close();
    }
    catch (ClassNotFoundException cnfex)
    {
      System.out.println("ClassNotFoundException " + cnfex);
    }
    catch (IOException ioex)
    {
      System.out.println("IOException " + ioex);
    }

    return subDirectory;
  }

  /**
   * Description of the Method
   */
  public void recordSystemFile()
  {
    try
    {
      FileOutputStream fileOutputStream = new FileOutputStream("fileSystem.dat");
      ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
      out.writeObject(msdosFat);
      out.writeObject(directory);
      out.writeObject(disk);

      fileOutputStream.close();
      out.close();

    }
    catch (IOException ioex)
    {
      System.out.println("ERRO no recorde: " + ioex.getMessage());
      ioex.printStackTrace();
    }
  }

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public FileSystem requestSystemFile()
  {
    MSDOSFileSystem newMSDOSFileSystem = null;
    try
    {
      File file = new File("fileSystem.dat");

      if (file.exists())
      {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        msdosFat = (MSDOSFAT) in.readObject();
        directory = (MSDOSDirectory) in.readObject();
        disk = (Disk) in.readObject();

        this.setMsdosFat(msdosFat);
        this.setDirectory(directory);
        this.setDisk(disk);

        in.close();
      }
    }
    catch (ClassNotFoundException ex)
    {
      System.out.println("ERRO em requestSystemFile: " + ex.getMessage());
      ex.printStackTrace();
    }
    catch (FileNotFoundException ex)
    {
      System.out.println("ERRO em requestSystemFile: " + ex.getMessage());
      ex.printStackTrace();
    }
    catch (OptionalDataException ex)
    {
      System.out.println("ERRO em requestSystemFile: " + ex.getMessage());
      ex.printStackTrace();
    }
    catch (IOException ex)
    {
      System.out.println("ERRO em requestSystemFile: " + ex.getMessage());
      ex.printStackTrace();
    }
    return newMSDOSFileSystem;
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
  public void dumpToScreen(String Operation, int item1, int item2)
  {

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
  private void handleReturnMessages(OSMessageAdapter mvTheMessage)
  {

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
  private byte[] convertFilename(String mvFilename)
  {

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
      int FSMRequestID, int RequestData, int block, byte[] data)
  {

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
    /*
     *  Message mvReturnMessage = new Message(id, mvDestination,  "RETURNVALUE",
     *  mvToReturn);
     *  SendMessage ( mvReturnMessage );
     */
  }

  /**
   * Sets the directory attribute of the MSDOSFileSystem object
   *
   * @param directory The new directory value
   */
  public void setDirectory(MSDOSDirectory directory)
  {
    this.directory = directory;
  }

  /**
   * Sets the disk attribute of the MSDOSFileSystem object
   *
   * @param disk The new disk value
   */
  public void setDisk(Disk disk)
  {
    this.disk = disk;
  }

  /**
   * Sets the msdosFat attribute of the MSDOSFileSystem object
   *
   * @param msdosFat The new msdosFat value
   */
  public void setMsdosFat(MSDOSFAT msdosFat)
  {
    this.msdosFat = msdosFat;
  }

  /**
   * If the mode is correct, this starts or contines writing a file.
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData write(int iRequestID, int iFSFileNo)
  {
    return null;
  }

  /**
   * Reads from the file if it is in the right mode.
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData read(int iRequestID, int iFSFileNumber)
  {
    return null;
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
  public String dumpDirectoryEntry(int item1, int item2)
  {
    return null;
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
  public String dumpBuffer(int item1)
  {
    return null;
  }
}
