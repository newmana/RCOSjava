// ************************************************************************//
// FILENAME : CPM14DeviceTableEntry.java
// PACKAGE  : FileSystem.CPM14
// AUTHOR   : Brett Carter
// PURPOSE  : To contain the data for an entry in the CPM14 FS Mount table.
// HISTORY  : 25/3/96 Created.
//            
// ************************************************************************//

package Software.FileSystem.CPM14;

import java.util.Hashtable;

class CPM14DeviceTableEntry extends Object
{
  // public int DeviceNumber // Device number is the key of the table.
  public String DeviceName;
  public byte[] DirectoryTable;
  public Hashtable OpenFileNames;
  public int Status;
  public boolean[] BlockList;
  public int NumberOfFreeBlocks;
  public boolean[] DirEntList;
  public int NumberOfFreeEntries;

  public CPM14DeviceTableEntry()
  {
    DeviceName = null;
    DirectoryTable = null;
    OpenFileNames = null;
    Status = -1;
    BlockList = null;
    NumberOfFreeBlocks = -1;
    DirEntList = null;
    NumberOfFreeEntries = -1;
  }
}

