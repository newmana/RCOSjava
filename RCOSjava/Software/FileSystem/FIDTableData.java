// ************************************************************************//
// FILENAME : FIDTableData.java
// PACKAGE  : FileSystem
// PURPOSE  : To contain the data for an entry in the FSMan File ID table.       
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 25/3/96 Created.
//            
// ************************************************************************//

package Software.FileSystem;

class FIDTableData extends Object
{
  private int iPID;
  private String sFSID;    
  private int iFSFileNo;

  public FIDTableData(int iNewPID, String sNewFSID, int iNewFSFileNo)
  {
    iPID = iPID;
    sFSID = sFSID;
    iFSFileNo = iFSFileNo;
  }

  public FIDTableData()
  {
    iPID = -1;
    sFSID = null;
    iFSFileNo = -1;
  }

  public int getPID()
  {
    return iPID;
  }
  
  public void setPID(int iNewPID)
  {
    iPID = iNewPID;
  }
  
  public String getFSID()
  {
    return sFSID;
  }
  
  public void setFSID(String sNewFSID)
  {
    sFSID = sNewFSID;
  }
  
  public int getFSFileNo()
  {
    return iFSFileNo;
  }
  
  public void setFSFileNo(int iNewFSFileNo)
  {
    iFSFileNo = iNewFSFileNo;
  }
}

