package org.rcosjava.software.animator.ipc;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.support.NewLabel;
import org.rcosjava.software.animator.support.RCOSBox;
import org.rcosjava.software.animator.support.RCOSQueue;
import org.rcosjava.software.animator.support.RCOSRectangle;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.ipc.SharedMemory;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * Based on the commands sent and received to the MMU and IPC displays
 * graphically what is happening.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 25/10/98 - Converted to Java 1.1. AN </DD>
 * <DD> 30/11/98 - Cleaned up. AN </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @created 1st August 1997
 * @version 1.00 $Date$
 */
public class IPCManagerFrame extends RCOSFrame
{
  /**
   * Default display when there is no semaphore or shared memory segments to
   * display.
   */
  private static String NONE = " None      ";

  /**
   * Default display when there are semaphores or shared memory segments to
   * display.
   */
  private static String SOME = " Select";

  /**
   * Panels for layout.
   */
  private Panel mainPanel, closePanel;

  /**
   * Images used to display UI.
   */
  private Image myImages[];

  /**
   * Semaphore queue to display.
   */
  private RCOSQueue semQueue;

  /**
   * Shared memory queue to display.
   */
  private RCOSQueue shmQueue;

  /**
   * Animator that I receive calls from and makes calls back when a user event
   * occurs.
   */
  private IPCManagerAnimator myIPCManagerAnimator;

  /**
   * Component used for UI display.
   */
  private Component myComponent;

  /**
   * Window dimensions.
   */
  private int windowWidth, windowHeight;

  /**
   * Memory images to display.
   */
  private MemoryGraphic[] memoryGraphics = new MemoryGraphic[20];

  /**
   * Map of current semaphores.
   */
  private HashMap semaphoreMap;

  /**
   * Map of current shared memory segments.
   */
   private HashMap sharedMemoryMap;

  /**
   * Description of the Field
   */
  private Choice shmOption, semOption;

  /**
   * Description of the Field
   */
  private java.awt.List shmList;

  /**
   * Description of the Field
   */
  private TextField semValue;

  /**
   * Description of the Field
   */
  private String selectedSemaphoreName;

  /**
   * Description of the Field
   */
  private String selectedSharedMemoryName;

  /**
   * Constructor for the IPCManagerFrame object
   *
   * @param x width of window.
   * @param y height of window.
   * @param ipcImages images used in display of ui.
   * @param thisIPCManager animator used to receive and make messages.
   */
  public IPCManagerFrame(int x, int y, Image[] ipcImages,
      IPCManagerAnimator thisIPCManager)
  {
    super();
    setTitle("IPC Manager Animator");
    myImages = ipcImages;
    windowWidth = x;
    windowHeight = y;
    myIPCManagerAnimator = thisIPCManager;
    semaphoreMap = new HashMap();
    sharedMemoryMap = new HashMap();
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    NewLabel lTmpLabel;
    RCOSBox rBox;

    setLayout(new BorderLayout());

    mainPanel = new Panel();
    closePanel = new Panel();

    Panel pTemp = new Panel();

    for (int count = 0; count < MemoryManager.MAX_PAGES; count++)
    {
      memoryGraphics[count] = new MemoryGraphic(myImages[0]);
    }

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();

    mainPanel.setLayout(gridBag);
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weightx = 1;
    constraints.insets = new Insets(1, 1, 0, 0);
    constraints.anchor = GridBagConstraints.CENTER;

    int iMemRows;

    int iMemCols;

    for (iMemRows = 0; iMemRows < 2; iMemRows++)
    {
      for (iMemCols = 0; iMemCols < 9; iMemCols++)
      {
        constraints.gridwidth = 1;
        gridBag.setConstraints(memoryGraphics[(iMemRows * 10) + iMemCols], constraints);
        mainPanel.add(memoryGraphics[(iMemRows * 10) + iMemCols]);
      }
      constraints.gridwidth = GridBagConstraints.REMAINDER;
      gridBag.setConstraints(memoryGraphics[(iMemRows * 10) + 9], constraints);
      mainPanel.add(memoryGraphics[(iMemRows * 10) + 9]);
    }

    RCOSBox iBox;
    RCOSRectangle allocBox;
    RCOSRectangle readBox;
    RCOSRectangle writeBox;
    RCOSRectangle unallocBox;

    pTemp = new Panel();
    pTemp.setLayout(gridBag);

    allocBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.allocatedColour, Color.white);
    unallocBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.unallocatedColour, Color.white);
    readBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.readingColour, Color.white);
    writeBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.writingColour, Color.white);

    constraints.fill = GridBagConstraints.BOTH;
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

    gridBag.setConstraints(allocBox, constraints);
    pTemp.add(allocBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Allocated ", defaultFont);
    gridBag.setConstraints(lTmpLabel, constraints);
    pTemp.add(lTmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(unallocBox, constraints);
    pTemp.add(unallocBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Unallocated ", defaultFont);
    gridBag.setConstraints(lTmpLabel, constraints);
    pTemp.add(lTmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(readBox, constraints);
    pTemp.add(readBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Being Read ", defaultFont);
    gridBag.setConstraints(lTmpLabel, constraints);
    pTemp.add(lTmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(writeBox, constraints);
    pTemp.add(writeBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Being Written ", defaultFont);
    gridBag.setConstraints(lTmpLabel, constraints);
    pTemp.add(lTmpLabel);

    iBox = new RCOSBox(pTemp, new NewLabel("Key", labelFont), 0, 2, 2, 2);

    Panel pSMem;

    Panel pSem;

    shmOption = new Choice();
    shmList = new java.awt.List(2, false);
    shmList.setBackground(Color.black);
    shmList.setForeground(textBoxColour);

    semOption = new Choice();
    semValue = new TextField(2);

    shmOption.addItem("None      ");
    shmOption.setBackground(Color.black);
    shmOption.setForeground(Color.white);
    shmOption.select("None      ");

    semOption.addItem(NONE);
    semOption.setBackground(Color.black);
    semOption.setForeground(Color.white);
    semOption.select(NONE);
    selectedSemaphoreName = NONE;

    pSMem = new Panel();

    GridBagConstraints tmpConstraints = new GridBagConstraints();
    GridBagLayout tmpGridBag = new GridBagLayout();

    pSMem.setLayout(tmpGridBag);

    tmpConstraints.weightx = 1;
    tmpConstraints.gridwidth = 1;
    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.anchor = GridBagConstraints.CENTER;

    tmpConstraints.anchor = GridBagConstraints.CENTER;
    lTmpLabel = new NewLabel("ID:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel, tmpConstraints);
    pSMem.add(lTmpLabel);

    lTmpLabel = new NewLabel("Process Queue:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel, tmpConstraints);
    pSMem.add(lTmpLabel);

    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel("Value:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel, tmpConstraints);
    pSMem.add(lTmpLabel);

    tmpConstraints.gridwidth = 1;
    tmpGridBag.setConstraints(shmOption, tmpConstraints);
    shmOption.addItemListener(new SharedMemorySelection());
    pSMem.add(shmOption);

    shmQueue = new RCOSQueue(5, defaultFont);
    tmpGridBag.setConstraints(shmQueue, tmpConstraints);
    pSMem.add(shmQueue);

    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpGridBag.setConstraints(shmList, tmpConstraints);
    pSMem.add(shmList);

    pSem = new Panel();

    tmpConstraints = new GridBagConstraints();
    tmpGridBag = new GridBagLayout();
    pSem.setLayout(tmpGridBag);

    tmpConstraints.gridwidth = 1;
    tmpConstraints.gridheight = 1;
    tmpConstraints.weighty = 1;
    tmpConstraints.weightx = 1;
    tmpConstraints.anchor = GridBagConstraints.CENTER;

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.gridwidth = 1;
    lTmpLabel = new NewLabel("ID:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel, tmpConstraints);
    pSem.add(lTmpLabel);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    lTmpLabel = new NewLabel("Process Queue:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel, tmpConstraints);
    pSem.add(lTmpLabel);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    lTmpLabel = new NewLabel("Value:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel, tmpConstraints);
    pSem.add(lTmpLabel);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.gridwidth = 1;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    tmpGridBag.setConstraints(semOption, tmpConstraints);
    semOption.addItemListener(new SemaphoreSelection());
    pSem.add(semOption);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    semQueue = new RCOSQueue(5, defaultFont);
    tmpGridBag.setConstraints(semQueue, tmpConstraints);
    pSem.add(semQueue);

    tmpConstraints.insets = new Insets(1, 1, 1, 1);
    tmpConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    semValue.setFont(defaultFont);
    semValue.setBackground(textBoxColour);
    semValue.setForeground(defaultFgColour);
    semValue.setBackground(defaultBgColour);
    tmpGridBag.setConstraints(semValue, tmpConstraints);
    pSem.add(semValue);

    constraints.gridheight = 3;
    constraints.gridwidth = 2;
    gridBag.setConstraints(iBox, constraints);
    mainPanel.add(iBox);

    constraints.gridheight = 1;
    rBox = new RCOSBox(pSem, new NewLabel("Semaphores", titleFont), 1, 1, 1, 1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(rBox, constraints);
    mainPanel.add(rBox);

    rBox = new RCOSBox(pSMem, new NewLabel("Shared Memory", titleFont), 1, 1, 1, 1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(rBox, constraints);
    mainPanel.add(rBox);

    closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

    Button tmpAWTButton = new Button("Close");

    closePanel.add(tmpAWTButton);
    tmpAWTButton.addMouseListener(new RCOSFrame.CloseAnimator());

    add("Center", mainPanel);
    add("South", closePanel);
  }

  /**
   * Adds a feature to the Notify attribute of the IPCManagerFrame object
   */
  public void addNotify()
  {
    this.repaint();
    super.addNotify();
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
  public void paint(Graphics g)
  {
    update(g);
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
  public synchronized void update(Graphics g)
  {
    for (int count = 0; count < MemoryManager.MAX_PAGES; count++)
    {
      memoryGraphics[count].repaint();
    }
    notify();
  }

  /**
   * Description of the Method
   *
   * @param time Description of Parameter
   */
  public synchronized void syncPaint(long time)
  {
    this.repaint();
    // do nothing
    try
    {
      wait(time);
    }
    catch (Exception v)
    {
    }
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreCreated(String semaphoreId, int pid, int value)
  {
    if (semOption.getItemCount() == 1)
    {
      semOption.removeAll();
      semOption.add(SOME);
      selectedSemaphoreName = SOME;
    }
    semOption.add(semaphoreId);
    semaphoreMap.put(semaphoreId, new SemaphoreSharedMemoryGraphic(pid,
        new Integer(value)));
    updateSemaphoreQueue();
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreOpened(String semaphoreId, int pid, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphoreId);

    if (tmpGraphic != null)
    {
      tmpGraphic.addProcess(pid);
    }
    updateSemaphoreQueue();
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreWaiting(String semaphoreId, int pid, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphoreId);

    if (tmpGraphic != null)
    {
      tmpGraphic.setValue(new Integer(value));
    }
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreSignalled(String semaphoreId, int pid, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphoreId);

    if (tmpGraphic != null)
    {
      tmpGraphic.setValue(new Integer(value));
    }
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param pid Description of Parameter
   * @param value Description of Parameter
   */
  void semaphoreClosed(String semaphoreId, int pid, int value)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        semaphoreMap.get(semaphoreId);

    if (tmpGraphic != null)
    {
      tmpGraphic.removeFirstProcess();
    }
    if (tmpGraphic.attachedProcesses() == 0)
    {
      semOption.remove(semaphoreId);
      if (semOption.getItemCount() == 1)
      {
        semOption.removeAll();
        semOption.add(NONE);
        selectedSemaphoreName = NONE;
      }
      else
      {
        selectedSemaphoreName = SOME;
      }
    }
    updateSemaphoreQueue();
  }

  /**
   * Add a newly created shared memory to the options to be selected by the
   * user.
   *
   * @param sharedMemoryId the name of the shared memory id.
   * @param memory the process id, memory type and how much memory was created.
   */
  void sharedMemoryCreated(String sharedMemoryId, MemoryReturn memoryReturn,
      Memory memory)
  {

    System.out.println("Shm created: " + sharedMemoryId);
    System.out.println("Shm created: " + memoryReturn.getPID());
    System.out.println("Shm created: " + memory.toString());

    if (shmOption.getItemCount() == 1)
    {
      shmOption.removeAll();
      shmOption.add(SOME);
      selectedSharedMemoryName = SOME;
    }
    shmOption.add(sharedMemoryId);
    sharedMemoryMap.put(sharedMemoryId, new SemaphoreSharedMemoryGraphic(
      memoryReturn.getPID(), memory.toString()));
    updateSharedMemoryQueue();
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param pid Description of Parameter
   */
  void sharedMemoryOpened(String sharedMemoryId, int pid)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        sharedMemoryMap.get(sharedMemoryId);

    if (tmpGraphic != null)
    {
      tmpGraphic.addProcess(pid);
    }
    updateSharedMemoryQueue();
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param pid Description of Parameter
   */
  void sharedMemoryClosed(String sharedMemoryId, int pid)
  {
    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        sharedMemoryMap.get(sharedMemoryId);

    if (tmpGraphic != null)
    {
      tmpGraphic.removeProcess(pid);
    }
    updateSharedMemoryQueue();
  }

  /**
   * Description of the Method
   *
   * @param semaphoreId Description of Parameter
   * @param memory Description of Parameter
   */
  void sharedMemoryRead(String semaphoreId, MemoryReturn memoryReturn,
      Memory memory)
  {
  }

  /**
   * Description of the Method
   *
   * @param sharedMemoryId Description of Parameter
   * @param pid Description of Parameter
   * @param memory Description of Parameter
   */
  void sharedMemoryWrote(String sharedMemoryId, Memory memory)
  {
    System.out.println("Shm wrote: " + sharedMemoryId);
    System.out.println("Shm wrote: " + memory.toString());

    SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
        sharedMemoryMap.get(sharedMemoryId);

    tmpGraphic.setValue(memory.toString());
    updateSharedMemoryQueue();
  }

  /**
   * Description of the Method
   *
   * @param processId Description of Parameter
   */
  void shmQueueAdd(String processId)
  {
    shmQueue.addToQueue(processId);
  }

  /**
   * Description of the Method
   */
  void shmQueueRemove()
  {
    shmQueue.removeFromQueue();
  }

  /**
   * Description of the Method
   *
   * @param aMemret Description of Parameter
   */
  void allocatedPages(MemoryReturn aMemret)
  {
    for (int count = 0; count < aMemret.getSize(); count++)
    {
      memoryGraphics[aMemret.getPage(count)].setAllocated(aMemret);
    }
  }

  /**
   * Description of the Method
   *
   * @param returnedMemory Description of Parameter
   */
  void deallocatedPages(MemoryReturn returnedMemory)
  {
    for (int count = 0; count < returnedMemory.getSize(); count++)
    {
      memoryGraphics[returnedMemory.getPage(count)].setDeallocated();
    }
  }

  /**
   * Description of the Method
   *
   * @param iID Description of Parameter
   * @param bType Description of Parameter
   */
  void readingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.readingColour, iID, bType);
  }

  /**
   * Description of the Method
   *
   * @param iID Description of Parameter
   * @param bType Description of Parameter
   */
  void writingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.writingColour, iID, bType);
  }

  /**
   * Description of the Method
   *
   * @param iID Description of Parameter
   * @param bType Description of Parameter
   */
  void finishedReadingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.allocatedColour, iID, bType);
  }

  /**
   * Description of the Method
   *
   * @param iID Description of Parameter
   * @param bType Description of Parameter
   */
  void finishedWritingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.allocatedColour, iID, bType);
  }

  /**
   * Description of the Method
   *
   * @param cColor Description of Parameter
   * @param iID Description of Parameter
   * @param bMemoryType Description of Parameter
   */
  private void colourMemory(Color cColor, int iID, byte bMemoryType)
  {
    for (int count = 0; count < MemoryManager.MAX_PAGES; count++)
    {
      if (memoryGraphics[count].isMemory(iID, bMemoryType))
      {
        memoryGraphics[count].setCurrentColour(cColor);
      }
    }
  }

  /**
   * Update the semaphore queue with the currently selected item.
   */
  private void updateSemaphoreQueue()
  {
    //Reset values
    semQueue.removeAllFromQueue();
    semValue.setText("");
    //Check that it's a real semaphore
    if (!selectedSemaphoreName.startsWith(NONE) &&
        !selectedSemaphoreName.startsWith(SOME))
    {
      SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
          semaphoreMap.get(selectedSemaphoreName);

      semValue.setText(((Integer) tmpGraphic.getValue()).toString());

      Iterator tmpIter = tmpGraphic.getAttachedProcesses();

      while (tmpIter.hasNext())
      {
        semQueue.addToQueue("P" + ((Integer) tmpIter.next()).toString());
      }
    }
    semQueue.repaint();
  }

  /**
   * Update the shared memory queue with the currently selected item.
   */
  private void updateSharedMemoryQueue()
  {
    //Reset values
    shmQueue.removeAllFromQueue();

    shmList.removeAll();
    //Check that it's a real semaphore
    if (!selectedSharedMemoryName.startsWith(NONE) &&
        !selectedSharedMemoryName.startsWith(SOME))
    {
      System.out.println("Got: " + selectedSharedMemoryName);
      SemaphoreSharedMemoryGraphic tmpGraphic = (SemaphoreSharedMemoryGraphic)
          sharedMemoryMap.get(selectedSharedMemoryName);

      System.out.println("Got: " + tmpGraphic.getValue().getClass());
      shmList.add((String) tmpGraphic.getValue());

      Iterator tmpIter = tmpGraphic.getAttachedProcesses();

      while (tmpIter.hasNext())
      {
        shmQueue.addToQueue("P" + ((Integer) tmpIter.next()).toString());
      }
    }
    semQueue.repaint();
  }

  /**
   * Change the semaphore information displayed based on the selection.
   *
   * @author administrator
   * @created 28 April 2002
   */
  class SemaphoreSelection implements ItemListener
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void itemStateChanged(ItemEvent e)
    {
      //Get new selection
      selectedSemaphoreName = (String) e.getItem();
      updateSemaphoreQueue();
    }
  }

  /**
   * Change the shared memory information displayed based on the selection.
   *
   * @author administrator
   * @created 28 April 2002
   */
  class SharedMemorySelection implements ItemListener
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void itemStateChanged(ItemEvent e)
    {
      //Get new selection
      selectedSharedMemoryName = (String) e.getItem();
      updateSharedMemoryQueue();
    }
  }
}
