//############################################################################
// Class    : CPM14RequestTableEntry
// Author   : Brett Carter
// Date     : 25/3/96 Created.
//
// Purpose  : To contain the data for an entry in the CPM14 FS Request table.
//

package Software.FileSystem.CPM14;

class CPM14RequestTableEntry
{
  public String Source;
  public String Type;
  public int FSFileNum;
  public int FSMRequestID;
  public int Data; // used for queueing write data

  public CPM14RequestTableEntry()
  {
    Source = null;
    Type = null;
    FSFileNum = -1;
    FSMRequestID = -1;
    int Data = -1;
  }
}

