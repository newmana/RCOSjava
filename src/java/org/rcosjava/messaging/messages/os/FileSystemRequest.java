package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class FileSystemRequest extends OSMessageAdapter
{
  /**
   * Description of the Field
   */
  private int iFSMRequestID;

  /**
   * Description of the Field
   */
  private int iFSFileNo;

  /**
   * Description of the Field
   */
  private String sFileName;

  /**
   * Description of the Field
   */
  private int iData;

  /**
   * Constructor for the FileSystemRequest object
   *
   * @param theSource Description of Parameter
   * @param iNewFSMRequestID Description of Parameter
   * @param iNewFSFileNo Description of Parameter
   * @param sNewFileName Description of Parameter
   * @param iNewData Description of Parameter
   */
  public FileSystemRequest(OSMessageHandler theSource,
      int iNewFSMRequestID, int iNewFSFileNo, String sNewFileName, int iNewData)
  {
    super(theSource);
    iFSMRequestID = iNewFSMRequestID;
    iFSFileNo = iNewFSFileNo;
    sFileName = sNewFileName;
    iData = iNewData;
  }

  /**
   * Constructor for the FileSystemRequest object
   *
   * @param theSource Description of Parameter
   * @param iNewFDSMRequestID Description of Parameter
   * @param sNewFileName Description of Parameter
   */
  public FileSystemRequest(OSMessageHandler theSource,
      int iNewFDSMRequestID, String sNewFileName)
  {
    super(theSource);
    iFSMRequestID = iNewFDSMRequestID;
    iFSFileNo = -1;
    sFileName = sNewFileName;
    iData = -1;
  }

  /**
   * Constructor for the FileSystemRequest object
   *
   * @param theSource Description of Parameter
   */
  public FileSystemRequest(OSMessageHandler theSource)
  {
    super(theSource);
    iFSMRequestID = -1;
    iFSFileNo = -1;
    sFileName = null;
    iData = -1;
  }

  /**
   * Sets the FSMRequestID attribute of the FileSystemRequest object
   *
   * @param iNewFSMRequestID The new FSMRequestID value
   */
  public void setFSMRequestID(int iNewFSMRequestID)
  {
    iFSMRequestID = iNewFSMRequestID;
  }

  /**
   * Sets the FSFileNo attribute of the FileSystemRequest object
   *
   * @param iNewFSFileNo The new FSFileNo value
   */
  public void setFSFileNo(int iNewFSFileNo)
  {
    iFSFileNo = iNewFSFileNo;
  }

  /**
   * Sets the FileName attribute of the FileSystemRequest object
   *
   * @param sNewFileName The new FileName value
   */
  public void setFileName(String sNewFileName)
  {
    sFileName = sNewFileName;
  }

  /**
   * Sets the Data attribute of the FileSystemRequest object
   *
   * @param iNewData The new Data value
   */
  public void setData(int iNewData)
  {
    iData = iNewData;
  }

  /**
   * Gets the FSMRequestID attribute of the FileSystemRequest object
   *
   * @return The FSMRequestID value
   */
  public int getFSMRequestID()
  {
    return iFSMRequestID;
  }

  /**
   * Gets the FSFileNo attribute of the FileSystemRequest object
   *
   * @return The FSFileNo value
   */
  public int getFSFileNo()
  {
    return iFSFileNo;
  }

  /**
   * Gets the FileName attribute of the FileSystemRequest object
   *
   * @return The FileName value
   */
  public String getFileName()
  {
    return sFileName;
  }

  /**
   * Gets the Data attribute of the FileSystemRequest object
   *
   * @return The Data value
   */
  public int getData()
  {
    return iData;
  }


}
