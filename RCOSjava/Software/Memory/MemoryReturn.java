// ****************************************************
// FILE     : MemoryReturn.java
// PACKAGE  : Software.Memory.MemoryReturn
// PURPOSE  : Structure sent to everyone after memory
//            request.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 30/03/96  Created. AN
//
// ****************************************************
 
package Software.Memory;
 
import java.lang.Integer;

public class MemoryReturn
{
  //Used by MMU 
  private int iPID;
  private byte bMemoryType;
  private int iSize;
  private short[] sPages;
 
  public MemoryReturn(int iNewID, byte bNewType, int iNewSize, 
    short[] sNewPages)
  {
    iPID = iNewID;
    bMemoryType = bNewType;
    iSize = iNewSize;
    sPages = sNewPages;
  }
  
  public int getPID()
  {
    return iPID;
  }
  
  public byte getMemoryType()
  {
    return bMemoryType;
  }
  
  public void setMemoryType(byte bNewType)
  {
    bMemoryType = bNewType;
  }
  
  public int getSize()
  {
    return iSize;
  }
  
  public void setSize(int iNewSize)
  {
    iSize = iNewSize;
  }
  
  public short[] getPages()
  {
    return sPages;
  }
  
  public short getPage(int iIndex)
  {
    return sPages[iIndex];
  }
  
  public void setPages(short[] sNewPages)
  {
    sPages = sNewPages;
  }
}
