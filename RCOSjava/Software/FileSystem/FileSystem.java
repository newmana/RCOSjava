// *************************************************************************
// FILE     : FileSystem
// PACKAGE  : FileSystem
// PURPOSE  : Interface for all file systems to implement (heavily 
//            influenced by CPM14 implementation).
// AUTHOR   : Andrew Newman
// MODIFIED : 
// DATE     : 08/08/98   Created.
// *************************************************************************

package Software.FileSystem;

public interface FileSystem
{
  public void mount(String mvNewMountPoint, String mvNewDeviceName);
  public FileSystemReturnData allocate(int iRequestID, String sFileName);
  public FileSystemReturnData eof(int iRequestID, int iFSFileNo);
  public FileSystemReturnData delete(int iRequestID, int iFSFileNo);
  public FileSystemReturnData create(int iRequestID, int iFSFileNo);
  public FileSystemReturnData close(int iRequestID, int iFSFileNo);
  public FileSystemReturnData open(int iRequestID, int iFSFileNo);
  public FileSystemReturnData read(int iRequestID, int iFSFileNo);
  public FileSystemReturnData write(int iRequestID, int iFSFileNo);
  public int getDirectoryPosition(String mvFilename);
  public int getFreeEntry(int mvDeviceNumber);
  public boolean diskFull(int mvDeviceNumber);
  public int getFreeBlock(int mvDeviceNumber);
  public int getNextDirectoryEntry(int mvDirEntry, int mvDeviceNumber);
  public void deallocateEntry(int mvEntryNumber, int mvDeviceNumber);
  public String getMountPoint(String mvFilename);
}