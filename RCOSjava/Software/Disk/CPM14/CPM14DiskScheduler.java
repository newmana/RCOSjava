//*************************************************************************//
// FILENAME : CPM14DiskScheduler.java
// PACKAGE  : Disk.CPM14
// PURPOSE  : A simulated CPM14 disk with simple processing of requests.
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 25/03/96 Created.
//            10/08/99 Moved to package and removed message handling.
//*************************************************************************//

package Software.Disk.CPM14;

import MessageSystem.Messages.MessageAdapter;
import MessageSystem.PostOffices.MessageHandler;
import MessageSystem.Messages.OS.OSMessageAdapter;
import Software.Disk.DiskScheduler;
import Software.Disk.DiskRequest;
import Software.Disk.DiskQueueItem;
import Software.Util.FIFOQueue;
import Software.Interrupt.CPM14DiskInterruptHandler;
import Hardware.Disk.CPM14.CPM14Harddrive;

public class CPM14DiskScheduler implements DiskScheduler
{
  //Defaults based on size of 247808 bytes and 1024 bytes per sector
  private final int SECTOR_SIZE = 1024;
	private final int SECTORS_PER_TRACK = 11;
	private final int TRACKS = 22;
  // Variables
  private CPM14DiskInterruptHandler cvIntHandler;
  private FIFOQueue cvRequestQueue;
  private DiskQueueItem cvCurrentRequest;
  private boolean cvBusy;
  private static int cvBlockSize = 0;
  private static int cvSize = 0;
  // Storage objects.
  private CPM14Harddrive cvDiskDrive = new CPM14Harddrive(TRACKS, 
		SECTORS_PER_TRACK, SECTOR_SIZE);

  // Constructor. Sets up variables and registers with the post office.
  public CPM14DiskScheduler(String myID, MessageHandler mhPostOffice)
  {
    //super(myID, mhPostOffice);
    cvRequestQueue = new FIFOQueue (10, 5);
    // Create and Register the InterruptHandler.
    //cvIntHandler = new CPM14DiskInterruptHandler( id, mvPostOffice,
    //                                     id+":INT", id);   
    //Message mvIHReg = new Message ( id, "KERNEL", "RegisterInterruptHandler",
    //                                  cvIntHandler);
  }

  public void processMessage(MessageAdapter aMsg)
  {
    OSMessageAdapter osmMessage;
//    if ( mvTheMessage.getType().equalsIgnoreCase("DiskRequestComplete"))
    //{
//      System.out.println("Handleing a DiskRequestComplete"); // DEBUG

    //  CurrentRequestComplete();
  //    ProcessQueue();
//    }
//    else if ( mvTheMessage.getType().equalsIgnoreCase("DiskRequest"))
    //{
//      System.out.println("Handleing a DiskRequest."); // DEBUG
//      QueueRequest ( mvTheMessage.source, (DiskRequest)mvTheMessage.body );
//      ProcessQueue();
    //}
//    System.out.println("CPM14Disk: ProcessMessage - Finish"); // DEBUG

    try
    {          
      osmMessage = (OSMessageAdapter) Class.forName(aMsg.getType()).newInstance();
      osmMessage.doMessage(this);        
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }  
  }

  public synchronized void queueRequest(String mvSource, DiskRequest mvTheRequest)
  {
    DiskQueueItem mvTheQueueItem = new DiskQueueItem(mvSource, mvTheRequest);
    cvRequestQueue.insert(mvTheQueueItem);
  }

  public synchronized void processQueue()
  {
    if (!cvBusy)
    {
      cvBusy = true;
      if (!cvRequestQueue.queueEmpty()) 
      {
        cvCurrentRequest = (DiskQueueItem) cvRequestQueue.retrieve();
        // Calc time.
        int mvTime = 10;
        // Register Interrupt.
//        Message mvIntMessage = new Message (id, "CPU", "INTERRUPT", id+":INT");
//        SendMessage( mvIntMessage );
      }
      else
      {
        cvBusy = false;
      }
    }
  }

  // Complete request.
  public synchronized void currentRequestComplete()
  {
    byte[] mvReturnData;

    if (cvCurrentRequest.getDiskRequest().Data == null)
    {
      mvReturnData = readBlock(cvCurrentRequest.getDiskRequest().DiskBlock);
    }
    else 
    {
      mvReturnData = null;
      writeBlock (cvCurrentRequest.getDiskRequest().DiskBlock,
                         cvCurrentRequest.getDiskRequest().Data);
    }
    DiskRequest mvTheReturnData = new DiskRequest ( 
                                     cvCurrentRequest.getDiskRequest().FSRequestID,
                                     cvCurrentRequest.getDiskRequest().DiskBlock,
			             mvReturnData);
//    Message mvTheMessage = new Message( id,
//					cvCurrentRequest.cvSource,
//					"DiskRequestComplete",
//					mvTheReturnData );
 //   SendMessage ( mvTheMessage );
    cvCurrentRequest = null;
    cvBusy = false;
  }

  // This proceedure is part of the Simulation. In a real system, this 
  // is where the device driver talks to the disk.
  public byte[] readBlock(int mvBlockNumber)
  {
    int mvCounter;

    int mvBlockOffset = (mvBlockNumber * cvBlockSize);
    byte[] mvReadData = new byte[cvBlockSize];

    for (mvCounter = 0; mvCounter < cvBlockSize; mvCounter++)
    {
      //mvReadData[mvCounter] = cvDiskData[mvCounter + mvBlockOffset];
    }
    return mvReadData;
  }

  // Note: This proceedure is part of the Simulation. In a real system, this 
  // is where the device driver talks to the disk.
  public void writeBlock(int mvBlockNumber, byte[] mvWriteData)
  {
    int mvCounter;

    int mvBlockOffset = (mvBlockNumber * cvBlockSize);

    for (mvCounter = 0; mvCounter < cvBlockSize; mvCounter++)
    {
      //cvDiskData[mvCounter + mvBlockOffset] = mvWriteData[mvCounter];
    }
  }

  // Used for debugging. Will dump the specified block to the screen.
  // Also part of the simulation.
  public void dump(int Block)
  {
    int X;
    for (X = 0; X<1024; X++)
    {
      /*if ( cvDiskData[(Block*cvBlockSize)+X] == (byte)0x1A)
      {
        System.out.print("<EOF>");
      }
      System.out.print((char)cvDiskData[(Block*cvBlockSize)+X]);*/
    }
    System.out.println("");
  }
}
