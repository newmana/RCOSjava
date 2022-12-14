package org.rcosjava.software.disk.cpm14;

import org.rcosjava.hardware.disk.Disk;
import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.postoffices.MessageHandler;
import org.rcosjava.software.disk.DiskQueueItem;
import org.rcosjava.software.disk.DiskRequest;
import org.rcosjava.software.disk.DiskScheduler;
import org.rcosjava.software.interrupt.CPM14DiskInterruptHandler;
import org.rcosjava.software.util.FIFOQueue;

/**
 * A simulated CPM14 disk with simple processing of requests.
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @created 10th September, 1999
 * @version 1.00 $Date$
 */
public class CPM14DiskScheduler implements DiskScheduler
{
  /**
   * Description of the Field
   */
  private static int blockSize = 1;

  /**
   * Description of the Field
   */
  private static int size = 1;

  /**
   * Defaults based on size of 247808 bytes and 1024 bytes per sector
   */
  private static final int SECTOR_SIZE = 1024;

  /**
   * Description of the Field
   */
  private static final int SECTORS_PER_TRACK = 11;

  /**
   * Description of the Field
   */
  private static final int TRACKS = 22;

  /**
   * Description of the Field
   */
  private CPM14DiskInterruptHandler interruptHandler;

  /**
   * Description of the Field
   */
  private FIFOQueue requestQueue;

  /**
   * Description of the Field
   */
  private DiskQueueItem currentRequest;

  /**
   * Description of the Field
   */
  private boolean busy;

  /**
   * Description of the Field
   */
  private Disk disk;

  // Constructor. Sets up variables and registers with the post office.
  /**
   * Constructor for the CPM14DiskScheduler object
   *
   * @param myID Description of Parameter
   * @param mhPostOffice Description of Parameter
   */
  public CPM14DiskScheduler(String myID, MessageHandler mhPostOffice)
  {
    //super(myID, mhPostOffice);
    requestQueue = new FIFOQueue(10, 5);
    // Create and Register the InterruptHandler.
    //interruptHandler = new CPM14DiskInterruptHandler( id, mvPostOffice,
    //                                     id+":INT", id);
    //Message mvIHReg = new Message ( id, "KERNEL", "RegisterInterruptHandler",
    //                                  interruptHandler);
  }

  /**
   * Description of the Method
   *
   * @param aMsg Description of Parameter
   */
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
      System.err.println("Error processing message: " + e);
      e.printStackTrace();
    }
  }

  /**
   * Description of the Method
   *
   * @param mvSource Description of Parameter
   * @param mvTheRequest Description of Parameter
   */
  public void queueRequest(String mvSource, DiskRequest mvTheRequest)
  {
    DiskQueueItem mvTheQueueItem = new DiskQueueItem(mvSource, mvTheRequest);

    requestQueue.insert(mvTheQueueItem);
  }

  /**
   * Description of the Method
   */
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

  /**
   * Complete request.
   */
  public void currentRequestComplete()
  {
    byte[] mvReturnData;

    if (currentRequest.getDiskRequest().getData() == null)
    {
      mvReturnData = readBlock(currentRequest.getDiskRequest().getDiskBlock());
    }
    else
    {
      mvReturnData = null;
      writeBlock(currentRequest.getDiskRequest().getDiskBlock(),
          currentRequest.getDiskRequest().getData());
    }

    DiskRequest mvTheReturnData = new DiskRequest(
        currentRequest.getDiskRequest().getRequestId(),
        currentRequest.getDiskRequest().getDiskBlock(),
        mvReturnData);

//    Message mvTheMessage = new Message( id,
//					currentRequest.cvSource,
//					"DiskRequestComplete",
//					mvTheReturnData );
//   SendMessage ( mvTheMessage );
    currentRequest = null;
    busy = false;
  }

  //
  /**
   * This proceedure is part of the Simulation. In a real system, this
   * is where the device driver talks to the disk.
   *
   * @param mvBlockNumber Description of Parameter
   * @return Description of the Returned Value
   */
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

  /**
   * Note: This proceedure is part of the Simulation. In a real system, this
   * is where the device driver talks to the disk.
   *
   * @param mvBlockNumber Description of Parameter
   * @param mvWriteData Description of Parameter
   */
  public void writeBlock(int mvBlockNumber, byte[] mvWriteData)
  {
    int mvCounter;

    int mvBlockOffset = (mvBlockNumber * blockSize);

    for (mvCounter = 0; mvCounter < blockSize; mvCounter++)
    {
      //cvDiskData[mvCounter + mvBlockOffset] = mvWriteData[mvCounter];
    }
  }

  /**
   * Used for debugging. Will dump the specified block to the screen.
   * Also part of the simulation.
   *
   * @param Block Description of Parameter
   */
  public void dump(int Block)
  {
    int X;

    for (X = 0; X < 1024; X++)
    {
      /*if ( cvDiskData[(Block*blockSize)+X] == (byte)0x1A)
      {
        System.out.print("<EOF>");
      }
      System.out.print((char)cvDiskData[(Block*blockSize)+X]);*/
    }
  }
}
