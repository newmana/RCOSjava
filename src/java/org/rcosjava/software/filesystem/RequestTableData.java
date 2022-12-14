package org.rcosjava.software.filesystem;

/**
 * Contain the data for an entry in the FSMan Request table.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @created 25th March 1996
 * @version 1.00 $Date$
 */
public class RequestTableData
{
  /**
   * Description of the Field
   */
  private int processId;

  /**
   * Description of the Field
   */
  private String type;

  /**
   * Description of the Field
   */
  private Object data;

  /**
   * Constructor for the RequestTableData object
   *
   * @param newRequest Description of Parameter
   * @param newType Description of Parameter
   */
  public RequestTableData(DiskRequestData newRequest, String newType)
  {
    processId = newRequest.getProcessId();
    type = newType;
    data = newRequest.clone();
  }

  /**
   * Constructor for the RequestTableData object
   *
   * @param newProcessId Description of Parameter
   * @param newType Description of Parameter
   * @param newData Description of Parameter
   */
  public RequestTableData(int newProcessId, String newType, Object newData)
  {
    processId = newProcessId;
    type = newType;
    data = newData;
  }

  /**
   * Constructor for the RequestTableData object
   */
  public RequestTableData()
  {
    processId = -1;
    type = null;
    data = null;
  }

  /**
   * Sets the ProcessId attribute of the RequestTableData object
   *
   * @param newProcessId The new ProcessId value
   */
  public void setProcessId(int newProcessId)
  {
    processId = newProcessId;
  }

  /**
   * Sets the Type attribute of the RequestTableData object
   *
   * @param newType The new Type value
   */
  public void setType(String newType)
  {
    type = newType;
  }

  /**
   * Sets the Data attribute of the RequestTableData object
   *
   * @param newData The new Data value
   */
  public void setData(Object newData)
  {
    data = newData;
  }

  /**
   * Gets the ProcessId attribute of the RequestTableData object
   *
   * @return The ProcessId value
   */
  public int getProcessId()
  {
    return processId;
  }

  /**
   * Gets the Type attribute of the RequestTableData object
   *
   * @return The Type value
   */
  public String getType()
  {
    return type;
  }

  /**
   * Gets the Data attribute of the RequestTableData object
   *
   * @return The Data value
   */
  public Object getData()
  {
    return data;
  }
}

