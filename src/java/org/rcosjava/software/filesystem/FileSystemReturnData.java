package org.rcosjava.software.filesystem;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class FileSystemReturnData
{
  /**
   * Description of the Field
   */
  private int iFSMRequestID;

  /**
   * Description of the Field
   */
  private int iReturnValue;

  /**
   * Constructor for the FileSystemReturnData object
   *
   * @param iNewFSMRequestID Description of Parameter
   * @param iNewReturnValue Description of Parameter
   */
  public FileSystemReturnData(int iNewFSMRequestID, int iNewReturnValue)
  {
    iFSMRequestID = iNewFSMRequestID;
    iReturnValue = iNewReturnValue;
  }

  /**
   * Constructor for the FileSystemReturnData object
   */
  public FileSystemReturnData()
  {
    iFSMRequestID = -1;
    iReturnValue = -1;
  }

  /**
   * Gets the RequestID attribute of the FileSystemReturnData object
   *
   * @return The RequestID value
   */
  public int getRequestID()
  {
    return iFSMRequestID;
  }

  /**
   * Gets the ReturnValue attribute of the FileSystemReturnData object
   *
   * @return The ReturnValue value
   */
  public int getReturnValue()
  {
    return iReturnValue;
  }
}