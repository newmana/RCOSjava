package org.rcosjava.software.filesystem.msdos;

import java.util.HashMap;

class MSDOSDeviceTableEntry{

  // public int DeviceNumber // Device number is the key of the table.
  public String deviceName;
  public byte[] directoryTable;
  public HashMap openFileNames;
  public int status;
  public boolean[] blockList;
  public int numberOfFreeBlocks;
  public boolean[] dirEntList;
  public int numberOfFreeEntries;

  /**
   * Constructor for the MSDOSDeviceTableEntry object
   */
  public MSDOSDeviceTableEntry()
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
