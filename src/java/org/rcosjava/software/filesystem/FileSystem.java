package org.rcosjava.software.filesystem;

import org.rcosjava.software.disk.DiskRequest;

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
   * Handle a mount request.  Creates a new device and sets its status to
   * mounted.
   *
   * @param newMountPoint The name of the new mount point e.g. "C"
   * @param newDeviceName The name of the new device name e.g. "DISK1"
   */
  public void mount(String newMountPoint, String newDeviceName);

  /**
   * Perfoms an allocation of the file. Creats an entry in the FID table
   * and inits it.
   *
   * @param requestId unique identifier for this request.
   * @param fileName the name of the file to allocate.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData allocate(int requestId, String fileName);

  /**
   * Sets up a directory entry for the file and sets it to a 0 length file.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData create(int requestId, int fileNumber);

  /**
   * Opens the specified file for reading.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData open(int requestId, int fsFileNumber);

  /**
   * Reads from the file if it is in the right mode.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData read(int requestId, int fsFileNumber);

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
  public FileSystemReturnData read(int requestId, FileSystemFile file,
      String path) throws AllocationTableException, DirectoryException;

  /**
   * If the mode is correct, this starts or contines writing a file.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @param data the data to write.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData write(int requestId, int fsFileNumber, byte data);

  /**
   * Description of the Method
   *
   * @param requestId Description of Parameter
   * @param msdosFile Description of the Parameter
   * @param data Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Returned Value
   * @throws MSDOSFATException Description of the Exception
   * @throws MSDOSDirectoryException Description of the Exception
   */
  public FileSystemReturnData write(int requestId, FileSystemFile file,
      String data, String path) throws AllocationTableException,
      DirectoryException;

  /**
   * Closes a file and removes it from the FID table.  First writes the
   * current buffer and the directory blocks to disk.
   *
   * @param requestId Description of Parameter
   * @param fsFileNumber Description of Parameter
   * @return Description of the Returned Value
   */
  public FileSystemReturnData close(int requestId, int fsFileNumber);

  /**
   * Replys to the sender of the message 1 if at end of file
   * 0 if not.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData eof(int requestId, int fsFileNumber);

  /**
   * Free's all disk structures associated with the specified file. Leaves the
   * file in the PID table though.
   *
   * @param requestId unique identifier for this request.
   * @param fsFileNumber unique identifier of the file.
   * @return the data structure indicating a success or failure.
   */
  public FileSystemReturnData delete(int requestId, int fsFileNumber);

  /**
   * Delete file.
   *
   * @param requestId Description of the Parameter
   * @param msdosFile Description of the Parameter
   * @param path Description of the Parameter
   * @return Description of the Return Value
   * @throws MSDOSFATException Description of the Exception
   * @throws MSDOSDirectoryException Description of the Exception
   */
  public FileSystemReturnData delete(int requestId, FileSystemFile file,
      String path) throws AllocationTableException, DirectoryException;

  /**
   * Checks to see if the file system indicated by device number is full.
   *
   * @param deviceNumber the unique device number to check.
   * @return true if the device has no free blocks or free directory entries.
   */
  public boolean diskFull(int deviceNumber);

  /**
   * A successful flush of data to disk.
   *
   * @param request the request from the disk manager.
   */
  public void flush(DiskRequest request);

  /**
   * Writes the buffer to the device.
   *
   * @param request the request details the request id and file and the data
   *   to write to the device.
   */
  public void writeBuffer(DiskRequest request);

  /**
   * Store the current file system's state.
   */
  public void recordSystemFile();

  /**
   * Request a previously stored system file to be loaded.
   *
   * @return a fully create system file.
   */
  public FileSystem requestSystemFile();

  /**
   * Returns the mount point based on the given file name.
   *
   * @param fileName the name of file which contains the mount point e.g.
   *   "C:fred.doc".
   * @return the mount point e.g. "C".
   */
  public String getMountPoint(String fileName);

  /**
   * Displays a file's entry on a devices directory as a string.
   *
   * @param deviceNumber the unique device number to check.
   * @param fsFileNumber unique identifier of the file.
   * @return a string representation of the file's directory entry.
   */
  public String dumpDirectoryEntry(int deviceNumber, int fsFileNumber);

  /**
   * Displays a file's buffer as a string.
   *
   * @param fsFileNumber unique identifier of the file.
   * @return a string representation of the file's buffer.
   */
  public String dumpBuffer(int fsFileNumber);

  /**
   * Returns a file's entry id entry file.
   *
   * @param fsFileNumber unique identifier of the file.
   * @return a string representation of the file's id entry.
   */
  public String dumpFIDEntry(int fsFileNumber);

}
