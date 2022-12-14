package org.rcosjava.software.filesystem;

/**
 * A container for data being passed to the FileSystemManager for disk requests.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @created 26th March 1996
 * @version 1.00 $Date$
 */
public class DiskRequestData implements Cloneable
{
  /**
   * Description of the Field
   */
  private int processId;

  /**
   * Description of the Field
   */
  private int fileId;

  /**
   * Description of the Field
   */
  private String fileName;

  /**
   * Description of the Field
   */
  private String type;

  /**
   * Description of the Field
   */
  private int data;

  /**
   * Constructor for the DiskRequestData object
   *
   * @param newProcessId Description of Parameter
   * @param newFileId Description of Parameter
   * @param newFileName Description of Parameter
   * @param newData Description of Parameter
   * @param newType Description of Parameter
   */
  public DiskRequestData(int newProcessId, int newFileId,
      String newFileName, int newData, String newType)
  {
    processId = newProcessId;
    fileId = newFileId;
    fileName = newFileName;
    data = newData;
    type = newType;
  }

  /**
   * Constructor for the DiskRequestData object
   */
  public DiskRequestData()
  {
    processId = -1;
    fileId = -1;
    fileName = null;
    data = -1;
    type = null;
  }

  /**
   * Sets the ProcessId attribute of the DiskRequestData object
   *
   * @param newProcessId The new ProcessId value
   */
  public void setProcessId(int newProcessId)
  {
    processId = newProcessId;
  }

  /**
   * Sets the FileId attribute of the DiskRequestData object
   *
   * @param newFileId The new FileId value
   */
  public void setFileId(int newFileId)
  {
    fileId = newFileId;
  }

  /**
   * Sets the FileName attribute of the DiskRequestData object
   *
   * @param sNewFileName The new FileName value
   */
  public void setFileName(String sNewFileName)
  {
    fileName = sNewFileName;
  }

  /**
   * Sets the Data attribute of the DiskRequestData object
   *
   * @param iNewData The new Data value
   */
  public void setData(int iNewData)
  {
    data = iNewData;
  }

  /**
   * Gets the ProcessId attribute of the DiskRequestData object
   *
   * @return The ProcessId value
   */
  public int getProcessId()
  {
    return processId;
  }

  /**
   * Gets the FileId attribute of the DiskRequestData object
   *
   * @return The FileId value
   */
  public int getFileId()
  {
    return fileId;
  }

  /**
   * Gets the FileName attribute of the DiskRequestData object
   *
   * @return The FileName value
   */
  public String getFileName()
  {
    return fileName;
  }

  /**
   * Gets the Data attribute of the DiskRequestData object
   *
   * @return The Data value
   */
  public int getData()
  {
    return data;
  }

  /**
   * Gets the Type attribute of the DiskRequestData object
   *
   * @return The Type value
   */
  public String getType()
  {
    return type;
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public Object clone()
  {
    return new DiskRequestData(processId, fileId, fileName, data, type);
  }

  /**
   * Description of the Method
   *
   * @param theFS Description of Parameter
   * @param iRequestID Description of Parameter
   * @return Description of the Returned Value
   */
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
