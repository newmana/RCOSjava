package net.sourceforge.rcosjava.software.filesystem;

/**
 * Contain the data for an entry in the FSMan Request table.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @version 1.00 $Date$
 * @created 25th March 1996
 */
class RequestTableData
{
  private int processId;
  private String type;
  private Object data;

  public RequestTableData(DiskRequestData newRequest, String newType)
  {
    processId = newRequest.getProcessId();
    type = newType;
    data = newRequest.clone();
  }

  public RequestTableData(int newProcessId, String newType, Object newData)
  {
    processId = newProcessId;
    type = newType;
    data = newData;
  }

  public RequestTableData()
  {
    processId = -1;
    type = null;
    data = null;
  }

  public int getProcessId()
  {
    return processId;
  }

  public void setProcessId(int newProcessId)
  {
    processId = newProcessId;
  }

  public String getType()
  {
    return type;
  }

  public void setType(String newType)
  {
    type = newType;
  }

  public void setData(Object newData)
  {
    data = newData;
  }

  public Object getData()
  {
    return data;
  }
}

