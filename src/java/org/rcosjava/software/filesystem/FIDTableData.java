package org.rcosjava.software.filesystem;/** * Contains the data for an entry in the FSMan File ID table. <P> * * * * @author Andrew Newman. * @author Brett Carter. * @created 25th March 1996 * @version 1.00 $Date$ */class FIDTableData{  /**   * Description of the Field   */  private int processId;  /**   * Description of the Field   */  private String fileSystemId;  /**   * Description of the Field   */  private int fileNo;  /**   * Constructor for the FIDTableData object   *   * @param newProcessId Description of Parameter   * @param newFileSystemId Description of Parameter   * @param newFileNo Description of Parameter   */  public FIDTableData(int newProcessId, String newFileSystemId,      int newFileNo)  {    processId = processId;    fileSystemId = fileSystemId;    fileNo = fileNo;  }  /**   * Constructor for the FIDTableData object   */  public FIDTableData()  {    processId = -1;    fileSystemId = null;    fileNo = -1;  }  /**   * Sets the ProcessId attribute of the FIDTableData object   *   * @param newProcessId The new ProcessId value   */  public void setProcessId(int newProcessId)  {    processId = newProcessId;  }  /**   * Sets the FileSystemId attribute of the FIDTableData object   *   * @param newFileSystemId The new FileSystemId value   */  public void setFileSystemId(String newFileSystemId)  {    fileSystemId = newFileSystemId;  }  /**   * Sets the FileNo attribute of the FIDTableData object   *   * @param newFileNo The new FileNo value   */  public void setFileNo(int newFileNo)  {    fileNo = newFileNo;  }  /**   * Gets the ProcessId attribute of the FIDTableData object   *   * @return The ProcessId value   */  public int getProcessId()  {    return processId;  }  /**   * Gets the FileSystemId attribute of the FIDTableData object   *   * @return The FileSystemId value   */  public String getFileSystemId()  {    return fileSystemId;  }  /**   * Gets the FileNo attribute of the FIDTableData object   *   * @return The FileNo value   */  public int getFileNo()  {    return fileNo;  }}