//*************************************************************************//
// FILENAME : RequestTableData 
// PACKAGE  : FileSystem
// PURPOSE  : To contain the data for an entry in the FSMan Request table.
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 25/3/96 Created.
//            
//*************************************************************************//

package Software.FileSystem;

class RequestTableData extends Object
{
  private int iPID;
  private String sType;   
  private Object oData; 

  public RequestTableData(DiskRequestData drdRequest, String sNewType)
  {
    iPID = drdRequest.getPID();
	sType = sNewType;
	oData = drdRequest.clone();
  }
  
  public RequestTableData(int thePID, String theType, Object theData)
  {
    iPID = thePID;
    sType = theType;
    oData = theData;
  }

  public RequestTableData()
  {
    iPID = -1;
    sType = null;
    oData = null;
  }

  public int getPID()
  {
    return iPID;
  }
  
  public void setPID(int iNewPID)
  {
    iPID = iNewPID;
  }
  
  public String getType()
  {
    return sType;
  }
  
  public void setType(String sNewType)
  {
    sType = sNewType;
  }
  
  public void setData(Object oNewData)
  {
    oData = oNewData;
  }
  
  public Object getData()
  {
    return oData;
  }
}

