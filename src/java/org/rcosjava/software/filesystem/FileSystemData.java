package org.rcosjava.software.filesystem;

/**
 * Title: RCOS Description: Copyright: Copyright (c) 2002 Company: UFPE
 *
 * @author Danielly Cruz
 * @created July 27, 2003
 * @version 1.0
 */
public class FileSystemData
{
  private int requestID;
  private String returnValue;
  private int[] entriesFileFromFAT;

  /**
   * Constructor for the FileSystemData object
   *
   * @param newRequestID Description of the Parameter
   * @param newReturnValue Description of the Parameter
   * @param newEntriesFileFromFAT Description of the Parameter
   */
  public FileSystemData(int newRequestID, String newReturnValue, int[] newEntriesFileFromFAT)
  {
    requestID = newRequestID;
    returnValue = newReturnValue;
    entriesFileFromFAT = newEntriesFileFromFAT;
  }

  /**
   * Constructor for the FileSystemData object
   */
  public FileSystemData()
  {
    requestID = -1;
    returnValue = "";
  }

  /**
   * Gets the requestID attribute of the FileSystemData object
   *
   * @return The requestID value
   */
  public int getRequestID()
  {
    return requestID;
  }

  /**
   * Gets the returnValue attribute of the FileSystemData object
   *
   * @return The returnValue value
   */
  public String getReturnValue()
  {
    return returnValue;
  }

  /**
   * Gets the entriesFileFromFAT attribute of the FileSystemData object
   *
   * @return The entriesFileFromFAT value
   */
  public int[] getEntriesFileFromFAT()
  {
    return entriesFileFromFAT;
  }
}
