package org.rcosjava.software.filesystem.cpm14;

/**
 * To contain the data for an entry in the CPM14 FS Request table.
 * <P>
 * @author Brett Carter
 * @author Andrew Newman
 * @created 28 March 1996
 */
public class CPM14RequestTableEntry
{
  /**
   * Write buffer.
   */
  public static final int WRITE_BUFFER = 1;

  /**
   * Get block
   */
  public static final int GET_BLOCK = 2;

  /**
   * Flush
   */
  public static final int FLUSH = 3;

  /**
   * The type of request.
   */
  public int requestType;

  /**
   * File system unique number.
   */
  public int fsFileNumber;

  /**
   * Unique file system request.
   */
  public int requestId;

  /**
   * Data to read/write.
   */
  public int data;

  /**
   * Create a new CPM14 table entry.
   */
  public CPM14RequestTableEntry(int newRequestType, int newFsFileNumber,
      int newRequestId, int newData)
  {
    requestType = newRequestType;
    fsFileNumber = newFsFileNumber;
    requestId = newRequestId;
    data = newData;
  }

  /**
   * Returns the request type.
   *
   * @return the request type.
   */
  public int getRequestType()
  {
    return requestType;
  }

  /**
   * Returns the file system number.
   *
   * @return the file system number.
   */
  public int getFileSystemNumber()
  {
    return fsFileNumber;
  }

  /**
   * Returns the request id.
   *
   * @return the request id.
   */
  public int getRequestId()
  {
    return requestId;
  }

  /**
   * Returns the data.
   *
   * @return the data.
   */
  public int getData()
  {
    return data;
  }
}