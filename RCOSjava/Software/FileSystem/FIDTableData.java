package Software.FileSystem;

/**
 * Contains the data for an entry in the FSMan File ID table.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @version 1.00 $Date$
 * @created 25th March 1996
 */
class FIDTableData
{
  private int processId;
  private String fileSystemId;
  private int fileNo;

  public FIDTableData(int newProcessId, String newFileSystemId,
    int newFileNo)
  {
    processId = processId;
    fileSystemId = fileSystemId;
    fileNo = fileNo;
  }

  public FIDTableData()
  {
    processId = -1;
    fileSystemId = null;
    fileNo = -1;
  }

  public int getProcessId()
  {
    return processId;
  }

  public void setProcessId(int newProcessId)
  {
    processId = newProcessId;
  }

  public String getFileSystemId()
  {
    return fileSystemId;
  }

  public void setFileSystemId(String newFileSystemId)
  {
    fileSystemId = newFileSystemId;
  }

  public int getFileNo()
  {
    return fileNo;
  }

  public void setFileNo(int newFileNo)
  {
    fileNo = newFileNo;
  }
}

