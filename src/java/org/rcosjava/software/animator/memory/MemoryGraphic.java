package org.rcosjava.software.animator.memory;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * Represents a single segment of memory and its current status either being
 * read, being written, allocated and deallocated.
 * <P>
 * @author Andrew Newman
 * @created 28th April 2002
 * @version 1.00 $Date$
 */
public class MemoryGraphic extends JComponent
{
  /**
   * The colour of the graphic when it is being read.
   */
  public final static Color readingColour = Color.blue;

  /**
   * The colour of the graphic when it is being written.
   */
  public final static Color writingColour = Color.red;

  /**
   * The colour of the graphic when it is allocated.
   */
  public final static Color allocatedColour = Color.gray;

  /**
   * The colour of the graphic when it is unallocated.
   */
  public final static Color unallocatedColour = Color.black;

  /**
   * The default width of the memory graphic.  Usually determined by the image.
   */
  private int defaultWidth;

  /**
   * The default height of the memory graphic.  Usually determined by the image.
   */
  private int defaultHeight;

  /**
   * The dimensions of the memory graphic.
   */
  private Dimension totalSize;

  /**
   * The current colour of the memory graphic.
   */
  private Color currentColour = unallocatedColour;

  /**
   * The colour of the text on the memory graphic.
   */
  private Color textColour = Color.yellow;

  /**
   * The graphical representation of the memory.
   */
  private Image memoryImage;

  /**
   * The font of the text to write on top of the memory graphic.
   */
  private Font textFont = new Font("TimesRoman", Font.PLAIN, 12);

  /**
   * The text to display on the memory graphic.
   */
  private String text;

  /**
   * Create a new memory graphic image.
   *
   * @param newImage the image to use on the memory graphic.
   */
  public MemoryGraphic(Image newImage)
  {
    super();
    text = "";
    memoryImage = newImage;
    defaultWidth = memoryImage.getWidth(getParent());
    defaultHeight = memoryImage.getHeight(getParent());
    totalSize = new Dimension(defaultWidth, defaultHeight);
  }

  /**
   * Sets the current colour of the memory object.
   *
   * @param newColour The new colour to set the text.
   */
  public void setCurrentColour(Color newColour)
  {
    currentColour = newColour;
    this.repaint();
  }

  /**
   * Change what is displayed on the graphic.  Pass it a zero length string
   * i.e. "" and nothing will be displayed.
   *
   * @param newText the text to display.
   */
  public void setText(String newText)
  {
    text = newText;
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
   * Paints the memory graphic.
   *
   * @param g graphics object to paint to.
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    FontMetrics fm = g.getFontMetrics();

    //Using image width and height and text size
    //center text (the program gets the font from
    //the current Graphics context).
    int x = (defaultWidth / 2) - (fm.stringWidth(text) / 2);
    int y = (defaultHeight / 2) + (fm.getAscent() / 2);

    g.setColor(currentColour);

    g.drawImage(memoryImage, 0, 0, this);
    g.fillRect(((int) defaultWidth / 2) - 10, ((int) defaultHeight / 2) - 10,
        20, 20);

    //When drawing text to the screen the x,y co-ordinate
    //is of the baseline of the text.
    g.setColor(textColour);
    g.setFont(textFont);
    g.drawString(text, x, y);
  }
}
