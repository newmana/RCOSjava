package org.rcosjava.software.animator.support;

import java.awt.*;
import javax.swing.*;

/**
 * A simple filled rectangle with a border colour.
 * <P>
 * @author Andrew Newman.
 * @created 1st July 1997
 * @version 1.00 $Date$
 */
public class RCOSRectangle extends JComponent
{
  /**
   * Background colour inside the rectangle.
   */
  private Color boxColour;

  /**
   * The colour of the box that makes up the rectangle border.
   */
  private Color borderColour;

  /**
   * Coordinates of the rectangle.
   */
  private int top, left, bottom, right;

  /**
   * The dimension of the box.
   */
  private Dimension totalSize;

  /**
   * Constructor for the RCOSRectangle object
   *
   * @param newTop top most coordinate of rectangle.
   * @param newLeft left most coordinate of rectangle.
   * @param newBottom bottom most coordinate of rectangle.
   * @param newRight right most coordinate of rectangle.
   * @param newBoxColour background colour of rectangle.
   * @param newBorderColour surrounding box colour.
   */
  public RCOSRectangle(int newTop, int newLeft, int newBottom, int newRight,
      Color newBoxColour, Color newBorderColour)
  {
    super();
    top = newTop;
    left = newLeft;
    bottom = newBottom;
    right = newRight;
    totalSize = new Dimension(Math.abs(left - right), Math.abs(top - bottom));
    boxColour = newBoxColour;
    borderColour = newBorderColour;
    repaint();
  }

  /**
   * Gets the MinimumSize attribute of the RCOSRectangle object
   *
   * @return The MinimumSize value
   */
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  /**
   * Gets the PreferredSize attribute of the RCOSRectangle object
   *
   * @return The PreferredSize value
   */
  public Dimension getPreferredSize()
  {
    return totalSize;
  }

  /**
   * Paints the rectangle with a border.
   *
   * @param g graphics object to paint to.
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    g.setColor(boxColour);
    g.fillRect(top, left, bottom, right);
    g.setColor(borderColour);
    g.drawRect(top, left, bottom - 1, right - 1);
  }
}
