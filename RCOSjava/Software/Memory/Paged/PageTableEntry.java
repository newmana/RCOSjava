// ****************************************************
// FILE:        PageTableEntry.java
// PURPOSE:     Table which contains type and PID of
//              all memory.  For fast look-up.
// AUTHOR:      Andrew Newman
// MODIFIED:
// HISTORY:     ?/?/97          Completed
//
// ****************************************************

package Software.Memory.Paged;

public class PageTableEntry
{
  private byte bType;
  private byte bPID;
  private short[] sPages;
  private short sNoPages;

  public PageTableEntry()
  {
    bType = -1;
    bPID = -1;
  }

  public PageTableEntry(byte aPID, byte aType, short[] aPages, short aNoPages)
  {
    bPID = aPID;
    bType = aType;
    sPages = aPages;
    sNoPages = aNoPages;
  }

  public byte getType()
  {
    return bType;
  }

  public byte getPID()
  {
    return bPID;
  }

  public short[] getPages()
  {
    return sPages;
  }

  public short getTotalNumberOfPages()
  {
    return sNoPages;
  }
}

