package org.rcosjava.software.animator.memory;

import java.awt.*;
import java.awt.image.*;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * Graphical version of a single segment of memory.
 * <P>
 * @author Andrew Newman
 * @created 22nd July 2002
 * @version 1.00 $Date$
 */
public class MemoryGraphic extends Canvas
{
  /**
   * Default colour when reading memory.
   */
  public final static Color readingColour = Color.blue;

  /**
   * Default colour when writing memory.
   */
  public final static Color writingColour = Color.red;

  /**
   * Default colour when memory allocated.
   */
  public final static Color allocatedColour = Color.gray;

  /**
   * Default colour when memory is deallocated.
   */
  public final static Color deallocatedColour = Color.black;

  /**
   * Unique identifier associated with memory (owner).
   */
  private int id;

  /**
   * Type of memory either stack or code.
   */
  private byte memoryType;

  /**
   * The size of the memory.
   */
  private int memorySize;

  /**
   * Width of the graphic.
   */
  private int defaultWidth;

  /**
   * Height of the graphic.
   */
  private int defaultHeight;

  /**
   * Background colour to write the name of the image.
   */
  private Color currentColour = Color.gray;

  /**
   * Text colour to write out the owner of the memory.
   */
  private Color textColour = Color.yellow;

  /**
   * Image of the memory.
   */
  private Image memoryImage;

  /**
   * Font to describe text.
   */
  private Font textFont = new Font("TimesRoman", Font.PLAIN, 12);

  /**
   * Assigned to a process or not.
   */
  private boolean allocated = false;

  /**
   * Text to display on memory graphic.
   */
  private String text;

  /**
   * Create a memory graphic with empty values.
   *
   * @param newImage image to display.
   */
  public MemoryGraphic(Image newImage)
  {
    super();
    text = "";
    memoryImage = newImage;
    defaultWidth = memoryImage.getWidth(getParent());
    defaultHeight = memoryImage.getHeight(getParent());
    this.setSize(defaultWidth, defaultHeight);
  }

  /**
   * Change the colour that is used to draw background of the process id.
   *
   * @param newColour the new current colour.
   */
  public void setCurrentColour(Color newColour)
  {
    currentColour = newColour;
    this.repaint();
  }

  /**
   * Sets the memory to be allocated.
   *
   * @param newMemoryReturn allocated return value.
   */
  public void setAllocated(MemoryReturn newMemoryReturn)
  {
    setValues(newMemoryReturn);
    allocated = true;
  }

  /**
   * Sets the memory to be deallocated i.e. text empty, allocated false, id = 0,
   * memoryType and memorySize 0.
   */
  public void setDeallocated()
  {
    text = "";
    allocated = false;
    id = 0;
    memoryType = 0;
    memorySize = 0;
    repaint();
  }

  /**
   * Returns if the given parameters are equal by both id and type.
   *
   * @param memoryId memory owner PID.
   * @param memoryType memory type.
   * @return if the given parameters are equal by both id and type.
   */
  public boolean isMemory(int memoryId, byte memoryType)
  {
    return ((id == memoryId) && (memoryType == memoryType));
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
    FontMetrics fm = g.getFontMetrics();

    //Using image width and height and text size
    //center text (the program gets the font from
    //the current Graphics context).

    int iX = (defaultWidth / 2) - (fm.stringWidth(text) / 2);
    int iY = (defaultHeight / 2) + (fm.getAscent() / 2);

    if (allocated)
    {
      g.setColor(currentColour);
    }
    else
    {
      g.setColor(deallocatedColour);
    }

    g.drawImage(memoryImage, 0, 0, this);

    g.fillRect(((int) defaultWidth / 2) - 10, ((int) defaultHeight / 2) - 10, 20, 20);

    //When drawing text to the screen the x,y co-ordinate
    //is of the baseline of the text.

    g.setColor(textColour);
    g.setFont(textFont);
    g.drawString(text, iX, iY);
  }

  /**
   * Sets the text, id, type and size of memory.
   *
   * @param memoryReturn the return value of an allocated memory call.
   */
  private void setValues(MemoryReturn memoryReturn)
  {
    text = "P" + memoryReturn.getPID();
    id = memoryReturn.getPID();
    memoryType = memoryReturn.getMemoryType();
    memorySize = memoryReturn.getSize();
    this.repaint();
  }
}
