package org.rcosjava.software.animator.ipc;

import java.awt.*;
import javax.swing.ImageIcon;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.animator.RCOSPanel;
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
   * Unique identifier to register to the animator post office.
   */
  private final static String MESSENGING_ID = "IPCManagerAnimator";

  /**
   * The panel in which to display all of the results to.
   */
  private IPCManagerPanel panel;

  /**
   * Constructor for the IPCManagerAnimator object
   *
   * @param postOffice Description of Parameter
   * @param images Description of Parameter
   */
  public IPCManagerAnimator(AnimatorOffice postOffice, ImageIcon[] images)
  {
    super(MESSENGING_ID, postOffice);
    panel = new IPCManagerPanel(images, this);
  }

  /**
   * Setup the layout of the frame (menus, etc).
   *
   * @param c the parent component.
   */
  public void setupLayout(Component c)
  {
    panel.setupLayout(c);
  }

  /**
   * Returns the panel of this component.
   *
   * @return the panel of this component.
   */
  public RCOSPanel getPanel()
  {
    return panel;
  }

  /**
   * @param semaphoreId Description of Parameter
   * @param processId Description of Parameter
   * @param value Description of Parameter
   */
  public void semaphoreCreated(String semaphoreId, int processId, int value)
  {
    panel.semaphoreCreated(semaphoreId, processId, value);
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
    panel.semaphoreOpened(semaphoreId, processId, value);
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
    panel.semaphoreWaiting(semaphoreId, processId, value);
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
    panel.semaphoreSignalled(semaphoreId, processId, value);
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
    panel.semaphoreClosed(semaphoreId, processId, value);
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
    panel.sharedMemoryCreated(sharedMemoryId, memoryReturn, memory);
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param processId Description of Parameter
   */
  public void sharedMemoryOpened(String sharedMemoryId, int processId)
  {
    panel.sharedMemoryOpened(sharedMemoryId, processId);
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param processId Description of Parameter
   */
  public void sharedMemoryClosed(String sharedMemoryId, int processId)
  {
    panel.sharedMemoryClosed(sharedMemoryId, processId);
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
    panel.sharedMemoryRead(sharedMemoryId, memoryReturn, memory);
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param memory Description of Parameter
   */
  public void sharedMemoryWrote(String sharedMemoryId, Memory memory)
  {
    panel.sharedMemoryWrote(sharedMemoryId, memory);
  }
}

