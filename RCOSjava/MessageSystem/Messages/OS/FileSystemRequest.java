// ************************************************************************//
// FILENAME : FileSystemRequestMessage.java
// PACKAGE  : MessageSystem.OS
// PURPOSE  : A container for data being passed to the FileSystem
//            from the file system manager.
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 26/3/96 Created.
//            
// ************************************************************************//

package MessageSystem.Messages.OS;

import Software.FileSystem.FileSystemManager;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class FileSystemRequest extends OSMessageAdapter
{
  private int iFSMRequestID;
  private int iFSFileNo;
  private String sFileName;
  private int iData;

  public FileSystemRequest(OSMessageHandler theSource,
    int iNewFSMRequestID, int iNewFSFileNo, String sNewFileName, int iNewData)
  {
    super(theSource);
    iFSMRequestID = iNewFSMRequestID;
    iFSFileNo = iNewFSFileNo;
    sFileName = sNewFileName;
    iData = iNewData;
  }
  
  public FileSystemRequest(OSMessageHandler theSource,
	  int iNewFDSMRequestID, String sNewFileName)
  {
		super(theSource);
    iFSMRequestID = iNewFDSMRequestID;
    iFSFileNo = -1;
    sFileName = sNewFileName;
    iData = -1;
  }

  public FileSystemRequest(OSMessageHandler theSource)
  {
		super(theSource);
    iFSMRequestID = -1;
    iFSFileNo = -1;
    sFileName = null;
    iData = -1;
  }
  
  public void setFSMRequestID(int iNewFSMRequestID)
  {
    iFSMRequestID = iNewFSMRequestID;
  }

  public int getFSMRequestID()
  {
    return iFSMRequestID;
  }
  
  public void setFSFileNo(int iNewFSFileNo)
  {
    iFSFileNo = iNewFSFileNo;
  }

  public int getFSFileNo()
  {
    return iFSFileNo;
  }
  
  public void setFileName(String sNewFileName)
  {
    sFileName = sNewFileName;
  }

  public String getFileName()
  {
    return sFileName;    
  }
  
  public void setData(int iNewData)
  {
    iData = iNewData;
  }
  
  public int getData()
  {
    return iData;
  }
}
