//############################################################################
// Class    : CPM14FIDTableEntry
// Author   : Brett Carter
// Date     : 25/3/96 Created.
//
// Purpose  : To contain the data for an entry in the CPM14 FS File ID table.

package net.sourceforge.rcosjava.software.filesystem.cpm14;

public class CPM14FIDTableEntry
{
  public String Filename;
  public int Device;  // Use device number to access the device table.
  public int FileNumber;     // Number of current directory entry in device.
  public int CurrentPosition;
  public byte[] Buffer;
  public int CurrentDiskBlock;
  public int Mode;


  public CPM14FIDTableEntry()
  {
    Filename = null;
    Device = -1;
    FileNumber = -1;
    CurrentPosition = -1;
    Buffer = null;
    CurrentDiskBlock = -1;
    Mode = 0;
  }

}

