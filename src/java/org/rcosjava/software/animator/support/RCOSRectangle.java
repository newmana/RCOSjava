package org.rcosjava.software.animator.support;

import java.awt.*;

/**
 * A simple filled rectangle with a border colour.
 * <P>
 * @author Andrew Newman.
 * @created 1st July 1997
 * @version 1.00 $Date$
 */
public class RCOSRectangle extends Canvas
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
    return new Dimension(Math.abs(top - bottom), Math.abs(left - right));
  }

  /**
   * Adds a feature to the Notify attribute of the RCOSRectangle object
   */
  public void addNotify()
  {
    repaint();
    super.addNotify();
  }

  /**
   * Paint the component.
   *
   * @param g graphics object.
   */
  public void paint(Graphics g)
  {
    update(g);
  }

  /**
   * Update the component.
   *
   * @param g graphics object.
   */
  public void update(Graphics g)
  {
    g.setColor(boxColour);
    g.fillRect(top, left, bottom, right);
    g.setColor(borderColour);
    g.drawRect(top, left, bottom - 1, right - 1);
  }
}
