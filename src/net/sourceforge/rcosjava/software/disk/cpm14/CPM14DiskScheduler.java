package net.sourceforge.rcosjava.software.disk.cpm14;

import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.software.disk.DiskScheduler;
import net.sourceforge.rcosjava.software.disk.DiskRequest;
import net.sourceforge.rcosjava.software.disk.DiskQueueItem;
import net.sourceforge.rcosjava.software.util.FIFOQueue;
import net.sourceforge.rcosjava.software.interrupt.CPM14DiskInterruptHandler;
import net.sourceforge.rcosjava.hardware.disk.Disk;

/**
 * A simulated CPM14 disk with simple processing of requests.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @version 1.00 $Date$
 * @created 10th September, 1999
 */
public class CPM14DiskScheduler implements DiskScheduler
{
  //Defaults based on size of 247808 bytes and 1024 bytes per sector
  private final int SECTOR_SIZE = 1024;
  private final int SECTORS_PER_TRACK = 11;
  private final int TRACKS = 22;
  // Variables
  private CPM14DiskInterruptHandler interruptHandler;
  private FIFOQueue requestQueue;
  private DiskQueueItem currentRequest;
  private boolean busy;
  private Disk disk;

  // Constructor. Sets up variables and registers with the post office.
  public CPM14DiskScheduler(String myID, MessageHandler mhPostOffice)
  {
    //super(myID, mhPostOffice);
    requestQueue = new FIFOQueue (10, 5);
    // Create and Register the InterruptHandler.
    //interruptHandler = new CPM14DiskInterruptHandler( id, mvPostOffice,
    //                                     id+":INT", id);
    //Message mvIHReg = new Message ( id, "KERNEL", "RegisterInterruptHandler",
    //                                  interruptHandler);
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
      System.err.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }

  public void queueRequest(String mvSource, DiskRequest mvTheRequest)
  {
    DiskQueueItem mvTheQueueItem = new DiskQueueItem(mvSource, mvTheRequest);
    requestQueue.insert(mvTheQueueItem);
  }

  public void processQueue()
  {
    if (!busy)
    {
      busy = true;
      if (!requestQueue.queueEmpty())
      {
        currentRequest = (DiskQueueItem) requestQueue.retrieve();
        // Calc time.
        int mvTime = 10;
        // Register Interrupt.
//        Message mvIntMessage = new Message (id, "CPU", "INTERRUPT", id+":INT");
//        SendMessage( mvIntMessage );
      }
      else
      {
        busy = false;
      }
    }
  }

  // Complete request.
  public void currentRequestComplete()
  {
    byte[] mvReturnData;

    if (currentRequest.getDiskRequest().Data == null)
    {
      mvReturnData = readBlock(currentRequest.getDiskRequest().DiskBlock);
    }
    else
    {
      mvReturnData = null;
      writeBlock (currentRequest.getDiskRequest().DiskBlock,
                         currentRequest.getDiskRequest().Data);
    }
    DiskRequest mvTheReturnData = new DiskRequest (
                                     currentRequest.getDiskRequest().FSRequestID,
                                     currentRequest.getDiskRequest().DiskBlock,
			             mvReturnData);
//    Message mvTheMessage = new Message( id,
//					currentRequest.cvSource,
//					"DiskRequestComplete",
//					mvTheReturnData );
 //   SendMessage ( mvTheMessage );
    currentRequest = null;
    busy = false;
  }

  // This proceedure is part of the Simulation. In a real system, this
  // is where the device driver talks to the disk.
  public byte[] readBlock(int mvBlockNumber)
  {
    int mvCounter;

    int mvBlockOffset = (mvBlockNumber * blockSize);
    byte[] mvReadData = new byte[blockSize];

    for (mvCounter = 0; mvCounter < blockSize; mvCounter++)
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

    int mvBlockOffset = (mvBlockNumber * blockSize);

    for (mvCounter = 0; mvCounter < blockSize; mvCounter++)
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
      /*if ( cvDiskData[(Block*blockSize)+X] == (byte)0x1A)
      {
        System.out.print("<EOF>");
      }
      System.out.print((char)cvDiskData[(Block*blockSize)+X]);*/
    }
  }
}
