// **************************************************************************
// FILE     : MemoryRequest.java
// PACKAGE  : Software.Memory
// PURPOSE  : Structure sent to the MMU requesting pages or byte-count
//            memory.
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 01/03/1996  Created
//            01/01/1998  get/set added, constructors added.
// **************************************************************************

package Software.Memory;

import java.io.Serializable;
import Hardware.Memory.Memory;

public class MemoryRequest implements Serializable
{
  private int iProcessID;
  private byte bMemoryType;
  private int iSize;
  private int iOffset;
  private Memory mMemory;

  public MemoryRequest(int iNewProcessID, byte iNewMemoryType,
    int iNewSize)
  {
    iProcessID = iNewProcessID;
    bMemoryType = iNewMemoryType;
    iSize = iNewSize;
    iOffset = 0;
  }

  public MemoryRequest(int iNewProcessID, byte iNewMemoryType,
    int iNewSize, Memory mNewMemory)
  {
    this(iNewProcessID, iNewMemoryType, iNewSize);
    mMemory = mNewMemory;
  }

  public MemoryRequest(int iNewProcessID, byte iNewMemoryType,
    int iNewSize, int iNewOffset)
  {
    this(iNewProcessID, iNewMemoryType, iNewSize);
    iOffset = iNewOffset;
  }

  public MemoryRequest(int iNewProcessID, byte iNewMemoryType,
    int iNewSize, int iNewOffset, Memory mNewMemory)
  {
    this(iNewProcessID, iNewMemoryType, iNewSize, iNewOffset);
    mMemory = mNewMemory;
  }

  public int getPID()
  {
    return iProcessID;
  }

  public byte getMemoryType()
  {
    return bMemoryType;
  }

  public void setMemoryType(byte bNewMemoryType)
  {
    bMemoryType = bNewMemoryType;
  }

  public int getSize()
  {
    return iSize;
  }

  public void setSize(int iNewSize)
  {
    iSize = iNewSize;
  }

  public int getOffset()
  {
    return iOffset;
  }

  public Memory getMemory()
  {
    return (Memory) mMemory.clone();
  }

  public void setMemory(Memory mNewMemory)
  {
    mMemory = mNewMemory;
  }
}
