package net.sourceforge.rcosjava.software.animator.ipc;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.mtgos.MTGO;
import net.sourceforge.rcosjava.software.animator.support.RCOSBox;
import net.sourceforge.rcosjava.software.animator.support.RCOSList;
import net.sourceforge.rcosjava.software.animator.support.RCOSRectangle;
import net.sourceforge.rcosjava.software.animator.support.RCOSQueue;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;

/**
 * Based on the commands sent and received to the MMU and IPC displays
 * graphically what is happening.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 25/10/98 - Converted to Java 1.1. AN
 * </DD><DD>
 * 30/11/98 - Cleaned up. AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 1st August 1997
 */
public class IPCManagerFrame extends RCOSFrame
{
  private Panel mainPanel, closePanel;
  private Image myImages[];
  private RCOSQueue semQueue, shmQueue;
  private IPCManagerAnimator myIPCManagerAnimator;
  private Component myComponent;
  private int iX, iY;
  private MemoryGraphic[] memoryGraphics = new MemoryGraphic[20];
  private Hashtable semaphoreList;
  private Choice shmOption;
  private RCOSList shmList, semList;
  private Choice semOption;

  public IPCManagerFrame(int x, int y, Image[] ipcImages,
    IPCManagerAnimator thisIPCManager)
  {
    super();
    setTitle("IPC Manager Animator");
    myImages = ipcImages;
    iX = x;
    iY = y;
    myIPCManagerAnimator = thisIPCManager;
    semaphoreList = new Hashtable(5);
  }

  public void addNotify()
  {
    this.repaint();
    super.addNotify();
  }

  public void paint(Graphics g)
  {
    update(g);
  }

  public synchronized void update(Graphics g)
  {
    for (int count=0; count < MemoryManager.MAX_PAGES; count++)
    {
      memoryGraphics[count].repaint();
    }
    notify();
  }

  public synchronized void syncPaint(long time)
  {
    this.repaint();
    // do nothing
    try
    {
      wait(time);
    }
    catch(Exception v)
    {
    }
  }

  void allocatedPages(MemoryReturn aMemret)
  {
    for (int count=0; count < aMemret.getSize(); count++)
    {
      memoryGraphics[aMemret.getPage(count)].setAllocated(aMemret);
    }
  }

  void deallocatedPages(MemoryReturn returnedMemory)
  {
    for (int count=0; count < returnedMemory.getSize(); count++)
    {
      memoryGraphics[returnedMemory.getPage(count)].setDeallocated();
    }
  }

  void semaphoreCreated(String semaphoreId, int pid, int value)
  {
    System.out.println("Semaphore added!");
    //semQueue.addToQueue(Integer.toString(pid));
    if (semOption.getItemCount() == 1)
    {
      semOption.removeAll();
      semOption.add("Select");
    }
    semOption.add(semaphoreId);
    semaphoreList.put(semaphoreId, new SemaphoreGraphic(pid, value));
  }

  void semaphoreWaiting(String semaphoreId, int pid, int value)
  {
  }

  void semaphoreClosed()
  {
  }

  void shmQueueAdd(String processId)
  {
    shmQueue.addToQueue(processId);
  }

  void shmQueueRemove()
  {
    shmQueue.removeFromQueue();
  }

  void readingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.readingColour, iID, bType);
  }

  void writingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.writingColour, iID, bType);
  }

  void finishedReadingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.allocatedColour, iID, bType);
  }

  void finishedWritingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.allocatedColour, iID, bType);
  }

  private void colourMemory(Color cColor, int iID, byte bMemoryType)
  {
    for (int count=0; count < MemoryManager.MAX_PAGES; count++)
    {
      if (memoryGraphics[count].isMemory(iID, bMemoryType))
      {
        memoryGraphics[count].setCurrentColour(cColor);
      }
    }
  }

  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    NewLabel lTmpLabel;
    RCOSBox rBox;

    setLayout(new BorderLayout());

    mainPanel = new Panel();
    closePanel = new Panel();
    Panel pTemp = new Panel();

    for(int count=0; count < MemoryManager.MAX_PAGES; count++)
    {
      memoryGraphics[count] = new MemoryGraphic(myImages[0]);
    }

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    mainPanel.setLayout(gridBag);
    constraints.fill = GridBagConstraints.BOTH;
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weightx=1;
    constraints.insets=new Insets(1,1,0,0);
    constraints.anchor = GridBagConstraints.CENTER;

    int iMemRows, iMemCols;

    for (iMemRows=0; iMemRows < 2; iMemRows++)
    {
      for (iMemCols=0; iMemCols < 9; iMemCols++)
      {
        constraints.gridwidth = 1;
        gridBag.setConstraints(memoryGraphics[(iMemRows*10)+iMemCols],constraints);
        mainPanel.add(memoryGraphics[(iMemRows*10)+iMemCols]);
      }
      constraints.gridwidth = GridBagConstraints.REMAINDER;
      gridBag.setConstraints(memoryGraphics[(iMemRows*10)+9],constraints);
      mainPanel.add(memoryGraphics[(iMemRows*10)+9]);
    }

    RCOSBox iBox;
    RCOSRectangle allocBox, readBox, writeBox, unallocBox;
    pTemp = new Panel();
    pTemp.setLayout(gridBag);

    allocBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.allocatedColour, Color.white);
    unallocBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.unallocatedColour, Color.white);
    readBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.readingColour, Color.white);
    writeBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.writingColour, Color.white);

    constraints.fill = GridBagConstraints.BOTH;
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;

    gridBag.setConstraints(allocBox,constraints);
    pTemp.add(allocBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Allocated ", defaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(unallocBox,constraints);
    pTemp.add(unallocBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Unallocated ", defaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(readBox,constraints);
    pTemp.add(readBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Being Read ", defaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(writeBox,constraints);
    pTemp.add(writeBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Being Written ", defaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    iBox = new RCOSBox(pTemp,new NewLabel("Key", labelFont),0,2,2,2);

    Panel pSMem, pSem;
    shmOption = new Choice();
    shmList = new RCOSList(this,2,false);
    semOption = new Choice();
    semList = new RCOSList(this,2,false);

    shmOption.addItem("None      ");
    shmOption.setBackground(Color.black);
    shmOption.setForeground(Color.white);
    shmOption.select("None      ");

    semOption.addItem("None       ");
    semOption.setBackground(Color.black);
    semOption.setForeground(Color.white);
    semOption.select("None       ");

    pSMem = new Panel();

    GridBagConstraints tmpConstraints = new GridBagConstraints();
    GridBagLayout tmpGridBag = new GridBagLayout();
    pSMem.setLayout(tmpGridBag);

    tmpConstraints.weightx=1;
    tmpConstraints.gridwidth=1;
    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.anchor = GridBagConstraints.CENTER;

    tmpConstraints.anchor = GridBagConstraints.CENTER;
    lTmpLabel = new NewLabel("ID:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSMem.add(lTmpLabel);

    lTmpLabel = new NewLabel("Process Queue:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSMem.add(lTmpLabel);

    tmpConstraints.gridwidth=GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel("Value:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSMem.add(lTmpLabel);

    tmpConstraints.gridwidth=1;
    tmpGridBag.setConstraints(shmOption,tmpConstraints);
    pSMem.add(shmOption);

    shmQueue = new RCOSQueue(5, defaultFont);
    tmpGridBag.setConstraints(shmQueue,tmpConstraints);
    pSMem.add(shmQueue);

    tmpConstraints.gridwidth=GridBagConstraints.REMAINDER;
    tmpGridBag.setConstraints(shmList,tmpConstraints);
    pSMem.add(shmList);

    pSem = new Panel();

    tmpConstraints = new GridBagConstraints();
    tmpGridBag = new GridBagLayout();
    pSem.setLayout(tmpGridBag);

    tmpConstraints.gridwidth=1;
    tmpConstraints.gridheight=1;
    tmpConstraints.weighty=1;
    tmpConstraints.weightx=1;
    tmpConstraints.anchor = GridBagConstraints.CENTER;

    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.gridwidth=1;
    lTmpLabel = new NewLabel("ID:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSem.add(lTmpLabel);

    tmpConstraints.insets=new Insets(1,1,1,1);
    lTmpLabel = new NewLabel("Process Queue:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSem.add(lTmpLabel);

    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.gridwidth=GridBagConstraints.REMAINDER;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    lTmpLabel = new NewLabel("Value:", defaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSem.add(lTmpLabel);

    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.gridwidth=1;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    tmpGridBag.setConstraints(semOption,tmpConstraints);
    semOption.addItemListener(new SemaphoreSelection());

    pSem.add(semOption);

    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    semQueue = new RCOSQueue(5, defaultFont);
    tmpGridBag.setConstraints(semQueue,tmpConstraints);
    pSem.add(semQueue);

    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.gridwidth=GridBagConstraints.REMAINDER;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    tmpGridBag.setConstraints(semList,tmpConstraints);
    pSem.add(semList);

    constraints.gridheight = 3;
    constraints.gridwidth = 2;
    gridBag.setConstraints(iBox,constraints);
    mainPanel.add(iBox);

    constraints.gridheight = 1;
    rBox = new RCOSBox(pSem,new NewLabel("Semaphores", titleFont),1,1,1,1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(rBox,constraints);
    mainPanel.add(rBox);

    rBox = new RCOSBox(pSMem,new NewLabel("Shared Memory", titleFont),1,1,1,1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(rBox,constraints);
    mainPanel.add(rBox);

    closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
   	Button tmpAWTButton = new Button("Close");
    closePanel.add(tmpAWTButton);
    tmpAWTButton.addMouseListener(new RCOSFrame.CloseAnimator());

    add("Center",mainPanel);
    add("South",closePanel);
  }

  /**
   * Change the quatum based on the option selected.
   */
  class SemaphoreSelection implements ItemListener
  {
    public void itemStateChanged(ItemEvent e)
    {
      System.out.println("Selected: " + (String) e.getItem());
    }
  }
}
