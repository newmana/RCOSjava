package org.rcosjava.software.filesystem;

/**
 * An interface in which all file system implementations must implement. This
 * includes allocating, deallocating, deleting and writing.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 26/07/2003 Combined Danielly's FileSystemInterface with this one. </DD>
 * </DT>
 * <P>
 * @author Brett Carter
 * @author Andrew Newman
 * @author Danielly Cruz
 * @created 28 April 2002
 */
public interface FileSystem
{
  /**
   * Description of the Method
   *
   * @param mvNewMountPoint Description of Parameter
   * @param mvNewDeviceName Description of Parameter
   */
  public void mount(String mvNewMountPoint, String mvNewDeviceName);

  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param sFileName Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData allocate(int iRequestID, String sFileName);

  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData eof(int iRequestID, int iFSFileNo);

  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData delete(int iRequestID, int iFSFileNo);

  /**
   * Delete file.
   *
   * @param iRequestID Description of the Parameter
   * @param msdosFile Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Return Value
   * @throws MSDOSFATException Description of the Exception
   * @throws MSDOSDirectoryException Description of the Exception
   */
  public FileSystemReturnData delete(int iRequestID, FileSystemFile file,
      String path) throws AllocationTableException, DirectoryException;

  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData create(int iRequestID, int iFSFileNo);

  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData close(int iRequestID, int iFSFileNo);

  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData open(int iRequestID, int iFSFileNo);

  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData read(int iRequestID, int iFSFileNo);

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
  public FileSystemReturnData read(int iRequestID, FileSystemFile file,
      String path) throws AllocationTableException, DirectoryException;

  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param iFSFileNo Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData write(int iRequestID, int iFSFileNo);

  /**
   * Description of the Method
   *
   * @param iRequestID Description of Parameter
   * @param msdosFile Description of the Parameter
   * @param data Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Returned Value
   * @throws MSDOSFATException Description of the Exception
   * @throws MSDOSDirectoryException Description of the Exception
   */
  public FileSystemReturnData write(int iRequestID, FileSystemFile file,
      String data, String path) throws AllocationTableException,
      DirectoryException;

  /**
   * Gets the DirectoryPosition attribute of the FileSystem object
   *
   * @param mvFilename Description of Parameter
   * @return The DirectoryPosition value
   */
  public int getDirectoryPosition(String mvFilename);

  /**
   * Gets the FreeEntry attribute of the FileSystem object
   *
   * @param mvDeviceNumber Description of Parameter
   * @return The FreeEntry value
   */
  public int getFreeEntry(int mvDeviceNumber);

  /**
   * Description of the Method
   *
   * @param mvDeviceNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean diskFull(int mvDeviceNumber);

  /**
   * Gets the FreeBlock attribute of the FileSystem object
   *
   * @param mvDeviceNumber Description of Parameter
   * @return The FreeBlock value
   */
  public int getFreeBlock(int mvDeviceNumber);

  /**
   * Description of the Method
   */
  public void recordSystemFile();

  /**
   * Description of the Method
   *
   * @return Description of the Return Value
   */
  public FileSystem requestSystemFile();

  /**
   * Gets the NextDirectoryEntry attribute of the FileSystem object
   *
   * @param mvDirEntry Description of Parameter
   * @param mvDeviceNumber Description of Parameter
   * @return The NextDirectoryEntry value
   */
  public int getNextDirectoryEntry(int mvDirEntry, int mvDeviceNumber);

  /**
   * Description of the Method
   *
   * @param mvEntryNumber Description of Parameter
   * @param mvDeviceNumber Description of Parameter
   */
  public void deallocateEntry(int mvEntryNumber, int mvDeviceNumber);

  /**
   * Gets the MountPoint attribute of the FileSystem object
   *
   * @param mvFilename Description of Parameter
   * @return The MountPoint value
   */
  public String getMountPoint(String mvFilename);

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
  public String dump(String Operation, int item1, int item2);
}
