// ************************************************************************//
// FILENAME : CPM14DeviceTableEntry.java
// PACKAGE  : FileSystem.CPM14
// AUTHOR   : Brett Carter
// PURPOSE  : To contain the data for an entry in the CPM14 FS Mount table.
// HISTORY  : 25/3/96 Created.
//
// ************************************************************************//

package net.sourceforge.rcosjava.software.filesystem.cpm14;

import java.util.HashMap;

class CPM14DeviceTableEntry
{
  // public int DeviceNumber // Device number is the key of the table.
  public String deviceName;
  public byte[] directoryTable;
  public HashMap openFileNames;
  public int status;
  public boolean[] blockList;
  public int numberOfFreeBlocks;
  public boolean[] dirEntList;
  public int numberOfFreeEntries;

  public CPM14DeviceTableEntry()
  {
    deviceName = null;
    directoryTable = null;
    openFileNames = null;
    status = -1;
    blockList = null;
    numberOfFreeBlocks = -1;
    dirEntList = null;
    numberOfFreeEntries = -1;
  }
}

