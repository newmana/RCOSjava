//***************************************************************************
// FILE     : IPCManagerFrame.java
// PACKAGE  : Animator
// PURPOSE  : Based on the commands sent and received to the MMU and IPC
//            displays graphically what is happening.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/09/97 - Created. AN
//            25/10/98 - Converted to Java 1.1. AN
//            30/11/98 - Cleaned up. AN
//***************************************************************************//

package Software.Animator.IPC;

import java.awt.*;
import java.awt.event.*;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.MTGOS.MTGO;
import Software.Animator.Support.RCOSBox;
import Software.Animator.Support.RCOSList;
import Software.Animator.Support.RCOSRectangle;
import Software.Animator.Support.RCOSQueue;
import Software.Animator.Support.NewLabel;
import Software.Memory.MemoryManager;
import MessageSystem.Messages.Message;
import Software.Memory.MemoryRequest;
import Software.Memory.MemoryReturn;

public class IPCManagerFrame extends RCOSFrame
{
  private Panel pMain, pClose;
  private Image myImages[];
  private RCOSQueue semQueue, shmQueue;
  private IPCManagerAnimator myIPCManagerAnimator;
  private Message msg;
  private Component myComponent;
  private int iX, iY;
  private MemoryGraphic[] memoryGraphics = new MemoryGraphic[20];
  private Choice cSMemOption;
  private RCOSList rSMemList, rSemList;
  private Choice cSemOption;

  public IPCManagerFrame(int x, int y, Image[] ipcImages,
                         IPCManagerAnimator thisIPCManager)
  {
    super();
    setTitle("IPC Manager Animator");
    myImages = ipcImages;
    iX = x;
    iY = y;
    myIPCManagerAnimator = thisIPCManager;
  }

  public synchronized void addNotify()
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

  public synchronized void syncPaint(long lTime)
  {
    this.repaint();
    // do nothing
    try
    {
      wait(lTime);
    } catch(Exception v)
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

  void deallocatedPages(MemoryReturn aMemret)
  {
    for (int count=0; count < aMemret.getSize(); count++)
    {
      memoryGraphics[aMemret.getPage(count)].setDeallocated();
    }
  }

  void shmQueueAdd(String processID)
  {
    shmQueue.addToQueue(processID);
  }

  void semQueueAdd(String processID)
  {
    semQueue.addToQueue(processID);
  }

  void shmQueueRemove()
  {
    shmQueue.removeFromQueue();
  }

  void semQueueRemove()
  {
    semQueue.removeFromQueue();
  }

  void readingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.cReading, iID, bType);
  }

  void writingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.cWriting, iID, bType);
  }

  void finishedReadingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.cAllocated, iID, bType);
  }

  void finishedWritingMemory(int iID, byte bType)
  {
    colourMemory(MemoryGraphic.cAllocated, iID, bType);
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

    pMain = new Panel();
    pClose = new Panel();
    Panel pTemp = new Panel();

    for(int count=0; count < MemoryManager.MAX_PAGES; count++)
    {
      memoryGraphics[count] = new MemoryGraphic(myImages[0]);
    }

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    pMain.setLayout(gridBag);
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
        pMain.add(memoryGraphics[(iMemRows*10)+iMemCols]);
      }
      constraints.gridwidth = GridBagConstraints.REMAINDER;
      gridBag.setConstraints(memoryGraphics[(iMemRows*10)+9],constraints);
      pMain.add(memoryGraphics[(iMemRows*10)+9]);
    }

    RCOSBox iBox;
    RCOSRectangle allocBox, readBox, writeBox, unallocBox;
    pTemp = new Panel();
    pTemp.setLayout(gridBag);

    allocBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.cAllocated, Color.white);
    unallocBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.cUnallocated, Color.white);
    readBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.cReading, Color.white);
    writeBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.cWriting, Color.white);

    constraints.fill = GridBagConstraints.BOTH;
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;

    gridBag.setConstraints(allocBox,constraints);
    pTemp.add(allocBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Allocated ", fDefaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(unallocBox,constraints);
    pTemp.add(unallocBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Unallocated ", fDefaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(readBox,constraints);
    pTemp.add(readBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Being Read ", fDefaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(writeBox,constraints);
    pTemp.add(writeBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel(" Being Written ", fDefaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    iBox = new RCOSBox(pTemp,new NewLabel("Key", fLabelFont),0,
                       2,2,2);

    Panel pSMem, pSem;
    cSMemOption = new Choice();
    rSMemList = new RCOSList(this,2,false);
    cSemOption = new Choice();
    rSemList = new RCOSList(this,2,false);

    cSMemOption.addItem("None      ");
    cSMemOption.setBackground(Color.black);
    cSMemOption.setForeground(Color.white);
    cSMemOption.select("None      ");

    cSemOption.addItem("None       ");
    cSemOption.setBackground(Color.black);
    cSemOption.setForeground(Color.white);
    cSemOption.select("None       ");

    pSMem = new Panel();

    GridBagConstraints tmpConstraints = new GridBagConstraints();
    GridBagLayout tmpGridBag = new GridBagLayout();
    pSMem.setLayout(tmpGridBag);

    tmpConstraints.weightx=1;
    tmpConstraints.gridwidth=1;
    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.anchor = GridBagConstraints.CENTER;

    tmpConstraints.anchor = GridBagConstraints.CENTER;
    lTmpLabel = new NewLabel("ID:", fDefaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSMem.add(lTmpLabel);

    lTmpLabel = new NewLabel("Process Queue:", fDefaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSMem.add(lTmpLabel);

    tmpConstraints.gridwidth=GridBagConstraints.REMAINDER;
    lTmpLabel = new NewLabel("Value:", fDefaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSMem.add(lTmpLabel);

    tmpConstraints.gridwidth=1;
    tmpGridBag.setConstraints(cSMemOption,tmpConstraints);
    pSMem.add(cSMemOption);

    shmQueue = new RCOSQueue(5, fDefaultFont);
    tmpGridBag.setConstraints(shmQueue,tmpConstraints);
    pSMem.add(shmQueue);

    tmpConstraints.gridwidth=GridBagConstraints.REMAINDER;
    tmpGridBag.setConstraints(rSMemList,tmpConstraints);
    pSMem.add(rSMemList);

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
    lTmpLabel = new NewLabel("ID:", fDefaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSem.add(lTmpLabel);

    tmpConstraints.insets=new Insets(1,1,1,1);
    lTmpLabel = new NewLabel("Process Queue:", fDefaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSem.add(lTmpLabel);

    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.gridwidth=GridBagConstraints.REMAINDER;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    lTmpLabel = new NewLabel("Value:", fDefaultFont);
    tmpGridBag.setConstraints(lTmpLabel,tmpConstraints);
    pSem.add(lTmpLabel);

    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.gridwidth=1;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    tmpGridBag.setConstraints(cSemOption,tmpConstraints);
    pSem.add(cSemOption);

    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    semQueue = new RCOSQueue(5, fDefaultFont);
    tmpGridBag.setConstraints(semQueue,tmpConstraints);
    pSem.add(semQueue);

    tmpConstraints.insets=new Insets(1,1,1,1);
    tmpConstraints.gridwidth=GridBagConstraints.REMAINDER;
    tmpConstraints.anchor = GridBagConstraints.CENTER;
    tmpGridBag.setConstraints(rSemList,tmpConstraints);
    pSem.add(rSemList);

    constraints.gridheight = 3;
    constraints.gridwidth = 2;
    gridBag.setConstraints(iBox,constraints);
    pMain.add(iBox);

    constraints.gridheight = 1;
    rBox = new RCOSBox(pSem,new NewLabel("Semaphores", fTitleFont),1,
                       1,1,1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(rBox,constraints);
    pMain.add(rBox);

    rBox = new RCOSBox(pSMem,new NewLabel("Shared Memory", fTitleFont),1,
                       1,1,1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridBag.setConstraints(rBox,constraints);
    pMain.add(rBox);

    pClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
   	Button tmpAWTButton = new Button("Close");
    pClose.add(tmpAWTButton);
    tmpAWTButton.addMouseListener(new RCOSFrame.CloseAnimator());

    add("Center",pMain);
    add("South",pClose);
  }
}
