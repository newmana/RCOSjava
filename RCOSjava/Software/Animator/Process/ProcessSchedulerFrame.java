//***************************************************************************
// FILE     : ProcessSchedulerFrame.java
// PACKAGE  : Animator
// PURPOSE  : Based on the commands given by processSchedulerAnimator
//            displays graphically what is happening.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 22/01/96  Original (processSchedulerAnimator).
//          : 20/01/97  New Version Created.
//          : 13/10/98  Converted to Java 1.1.
//***************************************************************************//

package Software.Animator.Process;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.NewLabel;
import Software.Animator.Support.RCOSBox;
import Software.Animator.Support.MTGOS.GraphicsEngine;
import Software.Animator.Support.MTGOS.MTGO;
import Software.Animator.Support.Positions.Movement;
import Software.Animator.Support.Positions.Position;
import MessageSystem.Messages.Message;
import MessageSystem.PostOffices.MessageHandler;
import Software.Memory.MemoryRequest;
import Software.Process.ProcessScheduler;
import Software.Util.LIFOQueue;


public class ProcessSchedulerFrame extends RCOSFrame
{
  private GraphicsEngine engine;
  private Position pCPU;
  private Position[] pReady = new Position[11];
  private Position[] pBlocked = new Position[11];
  private Position[] pZombie = new Position[11];
  private Position[] pZombieToReady = new Position[6];
  private Position[] pBlockedToReady = new Position[6];
  private Position[] pReadyToCPU = new Position[4];
  private Position[] pCPUToReady = new Position[4];
  private Position[] pCPUToBlocked = new Position[4];
  private MTGO tmpPic, cpuPic;
  private LIFOQueue ZombieQ, ReadyQ, BlockedQ;
  private Movement mZombie, mBlocked, mReady;
  private Movement mZombieToReady, mBlockedToReady, mReadyToCPU,
    mCPUToReady, mCPUToBlocked;
  private Panel pMain, pClose, pWest;
  private Image myImages[] = new Image[5];
  private int iX, iCountX, iCountY, iIndentL, iIndentR, iWidth, iHeight;
  private int iBoxHeight, iBoxWidth;
  private MTGO tmpMTGO;
  private int inX, inY;
  private long lDelay;
  private ProcessSchedulerAnimator myProcessScheduler;
  private Message msg;
  private RCOSBox rBox;

  public ProcessSchedulerFrame(int x, int y, Image[] psImages,
                               ProcessSchedulerAnimator thisProcessScheduler)
  {
    setTitle("Process Scheduler Animator");
    myImages = psImages;
    ZombieQ = new LIFOQueue(10,0);
    BlockedQ = new LIFOQueue(10,0);
    ReadyQ = new LIFOQueue(10,0);
    inX = x;
    inY = y;
    lDelay = 1;
    myProcessScheduler = thisProcessScheduler;
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

  public void update(Graphics g)
  {
    drawBackground();
    engine.repaint();
    rBox.repaint();
  }

  public synchronized void syncPaint(long lTime)
  {
    // do nothing
    try
    {
      wait(lTime);
    }
    catch(Exception v)
    {
    }
    repaint(100);
  }

  private void drawBackground()
  {
    //Draw the black background or image (if it's set)
    engine.drawBackground();

    int iIncrement;

    engine.pad.setColor(Color.lightGray);
    engine.pad.setFont(fTitleFont);
    FontMetrics fm = getFontMetrics(fTitleFont);

    for (iCountY = 1; iCountY <= 3; iCountY++)
    {
      String title;

      if (iCountY == 1)
      {
        title="Ready Queue";
      }
      else if (iCountY == 2)
      {
        title="Blocked Queue";
      }
      else
      {
        title="Zombie Queue";
      }

      iX = (iWidth/2) - (fm.stringWidth(title)/2);
      engine.pad.drawString(title,iX,(iCountY*(iHeight))+20);

      for (iCountX = -5; iCountX < 5; iCountX++)
      {
        engine.pad.draw3DRect((engine.iCenterX)+iCountX*iBoxHeight,(iCountY*(iHeight))+iBoxHeight,iBoxHeight,iBoxHeight,true);
      }
      //Lines out of boxes.
      engine.pad.drawLine(iIndentL,iCountY*(iHeight)+45,(engine.iCenterX)-150,iCountY*(iHeight)+45);
      engine.pad.drawLine(engine.iCenterX+150,iCountY*(iHeight)+45,iWidth-iIndentL,iCountY*(iHeight)+45);
    }

    //Horizontal lines for zombie and blocked
    engine.pad.drawLine(iIndentL,2*(iHeight)-5,iIndentR,2*(iHeight)-5);
    engine.pad.drawLine(iIndentL,3*(iHeight)-5,iIndentR,3*(iHeight)-5);

    //Horizontal lines off CPU
    engine.pad.drawLine(iIndentL,cpuPic.iImageHeight/2,engine.CenterX(cpuPic),cpuPic.iImageHeight/2);
    engine.pad.drawLine(engine.CenterX(cpuPic)+cpuPic.iImageWidth/2,cpuPic.iImageHeight/2,iIndentR,cpuPic.iImageHeight/2);

    //2 Vertical lines
    engine.pad.drawLine(iIndentL,cpuPic.iImageHeight/2,iIndentL,3*(iHeight)+45);
    engine.pad.drawLine(iIndentR,cpuPic.iImageHeight/2,iIndentR,3*(iHeight)+45);
  }

// Sets up the layout for the whole process scheduler window.
// This uses the border layout manager.  The left (west) contains
// option, the center contains the animation area and the bottom
// (south) contains the Close button.

  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    engine = new GraphicsEngine(this, inX, inY);
    engine.backgroundColour = cDefaultBgColour;

    iWidth = engine.iWidth;
    iHeight = engine.iHeight / 5;
    iIndentL = 50;
    iIndentR = iWidth - iIndentL;

    setupMovement();

    pMain = new Panel();
    pClose = new Panel();
    pWest = new Panel();
    Panel pTemp = new Panel();
    Choice speedOption = new Choice();
    Choice quantumOption = new Choice();
    Choice schedulerOption = new Choice();
    NewLabel lTmpLabel;

// Set-up options for choosing the speed of refresh.

    speedOption.addItem("Fastest");
    speedOption.addItem("Fast");
    speedOption.addItem("Normal");
    speedOption.addItem("Slow");
    speedOption.addItem("Slowest");
    speedOption.setBackground(cChoiceColour);
    speedOption.setForeground(cDefaultFgColour);
    speedOption.select("Fastest");

// Set-up the options for the choice of quantum ie.
// time spent on the CPU.

    quantumOption.addItem("20");
    quantumOption.addItem("10");
    quantumOption.addItem("5");
    quantumOption.addItem("3");
    quantumOption.addItem("2");
    quantumOption.addItem("1");
    quantumOption.setBackground(cChoiceColour);
    quantumOption.setForeground(cDefaultFgColour);
    quantumOption.select("2");

// Set-up the options for the choice of queue

    schedulerOption.addItem("FIFO");
    schedulerOption.addItem("LIFO");
    schedulerOption.addItem("Priority");
    schedulerOption.setBackground(cChoiceColour);
    schedulerOption.setForeground(cDefaultFgColour);
    schedulerOption.select("FIFO");

// Create a temporary gridbaglayout for the options portion
// of the screen.

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    pTemp.setLayout(gridBag);

    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;

    constraints.insets=new Insets(1,3,1,1);
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    lTmpLabel = new NewLabel("Speed:", fDefaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.insets=new Insets(1,6,1,1);
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(speedOption,constraints);
    pTemp.add(speedOption);
    speedOption.addItemListener(new SpeedSelection());

    constraints.insets=new Insets(1,3,1,1);
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    lTmpLabel = new NewLabel("Quantum:", fDefaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.insets=new Insets(1,6,1,1);
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(quantumOption,constraints);
    pTemp.add(quantumOption);
    quantumOption.addItemListener(new QuantumSelection());

    constraints.insets=new Insets(1,3,1,1);
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    lTmpLabel = new NewLabel("Type:", fDefaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.insets=new Insets(1,6,1,1);
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.gridheight=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(schedulerOption,constraints);
    pTemp.add(schedulerOption);
//    quantumOption.addItemListener(new QuantumSelection());

    rBox = new RCOSBox(pTemp,new NewLabel("Options", fTitleFont),3,
                       3,3,3);

    pWest.setLayout(gridBag);
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;

    constraints.insets=new Insets(21,2,1,2);
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.NORTH;
    gridBag.setConstraints(rBox,constraints);
    pWest.add(rBox);

    pClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new Button("Close");
    pClose.add(tmpButton);
    tmpButton.addMouseListener(new RCOSFrame.CloseAnimator());

    pMain.add(engine);
    engine.addMouseListener(new MTGOSelection());

    add("Center",pMain);
    add("South",pClose);
    add("West", pWest);
  }

// Perhaps a rather dumb way of creating the movement arrays between
// the CPU and queues.  There must be a better way?

  void setupMovement()
  {
    // Create the animation area for the panel.
    cpuPic = new MTGO(myImages[2], "RCOS CPU", false);
    cpuPic.iPriority = 1;
    engine.addMTGO(cpuPic,this);
    cpuPic.iX = engine.CenterX(cpuPic);
    cpuPic.iY = 0;
    tmpPic = new MTGO(myImages[0], "TEMP", false);
    tmpPic.iPriority = 2;
    engine.addMTGO(tmpPic,this);
    iBoxHeight = 30;
    iBoxWidth = 30;

    int count, count2, iIncrement, iArr;

    // Movement inside queues

    for (count=1; count <= 3; count++)
    {
      for (count2=5; count2 > -6; count2--)
      {
        if (count2 > -5)
        {
          iIncrement=-2;
        }
        else
        {
          iIncrement=0;
        }
        iArr = (count2-5) * -1;
        if (count == 1)
        {
          pReady[iArr] = new Position(((count2*iBoxHeight)+engine.iCenterX),
            ((count*iHeight)+iBoxHeight),iIncrement,0);
        }
        if (count == 2)
        {
          pBlocked[iArr] = new Position(((count2*iBoxHeight)+engine.iCenterX),
            ((count*iHeight)+iBoxHeight),iIncrement,0);
        }
        if (count == 3)
        {
          pZombie[iArr] = new Position(((count2*iBoxHeight)+engine.iCenterX),
            ((count*iHeight)+iBoxHeight),iIncrement,0);
        }
      }
    }

    // Movement of process from Zombie to Ready

    pZombieToReady[0] = new Position(engine.iCenterX-180,(3*iHeight)+iBoxHeight,-2,0);
    pZombieToReady[1] = new Position(iIndentL-(iBoxWidth/2),(3*iHeight)+iBoxHeight,0,-2);
    pZombieToReady[2] = new Position(iIndentL-(iBoxWidth/2),(2*iHeight)+45+(iBoxHeight/2),2,0);
    pZombieToReady[3] = new Position(iIndentR-(iBoxWidth/2),(2*iHeight)+45+(iBoxHeight/2),0,-2);
    pZombieToReady[4] = new Position(iIndentR-(iBoxWidth/2),iHeight+iBoxHeight,-2,0);
    pZombieToReady[5] = new Position(engine.iCenterX+150,iHeight+iBoxHeight,0,0);

    // Movement of process from Blocked to Ready

    pBlockedToReady[0] = new Position(engine.iCenterX-180,(2*iHeight)+iBoxHeight,-2,0);
    pBlockedToReady[1] = new Position(iIndentL-(iBoxWidth/2),(2*iHeight)+iBoxHeight,0,-2);
    pBlockedToReady[2] = new Position(iIndentL-(iBoxWidth/2),iHeight+45+(iBoxHeight/2),2,0);
    pBlockedToReady[3] = new Position(iIndentR-(iBoxWidth/2),iHeight+45+(iBoxHeight/2),0,-2);
    pBlockedToReady[4] = new Position(iIndentR-(iBoxWidth/2),iHeight+iBoxHeight,-2,0);
    pBlockedToReady[5] = new Position(engine.iCenterX+150,iHeight+iBoxHeight,0,0);

    // Movement of process from Ready to CPU

    pReadyToCPU[0] = new Position(engine.iCenterX-180,iHeight+iBoxHeight,-2,0);
    pReadyToCPU[1] = new Position(iIndentL-(iBoxWidth/2),iHeight+iBoxHeight,0,-2);
    pReadyToCPU[2] = new Position(iIndentL-(iBoxWidth/2),iBoxHeight/2,2,0);
    pReadyToCPU[3] = new Position(engine.CenterX(tmpPic),iBoxHeight/2,0,0);

    // Movement of process from CPU to Ready

    pCPUToReady[0] = new Position(engine.CenterX(tmpPic),iBoxHeight/2,2,0);
    pCPUToReady[1] = new Position(iIndentR-(iBoxWidth/2),iBoxHeight/2,0,2);
    pCPUToReady[2] = new Position(iIndentR-(iBoxWidth/2),iHeight+iBoxHeight,-2,0);
    pCPUToReady[3] = new Position(engine.iCenterX+150,iHeight+iBoxHeight,0,0);

    // Movement of process from CPU to Blocked

    pCPUToBlocked[0] = new Position(engine.CenterX(tmpPic),iBoxHeight/2,2,0);
    pCPUToBlocked[1] = new Position(iIndentR-(iBoxWidth/2),iBoxHeight/2,0,2);
    pCPUToBlocked[2] = new Position(iIndentR-(iBoxWidth/2),(2*iHeight)+iBoxHeight,-2,0);
    pCPUToBlocked[3] = new Position(engine.iCenterX+150,(2*iHeight)+iBoxHeight,0,0);

    // Add all positions to create a movement.

    mZombieToReady = new Movement();
    mBlockedToReady = new Movement();
    mReadyToCPU = new Movement();
    mCPUToReady = new Movement();
    mCPUToBlocked = new Movement();
    mReady = new Movement();
    mBlocked = new Movement();
    mZombie = new Movement();

    for (count=0; count < 11; count++)
    {
      mReady.addPosition(pReady[count]);
      mBlocked.addPosition(pBlocked[count]);
      mZombie.addPosition(pZombie[count]);
    }

    for (count=0; count < 6; count++)
    {
      mBlockedToReady.addPosition(pBlockedToReady[count]);
      mZombieToReady.addPosition(pZombieToReady[count]);
    }

    for (count=0; count < 4; count++)
    {
      mReadyToCPU.addPosition(pReadyToCPU[count]);
      mCPUToReady.addPosition(pCPUToReady[count]);
      mCPUToBlocked.addPosition(pCPUToBlocked[count]);
    }
    engine.removeMTGO("TEMP");
  }

// A process has finished running normally.  Simply remove
// it from being displayed.  It shouldn't be in any queues.

  synchronized void processFinished(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    tmpMTGO.bVisible = false;
    engine.removeMTGO("P" + iPID);
    syncPaint(lDelay);
  }

// A process has been removed without finishing being
// run.  This means that it could exist anywhere. ie.
// any queue, CPU or moving.

  synchronized void killProcess(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    tmpMTGO.bVisible = false;
    engine.removeMTGO("P" + iPID);

    // Remove it from any queues.

    removeQueue(ProcessScheduler.READYQ, iPID);
    removeQueue(ProcessScheduler.BLOCKEDQ, iPID);
    removeQueue(ProcessScheduler.ZOMBIEQ, iPID);
    syncPaint(lDelay);
  }

// Move a given process from the CPU to the Blocked Queue.
// procID is the unique process identifier.

  synchronized void cpuToBlocked(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    mCPUToBlocked.start();
    while (!mCPUToBlocked.finished())
    {
      mCPUToBlocked.step();
      tmpMTGO.iX = mCPUToBlocked.iCurrentX;
      tmpMTGO.iY = mCPUToBlocked.iCurrentY;
      syncPaint(lDelay);
    }
  }

// Move a given process from the Blocked Queue to the Ready Queue.
// procID is the unique process indentifier.

  synchronized void blockedToReady(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    mBlockedToReady.start();
    while (!mBlockedToReady.finished())
    {
      mBlockedToReady.step();
      tmpMTGO.iX = mBlockedToReady.iCurrentX;
      tmpMTGO.iY = mBlockedToReady.iCurrentY;
      syncPaint(lDelay);
    }
  }

// Move a given process from the CPU to the Ready Queue.
// procID is the unique process identifier.

  synchronized void cpuToReady(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    mCPUToReady.start();
    while (!mCPUToReady.finished())
    {
      mCPUToReady.step();
      tmpMTGO.iX = mCPUToReady.iCurrentX;
      tmpMTGO.iY = mCPUToReady.iCurrentY;
      syncPaint(lDelay);
    }
  }

// Move a given process from the Ready Queue to the CPU.
// procID is the unique process identifier.

  synchronized void readyToCPU(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    mReadyToCPU.start();
    while (!mReadyToCPU.finished())
    {
      mReadyToCPU.step();
      tmpMTGO.iX = mReadyToCPU.iCurrentX;
      tmpMTGO.iY = mReadyToCPU.iCurrentY;
      syncPaint(lDelay);
    }
  }

// Create a new process with the given number.  This alternates between
// creating green and blue processes.

  synchronized void newProcess(int iPID)
  {
    //Alternate between blue and green processes.

    if ((iPID % 2) == 1)
      tmpPic = new MTGO(myImages[0], "P" + iPID, true, Color.darkGray);
    else
      tmpPic = new MTGO(myImages[1], "P" + iPID, true, Color.darkGray);
    tmpPic.iPriority = 2;
    tmpPic.iX = engine.CenterX(tmpPic);
    tmpPic.iY = iHeight+iBoxHeight;
    engine.addMTGO(tmpPic,this);
  }

// The first thing that a process does once allocated a terminal
// is to move from the Zomebie Queue to the Ready Queue.  This
// is where the process is told to move.

  synchronized void zombieToReady(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    mZombieToReady.start();
    while (!mZombieToReady.finished())
    {
      mZombieToReady.step();
      tmpMTGO.iX = mZombieToReady.iCurrentX;
      tmpMTGO.iY = mZombieToReady.iCurrentY;
      syncPaint(lDelay);
    }
  }

// If a process has left or joined a queue then given the
// queue type (1-3) redisplay all the processes that are in
// that queue.

  private synchronized void refreshQueue(int iQType)
  {
    LIFOQueue tmpQ = new LIFOQueue();
    int XPosition = engine.iCenterX-150;
    int YPosition = iQType*iHeight+iBoxHeight;
    switch (iQType)
    {
      case ProcessScheduler.READYQ:
        tmpQ = ReadyQ;
        break;
      case ProcessScheduler.BLOCKEDQ:
        tmpQ = BlockedQ;
        break;
      case ProcessScheduler.ZOMBIEQ:
        tmpQ = ZombieQ;
        break;
    }
    tmpQ.goToHead();
    while (!tmpQ.atTail())
    {
      String procID = (String) tmpQ.peek();
      tmpMTGO = engine.returnMTGO(procID);
      tmpMTGO.iX = XPosition;
      tmpMTGO.iY = YPosition;
      XPosition = XPosition + iBoxWidth;
      tmpQ.goToNext();
    }
    syncPaint(lDelay);
  }

  synchronized void moveReadyQ(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    mReady.start();
    int iPos = 10-ReadyQ.itemCount();
    while (!mReady.finished(iPos))
    {
      mReady.step();
      tmpMTGO.iX = mReady.iCurrentX;
      tmpMTGO.iY = mReady.iCurrentY;
      syncPaint(lDelay);
    }
  }

  synchronized void moveBlockedQ(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    mBlocked.start();
    int iPos = 10-ReadyQ.itemCount();
    while (!mBlocked.finished(iPos))
    {
      mBlocked.step();
      tmpMTGO.iX = mBlocked.iCurrentX;
      tmpMTGO.iY = mBlocked.iCurrentY;
      syncPaint(lDelay);
    }
  }

  synchronized void moveZombieQ(int iPID)
  {
    tmpMTGO = engine.returnMTGO("P" + iPID);
    mZombie.start();
    int iPos = 10-ReadyQ.itemCount();
    while (!mZombie.finished(iPos))
    {
      mZombie.step();
      tmpMTGO.iX = mZombie.iCurrentX;
      tmpMTGO.iY = mZombie.iCurrentY;
      syncPaint(lDelay);
    }
  }

  synchronized void addQueue(int iQType, int iPID)
  {
    String sProcessID = "P" + iPID;
    switch (iQType)
    {
      case ProcessScheduler.READYQ:
        ReadyQ.insert(sProcessID);
        moveReadyQ(iPID);
        break;
      case ProcessScheduler.BLOCKEDQ:
        BlockedQ.insert(sProcessID);
        moveBlockedQ(iPID);
        break;
      case ProcessScheduler.ZOMBIEQ:
        ZombieQ.insert(sProcessID);
        moveZombieQ(iPID);
        break;
    }
    refreshQueue(iQType);
  }

  synchronized void removeQueue(int iQType, int iPID)
  {
    switch (iQType)
    {
      case ProcessScheduler.READYQ:
        ReadyQ = removeProcID(iPID, ReadyQ);
        break;
      case ProcessScheduler.BLOCKEDQ:
        BlockedQ = removeProcID(iPID, BlockedQ);
        break;
      case ProcessScheduler.ZOMBIEQ:
        ZombieQ = removeProcID(iPID, ZombieQ);
        break;
    }
    refreshQueue(iQType);
  }

  synchronized LIFOQueue removeProcID (int iPID, LIFOQueue tmpQueue)
  {
    String tmpID;
    String sProcessID = "P" + iPID;
    tmpQueue.goToHead();
    while (!tmpQueue.atTail())
    {
      tmpID = (String) tmpQueue.peek();
      if (tmpID.compareTo(sProcessID) == 0)
      {
        tmpQueue.retrieveCurrent();
      }
      tmpQueue.goToNext();
    }
    return tmpQueue;
  }

// If you click on an object in the animation area then do
// something based on the object selected.  For example, display
// the CPU frame if you click on the CPU object.

  class MTGOSelection extends MouseAdapter
  {
    public void mousePressed(MouseEvent e)
    {
      try
      {
//        this.repaint();
        String sTemp = new String(engine.isInside(e.getX(),e.getY()));
        if (sTemp != " ")
        {
          if (sTemp.compareTo("RCOS CPU") == 0)
          {
            myProcessScheduler.showCPU();
          }
          System.out.println("");
        }
      }
      catch (Exception e2)
      {
      }
    }
  }

// Change the refresh rate based on the item selected.

  class SpeedSelection implements ItemListener
  {
    public void itemStateChanged(ItemEvent e)
    {
      String whichObject = (String) e.getItem();
      if (whichObject.compareTo("Fastest") == 0)
      {
        lDelay = 1;
      }
      else if (whichObject.compareTo("Fast") == 0)
      {
        lDelay = 3;
      }
      else if (whichObject.compareTo("Normal") == 0)
      {
        lDelay = 6;
      }
      else if (whichObject.compareTo("Slow") == 0)
      {
        lDelay = 12;
      }
      else if (whichObject.compareTo("Slowest") == 0)
      {
        lDelay = 24;
      }
    }
  }

// Change the quatum based on the option selected.

  class QuantumSelection implements ItemListener
  {
    public void itemStateChanged(ItemEvent e)
    {
      myProcessScheduler.sendQuantum(
        new Integer((String) e.getItem()));
    }
  }
}
