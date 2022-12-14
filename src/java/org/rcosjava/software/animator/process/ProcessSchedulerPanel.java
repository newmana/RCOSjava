package org.rcosjava.software.animator.process;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.net.*;
import java.util.*;

import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.animator.support.RCOSRectangle;
import org.rcosjava.software.animator.support.mtgos.GraphicsEngine;
import org.rcosjava.software.animator.support.mtgos.MTGO;
import org.rcosjava.software.animator.support.positions.Movement;
import org.rcosjava.software.animator.support.positions.Position;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;
import org.rcosjava.software.util.Queue;

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
  /**
   * Serial id.
   */
  private static final long serialVersionUID = 5574586892694168300L;

  /**
   * The process scheduler display area.
   */
  private GraphicsEngine engine;

  /**
   * The positions to move the process around the ready queue.
   */
  private Position[] readyPositions;

  /**
   * The positions to move the process around the blocked queue.
   */
  private Position[] blockedPositions;

  /**
   * The positions to move the process around the zombie queue.
   */
  private Position[] zombiePositions;

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
  private MTGO cpuPic;

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
   * Images to use to display.
   */
  private Image myImages[] = new Image[1];

  /**
   * Process images.
   */
  private Image processImages[] = new Image[2];

  /**
   * Center of the window.
   */
  private int windowCenter;

  /**
   * Indentation into the graphics engine.
   */
  private int leftIndent, rightIndent;

  /**
   * Dimensions of the graphics engine.
   */
  private int width, height;

  /**
   * The number of processes in a single queue.
   */
   private int noBoxes;

  /**
   * The dimensions of the box.
   */
  private int boxHeight, boxWidth;

  /**
   * Metrics for the default font.
   */
  private FontMetrics fm = getFontMetrics(defaultFont);

  /**
   * The process scheduler that we are representing.
   */
  private ProcessSchedulerAnimator myProcessScheduler;

  /**
   * True if we've successfully set-up the movement of the processes.
   */
  private boolean movementSetup = false;

  /**
   * Combo box for selecting the speed of animation.
   */
  private JComboBox speedOption = new JComboBox();

  /**
   * Combo box for selecting the quantum of the processes.
   */
  private JComboBox quantumOption = new JComboBox();

  /**
   * Combo box for selecting the scheduler option.
   */
  private JComboBox schedulerOption = new JComboBox();

  /**
   * Constructor for the ProcessSchedulerFrame object
   *
   * @param images Description of Parameter
   * @param newProcessSchedulerAnimator Description of Parameter
   */
  public ProcessSchedulerPanel(ImageIcon[] images,
      ProcessSchedulerAnimator newProcessSchedulerAnimator)
  {
    myImages = new Image[images.length];
    for (int index = 0; index < images.length; index++)
    {
      myImages[index] = images[index].getImage();
    }

    setManager(newProcessSchedulerAnimator);
  }

  /**
   * Sets a new process scheduler animator.
   *
   * @param newProcessSchedulerAnimator the new process scheduler animator.
   */
  void setManager(ProcessSchedulerAnimator newProcessSchedulerAnimator)
  {
    myProcessScheduler = newProcessSchedulerAnimator;
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
    // Initialise the graphics engine.
    engine = new GraphicsEngine();

    // Setup the process graphics.
    boxHeight = 25;
    boxWidth = 25;

    BufferedImage process;
    Graphics processGraphics;

    // Set-up green process
    process = new BufferedImage(boxWidth, boxHeight,
        BufferedImage.TYPE_INT_ARGB);
    processGraphics = process.createGraphics();
    processGraphics.setColor(new Color(51, 255, 153));
    processGraphics.fillOval(0, 0, boxWidth, boxHeight);
    processImages[0] = process;

    // Set-up blue process
    process = new BufferedImage(boxWidth, boxHeight,
        BufferedImage.TYPE_INT_ARGB);
    processGraphics = process.createGraphics();
    processGraphics.setColor(new Color(153, 153, 255));
    processGraphics.fillOval(0, 0, boxWidth, boxHeight);
    processImages[1] = process;

    JPanel optionsPanel = new JPanel();
    JPanel processPanel = new JPanel();

    setBackground(defaultBgColour);
    setForeground(defaultFgColour);
    setLayout(new BorderLayout());

    // Setup the panel with a border around it.
    optionsPanel.setBackground(defaultBgColour);
    optionsPanel.setForeground(defaultFgColour);
    TitledBorder optionsTitle = BorderFactory.createTitledBorder("Options");
    optionsTitle.setTitleColor(defaultFgColour);
    optionsPanel.setBorder(BorderFactory.createCompoundBorder(
        optionsTitle, BorderFactory.createEmptyBorder(3,3,3,3)));

    // Create a temporary gridbaglayout for the options portion
    // of the screen.
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

    GridBagLayout gridBag = new GridBagLayout();
    optionsPanel.setLayout(gridBag);

    // Set-up options for choosing the speed of refresh.
    speedOption.addItem("Fastest");
    speedOption.addItem("Fast");
    speedOption.addItem("Normal");
    speedOption.addItem("Slow");
    speedOption.addItem("Slowest");
    speedOption.setForeground(listFgColour);
    speedOption.setBackground(listBgColour);
    speedOption.setSelectedItem("Fastest");

    // Set-up the options for the choice of quantum ie.
    // time spent on the CPU.
    quantumOption.addItem("20");
    quantumOption.addItem("10");
    quantumOption.addItem("5");
    quantumOption.addItem("3");
    quantumOption.addItem("2");
    quantumOption.addItem("1");
    quantumOption.setForeground(listFgColour);
    quantumOption.setBackground(listBgColour);
    quantumOption.setSelectedItem("2");

    // Set-up the options for the choice of queue
    schedulerOption.addItem("FIFO");
    schedulerOption.addItem("LIFO");
    schedulerOption.addItem("Priority");
    schedulerOption.setForeground(choiceFgColour);
    schedulerOption.setBackground(choiceBgColour);
    schedulerOption.setSelectedItem("FIFO");

    // Create speed label and add the drop down.
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

    // Create quantum label and add the drop down.
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

    // Create type of queuing label and add the drop down.
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

    engine.setBackground(Color.white);
    engine.addMouseListener(new MTGOSelection());
    processPanel.add(engine);

    add(engine, BorderLayout.CENTER);
    add(optionsPanel, BorderLayout.WEST);
  }

  /**
   * Sets the currently selected item of the repaint speed selection.
   *
   * @param delay the delay value that was set.
   */
  void setDelay(long newDelay)
  {
    if (newDelay == 1)
    {
      speedOption.setSelectedItem("Fastest");
    }
    else if (newDelay == 3)
    {
      speedOption.setSelectedItem("Fast");
    }
    else if (newDelay == 6)
    {
      speedOption.setSelectedItem("Normal");
    }
    else if (newDelay == 12)
    {
      speedOption.setSelectedItem("Slow");
    }
    else if (newDelay == 24)
    {
      speedOption.setSelectedItem("Slowest");
    }
    speedOption.repaint();
  }

  /**
   * Sets the currently selected item for the process' quantum.
   *
   * @param newQuantum the quantum value to select.
   */
  void setQuantum(int newQuantum)
  {
    Integer newValue = new Integer(newQuantum);
    quantumOption.setSelectedItem(newValue.toString());
    quantumOption.repaint();
  }

  /**
   * Sets the currently selected item for the queue type.
   */
  void setQueueType(int newQueueType)
  {
    if (newQueueType == 1)
    {
      schedulerOption.setSelectedItem("FIFO");
      myProcessScheduler.switchToFIFO();
    }
    else if (newQueueType == 2)
    {
      schedulerOption.setSelectedItem("LIFO");
      myProcessScheduler.switchToLIFO();
    }
    else if (newQueueType == 3)
    {
      schedulerOption.setSelectedItem("Priority");
      myProcessScheduler.switchToPriority();
    }
    schedulerOption.repaint();
  }

  /**
   * Paint the compontents call super, then repaints background if engine
   * is initialized.
   *
   * @param g graphics object.
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);

    if (engine != null && engine.getPad() != null)
    {
      if (!movementSetup)
      {
        setupMovement();
      }
      drawBackground();
    }
  }

  /**
   * Waits for a given set of time and then calls repaint.  This is to slow
   * down repaints.
   *
   * @param time number of milliseconds to wait before running.
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
      v.printStackTrace();
    }
    repaint(100);
  }

  /**
   * Perhaps a rather dumb way of creating the movement arrays between the CPU
   * and queues. There must be a better way?
   */
  void setupMovement()
  {
    cpuPic = new MTGO(myImages[0], "RCOS CPU", false);
    cpuPic.setPriority(1);
    engine.addMTGO(cpuPic, this);
    cpuPic.setXPosition(engine.centerImageWidth(cpuPic.getImageWidth()));
    cpuPic.setYPosition(0);

    width = engine.getWidth();
    height = (engine.getHeight() - cpuPic.getImageHeight()) / 3;
    leftIndent = 50;
    rightIndent = width - leftIndent;

    int increment = 0;
    int arrayIndex = 0;

    noBoxes = ((engine.getWidth() - (leftIndent * 2)) / boxWidth);
    int halfNoBoxes =  noBoxes / 2;
    int firstHalfNoBoxes = halfNoBoxes + (noBoxes % 2);

    readyPositions = new Position[noBoxes];
    blockedPositions = new Position[noBoxes];
    zombiePositions = new Position[noBoxes];

    // Movement inside queues
    for (int count = 1; count <= 3; count++)
    {
      // Position of the bottom of the text plus space between title and box.
      int topOfBox = (count * height) + fm.getHeight() + 5;

      arrayIndex = noBoxes - 1;
      increment = -2;

      for (int count2 = -firstHalfNoBoxes; count2 < halfNoBoxes; count2++)
      {
        if (count == 1)
        {
          readyPositions[arrayIndex] = new Position(
              ((count2 * boxWidth) + engine.getCenterX()), topOfBox, increment,
              0);
        }
        if (count == 2)
        {
          blockedPositions[arrayIndex] = new Position(
              ((count2 * boxWidth) + engine.getCenterX()), topOfBox, increment,
              0);
        }
        if (count == 3)
        {
          zombiePositions[arrayIndex] = new Position(
              ((count2 * boxWidth) + engine.getCenterX()), topOfBox, increment,
              0);
        }
        arrayIndex--;
      }
    }

    // Process going through line out of CPI.
    int cpuLine = (cpuPic.getImageHeight() / 2) - (boxWidth / 2);
    int middleOfReady = height + fm.getHeight() + 5;
    int middleOfBlocked = (height * 2) + fm.getHeight() + 5;
    int middleOfZombie = (height * 3) + fm.getHeight() + 5;

    // Movement of process from Zombie to Ready
    zombieToReadyPositions[0] = new Position(engine.getCenterX() -
        (firstHalfNoBoxes * boxWidth), middleOfZombie, -2, 0);
    zombieToReadyPositions[1] = new Position(leftIndent - (boxWidth / 2),
        middleOfZombie, 0, -2);
    zombieToReadyPositions[2] = new Position(leftIndent - (boxWidth / 2),
        (height * 3) - 5 - (boxHeight / 2), 2, 0);
    zombieToReadyPositions[3] = new Position(rightIndent - (boxWidth / 2),
        (height * 3) - 5 - (boxHeight / 2), 0, -2);
    zombieToReadyPositions[4] = new Position(rightIndent - (boxWidth / 2),
        middleOfReady, -2, 0);
    zombieToReadyPositions[5] = new Position(engine.getCenterX() +
        (boxWidth * noBoxes / 2), middleOfReady, 0, 0);

    // Movement of process from Blocked to Ready
    blockedToReadyPositions[0] = new Position(engine.getCenterX() -
        (firstHalfNoBoxes * boxWidth), middleOfBlocked, -2, 0);
    blockedToReadyPositions[1] = new Position(leftIndent - (boxWidth / 2),
        middleOfBlocked, 0, -2);
    blockedToReadyPositions[2] = new Position(leftIndent - (boxWidth / 2),
        (height * 2) - 5 - (boxHeight / 2), 2, 0);
    blockedToReadyPositions[3] = new Position(rightIndent - (boxWidth / 2),
        (height * 2) - 5 - (boxHeight / 2), 0, -2);
    blockedToReadyPositions[4] = new Position(rightIndent - (boxWidth / 2),
        middleOfReady, -2, 0);
    blockedToReadyPositions[5] = new Position(engine.getCenterX() + 125,
        middleOfReady, 0, 0);

    // Movement of process from Ready to CPU
    readyToCPUPositions[0] = new Position(engine.getCenterX() -
        (firstHalfNoBoxes * boxWidth), middleOfReady, -2, 0);
    readyToCPUPositions[1] = new Position(leftIndent - (boxWidth / 2),
        middleOfReady, 0, -2);
    readyToCPUPositions[2] = new Position(leftIndent - (boxWidth / 2), cpuLine,
        2, 0);
    readyToCPUPositions[3] = new Position(engine.centerImageWidth(boxWidth),
        cpuLine, 0, 0);

    // Movement of process from CPU to Ready
    cpuToReadyPositions[0] = new Position(engine.centerImageWidth(boxWidth),
        cpuLine, 2, 0);
    cpuToReadyPositions[1] = new Position(rightIndent - (boxWidth / 2),
        cpuLine, 0, 2);
    cpuToReadyPositions[2] = new Position(rightIndent - (boxWidth / 2),
        middleOfReady, -2, 0);
    cpuToReadyPositions[3] = new Position(engine.getCenterX() + 125,
        middleOfReady, 0, 0);

    // Movement of process from CPU to Blocked
    cpuToBlockedPositions[0] = new Position(engine.centerImageWidth(boxWidth),
        cpuLine, 2, 0);
    cpuToBlockedPositions[1] = new Position(rightIndent - (boxWidth / 2),
        cpuLine, 0, 2);
    cpuToBlockedPositions[2] = new Position(rightIndent - (boxWidth / 2),
        middleOfBlocked, -2, 0);
    cpuToBlockedPositions[3] = new Position(engine.getCenterX() + 150, cpuLine,
        0, 0);

    // Add all positions to create a movement.
    zombieToReadyMovement = new Movement();
    blockedToReadyMovement = new Movement();
    readyToCPUMovement = new Movement();
    cpuToReadyMovement = new Movement();
    cpuToBlockedMovement = new Movement();
    readyMovement = new Movement();
    blockedMovement = new Movement();
    zombieMovement = new Movement();

    for (int count = 0; count < noBoxes; count++)
    {
      readyMovement.addPosition(readyPositions[count]);
      blockedMovement.addPosition(blockedPositions[count]);
      zombieMovement.addPosition(zombiePositions[count]);
    }

    for (int count = 0; count < 6; count++)
    {
      blockedToReadyMovement.addPosition(blockedToReadyPositions[count]);
      zombieToReadyMovement.addPosition(zombieToReadyPositions[count]);
    }

    for (int count = 0; count < 4; count++)
    {
      readyToCPUMovement.addPosition(readyToCPUPositions[count]);
      cpuToReadyMovement.addPosition(cpuToReadyPositions[count]);
      cpuToBlockedMovement.addPosition(cpuToBlockedPositions[count]);
    }

    engine.removeMTGO("TEMP");
    movementSetup = true;
  }

  /**
   * A process has finished running normally. Simply remove it from being
   * displayed. It shouldn't be in any queues.
   *
   * @param pid Description of Parameter
   */
  void processFinished(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);

    if (tmpMTGO != null)
    {
      tmpMTGO.setInvisible();
      engine.removeMTGO("P" + pid);
    }
    syncPaint(myProcessScheduler.getDelay());
  }

  /**
   * A process has been removed without finishing being run. This means that it
   * could exist anywhere. ie. any queue, CPU or moving.
   *
   * @param pid Description of Parameter
   */
  void killProcess(int pid)
  {
    // Remove it from any queues.
    processFinished(pid);
    syncPaint(myProcessScheduler.getDelay());
  }

  /**
   * Move a given process from the CPU to the Blocked Queue.
   *
   * @param pid is the unique process identifier.
   */
  void cpuToBlocked(int pid)
  {
    if (movementSetup)
    {
      MTGO tmpMTGO = engine.returnMTGO("P" + pid);

      cpuToBlockedMovement.start();
      while (!cpuToBlockedMovement.finished())
      {
        cpuToBlockedMovement.step();
        tmpMTGO.setXPosition(cpuToBlockedMovement.getCurrentX());
        tmpMTGO.setYPosition(cpuToBlockedMovement.getCurrentY());
        syncPaint(myProcessScheduler.getDelay());
      }
    }
  }

  /**
   * Move a given process from the Blocked Queue to the Ready Queue.
   *
   * @param pid is the unique process indentifier.
   */
  void blockedToReady(int pid)
  {
    if (movementSetup)
    {
      MTGO tmpMTGO = engine.returnMTGO("P" + pid);

      blockedToReadyMovement.start();
      while (!blockedToReadyMovement.finished())
      {
        blockedToReadyMovement.step();
        tmpMTGO.setXPosition(blockedToReadyMovement.getCurrentX());
        tmpMTGO.setYPosition(blockedToReadyMovement.getCurrentY());
        syncPaint(myProcessScheduler.getDelay());
      }
    }
  }

  /**
   * Move a given process from the CPU to the Ready Queue.
   *
   * @param pid is the unique process identifier.
   */
  void cpuToReady(int pid)
  {
    if (movementSetup)
    {
      MTGO tmpMTGO = engine.returnMTGO("P" + pid);

      cpuToReadyMovement.start();
      while (!cpuToReadyMovement.finished())
      {
        cpuToReadyMovement.step();
        tmpMTGO.setXPosition(cpuToReadyMovement.getCurrentX());
        tmpMTGO.setYPosition(cpuToReadyMovement.getCurrentY());
        syncPaint(myProcessScheduler.getDelay());
      }
    }
  }

  /**
   * Move a given process from the Ready Queue to the CPU.
   *
   * @param pid is the unique process identifier.
   */
  void readyToCPU(int pid)
  {
    if (movementSetup)
    {
      MTGO tmpMTGO = engine.returnMTGO("P" + pid);

      readyToCPUMovement.start();
      while (!readyToCPUMovement.finished())
      {
        readyToCPUMovement.step();
        tmpMTGO.setXPosition(readyToCPUMovement.getCurrentX());
        tmpMTGO.setYPosition(readyToCPUMovement.getCurrentY());
        syncPaint(myProcessScheduler.getDelay());
      }
    }
  }

  /**
   * Create a new process with the given number. This alternates between
   * creating green and blue processes.
   *
   * @param pid Description of Parameter
   */
  void newProcess(int pid)
  {
    MTGO tmpPic;

    //Alternate between blue and green processes.
    if ((pid % 2) == 1)
    {
      tmpPic = new MTGO(processImages[0], "P" + pid, true, Color.darkGray);
    }
    else
    {
      tmpPic = new MTGO(processImages[1], "P" + pid, true, Color.darkGray);
    }
    tmpPic.setPriority(2);
    tmpPic.setXPosition(engine.centerImageWidth(tmpPic.getImageWidth()));
    tmpPic.setYPosition(height + boxHeight);

    engine.addMTGO(tmpPic, this);
  }

  /**
   * The first thing that a process does once allocated a terminal is to move
   * from the Zomebie Queue to the Ready Queue. This is where the process is
   * told to move.
   *
   * @param pid Description of Parameter
   */
  void zombieToReady(int pid)
  {
    MTGO tmpMTGO = engine.returnMTGO("P" + pid);

    if (movementSetup)
    {
      zombieToReadyMovement.start();
      while (!zombieToReadyMovement.finished())
      {
        zombieToReadyMovement.step();
        tmpMTGO.setXPosition(zombieToReadyMovement.getCurrentX());
        tmpMTGO.setYPosition(zombieToReadyMovement.getCurrentY());
        syncPaint(myProcessScheduler.getDelay());
      }
    }
  }

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   */
  void moveReadyQueue(int pid, int itemCount)
  {
    if (movementSetup)
    {
      MTGO tmpMTGO = engine.returnMTGO("P" + pid);
      readyMovement.start();
      int pos = noBoxes - itemCount;

      while (!readyMovement.finished(pos))
      {
        readyMovement.step();
        tmpMTGO.setXPosition(readyMovement.getCurrentX());
        tmpMTGO.setYPosition(readyMovement.getCurrentY());
        syncPaint(myProcessScheduler.getDelay());
      }
    }
  }

  /**
   * Move the given process through the blocked queue to its place.
   *
   * @param pid the process id to move.
   * @param itemCount the number of items in the queue.
   */
  void moveBlockedQueue(int pid, int itemCount)
  {
    if (movementSetup)
    {
      MTGO tmpMTGO = engine.returnMTGO("P" + pid);
      blockedMovement.start();
      int pos = noBoxes - itemCount;

      while (!blockedMovement.finished(pos))
      {
        blockedMovement.step();
        tmpMTGO.setXPosition(blockedMovement.getCurrentX());
        tmpMTGO.setYPosition(blockedMovement.getCurrentY());
        syncPaint(myProcessScheduler.getDelay());
      }
    }
  }

  /**
   * Move the given process through the zombie queue to its place.
   *
   * @param pid process id to move.
   * @param itemCount the number of items in the queue.
   */
  void moveZombieQueue(int pid, int itemCount)
  {
    if (movementSetup)
    {
      MTGO tmpMTGO = engine.returnMTGO("P" + pid);
      zombieMovement.start();
      int pos = noBoxes - itemCount;

      while (!zombieMovement.finished(pos))
      {
        zombieMovement.step();
        tmpMTGO.setXPosition(zombieMovement.getCurrentX());
        tmpMTGO.setYPosition(zombieMovement.getCurrentY());
        syncPaint(myProcessScheduler.getDelay());
      }
    }
  }

  /**
   * Draw the background images the queue names, the boxes, etc.
   */
  private synchronized void drawBackground()
  {
    //Draw the black background or image (if it's set)
    int increment;

    engine.getPad().setColor(Color.black);
    engine.getPad().fillRect(0, 0, engine.getWidth(), engine.getHeight());
    engine.getPad().setColor(Color.lightGray);
    engine.getPad().setFont(defaultFont);

    for (int heightCount = 1; heightCount <= 3; heightCount++)
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
      engine.getPad().drawString(title, windowCenter,
          (heightCount * height) + fm.getHeight());

      int noBoxes = ((engine.getWidth() - (leftIndent * 2)) / boxWidth);
      int halfNoBoxes =  noBoxes / 2;

      for (int boxIndex = -halfNoBoxes; boxIndex < halfNoBoxes; boxIndex++)
      {
        engine.getPad().drawRect(engine.getCenterX() + (boxWidth * boxIndex),
            (heightCount * height) + fm.getHeight() + 5, boxWidth, boxHeight);
      }

      int middleOfBox = (heightCount * height) + fm.getHeight() + 5 +
          (boxHeight / 2);

      //Lines out of boxes.
      engine.getPad().drawLine(leftIndent, middleOfBox,
          engine.getCenterX() + (boxWidth * -halfNoBoxes), middleOfBox);
      engine.getPad().drawLine(engine.getCenterX() + (boxWidth * (halfNoBoxes)),
          middleOfBox, rightIndent, middleOfBox);
    }

    //Horizontal lines for zombie and blocked
    engine.getPad().drawLine(leftIndent, 2 * (height) - 5, rightIndent,
        2 * (height) - 5);
    engine.getPad().drawLine(leftIndent, 3 * (height) - 5, rightIndent,
        3 * (height) - 5);

    //Horizontal lines off CPU
    engine.getPad().drawLine(leftIndent, cpuPic.getImageHeight() / 2,
        engine.centerImageWidth(cpuPic.getImageWidth()),
        cpuPic.getImageHeight() / 2);
    engine.getPad().drawLine(
        engine.centerImageWidth(cpuPic.getImageWidth()) +
        cpuPic.getImageHeight() / 2, cpuPic.getImageHeight() / 2, rightIndent,
        cpuPic.getImageHeight() / 2);

    //2 Vertical lines
    engine.getPad().drawLine(leftIndent,
        cpuPic.getImageHeight() / 2, leftIndent,
        (3 * height) + fm.getHeight() + 5 + (boxHeight / 2));
    engine.getPad().drawLine(rightIndent, cpuPic.getImageHeight() / 2,
        rightIndent, (3 * height) + fm.getHeight() + 5 + (boxHeight / 2));
  }

  /**
   * If a process has left or joined a queue then given the queue type (1-3)
   * redisplay all the processes that are in that queue.
   *
   * @param queueType the type of the queue ready, blocked or zombie.
   * @param queue the queue to refresh.
   */
  void refreshQueue(int queueType, Queue queue)
  {
    int xPosition = engine.getCenterX() - ((noBoxes / 2) * boxWidth);
    int yPosition = (queueType * height) + fm.getHeight() + 5;

    queue.goToHead();
    while (!queue.atTail())
    {
      Integer PID = (Integer) queue.peek();
      MTGO tmpMTGO = engine.returnMTGO("P" + PID);

      tmpMTGO.setXPosition(xPosition);
      tmpMTGO.setYPosition(yPosition);
      xPosition = xPosition + boxWidth;
      queue.goToNext();
    }
    syncPaint(myProcessScheduler.getDelay());
  }

  /**
   * Displays a frame with the given PCB of the process given.
   *
   * @param int processId the id of the process to display.
   */
  private void displayPCB(int processId)
  {
    myProcessScheduler.displayPCB(processId);
  }

  /**
   * If you click on an object in the animation area then do something based on
   * the object selected. For example, display the CPU frame if you click on the
   * CPU object.
   */
  class MTGOSelection extends MouseAdapter
  {
    /**
     * Send a message when we get a mouse pressed event.
     *
     * @param e the mouse event generated.
     */
    public void mousePressed(MouseEvent e)
    {
      try
      {
        String whichObject = engine.isInside(e.getX(), e.getY());

        // Have we hit a process?
        if (whichObject.startsWith("P"))
        {
          try
          {
            int pid = (Integer.parseInt(whichObject.substring(1)));
            displayPCB(pid);
          }
          catch (NumberFormatException nfe)
          {
            // This shouldn't happen
            nfe.printStackTrace();
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
    /**
     * Change the refresh rate.
     *
     * @param e the item event generated.
     */
    public void itemStateChanged(ItemEvent e)
    {
      if (e.getStateChange() == e.SELECTED)
      {
        String whichObject = (String) e.getItem();

        if (whichObject.compareTo("Fastest") == 0)
        {
          myProcessScheduler.setDelay(1);
        }
        else if (whichObject.compareTo("Fast") == 0)
        {
          myProcessScheduler.setDelay(3);
        }
        else if (whichObject.compareTo("Normal") == 0)
        {
          myProcessScheduler.setDelay(6);
        }
        else if (whichObject.compareTo("Slow") == 0)
        {
          myProcessScheduler.setDelay(12);
        }
        else if (whichObject.compareTo("Slowest") == 0)
        {
          myProcessScheduler.setDelay(24);
        }
      }
    }
  }

  /**
   * Change the type of process queueing.
   */
  class SchedulerSelection implements ItemListener
  {
    /**
     * When a new queue type has been selected initiate a switch with the
     * process scheduler.
     *
     * @param e item event generated.
     */
    public void itemStateChanged(ItemEvent e)
    {
      if (e.getStateChange() == e.SELECTED)
      {
        String whichObject = (String) e.getItem();

        if (whichObject.compareTo("FIFO") == 0)
        {
          myProcessScheduler.switchToFIFO();
        }
        else if (whichObject.compareTo("LIFO") == 0)
        {
          myProcessScheduler.switchToLIFO();
        }
        else if (whichObject.compareTo("Priority") == 0)
        {
          myProcessScheduler.switchToPriority();
        }
      }
    }
  }

  /**
   * Change the quatum based on the option selected.
   */
  class QuantumSelection implements ItemListener
  {
    /**
     * When a different quantum level has change send a new quantum message.
     *
     * @param e item event generated.
     */
    public void itemStateChanged(ItemEvent e)
    {
      if (e.getStateChange() == e.SELECTED)
      {
        myProcessScheduler.sendQuantum(
            new Integer((String) e.getItem()));
      }
    }
  }
}
