package net.sourceforge.rcosjava.software.animator.process;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;
import net.sourceforge.rcosjava.software.animator.support.RCOSBox;
import net.sourceforge.rcosjava.software.animator.support.mtgos.GraphicsEngine;
import net.sourceforge.rcosjava.software.animator.support.mtgos.MTGO;
import net.sourceforge.rcosjava.software.animator.support.positions.Movement;
import net.sourceforge.rcosjava.software.animator.support.positions.Position;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.util.LIFOQueue;

/**
 * Based on the commands given by processSchedulerAnimator displays graphically
 * what is happening.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 20/01/97  New Version Created.
 * </DD><DD>
 * 01/01/1997 First process set to 1. AN
 * </DD><DD>
 * 13/10/98  Converted to Java 1.1.
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 22nd January 1996
 */
public class ProcessSchedulerFrame extends RCOSFrame
{
  private GraphicsEngine engine;
  private Position cpuPosition;
  private Position[] readyPositions = new Position[11];
  private Position[] blockedPositions = new Position[11];
  private Position[] zombiePositions = new Position[11];
  private Position[] zombieToReadyPositions = new Position[6];
  private Position[] blockedToReadyPositions = new Position[6];
  private Position[] readyToCPUPositions = new Position[4];
  private Position[] cpuToReadyPositions = new Position[4];
  private Position[] cpuToBlockedPositions = new Position[4];
  private MTGO tmpPic, cpuPic;
  private LIFOQueue ZombieQ, ReadyQ, BlockedQ;
  private Movement zombieMovement, blockedMovement, readyMovement;
  private Movement zombieToReadyMovement, blockedToReadyMovement, readyToCPUMovement,
    cpuToReadyMovement, cpuToBlockedMovement;
  private Panel mainPanel, closePanel, westPanel;
  private Image myImages[] = new Image[5];
  private int windowCenter;
  private int widthCount, heightCount;
  private int leftIndent, rightIndent;
  private int width, height;
  private int boxHeight, boxWidth;
  private int windowWidth, windowHeight;
  private long delay;
  private ProcessSchedulerAnimator myProcessScheduler;
  private RCOSBox box;

  public ProcessSchedulerFrame(int x, int y, Image[] images,
    ProcessSchedulerAnimator thisProcessScheduler)
  {
    setTitle("Process Scheduler Animator");
    myImages = images;
    ZombieQ = new LIFOQueue(10,0);
    BlockedQ = new LIFOQueue(10,0);
    ReadyQ = new LIFOQueue(10,0);
    windowWidth = x;
    windowHeight = y;
    delay = 1;
    myProcessScheduler = thisProcessScheduler;
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
    drawBackground();
    engine.repaint();
    box.repaint();
  }

  public synchronized void syncPaint(long time)
  {
    // do nothing
    try
    {
      wait(time);
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

    int increment;

    engine.pad.setColor(Color.lightGray);
    engine.pad.setFont(titleFont);
    FontMetrics fm = getFontMetrics(titleFont);

    for (heightCount = 1; heightCount <= 3; heightCount++)
    {
      String title;

      if (heightCount == 1)
      {
        title="Ready Queue";
      }
      else if (heightCount == 2)
      {
        title="Blocked Queue";
      }
      else
      {
        title="Zombie Queue";
      }

      windowCenter = (width/2) - (fm.stringWidth(title)/2);
      engine.pad.drawString(title,windowCenter,(heightCount*(height))+20);

      for (widthCount = -5; widthCount < 5; widthCount++)
      {
        engine.pad.draw3DRect((engine.centerX)+widthCount*boxHeight,(heightCount*(height))+boxHeight,boxHeight,boxHeight,true);
      }
      //Lines out of boxes.
      engine.pad.drawLine(leftIndent,heightCount*(height)+45,(engine.centerX)-150,heightCount*(height)+45);
      engine.pad.drawLine(engine.centerX+150,heightCount*(height)+45,width-leftIndent,heightCount*(height)+45);
    }

    //Horizontal lines for zombie and blocked
    engine.pad.drawLine(leftIndent,2*(height)-5,rightIndent,2*(height)-5);
    engine.pad.drawLine(leftIndent,3*(height)-5,rightIndent,3*(height)-5);

    //Horizontal lines off CPU
    engine.pad.drawLine(leftIndent,cpuPic.imageHeight/2,engine.CenterX(cpuPic),cpuPic.imageHeight/2);
    engine.pad.drawLine(engine.CenterX(cpuPic)+cpuPic.imageWidth/2,cpuPic.imageHeight/2,rightIndent,cpuPic.imageHeight/2);

    //2 Vertical lines
    engine.pad.drawLine(leftIndent,cpuPic.imageHeight/2,leftIndent,3*(height)+45);
    engine.pad.drawLine(rightIndent,cpuPic.imageHeight/2,rightIndent,3*(height)+45);
  }


  /**
   * Sets up the layout for the whole process scheduler window.
   * This uses the border layout manager.  The left (west) contains
   * option, the center contains the animation area and the bottom
   * (south) contains the Close button.
   */
  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    engine = new GraphicsEngine(this, windowWidth, windowHeight);
    engine.backgroundColour = defaultBgColour;

    width = engine.getWidth();
    height = engine.getHeight() / 5;
    leftIndent = 50;
    rightIndent = width - leftIndent;

    setupMovement();

    mainPanel = new Panel();
    closePanel = new Panel();
    westPanel = new Panel();
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
    speedOption.setBackground(choiceColour);
    speedOption.setForeground(defaultFgColour);
    speedOption.select("Fastest");

// Set-up the options for the choice of quantum ie.
// time spent on the CPU.

    quantumOption.addItem("20");
    quantumOption.addItem("10");
    quantumOption.addItem("5");
    quantumOption.addItem("3");
    quantumOption.addItem("2");
    quantumOption.addItem("1");
    quantumOption.setBackground(choiceColour);
    quantumOption.setForeground(defaultFgColour);
    quantumOption.select("2");

// Set-up the options for the choice of queue

    schedulerOption.addItem("FIFO");
    schedulerOption.addItem("LIFO");
    schedulerOption.addItem("Priority");
    schedulerOption.setBackground(choiceColour);
    schedulerOption.setForeground(defaultFgColour);
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
    lTmpLabel = new NewLabel("Speed:", defaultFont);
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
    lTmpLabel = new NewLabel("Quantum:", defaultFont);
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
    lTmpLabel = new NewLabel("Type:", defaultFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pTemp.add(lTmpLabel);

    constraints.insets=new Insets(1,6,1,1);
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.gridheight=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(schedulerOption,constraints);
    pTemp.add(schedulerOption);
    schedulerOption.addItemListener(new SchedulerSelection());

    box = new RCOSBox(pTemp,new NewLabel("Options", titleFont),3,3,3,3);

    westPanel.setLayout(gridBag);
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;

    constraints.insets=new Insets(21,2,1,2);
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.NORTH;
    gridBag.setConstraints(box,constraints);
    westPanel.add(box);

    closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new Button("Close");
    closePanel.add(tmpButton);
    tmpButton.addMouseListener(new RCOSFrame.CloseAnimator());

    mainPanel.add(engine);
    engine.addMouseListener(new MTGOSelection());

    add("Center",mainPanel);
    add("South",closePanel);
    add("West", westPanel);
  }

  /**
   * Perhaps a rather dumb way of creating the movement arrays between
   * the CPU and queues.  There must be a better way?
   */
  void setupMovement()
  {
    // Create the animation area for the panel.
    cpuPic = new MTGO(myImages[2], "RCOS CPU", false);
    cpuPic.priority = 1;
    engine.addMTGO(cpuPic,this);
    cpuPic.xPosition = engine.CenterX(cpuPic);
    cpuPic.yPosition = 0;
    tmpPic = new MTGO(myImages[0], "TEMP", false);
    tmpPic.priority = 2;
    engine.addMTGO(tmpPic,this);
    boxHeight = 30;
    boxWidth = 30;

    int count, count2, increment, iArr;

    // Movement inside queues

    for (count=1; count <= 3; count++)
    {
      for (count2=5; count2 > -6; count2--)
      {
        if (count2 > -5)
        {
          increment=-2;
        }
        else
        {
          increment=0;
        }
        iArr = (count2-5) * -1;
        if (count == 1)
        {
          readyPositions[iArr] = new Position(((count2*boxHeight)+engine.centerX),
            ((count*height)+boxHeight),increment,0);
        }
        if (count == 2)
        {
          blockedPositions[iArr] = new Position(((count2*boxHeight)+engine.centerX),
            ((count*height)+boxHeight),increment,0);
        }
        if (count == 3)
        {
          zombiePositions[iArr] = new Position(((count2*boxHeight)+engine.centerX),
            ((count*height)+boxHeight),increment,0);
        }
      }
    }

    // Movement of process from Zombie to Ready

    zombieToReadyPositions[0] = new Position(engine.centerX-180,(3*height)+boxHeight,-2,0);
    zombieToReadyPositions[1] = new Position(leftIndent-(boxWidth/2),(3*height)+boxHeight,0,-2);
    zombieToReadyPositions[2] = new Position(leftIndent-(boxWidth/2),(2*height)+45+(boxHeight/2),2,0);
    zombieToReadyPositions[3] = new Position(rightIndent-(boxWidth/2),(2*height)+45+(boxHeight/2),0,-2);
    zombieToReadyPositions[4] = new Position(rightIndent-(boxWidth/2),height+boxHeight,-2,0);
    zombieToReadyPositions[5] = new Position(engine.centerX+150,height+boxHeight,0,0);

    // Movement of process from Blocked to Ready

    blockedToReadyPositions[0] = new Position(engine.centerX-180,(2*height)+boxHeight,-2,0);
    blockedToReadyPositions[1] = new Position(leftIndent-(boxWidth/2),(2*height)+boxHeight,0,-2);
    blockedToReadyPositions[2] = new Position(leftIndent-(boxWidth/2),height+45+(boxHeight/2),2,0);
    blockedToReadyPositions[3] = new Position(rightIndent-(boxWidth/2),height+45+(boxHeight/2),0,-2);
    blockedToReadyPositions[4] = new Position(rightIndent-(boxWidth/2),height+boxHeight,-2,0);
    blockedToReadyPositions[5] = new Position(engine.centerX+150,height+boxHeight,0,0);

    // Movement of process from Ready to CPU

    readyToCPUPositions[0] = new Position(engine.centerX-180,height+boxHeight,-2,0);
    readyToCPUPositions[1] = new Position(leftIndent-(boxWidth/2),height+boxHeight,0,-2);
    readyToCPUPositions[2] = new Position(leftIndent-(boxWidth/2),boxHeight/2,2,0);
    readyToCPUPositions[3] = new Position(engine.CenterX(tmpPic),boxHeight/2,0,0);

    // Movement of process from CPU to Ready

    cpuToReadyPositions[0] = new Position(engine.CenterX(tmpPic),boxHeight/2,2,0);
    cpuToReadyPositions[1] = new Position(rightIndent-(boxWidth/2),boxHeight/2,0,2);
    cpuToReadyPositions[2] = new Position(rightIndent-(boxWidth/2),height+boxHeight,-2,0);
    cpuToReadyPositions[3] = new Position(engine.centerX+150,height+boxHeight,0,0);

    // Movement of process from CPU to Blocked

    cpuToBlockedPositions[0] = new Position(engine.CenterX(tmpPic),boxHeight/2,2,0);
    cpuToBlockedPositions[1] = new Position(rightIndent-(boxWidth/2),boxHeight/2,0,2);
    cpuToBlockedPositions[2] = new Position(rightIndent-(boxWidth/2),(2*height)+boxHeight,-2,0);
    cpuToBlockedPositions[3] = new Position(engine.centerX+150,(2*height)+boxHeight,0,0);

    // Add all positions to create a movement.

    zombieToReadyMovement = new Movement();
    blockedToReadyMovement = new Movement();
    readyToCPUMovement = new Movement();
    cpuToReadyMovement = new Movement();
    cpuToBlockedMovement = new Movement();
    readyMovement = new Movement();
    blockedMovement = new Movement();
    zombieMovement = new Movement();

    for (count=0; count < 11; count++)
    {
      readyMovement.addPosition(readyPositions[count]);
      blockedMovement.addPosition(blockedPositions[count]);
      zombieMovement.addPosition(zombiePositions[count]);
    }

    for (count=0; count < 6; count++)
    {
      blockedToReadyMovement.addPosition(blockedToReadyPositions[count]);
      zombieToReadyMovement.addPosition(zombieToReadyPositions[count]);
    }

    for (count=0; count < 4; count++)
    {
      readyToCPUMovement.addPosition(readyToCPUPositions[count]);
      cpuToReadyMovement.addPosition(cpuToReadyPositions[count]);
      cpuToBlockedMovement.addPosition(cpuToBlockedPositions[count]);
    }
    engine.removeMTGO("TEMP");
  }

  /**
   * A process has finished running normally.  Simply remove it from being
   * displayed.  It shouldn't be in any queues.
   */
  synchronized void processFinished(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);
    if (tmpMTGO != null)
    {
      tmpMTGO.isVisible = false;
      engine.removeMTGO("P" + pid);
    }
    syncPaint(delay);
  }

  /**
   * A process has been removed without finishing being run.  This means that
   * it could exist anywhere. ie. any queue, CPU or moving.
   */
  synchronized void killProcess(int pid)
  {
    // Remove it from any queues.

    removeQueue(ProcessScheduler.READYQ, pid);
    removeQueue(ProcessScheduler.BLOCKEDQ, pid);
    removeQueue(ProcessScheduler.ZOMBIEQ, pid);
    processFinished(pid);

    syncPaint(delay);
  }

  /**
   * Move a given process from the CPU to the Blocked Queue.
   *
   * @param pid is the unique process identifier.
   */
  synchronized void cpuToBlocked(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);
    cpuToBlockedMovement.start();
    while (!cpuToBlockedMovement.finished())
    {
      cpuToBlockedMovement.step();
      tmpMTGO.xPosition = cpuToBlockedMovement.getCurrentX();
      tmpMTGO.yPosition = cpuToBlockedMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  /**
   * Move a given process from the Blocked Queue to the Ready Queue.
   *
   * @param pid is the unique process indentifier.
   */
  synchronized void blockedToReady(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);
    blockedToReadyMovement.start();
    while (!blockedToReadyMovement.finished())
    {
      blockedToReadyMovement.step();
      tmpMTGO.xPosition = blockedToReadyMovement.getCurrentX();
      tmpMTGO.yPosition = blockedToReadyMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  /**
   * Move a given process from the CPU to the Ready Queue.
   *
   * @param pid is the unique process identifier.
   */
  synchronized void cpuToReady(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);
    cpuToReadyMovement.start();
    while (!cpuToReadyMovement.finished())
    {
      cpuToReadyMovement.step();
      tmpMTGO.xPosition = cpuToReadyMovement.getCurrentX();
      tmpMTGO.yPosition = cpuToReadyMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  /**
   * Move a given process from the Ready Queue to the CPU.
   *
   * @param pid is the unique process identifier.
   */
  synchronized void readyToCPU(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);
    readyToCPUMovement.start();
    while (!readyToCPUMovement.finished())
    {
      readyToCPUMovement.step();
      tmpMTGO.xPosition = readyToCPUMovement.getCurrentX();
      tmpMTGO.yPosition = readyToCPUMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  /**
   * Create a new process with the given number.  This alternates between
   * creating green and blue processes.
   */
  synchronized void newProcess(int pid)
  {
    //Alternate between blue and green processes.
    if ((pid % 2) == 1)
      tmpPic = new MTGO(myImages[0], "P" + pid, true, Color.darkGray);
    else
      tmpPic = new MTGO(myImages[1], "P" + pid, true, Color.darkGray);
    tmpPic.priority = 2;
    tmpPic.xPosition = engine.CenterX(tmpPic);
    tmpPic.yPosition = height+boxHeight;
    engine.addMTGO(tmpPic,this);
  }


  /**
   * The first thing that a process does once allocated a terminal is to move
   * from the Zomebie Queue to the Ready Queue.  This is where the process is
   * told to move.
   */
  synchronized void zombieToReady(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);
    zombieToReadyMovement.start();
    while (!zombieToReadyMovement.finished())
    {
      zombieToReadyMovement.step();
      tmpMTGO.xPosition = zombieToReadyMovement.getCurrentX();
      tmpMTGO.yPosition = zombieToReadyMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  /**
   * If a process has left or joined a queue then given the queue type (1-3)
   * redisplay all the processes that are in that queue.
   */
  private synchronized void refreshQueue(int queueType)
  {
    LIFOQueue tmpQ = new LIFOQueue();
    int XPosition = engine.centerX-150;
    int YPosition = queueType*height+boxHeight;
    switch (queueType)
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
      MTGO tmpMTGO = engine.returnMTGO(procID);
      tmpMTGO.xPosition = XPosition;
      tmpMTGO.yPosition = YPosition;
      XPosition = XPosition + boxWidth;
      tmpQ.goToNext();
    }
    syncPaint(delay);
  }

  synchronized void moveReadyQ(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);
    readyMovement.start();
    int iPos = 10-ReadyQ.itemCount();
    while (!readyMovement.finished(iPos))
    {
      readyMovement.step();
      tmpMTGO.xPosition = readyMovement.getCurrentX();
      tmpMTGO.yPosition = readyMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  synchronized void moveBlockedQ(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);
    blockedMovement.start();
    int iPos = 10-ReadyQ.itemCount();
    while (!blockedMovement.finished(iPos))
    {
      blockedMovement.step();
      tmpMTGO.xPosition = blockedMovement.getCurrentX();
      tmpMTGO.yPosition = blockedMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  synchronized void moveZombieQ(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);
    zombieMovement.start();
    int iPos = 10-ReadyQ.itemCount();
    while (!zombieMovement.finished(iPos))
    {
      zombieMovement.step();
      tmpMTGO.xPosition = zombieMovement.getCurrentX();
      tmpMTGO.yPosition = zombieMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  synchronized void addQueue(int queueType, int pid)
  {
    String sProcessID = "P" + pid;
    switch (queueType)
    {
      case ProcessScheduler.READYQ:
        ReadyQ.insert(sProcessID);
        moveReadyQ(pid);
        break;
      case ProcessScheduler.BLOCKEDQ:
        BlockedQ.insert(sProcessID);
        moveBlockedQ(pid);
        break;
      case ProcessScheduler.ZOMBIEQ:
        ZombieQ.insert(sProcessID);
        moveZombieQ(pid);
        break;
    }
    refreshQueue(queueType);
  }

  synchronized void removeQueue(int queueType, int pid)
  {
    switch (queueType)
    {
      case ProcessScheduler.READYQ:
        ReadyQ = removeProcID(pid, ReadyQ);
        break;
      case ProcessScheduler.BLOCKEDQ:
        BlockedQ = removeProcID(pid, BlockedQ);
        break;
      case ProcessScheduler.ZOMBIEQ:
        ZombieQ = removeProcID(pid, ZombieQ);
        break;
    }
    refreshQueue(queueType);
  }

  synchronized LIFOQueue removeProcID(int pid, LIFOQueue tmpQueue)
  {
    String tmpID;
    String sProcessID = "P" + pid;
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

  /**
   * If you click on an object in the animation area then do something based
   * on the object selected.  For example, display the CPU frame if you click
   * on the CPU object.
   */
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
        }
      }
      catch (Exception e2)
      {
      }
    }
  }

  /**
   * Change the refresh rate based on the item selected.
   */
  class SpeedSelection implements ItemListener
  {
    public void itemStateChanged(ItemEvent e)
    {
      String whichObject = (String) e.getItem();
      if (whichObject.compareTo("Fastest") == 0)
      {
        delay = 1;
      }
      else if (whichObject.compareTo("Fast") == 0)
      {
        delay = 3;
      }
      else if (whichObject.compareTo("Normal") == 0)
      {
        delay = 6;
      }
      else if (whichObject.compareTo("Slow") == 0)
      {
        delay = 12;
      }
      else if (whichObject.compareTo("Slowest") == 0)
      {
        delay = 24;
      }
    }
  }

  /**
   * Change the type of process queueing.
   */
  class SchedulerSelection implements ItemListener
  {
    public void itemStateChanged(ItemEvent e)
    {
      String whichObject = (String) e.getItem();
      if (whichObject.compareTo("FIFO") == 0)
      {
        myProcessScheduler.sendSwitchFIFO();
      }
      else if (whichObject.compareTo("LIFO") == 0)
      {
        myProcessScheduler.sendSwitchLIFO();
      }
      else if (whichObject.compareTo("Priority") == 0)
      {
        myProcessScheduler.sendSwitchPriority();
      }
    }
  }

  /**
   * Change the quatum based on the option selected.
   */
  class QuantumSelection implements ItemListener
  {
    public void itemStateChanged(ItemEvent e)
    {
      myProcessScheduler.sendQuantum(
        new Integer((String) e.getItem()));
    }
  }
}
