package org.rcosjava.software.animator.support;

import java.awt.*;
import javax.swing.*;
import org.rcosjava.software.util.LIFOQueue;

/**
 * Display the queues in a graphical fashion.
 * <P>
 * @author Andrew Newman.
 * @created 1st July 1997
 * @version 1.00 $Date$
 */
public class RCOSQueue extends JComponent
{
  /**
   * The list of processes as strings.
   */
  private LIFOQueue queueMembers;

  /**
   * The width in pixels of the box.
   */
  private int boxWidth;

  /**
   * The height in pixels of the box.
   */
  private int boxHeight;

  /**
   * The number of boxes in the queue.
   */
  private int noBoxes;

  /**
   * Default background colour of the queue.
   */
  private Color bgColour = Color.black;

  /**
   * Default foreground colour of the queue.
   */
  private Color fgColour = Color.white;

  /**
   * Font size of text to calculate the size of the boxes.
   */
  private Font myFont;

  /**
   * Number of pixels along the X axis to buffer around the process.
   */
  private int padX = 5;

  /**
   * Number of pixels along the Y axis to buffer around the process.
   */
  private int padY = 5;

  /**
   * The dimension of the queue.
   */
  private Dimension totalSize;

  /**
   * Creates a new process queue.
   *
   * @param noBoxes the number of processes in the queue.
   * @param newFont the font used to create the size of the boxes.
   */
  public RCOSQueue(int newNoBoxes, Font newFont)
  {
    myFont = newFont;
    noBoxes = newNoBoxes;

    FontMetrics fm = getFontMetrics(myFont);

    boxWidth = fm.stringWidth("XXX");
    boxHeight = fm.getHeight();
    queueMembers = new LIFOQueue(noBoxes, 0);

    totalSize = new Dimension((boxWidth * noBoxes) + (padX * 2),
        boxHeight + (padY * 2));
  }

  /**
   * Returns the preferred size.
   *
   * @return the preferred size.
   */
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  /**
   * Returns the total size of the boxes generated at creation time.
   *
   * @return the total size of the boxes generated at creation time.
   */
  public Dimension getPreferredSize()
  {
    return totalSize;
  }

  /**
   * Adds a new process to the end of the queue.
   *
   * @param newMember the name of the process to display.
   */
  public void addToQueue(String newMember)
  {
    queueMembers.insert(newMember);
  }

  /**
   * Removes the first process from the queue.
   */
  public void removeFromQueue()
  {
    queueMembers.retrieve();
  }

  /**
   * Removes all of the processes from the queue.
   */
  public void removeAllFromQueue()
  {
    queueMembers.clear();
  }

  /**
   * Draws the queues with any values in them.
   *
   * @param g graphics object to paint to.
   */
  public void paintComponent(Graphics g)
  {
    Dimension size = getSize();
    FontMetrics fm = getFontMetrics(myFont);

    g.setColor(Color.black);
    g.fillRect(0, 0, size.width, size.height);
    g.setColor(Color.white);
    g.setFont(myFont);

    String tmpString;
    int tmpX;
    int count;

    for (count = 0; count < noBoxes; count++)
    {
      tmpX = count * boxWidth;
      g.draw3DRect(tmpX, 0, boxWidth, boxHeight, true);
      try
      {
        tmpString = (String) queueMembers.peek(count);
        g.drawString(tmpString, tmpX + padX, (boxHeight / 2) +
            (fm.getAscent() / 2) - 1);
      }
      catch (Exception e)
      {
      }
    }
  }
}
