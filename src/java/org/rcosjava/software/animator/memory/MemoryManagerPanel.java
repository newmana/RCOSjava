package org.rcosjava.software.animator.memory;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.util.*;

import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.animator.support.RCOSBox;
import org.rcosjava.software.animator.support.RCOSRectangle;
import org.rcosjava.software.ipc.SharedMemory;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * Based on the commands sent and received to the MMU and displays graphically
 * what is happening to the raw memory storage.
 * <P>
 * @author Andrew Newman
 * @created 22nd July 2002
 * @version 1.00 $Date$
 */
public class MemoryManagerPanel extends RCOSPanel
{
  /**
   * Images used to display UI.
   */
  private Image myImages[];

  /**
   * Animator that I receive calls from and makes calls back when a user event
   * occurs.
   */
  private MemoryManagerAnimator myAnimator;

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
   * Constructor for the MemoryManagerFrame object
   *
   * @param images images used in display of ui.
   * @param newMemoryManager animator used to receive and make messages.
   */
  public MemoryManagerPanel(ImageIcon[] images,
      MemoryManagerAnimator newMemoryManager)
  {
    super();
    myImages = new Image[images.length];
    for (int index = 0; index < images.length; index++)
    {
      myImages[index] = images[index].getImage();
    }
    myAnimator = newMemoryManager;
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

    setBackground(defaultBgColour);
    setForeground(defaultFgColour);
    setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel();
    mainPanel.setBackground(defaultBgColour);
    mainPanel.setForeground(defaultFgColour);

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
    constraints.gridheight = 85;
    constraints.gridwidth = 48;

    int memRows, memCols;

    for (memRows = 0; memRows < 2; memRows++)
    {
      for (memCols = 0; memCols < 9; memCols++)
      {
        constraints.gridwidth = 1;
        gridBag.setConstraints(memoryGraphics[(memRows * 10) + memCols],
            constraints);
        mainPanel.add(memoryGraphics[(memRows * 10) + memCols]);
      }
      constraints.gridwidth = GridBagConstraints.REMAINDER;
      gridBag.setConstraints(memoryGraphics[(memRows * 10) + 9], constraints);
      mainPanel.add(memoryGraphics[(memRows * 10) + 9]);
    }

    RCOSRectangle allocBox;
    RCOSRectangle readBox;
    RCOSRectangle writeBox;
    RCOSRectangle unallocBox;

    JPanel boxPanel = new JPanel();
    boxPanel.setBackground(defaultBgColour);
    boxPanel.setForeground(defaultFgColour);
    TitledBorder boxTitle = BorderFactory.createTitledBorder("Key");
    boxTitle.setTitleColor(defaultFgColour);
    boxPanel.setBorder(BorderFactory.createCompoundBorder(
        boxTitle, BorderFactory.createEmptyBorder(3,3,3,3)));

    boxPanel.setLayout(gridBag);

    allocBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.allocatedColour,
        defaultFgColour);
    unallocBox = new RCOSRectangle(0, 0, 20, 20,
        MemoryGraphic.unallocatedColour, defaultFgColour);
    readBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.readingColour,
        defaultFgColour);
    writeBox = new RCOSRectangle(0, 0, 20, 20, MemoryGraphic.writingColour,
        defaultFgColour);

    constraints.fill = GridBagConstraints.BOTH;
    constraints.anchor = GridBagConstraints.NORTH;
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

    gridBag.setConstraints(allocBox, constraints);
    boxPanel.add(allocBox);

    JLabel tmpLabel = new JLabel(" Allocated ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    boxPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(unallocBox, constraints);
    boxPanel.add(unallocBox);

    tmpLabel = new JLabel(" Unallocated ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    boxPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(readBox, constraints);
    boxPanel.add(readBox);

    tmpLabel = new JLabel(" Being Read ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    boxPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    gridBag.setConstraints(writeBox, constraints);
    boxPanel.add(writeBox);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    tmpLabel = new JLabel(" Being Written ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    boxPanel.add(tmpLabel);

    add(mainPanel, BorderLayout.CENTER);
    add(boxPanel, BorderLayout.SOUTH);
  }

  /**
   * Paint the compontents call super, then repaints background if engine
   * is initialized.
   *
   * @param g graphics object.
   */
  public synchronized void paintComponent(Graphics g)
  {
    super.paintComponent(g);

    for (int count = 0; count < MemoryManager.MAX_PAGES; count++)
    {
      memoryGraphics[count].repaint();
    }
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
//  public void paint(Graphics g)
//  {
//    update(g);
//  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
//  public void update(Graphics g)
//  {
//    super.update(g);
//    for (int count = 0; count < MemoryManager.MAX_PAGES; count++)
//    {
//      memoryGraphics[count].repaint();
//    }
//    notify();
//  }

  /**
   * Description of the Method
   *
   * @param memoryReturn Description of Parameter
   */
  void allocatedPages(MemoryReturn memoryReturn)
  {
    for (int count = 0; count < memoryReturn.getSize(); count++)
    {
      memoryGraphics[memoryReturn.getPage(count)].setAllocated(memoryReturn);
    }
  }

  /**
   * Description of the Method
   *
   * @param returnedMemory Description of Parameter
   */
  void deallocatedPages(MemoryReturn memoryReturn)
  {
    for (int count = 0; count < memoryReturn.getSize(); count++)
    {
      memoryGraphics[memoryReturn.getPage(count)].setDeallocated();
    }
  }

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   * @param memoryType Description of Parameter
   */
  void readingMemory(int pid, byte memoryType)
  {
    colourMemory(MemoryGraphic.readingColour, pid, memoryType);
  }

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   * @param memoryType Description of Parameter
   */
  void writingMemory(int pid, byte memoryType)
  {
    colourMemory(MemoryGraphic.writingColour, pid, memoryType);
  }

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   * @param memoryType Description of Parameter
   */
  void finishedReadingMemory(int pid, byte memoryType)
  {
    colourMemory(MemoryGraphic.allocatedColour, pid, memoryType);
  }

  /**
   * Description of the Method
   *
   * @param pid Description of Parameter
   * @param memoryType Description of Parameter
   */
  void finishedWritingMemory(int pid, byte memoryType)
  {
    colourMemory(MemoryGraphic.allocatedColour, pid, memoryType);
  }

  /**
   * Description of the Method
   *
   * @param cColor Description of Parameter
   * @param pid Description of Parameter
   * @param bMemoryType Description of Parameter
   */
  private void colourMemory(Color color, int pid, byte memoryType)
  {
    for (int count = 0; count < MemoryManager.MAX_PAGES; count++)
    {
      if (memoryGraphics[count].isMemory(pid, memoryType))
      {
        memoryGraphics[count].setCurrentColour(color);
      }
    }
  }
}
