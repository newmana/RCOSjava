// ************************************************************************//
// FILENAME : FileSystemReturnData.java
// PACKAGE  : FileSystem
// PURPOSE  : A container for data being passed to the FileSystemManager
//            containg the return information
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 28/03/96 Created.
//            
// ************************************************************************//

package Software.FileSystem;

public class FileSystemReturnData
{
  private int iFSMRequestID;
  private int iReturnValue;

  public FileSystemReturnData(int iNewFSMRequestID, int iNewReturnValue)
  {
    iFSMRequestID = iNewFSMRequestID;
    iReturnValue = iNewReturnValue;
  }

  public FileSystemReturnData()
  {
    iFSMRequestID = -1;
    iReturnValue = -1;
  }
	
	public int getRequestID()
	{
		return iFSMRequestID;
	}
	
	public int getReturnValue()
	{
		return iReturnValue;
	}
}


