package org.rcosjava.software.filesystem;

import java.util.HashMap;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.software.util.IndexedList;

//dany 18/09/02
import org.rcosjava.software.filesystem.msdos.MSDOSFileSystem;
import org.rcosjava.software.filesystem.msdos.MSDOSFile;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSFATException;
import org.rcosjava.software.filesystem.msdos.exception.MSDOSDirectoryException;
import org.rcosjava.software.util.HorarioInvalidoException;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class FileSystemManager extends OSMessageHandler
{
  // indexed on Mount Point

  //public final static String F_REGISTER = "F_REGISTER";
  //public final static String F_MOUNT = "F_MOUNT";
  /**
   * Description of the Field
   */
  public final static String F_ALLOC = "F_ALLOC";
  /**
   * Description of the Field
   */
  public final static String F_OPEN = "F_OPEN";
  /**
   * Description of the Field
   */
  public final static String F_CLOSE = "F_CLOSE";
  /**
   * Description of the Field
   */
  public final static String F_EOF = "F_EOF";
  /**
   * Description of the Field
   */
  public final static String F_CREATE = "F_CREATE";
  /**
   * Description of the Field
   */
  public final static String F_DEL = "F_DEL";
  /**
   * Description of the Field
   */
  public final static String F_READ = "F_READ";
  /**
   * Description of the Field
   */
  public final static String F_WRITE = "F_WRITE";
  /**
   * Description of the Field
   */
  private final static String MESSENGING_ID = "FileSystemManager";
  /**
   * Description of the Field
   */
  private IndexedList cvFIDTable;
  /**
   * Description of the Field
   */
  private IndexedList cvRequestTable;
  /**
   * Description of the Field
   */
  private HashMap cvFileSystems;
  // Indexed on FStype
  /**
   * Description of the Field
   */
  private HashMap cvMountTable;

  private FileSystemInterface fileSystemInterface;

  /**
   * Constructor for the FileSystemManager object
   *
   * @param myPO Description of Parameter
   */
  public FileSystemManager(OSOffice myPO)
  {
    super(MESSENGING_ID, myPO);
    cvFIDTable = new IndexedList(100, 10);
    cvRequestTable = new IndexedList(100, 10);
    cvFileSystems = new HashMap(20);
    cvMountTable = new HashMap(20);

    //dany 18/09/02
    fileSystemInterface = new MSDOSFileSystem("1", myPO);
  }


  public FileSystemInterface getFileSystemInterface(){
     return this.fileSystemInterface;
  }

  // Returns a the File System that a request should be sent to. This
  // essentially  breaks down the filename and checks the mount point
  // table. If an entry is found, it returns the name
  // of the filesystem to handle the request.
  /**
   * Gets the FSForFile attribute of the FileSystemManager object
   *
   * @param sFileName Description of Parameter
   * @return The FSForFile value
   */
  public FileSystem getFSForFile(String sFileName){

    // extract mount point from file name.
    int mvSearchIndex = sFileName.indexOf(":");

    if (mvSearchIndex == -1){
      return null;
    }

    String mvMountPoint = sFileName.substring(0, mvSearchIndex);
    // Get the file system name based on mount point name
    String mvTheFSName = (String) cvMountTable.get(mvMountPoint);
    // Get file system type based on the name
    FileSystem fsFileSystem = (FileSystem) cvFileSystems.get(mvTheFSName);

    return fsFileSystem;
  }

  // Registers and dynamically creates a file system with the specified FS type.
  // File System type is the class name of the file system.
  // e.g. FileSystem.CPM14.CPM14FileSystem
  // File System name is the unique identifier of the file system.  e.g. CPM14.
  // One file system name per file system type.
  /**
   * Description of the Method
   *
   * @param sFSName Description of Parameter
   * @param sFSType Description of Parameter
   */
  public void register(String sFSName, String sFSType){

    if (!cvFileSystems.containsKey(sFSName)){
      try
      {
        FileSystem fsNewFileSystem = (FileSystem) Class.forName(sFSType).newInstance();

        cvFileSystems.put(sFSName, fsNewFileSystem);
      }
      catch (Exception e)
      {
        System.err.println("Error registering file system: " + e);
        e.printStackTrace();
      }
    }
    else{
      //Report some error message (already exists).
    }
  }

  // Allocats a slot in the mount table and send the mount request.
  // e.g. sFSName - CPM14, sMountPoint - C (drive), sDeviceName - cdrom/Disk1/Orion/Infinity/etc
  /**
   * Description of the Method
   *
   * @param sFSName Description of Parameter
   * @param sMountPoint Description of Parameter
   * @param sDeviceName Description of Parameter
   */
  public void mount(String sFSName, String sMountPoint, String sDeviceName)
  {
    FileSystem fsExistingFS = (FileSystem) cvFileSystems.get(sFSName);

    if (fsExistingFS == null)
    {
      //No filesystem registered under that name
    }
    else
    {
      // Check if that point is already mounted.
      if (!cvMountTable.containsKey(sMountPoint))
      {
        cvMountTable.put(sMountPoint, sFSName);
        fsExistingFS.mount(sMountPoint, sDeviceName);
      }
      else
      {
        //Mount point already in use
      }
    }
  }

  /**
   * Description of the Method
   *
   * @param drdNewRequest Description of Parameter
   */
  public void allocate(DiskRequestData drdNewRequest)
  {
    FileSystem fsExistingFS = getFSForFile(drdNewRequest.getFileName());

    if (fsExistingFS == null)
    {
      replyError(drdNewRequest.getProcessId());
    }
    else
    {
      int iRequestID = addRequest(drdNewRequest);

      if (iRequestID == -1)
      {
        replyError(drdNewRequest.getProcessId());
        return;
      }
      else
      {
        fsExistingFS.allocate(iRequestID, drdNewRequest.getFileName());
      }
    }
  }

  /**
   * Description of the Method
   *
   * @param drdNewRequest Description of Parameter
   */
  public void handleRequest(DiskRequestData drdNewRequest)
  {
    FileSystem fsExistingFS = getFSForFile(drdNewRequest.getFileName());

    if (fsExistingFS == null)
    {
      replyError(drdNewRequest.getProcessId());
    }
    else
    {
      if (addRequest(drdNewRequest) == -1)
      {
        replyError(drdNewRequest.getProcessId());
        return;
      }
      else
      {
        drdNewRequest.doRequest(fsExistingFS, 1);
      }
    }
  }

  // Handles the return value from requests sent to the file systems.
  /**
   * Description of the Method
   *
   * @param mvTheMessage Description of Parameter
   */
  void handleReturnValue(OSMessageAdapter mvTheMessage)
  {
    FileSystemReturnData mvReturnData =
        (FileSystemReturnData) mvTheMessage.getBody();
    RequestTableData mvRequestData =
        (RequestTableData) cvRequestTable.getItem(mvReturnData.getRequestID());

    if (mvRequestData == null)
    {
      return;
    }

    int mvToReturn;

    // Fill out return message depending upon request.
    if (mvRequestData.getType().equalsIgnoreCase("F_ALLOC"))
    {
      if (mvReturnData.getReturnValue() >= 0)
      {
        FIDTableData mvNewFID = new FIDTableData();

        mvNewFID.setProcessId(mvRequestData.getProcessId());
        mvNewFID.setFileSystemId(mvTheMessage.getSource().getId());
        mvNewFID.setFileNo(mvReturnData.getReturnValue());
        mvToReturn = cvFIDTable.add(mvNewFID);
      }
      else
      {
        mvToReturn = -1;
      }
    }
    else if (mvRequestData.getType().equalsIgnoreCase("F_CLOSE"))
    {
      if (mvReturnData.getReturnValue() >= 0)
      {

        RequestTableData mvOriginalRequest =
            (RequestTableData) (cvRequestTable.getItem(mvReturnData.getRequestID()));

        //FileSystemReturnData mvOriginalRequestData =
        //          (FileMessageData)mvOriginalRequest.getData();

        //int mvFID = mvOriginalRequestData.getFileId();
        int mvFID = 0;

        cvFIDTable.remove(mvFID);
        mvToReturn = 0;
      }
      else
      {
        mvToReturn = -1;
      }
    }
    else
    {
      mvToReturn = mvReturnData.getReturnValue();
    }
    reply(mvRequestData.getProcessId(), mvToReturn);

    // Remove request from queue
    int mvCheck = cvRequestTable.remove(mvReturnData.getRequestID());
  }

  // Returns the Files entry stored in the
  /**
   * Gets the TableData attribute of the FileSystemManager object
   *
   * @param drdNewRequest Description of Parameter
   * @return The TableData value
   */
  private FIDTableData getTableData(DiskRequestData drdNewRequest)
  {
    // Get FID table entry
    FIDTableData mvFileData = (FIDTableData) cvFIDTable.getItem(drdNewRequest.getFileId());

    if (mvFileData == null)
    {
      replyError(drdNewRequest.getProcessId());
      return null;
    }
    // Check Matching PID's
    if (mvFileData.getProcessId() != drdNewRequest.getProcessId())
    {
      replyError(drdNewRequest.getProcessId());
      return null;
    }
    return mvFileData;
  }

  // Using Reply, send the ProcessScheduler an error Message.
  /**
   * Description of the Method
   *
   * @param mvPID Description of Parameter
   */
  private void replyError(int mvPID)
  {
    reply(mvPID, -1);
  }

  // Sends a RETURNVALUE message to the process scheduler with the specified return value.
  /**
   * Description of the Method
   *
   * @param mvPID Description of Parameter
   * @param mvRetVal Description of Parameter
   */
  private void reply(int mvPID, int mvRetVal)
  {
    FileSystemReturnData mvToReturn = new FileSystemReturnData(mvPID, mvRetVal);
    //MessageAdapter mvReplyMessage = new MessageAdapter(getID(),
    //  "ProcessScheduler", "SysCallReturnValue", mvToReturn);
    //sendMessage(mvReplyMessage);
  }

  /**
   * Adds a feature to the Request attribute of the FileSystemManager object
   *
   * @param drdNewRequest The feature to be added to the Request attribute
   * @return Description of the Returned Value
   */
  private int addRequest(DiskRequestData drdNewRequest){
    //RequestTableData rtdNewEntry = new RequestTableData(drdNewRequest);
    //return(cvRequestTable.add(rtdNewEntry));
    return 0;
  }

  public void mount(String mvNewMountPoint, String mvNewDeviceName){
     fileSystemInterface.mount( mvNewMountPoint, mvNewDeviceName);
  }

  public FileSystemData allocate(int iRequestID, String sFileName){
     return fileSystemInterface.allocate( iRequestID, sFileName);
  }

  public FileSystemData eof(int iRequestID, int iFSFileNo){
     return fileSystemInterface.eof( iRequestID, iFSFileNo);
  }

  public FileSystemData delete(int iRequestID, int iFSFileNo){
     return fileSystemInterface.delete( iRequestID, iFSFileNo);
  }

  public FileSystemData create(int iRequestID, int iFSFileNo){
     return fileSystemInterface.create(iRequestID, iFSFileNo);
  }

  public FileSystemData close(int iRequestID, int iFSFileNo){
     return fileSystemInterface.close(iRequestID, iFSFileNo);
  }

  public FileSystemData open(int iRequestID, int iFSFileNo){
     return fileSystemInterface.open(iRequestID, iFSFileNo);
  }

  public FileSystemData read(int iRequestID,
                              MSDOSFile msdosFile,
                              String path) throws MSDOSFATException, MSDOSDirectoryException{
     return fileSystemInterface.read(iRequestID, msdosFile, path);
  }

  public FileSystemData write(int iRequestID,
                              MSDOSFile msdosFile,
                              String data,
                              String path) throws MSDOSFATException, MSDOSDirectoryException{
     return fileSystemInterface.write(iRequestID, msdosFile, data, path);
  }

  public FileSystemData delete(int iRequestID,
                              MSDOSFile msdosFile,
                              String path) throws MSDOSFATException, MSDOSDirectoryException{
     return fileSystemInterface.delete(iRequestID, msdosFile, path);
  }

  public void recordeSystemFile(){
     fileSystemInterface.recordeSystemFile();
  }

  public void requestSystemFile(){
     fileSystemInterface.requestSystemFile();
  }

  public int getDirectoryPosition(String mvFilename){
     return fileSystemInterface.getDirectoryPosition(mvFilename);
  }

  public int getFreeEntry(int mvDeviceNumber){
     return fileSystemInterface.getFreeEntry(mvDeviceNumber);
  }

  public boolean diskFull(int mvDeviceNumber){
     return fileSystemInterface.diskFull(mvDeviceNumber);
  }

  public int getFreeBlock(int mvDeviceNumber){
     return fileSystemInterface.getFreeBlock(mvDeviceNumber);
  }

  public int getNextDirectoryEntry(int mvDirEntry, int mvDeviceNumber){
     return fileSystemInterface.getNextDirectoryEntry(mvDirEntry, mvDeviceNumber);
  }

  public void deallocateEntry(int mvEntryNumber, int mvDeviceNumber){
    fileSystemInterface.deallocateEntry(mvEntryNumber, mvDeviceNumber);
  }

  public String getMountPoint(String mvFilename){
     return fileSystemInterface.getMountPoint(mvFilename);
  }
}
