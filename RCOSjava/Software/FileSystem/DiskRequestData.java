package Software.FileSystem;

/**
 * A container for data being passed to the FileSystemManager for disk requests.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @version 1.00 $Date$
 * @created 26th March 1996
 */
public class DiskRequestData implements Cloneable
{
  private int processId;
  private int fileId;
  private String fileName;
  private String type;
  private int data;

  public DiskRequestData(int newProcessId, int newFileId,
    String newFileName, int newData, String newType)
  {
    processId = newProcessId;
    fileId = newFileId;
    fileName = newFileName;
    data = newData;
    type = newType;
  }

  public DiskRequestData()
  {
    processId = -1;
    fileId = -1;
    fileName = null;
    data = -1;
    type = null;
  }

  public Object clone()
  {
    return new DiskRequestData(processId, fileId, fileName, data, type);
  }

  public int getProcessId()
  {
    return processId;
  }

  public void setProcessId(int newProcessId)
  {
    processId = newProcessId;
  }

  public int getFileId()
  {
    return fileId;
  }

  public void setFileId(int newFileId)
  {
    fileId = newFileId;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String sNewFileName)
  {
    fileName = sNewFileName;
  }

  public int getData()
  {
    return data;
  }

  public void setData(int iNewData)
  {
    data = iNewData;
  }

  public String getType()
  {
    return type;
  }

  public FileSystemReturnData doRequest(FileSystem theFS, int iRequestID)
  {
    if (type == FileSystemManager.F_ALLOC)
    {
      theFS.allocate(iRequestID, getFileName());
    }
    else if (type == FileSystemManager.F_CLOSE)
    {
      //return theFS.close(iRequestID, getFileName());
    }
    else if (type == FileSystemManager.F_CREATE)
    {
      //return theFS.create(iRequestID, getFileName());
    }
    else if (type == FileSystemManager.F_DEL)
    {
      //return theFS.delete(iRequestID, getFileName());
    }
    else if (type == FileSystemManager.F_EOF)
    {
      //return theFS.eof(iRequestID, getFileName());
    }
    else if (type == FileSystemManager.F_OPEN)
    {
      //return theFS.open(iRequestID, getFileName());
    }
    else if (type == FileSystemManager.F_READ)
    {
      //return theFS.read(iRequestID, getFileName());
    }
    else if (type == FileSystemManager.F_WRITE)
    {
      //return theFS.write(iRequestID, getFileName());
    }
    return null;
  }
}
