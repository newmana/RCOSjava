//*************************************************************************//
// FILENAME : DiskManager.java
// PACKAGE  : Hardware.Disk
// PURPOSE  : Handles the creation/deletion of physical disks, their
//            scheduler and other attributes.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/08/99 Created.
//*************************************************************************//

package net.sourceforge.rcosjava.software.disk;

import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.software.util.FIFOQueue;
import net.sourceforge.rcosjava.software.interrupt.CPM14DiskInterruptHandler;

public class DiskManager extends OSMessageHandler
{
  private static final String MESSENGING_ID = "DiskManager";

  // Constructor. Sets up variables and registers with the post office.
  public DiskManager(OSOffice mhPostOffice)
  {
    super(MESSENGING_ID, mhPostOffice);
    //cvRequestQueue = new FIFOQueue (10, 5);
    // Create and Register the InterruptHandler.
    //cvIntHandler = new CPM14DiskInterruptHandler( id, mvPostOffice,
    //                                     id+":INT", id);
    //Message mvIHReg = new Message ( id, "KERNEL", "RegisterInterruptHandler",
    //                                  cvIntHandler);
  }

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

  public void queueRequest(String mvSource, DiskRequest mvTheRequest)
  {
//    System.out.println("Inserting Item in queue."); // DEBUG
//    System.out.println("  From      : "+mvSource); // DEBUG
//    System.out.println("  DiskBlock : "+mvTheRequest.DiskBlock); // DEBUG
    DiskQueueItem mvTheQueueItem = new DiskQueueItem(mvSource, mvTheRequest);
    //cvRequestQueue.insert(mvTheQueueItem);
  }

  public void processQueue()
  {
//    System.out.println("CPM14Disk: ProcessQueue - Starting"); // DEBUG
    /*if (!cvBusy)
    {
      cvBusy = true;
      if (!cvRequestQueue.queueEmpty())
      {
//        System.out.println("Setting current request"); // DEBUG

        cvCurrentRequest = (DiskQueueItem)cvRequestQueue.retrieve();
        // Calc time.
        int mvTime = 10;

        // Register Interrupt.
//        System.out.println("Registering interrupt."); // DEBUG

//        Message mvIntMessage = new Message (id, "CPU", "INTERRUPT", id+":INT");
//        SendMessage( mvIntMessage );
      }
      else
      {
        cvBusy = false;
      }
    }*/
//    System.out.println("CPM14Disk: ProcessQueue - End"); // DEBUG
  }

  // Complete request.
  public void currentRequestComplete()
  {
//    System.out.println("CPM14Disk: ReqComp - Starting"); // DEBUG
    byte[] mvReturnData;

    /*if (cvCurrentRequest.getDiskRequest().Data == null)
    //{
//      System.out.println("Reading Block"); // DEBUG
      mvReturnData = readBlock( cvCurrentRequest.getDiskRequest().DiskBlock );
    }
    else
    {
//      System.out.println("Writing Block"); // DEBUG
      mvReturnData = null;
      writeBlock (cvCurrentRequest.getDiskRequest().DiskBlock,
                         cvCurrentRequest.getDiskRequest().Data );
    }*/
//    DiskRequest mvTheReturnData = new DiskRequest (
                                     //cvCurrentRequest.getDiskRequest().FSRequestID,
                                     //cvCurrentRequest.getDiskRequest().DiskBlock,
//			             mvReturnData);
//    System.out.println("Sending the data back"); // DEBUG

//    Message mvTheMessage = new Message( id,
//					cvCurrentRequest.cvSource,
//					"DiskRequestComplete",
//					mvTheReturnData );
 //   SendMessage ( mvTheMessage );
//    cvCurrentRequest = null;
 //   cvBusy = false;
//    System.out.println("CMP14Disk: ReqComp - end"); // DEBUG
  }

  // Note: This proceedure is part of the Simulation. In a real system, this
  // is where the device driver talks to the disk.
  //public byte[] readBlock(int mvBlockNumber)
  //{
/*    int mvCounter;

    int mvBlockOffset = (mvBlockNumber * cvBlockSize);
    byte[] mvReadData = new byte[cvBlockSize];

    for (mvCounter = 0; mvCounter < cvBlockSize; mvCounter++)
    {
      mvReadData[mvCounter] = cvDiskData[mvCounter + mvBlockOffset];
    }
    return mvReadData;*/
  //}

  // Note: This proceedure is part of the Simulation. In a real system, this
  // is where the device driver talks to the disk.
  public void writeBlock(int mvBlockNumber, byte[] mvWriteData)
  {
/*    int mvCounter;

    int mvBlockOffset = (mvBlockNumber * cvBlockSize);

    for (mvCounter = 0; mvCounter < cvBlockSize; mvCounter++)
    {
      cvDiskData[mvCounter + mvBlockOffset] = mvWriteData[mvCounter];
    }*/
  }

  // Used for debugging. Will dump the specified block to the screen.
  // Also part of the simulation.
  public void dump(int Block)
  {
/*    int X;
    for (X = 0; X<1024; X++)
    {
      if ( cvDiskData[(Block*cvBlockSize)+X] == (byte)0x1A)
      {
        System.out.print("<EOF>");
      }
      System.out.print((char)cvDiskData[(Block*cvBlockSize)+X]);
    }
    System.out.println("");*/
  }
}
