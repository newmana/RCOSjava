package org.rcosjava.software.animator.ipc;

import java.awt.Component;
import java.util.HashMap;
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
   * Map of current semaphores.
   */
  private HashMap semaphoreMap;

  /**
   * Map of current shared memory segments.
   */
  private HashMap sharedMemoryMap;

  /**
   * The current id of the semaphore selected.
   */
  private String selectedSemaphoreName;

  /**
   * The current id of the shared memory selected.
   */
  private String selectedSharedMemoryName;

  /**
   * Constructor for the IPCManagerAnimator object
   *
   * @param postOffice the post office to register to.
   * @param images any images for the IPC to use.
   */
  public IPCManagerAnimator(AnimatorOffice postOffice, ImageIcon[] images)
  {
    super(MESSENGING_ID, postOffice);
    semaphoreMap = new HashMap();
    sharedMemoryMap = new HashMap();
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
   * New semaphore created.  Assumes the semaphore does not already exist.
   *
   * @param semaphoreId the unique id of the semaphore that created.
   * @param processId the process id that created semaphore.
   * @param value the initial value of the semaphore.
   */
  public void semaphoreCreated(String semaphoreId, int processId, int value)
  {
    panel.semaphoreCreated(semaphoreId, processId, value);
  }

  /**
   * Open the semaphore.  Requires that the semaphore be created.
   *
   * @param semaphoreId the unique id of the semaphore that is opened.
   * @param processId the process id that opened the semaphore.
   * @param value the value of the semaphore.
   */
  public void semaphoreOpened(String semaphoreId, int processId, int value)
  {
    panel.semaphoreOpened(semaphoreId, processId, value);
  }

  /**
   * Wait on the semaphore.  Requires that the semaphore be opened and created.
   *
   * @param semaphoreId the unique id of the semaphore to wait on.
   * @param processId the process id that is waiting on the semaphore.
   * @param value the value of the semaphore.
   */
  public void semaphoreWaiting(String semaphoreId, int processId, int value)
  {
    panel.semaphoreWaiting(semaphoreId, processId, value);
  }

  /**
   * Signal the semaphore.  Requires that a semaphore is being opened and
   * created and usually means that something is waiting on it.
   *
   * @param semaphoreId the unique id of the semaphore to signal on.
   * @param processId the process id that is signalling the semaphore.
   * @param value the value of the semaphore.
   */
  public void semaphoreSignalled(String semaphoreId, int processId, int value)
  {
    panel.semaphoreSignalled(semaphoreId, processId, value);
  }

  /**
   * Close the semaphore.  Assumes that the semaphore exists and that any
   * processes will have finished using it.
   *
   * @param semaphoreId the unique id of the semaphore to close.
   * @param processId the process id that is closing the semaphore.
   * @param value the value of the semaphore.
   */
  public void semaphoreClosed(String semaphoreId, int processId, int value)
  {
    panel.semaphoreClosed(semaphoreId, processId, value);
  }

  /**
   * Called when a memory segment has been created.
   *
   * @param sharedMemoryId the unique id of the shared memeory segment created.
   * @param processId the process id that is creating the shared memory segment.
   * @param memory the memory object being shared.
   */
  public void sharedMemoryCreated(String sharedMemoryId,
      MemoryReturn memoryReturn, Memory memory)
  {
    panel.sharedMemoryCreated(sharedMemoryId, memoryReturn, memory);
  }

  /**
   * Called when a memory segment is being opened by a process.  It's assumed
   * that it's already created.
   *
   * @param sharedMemoryId the unique id of the shared memory segment opened.
   * @param processId the process id that is opening the shared memory segment.
   */
  public void sharedMemoryOpened(String sharedMemoryId, int processId)
  {
    panel.sharedMemoryOpened(sharedMemoryId, processId);
  }

  /**
   * Called when a memory segment is being closed by a process.  It's assumed
   * that all process are not accessing it and that it does exist.
   *
   * @param sharedMemoryId the unique id of the shared memory segment to close.
   * @param processId the process id that is closing the shared memory segment.
   */
  public void sharedMemoryClosed(String sharedMemoryId, int processId)
  {
    panel.sharedMemoryClosed(sharedMemoryId, processId);
  }

  /**
   * Called when a memory segment is being read by a process.  Assumes that the
   * process already exists.
   *
   * @param sharedMemoryId the unique id of the shared memory segment to read.
   * @param memoryReturn the resultant object from a memory read.
   * @param memory the current memory object being read.
   */
  public void sharedMemoryRead(String sharedMemoryId, MemoryReturn memoryReturn,
      Memory memory)
  {
    panel.sharedMemoryRead(sharedMemoryId, memoryReturn, memory);
  }

  /**
   * Called when a memory segment has been written by a process.  Assumes that
   * the process already exists.
   *
   * @param sharedMemoryId the unique id of the shared memory segment written.
   * @param memory the current memory object being written.
   */
  public void sharedMemoryWrote(String sharedMemoryId, Memory memory)
  {
    panel.sharedMemoryWrote(sharedMemoryId, memory);
  }

  /**
   * Returns the currently selected semaphore.
   *
   * @return the currently selected semaphore.
   */
  String getSelectedSemaphoreName()
  {
    return selectedSemaphoreName;
  }

  /**
   * Sets the selected semaphore.
   *
   * @param newSelectedSemaphoreName new semaphore.
   */
  void setSelectedSemaphoreName(String newSelectedSemaphoreName)
  {
    selectedSemaphoreName = newSelectedSemaphoreName;
  }

  /**
   * Returns the currently selected shared memory.
   *
   * @return the currently selected shared memory.
   */
  String getSelectedSharedMemoryName()
  {
    return selectedSharedMemoryName;
  }

  /**
   * Sets the selected shared memory.
   *
   * @param newSelectedSharedMemoryName new semaphore.
   */
  void setSelectedSharedMemoryName(String newSelectedSharedMemoryName)
  {
    selectedSharedMemoryName = newSelectedSharedMemoryName;
  }

  /**
   * Sets the semaphore map consisting of the name of the semaphore and a
   * graphic object.
   *
   * @param semaphoreId the name of the semaphore.
   * @param graphic the semaphore graphic.
   */
  void setSemaphoreGraphic(String semaphoreId,
      SemaphoreSharedMemoryGraphic graphic)
  {
    semaphoreMap.put(semaphoreId, graphic);
  }

  /**
   * Gets the semaphore graphic based on the semaphore id.
   *
   * @param semaphoreId the name of the semaphore.
   * @return graphic the semaphore graphic.
   */
  SemaphoreSharedMemoryGraphic getSemaphoreGraphic(String semaphoreId)
  {
    return (SemaphoreSharedMemoryGraphic) semaphoreMap.get(semaphoreId);
  }

  /**
   * Sets the shared memory map consisting of the name of the shared memory and
   * a graphic object.
   *
   * @param sharedMemoryId the name of the shared memory.
   * @param graphic the shared memory graphic.
   */
  void setSharedMemoryGraphic(String sharedMemoryId,
      SemaphoreSharedMemoryGraphic graphic)
  {
    sharedMemoryMap.put(sharedMemoryId, graphic);
  }

  /**
   * Gets the graphic based on the shared memory id.
   *
   * @param sharedMemoryId the name of the shared memory.
   * @return graphic the semaphore graphic.
   */
  SemaphoreSharedMemoryGraphic getSharedMemoryGraphic(String sharedMemoryId)
  {
    return (SemaphoreSharedMemoryGraphic) sharedMemoryMap.get(sharedMemoryId);
  }

  /**
   * Returns the currently selected semaphore.
   *
   * @return the currently selected semaphore.
   */
  SemaphoreSharedMemoryGraphic getCurrentSemaphore()
  {
    return getSemaphoreGraphic(getSelectedSemaphoreName());
  }

  /**
   * Returns the currently selected shared memory.
   *
   * @return the currently selected shared memory.
   */
  SemaphoreSharedMemoryGraphic getCurrentSharedMemory()
  {
    return getSharedMemoryGraphic(getSelectedSharedMemoryName());
  }
}