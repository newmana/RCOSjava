package org.rcosjava.software.animator.process;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.net.*;
import java.util.*;
import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.animator.support.RCOSBox;
import org.rcosjava.software.animator.support.mtgos.GraphicsEngine;
import org.rcosjava.software.animator.support.mtgos.MTGO;
import org.rcosjava.software.animator.support.positions.Movement;
import org.rcosjava.software.animator.support.positions.Position;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.util.LIFOQueue;

/**
 * Based on the commands given by processSchedulerAnimator displays graphically
 * what is happening.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 20/01/97 New Version Created. </DD>
 * <DD> 01/01/1997 First process set to 1. AN </DD>
 * <DD> 13/10/98 Converted to Java 1.1. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 22nd January 1996
 * @version 1.00 $Date$
 */
public class ProcessSchedulerPanel extends RCOSPanel
{
  private JPanel optionsPanel;

  /**
   * The process scheduler display area.
   */
  private GraphicsEngine engine;

  /**
   * The positioning of the CPU.
   */
  private Position cpuPosition;

  /**
   * The positions to move the process around the ready queue.
   */
  private Position[] readyPositions = new Position[11];

  /**
   * The positions to move the process around the blocked queue.
   */
  private Position[] blockedPositions = new Position[11];

  /**
   * The positions to move the process around the zombie queue.
   */
  private Position[] zombiePositions = new Position[11];

  /**
   * Positions to move process to the from the zombie to the ready queue.
   */
  private Position[] zombieToReadyPositions = new Position[6];

  /**
   * Positions to move process to the from the blocked to the ready queue.
   */
  private Position[] blockedToReadyPositions = new Position[6];

  /**
   * Positions to move process to the from the ready queue to the CPU.
   */
  private Position[] readyToCPUPositions = new Position[4];

  /**
   * Positions to move process to the from the CPU to the ready queue.
   */
  private Position[] cpuToReadyPositions = new Position[4];

  /**
   * Positions to move process to the from the CPU to the blocked queue.
   */
  private Position[] cpuToBlockedPositions = new Position[4];

  /**
   * Pictures to display.
   */
  private MTGO tmpPic, cpuPic;

  /**
   * Holds which processes are the the queue.
   */
  private LIFOQueue ZombieQ, ReadyQ, BlockedQ;

  /**
   * Holds the movement of the queue.
   */
  private Movement zombieMovement, blockedMovement, readyMovement;

  /**
   * Holds the movement of processes.
   */
  private Movement zombieToReadyMovement, blockedToReadyMovement,
    readyToCPUMovement, cpuToReadyMovement, cpuToBlockedMovement;

  /**
   * Description of the Field
   */
  private Image myImages[] = new Image[5];

  /**
   * Description of the Field
   */
  private int windowCenter;

  /**
   * Description of the Field
   */
  private int widthCount, heightCount;

  /**
   * Description of the Field
   */
  private int leftIndent, rightIndent;

  /**
   * Description of the Field
   */
  private int width, height;

  /**
   * Description of the Field
   */
  private int boxHeight, boxWidth;

  /**
   * Description of the Field
   */
  private int windowWidth, windowHeight;

  /**
   * Description of the Field
   */
  private long delay;

  /**
   * Description of the Field
   */
  private ProcessSchedulerAnimator myProcessScheduler;

  /**
   * Constructor for the ProcessSchedulerFrame object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param images Description of Parameter
   * @param thisProcessScheduler Description of Parameter
   */
  public ProcessSchedulerPanel(int x, int y, ImageIcon[] images,
      ProcessSchedulerAnimator thisProcessScheduler)
  {
    myImages = new Image[images.length];
    for (int index = 0; index < images.length-1; index++)
    {
      myImages[index] = images[index].getImage();
    }
    ZombieQ = new LIFOQueue(10, 0);
    BlockedQ = new LIFOQueue(10, 0);
    ReadyQ = new LIFOQueue(10, 0);
    windowWidth = x - 100;
    windowHeight = y - 175;
    delay = 1;
    myProcessScheduler = thisProcessScheduler;
  }

  /**
   * Sets up the layout for the whole process scheduler window. This uses the
   * border layout manager. The left (west) contains option, the center contains
   * the animation area and the bottom (south) contains the Close button.
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    optionsPanel = new JPanel();
    JPanel processPanel = new JPanel();
    JPanel main = new JPanel();

    main.setBackground(defaultBgColour);
    main.setForeground(defaultFgColour);
    main.setLayout(new BorderLayout());

    engine = new GraphicsEngine(this, windowWidth, windowHeight);
    engine.setBackgroundColour(defaultBgColour);

    width = engine.getWidth();
    height = engine.getHeight() / 5;
    leftIndent = 50;
    rightIndent = width - leftIndent;

    setupMovement();

    Choice speedOption = new Choice();
    Choice quantumOption = new Choice();
    Choice schedulerOption = new Choice();

    optionsPanel.setBackground(defaultBgColour);
    optionsPanel.setForeground(defaultFgColour);
    TitledBorder optionsTitle = BorderFactory.createTitledBorder("Options");
    optionsTitle.setTitleColor(defaultFgColour);
    optionsPanel.setBorder(BorderFactory.createCompoundBorder(
        optionsTitle, BorderFactory.createEmptyBorder(3,3,3,3)));

    // Set-up options for choosing the speed of refresh.

    // Create a temporary gridbaglayout for the options portion
    // of the screen.

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();

    optionsPanel.setLayout(gridBag);

    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

    speedOption.addItem("Fastest");
    speedOption.addItem("Fast");
    speedOption.addItem("Normal");
    speedOption.addItem("Slow");
    speedOption.addItem("Slowest");
    speedOption.setForeground(choiceColour);
    speedOption.setBackground(defaultBgColour);
    speedOption.select("Fastest");

    // Set-up the options for the choice of quantum ie.
    // time spent on the CPU.

    quantumOption.addItem("20");
    quantumOption.addItem("10");
    quantumOption.addItem("5");
    quantumOption.addItem("3");
    quantumOption.addItem("2");
    quantumOption.addItem("1");
    quantumOption.setForeground(choiceColour);
    quantumOption.setBackground(defaultBgColour);
    quantumOption.select("2");

    // Set-up the options for the choice of queue

    schedulerOption.addItem("FIFO");
    schedulerOption.addItem("LIFO");
    schedulerOption.addItem("Priority");
    schedulerOption.setForeground(choiceColour);
    schedulerOption.setBackground(defaultBgColour);
    schedulerOption.select("FIFO");

    JLabel tmpLabel = new JLabel();

    constraints.insets = new Insets(1, 3, 1, 1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    tmpLabel = new JLabel("Speed:", JLabel.CENTER);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    optionsPanel.add(tmpLabel);

    constraints.insets = new Insets(1, 6, 1, 1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(speedOption, constraints);
    optionsPanel.add(speedOption);
    speedOption.addItemListener(new SpeedSelection());

    constraints.insets = new Insets(1, 3, 1, 1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    tmpLabel = new JLabel("Quantum:", JLabel.CENTER);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    optionsPanel.add(tmpLabel);

    constraints.insets = new Insets(1, 6, 1, 1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(quantumOption, constraints);
    optionsPanel.add(quantumOption);
    quantumOption.addItemListener(new QuantumSelection());

    constraints.insets = new Insets(1, 3, 1, 1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    tmpLabel = new JLabel("Type:");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    optionsPanel.add(tmpLabel);

    constraints.insets = new Insets(1, 6, 1, 1);
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.gridheight = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(schedulerOption, constraints);
    optionsPanel.add(schedulerOption);
    schedulerOption.addItemListener(new SchedulerSelection());

    processPanel.add(engine);
    engine.addMouseListener(new MTGOSelection());

    main.add("Center", processPanel);
    main.add("West", optionsPanel);

    add("Center", main);
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
    drawBackground();
    engine.repaint();
    optionsPanel.repaint();
  }

  /**
   * Description of the Method
   *
   * @param time Description of Parameter
   */
  public synchronized void syncPaint(long time)
  {
    // do nothing
    try
    {
      wait(time);
    }
    catch (Exception v)
    {
    }
    repaint(100);
  }

  /**
   * Perhaps a rather dumb way of creating the movement arrays between the CPU
   * and queues. There must be a better way?
   */
  void setupMovement()
  {
    // Create the animation area for the panel.
    cpuPic = new MTGO(myImages[2], "RCOS CPU", false);
    cpuPic.priority = 1;
    engine.addMTGO(cpuPic, this);
    cpuPic.xPosition = engine.CenterX(cpuPic);
    cpuPic.yPosition = 0;
    tmpPic = new MTGO(myImages[0], "TEMP", false);
    tmpPic.priority = 2;
    engine.addMTGO(tmpPic, this);
    boxHeight = 30;
    boxWidth = 30;

    int count;

    int count2;

    int increment;

    int iArr;

    // Movement inside queues

    for (count = 1; count <= 3; count++)
    {
      for (count2 = 5; count2 > -6; count2--)
      {
        if (count2 > -5)
        {
          increment = -2;
        }
        else
        {
          increment = 0;
        }
        iArr = (count2 - 5) * -1;
        if (count == 1)
        {
          readyPositions[iArr] = new Position(((count2 * boxHeight) + engine.centerX),
              ((count * height) + boxHeight), increment, 0);
        }
        if (count == 2)
        {
          blockedPositions[iArr] = new Position(((count2 * boxHeight) + engine.centerX),
              ((count * height) + boxHeight), increment, 0);
        }
        if (count == 3)
        {
          zombiePositions[iArr] = new Position(((count2 * boxHeight) + engine.centerX),
              ((count * height) + boxHeight), increment, 0);
        }
      }
    }

    // Movement of process from Zombie to Ready

    zombieToReadyPositions[0] = new Position(engine.centerX - 180, (3 * height) + boxHeight, -2, 0);
    zombieToReadyPositions[1] = new Position(leftIndent - (boxWidth / 2), (3 * height) + boxHeight, 0, -2);
    zombieToReadyPositions[2] = new Position(leftIndent - (boxWidth / 2), (2 * height) + 45 + (boxHeight / 2), 2, 0);
    zombieToReadyPositions[3] = new Position(rightIndent - (boxWidth / 2), (2 * height) + 45 + (boxHeight / 2), 0, -2);
    zombieToReadyPositions[4] = new Position(rightIndent - (boxWidth / 2), height + boxHeight, -2, 0);
    zombieToReadyPositions[5] = new Position(engine.centerX + 150, height + boxHeight, 0, 0);

    // Movement of process from Blocked to Ready

    blockedToReadyPositions[0] = new Position(engine.centerX - 180, (2 * height) + boxHeight, -2, 0);
    blockedToReadyPositions[1] = new Position(leftIndent - (boxWidth / 2), (2 * height) + boxHeight, 0, -2);
    blockedToReadyPositions[2] = new Position(leftIndent - (boxWidth / 2), height + 45 + (boxHeight / 2), 2, 0);
    blockedToReadyPositions[3] = new Position(rightIndent - (boxWidth / 2), height + 45 + (boxHeight / 2), 0, -2);
    blockedToReadyPositions[4] = new Position(rightIndent - (boxWidth / 2), height + boxHeight, -2, 0);
    blockedToReadyPositions[5] = new Position(engine.centerX + 150, height + boxHeight, 0, 0);

    // Movement of process from Ready to CPU

    readyToCPUPositions[0] = new Position(engine.centerX - 180, height + boxHeight, -2, 0);
    readyToCPUPositions[1] = new Position(leftIndent - (boxWidth / 2), height + boxHeight, 0, -2);
    readyToCPUPositions[2] = new Position(leftIndent - (boxWidth / 2), boxHeight / 2, 2, 0);
    readyToCPUPositions[3] = new Position(engine.CenterX(tmpPic), boxHeight / 2, 0, 0);

    // Movement of process from CPU to Ready

    cpuToReadyPositions[0] = new Position(engine.CenterX(tmpPic), boxHeight / 2, 2, 0);
    cpuToReadyPositions[1] = new Position(rightIndent - (boxWidth / 2), boxHeight / 2, 0, 2);
    cpuToReadyPositions[2] = new Position(rightIndent - (boxWidth / 2), height + boxHeight, -2, 0);
    cpuToReadyPositions[3] = new Position(engine.centerX + 150, height + boxHeight, 0, 0);

    // Movement of process from CPU to Blocked

    cpuToBlockedPositions[0] = new Position(engine.CenterX(tmpPic), boxHeight / 2, 2, 0);
    cpuToBlockedPositions[1] = new Position(rightIndent - (boxWidth / 2), boxHeight / 2, 0, 2);
    cpuToBlockedPositions[2] = new Position(rightIndent - (boxWidth / 2), (2 * height) + boxHeight, -2, 0);
    cpuToBlockedPositions[3] = new Position(engine.centerX + 150, (2 * height) + boxHeight, 0, 0);

    // Add all positions to create a movement.

    zombieToReadyMovement = new Movement();
    blockedToReadyMovement = new Movement();
    readyToCPUMovement = new Movement();
    cpuToReadyMovement = new Movement();
    cpuToBlockedMovement = new Movement();
    readyMovement = new Movement();
    blockedMovement = new Movement();
    zombieMovement = new Movement();

    for (count = 0; count < 11; count++)
    {
      readyMovement.addPosition(readyPositions[count]);
      blockedMovement.addPosition(blockedPositions[count]);
      zombieMovement.addPosition(zombiePositions[count]);
    }

    for (count = 0; count < 6; count++)
    {
      blockedToReadyMovement.addPosition(blockedToReadyPositions[count]);
      zombieToReadyMovement.addPosition(zombieToReadyPositions[count]);
    }

    for (count = 0; count < 4; count++)
    {
      readyToCPUMovement.addPosition(readyToCPUPositions[count]);
      cpuToReadyMovement.addPosition(cpuToReadyPositions[count]);
      cpuToBlockedMovement.addPosition(cpuToBlockedPositions[count]);
    }
    engine.removeMTGO("TEMP");
  }

  /**
   * A process has finished running normally. Simply remove it from being
   * displayed. It shouldn't be in any queues.
   *
   * @param pid Description of Parameter
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
   * A process has been removed without finishing being run. This means that it
   * could exist anywhere. ie. any queue, CPU or moving.
   *
   * @param pid Description of Parameter
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
   * Create a new process with the given number. This alternates between
   * creating green and blue processes.
   *
   * @param pid Description of Parameter
   */
  synchronized void newProcess(int pid)
  {
    //Alternate between blue and green processes.
    if ((pid % 2) == 1)
    {
      tmpPic = new MTGO(myImages[0], "P" + pid, true, Color.darkGray);
    }
    else
    {
      tmpPic = new MTGO(myImages[1], "P" + pid, true, Color.darkGray);
    }
    tmpPic.priority = 2;
    tmpPic.xPosition = engine.CenterX(tmpPic);
    tmpPic.yPosition = height + boxHeight;
    engine.addMTGO(tmpPic, this);
  }


  /**
   * The first thing that a process does once allocated a terminal is to move
   * from the Zomebie Queue to the Ready Queue. This is where the process is
   * told to move.
   *
   * @param pid Description of Parameter
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
   * Description of the Method
   *
   * @param pid Description of Parameter
   */
  synchronized void moveReadyQ(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);

    readyMovement.start();

    int iPos = 10 - ReadyQ.itemCount();

    while (!readyMovement.finished(iPos))
    {
      readyMovement.step();
      tmpMTGO.xPosition = readyMovement.getCurrentX();
      tmpMTGO.yPosition = readyMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   */
  synchronized void moveBlockedQ(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);

    blockedMovement.start();

    int iPos = 10 - ReadyQ.itemCount();

    while (!blockedMovement.finished(iPos))
    {
      blockedMovement.step();
      tmpMTGO.xPosition = blockedMovement.getCurrentX();
      tmpMTGO.yPosition = blockedMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   */
  synchronized void moveZombieQ(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);

    zombieMovement.start();

    int iPos = 10 - ReadyQ.itemCount();

    while (!zombieMovement.finished(iPos))
    {
      zombieMovement.step();
      tmpMTGO.xPosition = zombieMovement.getCurrentX();
      tmpMTGO.yPosition = zombieMovement.getCurrentY();
      syncPaint(delay);
    }
  }

  /**
   * Adds a feature to the Queue attribute of the ProcessSchedulerFrame object
   *
   * @param queueType The feature to be added to the Queue attribute
   * @param pid The feature to be added to the Queue attribute
   */
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

  /**
   * Description of the Method
   *
   * @param queueType Description of Parameter
   * @param pid Description of Parameter
   */
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

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   * @param tmpQueue Description of Parameter
   * @return Description of the Returned Value
   */
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
   * Description of the Method
   */
  private void drawBackground()
  {
    //Draw the black background or image (if it's set)
    engine.drawBackground();

    int increment;

    engine.pad.setColor(Color.lightGray);
    engine.pad.setFont(defaultFont);

    FontMetrics fm = getFontMetrics(defaultFont);

    for (heightCount = 1; heightCount <= 3; heightCount++)
    {
      String title;

      if (heightCount == 1)
      {
        title = "Ready Queue";
      }
      else if (heightCount == 2)
      {
        title = "Blocked Queue";
      }
      else
      {
        title = "Zombie Queue";
      }

      windowCenter = (width / 2) - (fm.stringWidth(title) / 2);
      engine.pad.drawString(title, windowCenter, (heightCount * (height)) + 20);

      for (widthCount = -5; widthCount < 5; widthCount++)
      {
        engine.pad.draw3DRect((engine.centerX) + widthCount * boxHeight, (heightCount * (height)) + boxHeight, boxHeight, boxHeight, true);
      }
      //Lines out of boxes.
      engine.pad.drawLine(leftIndent, heightCount * (height) + 45, (engine.centerX) - 150, heightCount * (height) + 45);
      engine.pad.drawLine(engine.centerX + 150, heightCount * (height) + 45, width - leftIndent, heightCount * (height) + 45);
    }

    //Horizontal lines for zombie and blocked
    engine.pad.drawLine(leftIndent, 2 * (height) - 5, rightIndent, 2 * (height) - 5);
    engine.pad.drawLine(leftIndent, 3 * (height) - 5, rightIndent, 3 * (height) - 5);

    //Horizontal lines off CPU
    engine.pad.drawLine(leftIndent, cpuPic.imageHeight / 2, engine.CenterX(cpuPic), cpuPic.imageHeight / 2);
    engine.pad.drawLine(engine.CenterX(cpuPic) + cpuPic.imageWidth / 2, cpuPic.imageHeight / 2, rightIndent, cpuPic.imageHeight / 2);

    //2 Vertical lines
    engine.pad.drawLine(leftIndent, cpuPic.imageHeight / 2, leftIndent, 3 * (height) + 45);
    engine.pad.drawLine(rightIndent, cpuPic.imageHeight / 2, rightIndent, 3 * (height) + 45);
  }

  /**
   * If a process has left or joined a queue then given the queue type (1-3)
   * redisplay all the processes that are in that queue.
   *
   * @param queueType Description of Parameter
   */
  private synchronized void refreshQueue(int queueType)
  {
    LIFOQueue tmpQ = new LIFOQueue();
    int XPosition = engine.centerX - 150;
    int YPosition = queueType * height + boxHeight;

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

  /**
   * If you click on an object in the animation area then do something based on
   * the object selected. For example, display the CPU frame if you click on the
   * CPU object.
   *
   * @author administrator
   * @created 28 April 2002
   */
  class MTGOSelection extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mousePressed(MouseEvent e)
    {
      try
      {
//        this.repaint();
        String sTemp = new String(engine.isInside(e.getX(), e.getY()));

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
   *
   * @author administrator
   * @created 28 April 2002
   */
  class SpeedSelection implements ItemListener
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
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
   *
   * @author administrator
   * @created 28 April 2002
   */
  class SchedulerSelection implements ItemListener
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
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
   *
   * @author administrator
   * @created 28 April 2002
   */
  class QuantumSelection implements ItemListener
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void itemStateChanged(ItemEvent e)
    {
      myProcessScheduler.sendQuantum(
          new Integer((String) e.getItem()));
    }
  }
}