// ************************************************************************//
// FILENAME : DiskRequestData.java
// PACKAGE  : FileSystem
// PURPOSE  : A container for data being passed to the FileSystemManager
//            for disk requests.
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 26/3/96 Created.
//            
// ************************************************************************//

package Software.FileSystem;

public class DiskRequestData implements Cloneable
{
  private int iPID;
  private int iFID;
  private String sFileName;
	private String sType;
  private int iData;

  public DiskRequestData(int iNewPID, int iNewFID, 
    String sNewFileName, int iNewData, String sNewType) 
  {
    iPID = iNewPID;
    iFID = iNewFID;
    sFileName = sNewFileName;
    iData = iNewData;
		sType = sNewType;
  }

  public DiskRequestData()
  {
    iPID = -1;
    iFID = -1;
    sFileName = null;
    iData = -1;
    sType = null;
  }

  public Object clone()
  {
    return new DiskRequestData(iPID, iFID, sFileName, iData, sType);
  }
  
  public int getPID()
  {
    return iPID;
  }
  
  public void setPID(int iNewPID)
  {
    iPID = iNewPID;
  }
  
  public int getFID()
  {
    return iFID;
  }
  
  public void setFID(int iNewFID)
  {
    iFID = iNewFID;
  }
  
  public String getFileName()
  {
    return sFileName;
  }
  
  public void setFileName(String sNewFileName)
  {
    sFileName = sNewFileName;
  }
  
  public int getData()
  {
    return iData;
  }
  
  public void setData(int iNewData)
  {
    iData = iNewData;
  }
  
  public String getType()
  {
    return sType;
  }
  
  public FileSystemReturnData doRequest(FileSystem theFS, int iRequestID)
  {
    if (sType == FileSystemManager.F_ALLOC)
    {
      theFS.allocate(iRequestID, getFileName());
    }
    else if (sType == FileSystemManager.F_CLOSE)
    {
      //return theFS.close(iRequestID, getFileName());
    }
    else if (sType == FileSystemManager.F_CREATE)
    {
      //return theFS.create(iRequestID, getFileName());
    }
    else if (sType == FileSystemManager.F_DEL)
    {
      //return theFS.delete(iRequestID, getFileName());
    }
    else if (sType == FileSystemManager.F_EOF)
    {
      //return theFS.eof(iRequestID, getFileName());
    }
    else if (sType == FileSystemManager.F_OPEN)
    {
      //return theFS.open(iRequestID, getFileName());
    }
    else if (sType == FileSystemManager.F_READ)
    {
      //return theFS.read(iRequestID, getFileName());
    }
    else if (sType == FileSystemManager.F_WRITE)
    {
      //return theFS.write(iRequestID, getFileName());
    }
		return null;
  }
}
