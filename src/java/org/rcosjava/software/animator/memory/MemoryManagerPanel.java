package org.rcosjava.software.animator.memory;

import java.awt.Image;
import java.awt.Component;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.*;
import java.util.*;

import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.software.animator.RCOSPanel;
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
   * Memory images to display.
   */
  private MemoryGraphic[] memoryGraphics =
      new MemoryGraphic[MemoryManager.MAX_PAGES];

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
   * Indicate that the page has been allocated.
   *
   * @param memoryIndex list of memory being read.
   * @param text text to display.
   */
  void allocatedPages(List memoryIndex)
  {
    colourMemory(MemoryGraphic.allocatedColour, memoryIndex);
  }

  /**
   * Indiciate that the page is no longer allocated.
   *
   * @param memoryIndex list of memory being read.
   */
  void unallocatedPages(List memoryIndex)
  {
    colourMemory(MemoryGraphic.unallocatedColour, memoryIndex);
  }

  /**
   * Indicate that we are reading a memory.
   *
   * @param memoryIndex list of memory being read.
   */
  void readingMemory(List memoryIndex)
  {
    colourMemory(MemoryGraphic.readingColour, memoryIndex);
  }

  /**
   * Started writing memory, set to allocated colour.
   *
   * @param memoryIndex list of memory being read.
   */
  void writingMemory(List memoryIndex)
  {
    colourMemory(MemoryGraphic.writingColour, memoryIndex);
  }

  /**
   * Finished reading from memory, change back to allocated colour.
   *
   * @param memoryIndex list of memory being read.
   */
  void finishedReadingMemory(List memoryIndex)
  {
    colourMemory(MemoryGraphic.allocatedColour, memoryIndex);
  }

  /**
   * Finished writing memory, change back to allocated colour.
   *
   * @param memoryIndex list of memory being read.
   */
  void finishedWritingMemory(List memoryIndex)
  {
    colourMemory(MemoryGraphic.allocatedColour, memoryIndex);
  }

  /**
   * Changes the colour of the memory graphic object.
   *
   * @param color colour to change the graphic to.
   * @param memoryIndex the index to the memory array to colour each item.
   */
  private void colourMemory(Color color, List memoryIndex)
  {
    Iterator iter = memoryIndex.iterator();
    while (iter.hasNext())
    {
      int index = ((Integer) iter.next()).intValue();
      memoryGraphics[index].setCurrentColour(color);
    }
    repaint();
  }

  /**
   * Sets the process id of a given list of memory pages.  If the PID is 0 it
   * will not display anything.
   *
   * @param PID the process to use.
   * @param memoryIndex the index to the memory array to colour each item.
   */
  void setPID(int PID, List memoryIndex)
  {
    Iterator iter = memoryIndex.iterator();
    while (iter.hasNext())
    {
      int index = ((Integer) iter.next()).intValue();
      if (PID != 0)
      {
        memoryGraphics[index].setText("P" + PID);
      }
      else
      {
        memoryGraphics[index].setText("  ");
      }
    }
    repaint();
  }
}
