//*******************************************************************/
// FILENAME : DiskRequest
// AUTHOR   : Brett Carter
// DATE     : 1/03/96
// PURPOSE  : This object contains the data passed between the Disk
//            Scheduler and the File System. (Both Ways, as Request
//            and it's completion notification.
//
//
// -> Disk
// --------
// Structure holds the FSRequestID (int), the Block Number (int), and
// a pointer to the data. If the pointer is null, it is a read
// operation.
//
// <-Disk
// ------
// If the pointer is null, it was a write. if Block number is < 0, an
// error occurred.
// Data is all public. Avoids having to call methods and increases
// speed.
//*******************************************************************/

package net.sourceforge.rcosjava.software.disk;

public class DiskRequest
{
  public int FSRequestID;
  public int DiskBlock;
  public byte[] Data;

  public DiskRequest(int Req, int Block, byte[] theData)
  {
    FSRequestID = Req;
    DiskBlock = Block;
    Data = theData;
  }

  public DiskRequest()
  {
    FSRequestID = -1;
    DiskBlock = -1;
    Data = null;
  }
}

