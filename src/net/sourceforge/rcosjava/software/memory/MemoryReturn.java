package net.sourceforge.rcosjava.software.memory;

import java.io.Serializable;

/**
 * Structure sent to everyone after memory request.
 * <P>
 *
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 30th March 1996
 */
public class MemoryReturn implements Serializable
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
