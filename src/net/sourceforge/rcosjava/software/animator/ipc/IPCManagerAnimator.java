package net.sourceforge.rcosjava.software.animator.ipc;

import java.awt.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.RCOSList;
import net.sourceforge.rcosjava.software.animator.support.mtgos.MTGO;
import net.sourceforge.rcosjava.software.animator.support.positions.Position;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;

/**
 * Receives messages from MMU and IPC and manipulates memoryManagerFrame
 * based on messages received.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 10th of January 1997
 */
public class IPCManagerAnimator extends RCOSAnimator
{
  private IPCManagerFrame myFrame;
  private static final String MESSENGING_ID = "IPCManagerAnimator";

  public IPCManagerAnimator (AnimatorOffice postOffice, int x, int y,
    Image[] images)
  {
    super(MESSENGING_ID, postOffice);
    myFrame = new IPCManagerFrame(x, y, images, this);
    myFrame.pack();
    myFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    myFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    myFrame.dispose();
  }

  public void showFrame()
  {
    myFrame.setVisible(true);
  }

  public void hideFrame()
  {
    myFrame.setVisible(false);
  }

  /**
   * Calls allocatedPages on the associated frame.
   *
   * @param returnedMemory the object representing the allocated memory.
   */
  public void allocatedPages(MemoryReturn returnedMemory)
  {
    myFrame.allocatedPages(returnedMemory);
  }

  /**
   * Calls deallocatedPages on the associated frame.
   *
   * @param returnedMemory the object representing the deallocated memory.
   */
  public void deallocatedPages(MemoryReturn returnedMemory)
  {
    myFrame.deallocatedPages(returnedMemory);
  }

  /**
   * Calls readingMemory on the associated frame.
   *
   * @param requestedMemory the data structure with the process id and the type
   * of memory.
   */
  public void readingMemory(MemoryRequest requestedMemory)
  {
    myFrame.readingMemory(requestedMemory.getPID(),
      requestedMemory.getMemoryType());
  }

  /**
   * Calls writingMemory on the associated frame.
   *
   * @param requestMemory the data structure with the process id and the type
   * of memory.
   */
  public void writingMemory(MemoryRequest requestedMemory)
  {
    myFrame.writingMemory(requestedMemory.getPID(),
      requestedMemory.getMemoryType());
  }

  /**
   * Calls finishedReadingMemory on the associated frame.
   *
   * @param requestedMemory the data structure containing the process id and
   * the type of memory that has just been read.
   */
  public void finishedReadingMemory(MemoryRequest requestedMemory)
  {
    myFrame.finishedReadingMemory(requestedMemory.getPID(),
      requestedMemory.getMemoryType());
  }

  /**
   * Calls finishedWritingMemory on the associated frame.
   *
   * @param requestedMemory the data structure containing the process id and
   * the type of memory that is being written to.
   */
  public void finishedWritingMemory(MemoryRequest requestedMemory)
  {
    myFrame.finishedWritingMemory(requestedMemory.getPID(),
      requestedMemory.getMemoryType());
  }

  /**
   * Calls semQueueAdd with a given process id.
   */
  public void semaphoreCreated(String semaphoreId, int processId, int value)
  {
    myFrame.semQueueAdd(semaphoreId, processId, value);
  }

  public void semaphoreClosed()
  {
   myFrame.semQueueRemove();
  }

  public void semaphoreWaiting()
  {
  }

  public void sempahoreSignalled()
  {
  }

  public void sharedMemoryOpen()
  {
  }

  public void sharedMemoryClosed()
  {
  }

  public void sharedMemoryRead()
  {
  }

  public void sharedMemoryWrite()
  {
  }

  public synchronized void processMessage (AnimatorMessageAdapter aMsg)
  {
  }

  public synchronized void processMessage(UniversalMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println(this + "- exception: "+e);
    }
  }
}

