package org.rcosjava.software.animator.ipc;

import java.awt.*;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.ipc.SharedMemory;
import org.rcosjava.software.memory.MemoryRequest;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * Receives messages from MMU and IPC and manipulates memoryManagerFrame based
 * on messages received.
 * <P>
 * @author Andrew Newman.
 * @created 10th of January 1997
 * @version 1.00 $Date$
 */
public class IPCManagerAnimator extends RCOSAnimator
{
  /**
   * Description of the Field
   */
  private final static String MESSENGING_ID = "IPCManagerAnimator";
  /**
   * Description of the Field
   */
  private IPCManagerFrame myFrame;

  /**
   * Constructor for the IPCManagerAnimator object
   *
   * @param postOffice Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param images Description of Parameter
   */
  public IPCManagerAnimator(AnimatorOffice postOffice, int x, int y,
      Image[] images)
  {
    super(MESSENGING_ID, postOffice);
    myFrame = new IPCManagerFrame(x, y, images, this);
    myFrame.pack();
    myFrame.setSize(x, y);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    myFrame.setupLayout(c);
  }

  /**
   * Description of the Method
   */
  public void disposeFrame()
  {
    myFrame.dispose();
  }

  /**
   * Description of the Method
   */
  public void showFrame()
  {
    myFrame.setVisible(true);
  }

  /**
   * Description of the Method
   */
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
   *      of memory.
   */
  public void readingMemory(MemoryRequest requestedMemory)
  {
    myFrame.readingMemory(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
  }

  /**
   * Calls writingMemory on the associated frame.
   *
   * @param requestedMemory Description of Parameter
   */
  public void writingMemory(MemoryRequest requestedMemory)
  {
    myFrame.writingMemory(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
  }

  /**
   * Calls finishedReadingMemory on the associated frame.
   *
   * @param requestedMemory the data structure containing the process id and the
   *      type of memory that has just been read.
   */
  public void finishedReadingMemory(MemoryRequest requestedMemory)
  {
    myFrame.finishedReadingMemory(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
  }

  /**
   * Calls finishedWritingMemory on the associated frame.
   *
   * @param requestedMemory the data structure containing the process id and the
   *      type of memory that is being written to.
   */
  public void finishedWritingMemory(MemoryRequest requestedMemory)
  {
    myFrame.finishedWritingMemory(requestedMemory.getPID(),
        requestedMemory.getMemoryType());
  }

  /**
   * @param semaphoreId Description of Parameter
   * @param processId Description of Parameter
   * @param value Description of Parameter
   */
  public void semaphoreCreated(String semaphoreId, int processId, int value)
  {
    myFrame.semaphoreCreated(semaphoreId, processId, value);
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param processId Description of Parameter
   * @param value Description of Parameter
   */
  public void semaphoreOpened(String semaphoreId, int processId, int value)
  {
    myFrame.semaphoreOpened(semaphoreId, processId, value);
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param processId Description of Parameter
   * @param value Description of Parameter
   */
  public void semaphoreWaiting(String semaphoreId, int processId, int value)
  {
    myFrame.semaphoreWaiting(semaphoreId, processId, value);
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param processId Description of Parameter
   * @param value Description of Parameter
   */
  public void semaphoreSignalled(String semaphoreId, int processId, int value)
  {
    myFrame.semaphoreSignalled(semaphoreId, processId, value);
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param processId Description of Parameter
   * @param value Description of Parameter
   */
  public void semaphoreClosed(String semaphoreId, int processId, int value)
  {
    myFrame.semaphoreClosed(semaphoreId, processId, value);
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param processId
   * @param memory Description of Parameter
   */
  public void sharedMemoryCreated(String sharedMemoryId,
      MemoryReturn memoryReturn, Memory memory)
  {
    myFrame.sharedMemoryCreated(sharedMemoryId, memoryReturn, memory);
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param processId Description of Parameter
   */
  public void sharedMemoryOpened(String sharedMemoryId, int processId)
  {
    myFrame.sharedMemoryOpened(sharedMemoryId, processId);
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param processId Description of Parameter
   */
  public void sharedMemoryClosed(String sharedMemoryId, int processId)
  {
    myFrame.sharedMemoryClosed(sharedMemoryId, processId);
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param memory Description of Parameter
   */
  public void sharedMemoryRead(String sharedMemoryId, MemoryReturn memoryReturn,
      Memory memory)
  {
    myFrame.sharedMemoryRead(sharedMemoryId, memoryReturn, memory);
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param memory Description of Parameter
   */
  public void sharedMemoryWrote(String sharedMemoryId, Memory memory)
  {
    myFrame.sharedMemoryWrote(sharedMemoryId, memory);
  }
}

