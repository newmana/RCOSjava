package org.rcosjava.software.animator.ipc;

import java.awt.*;
import java.awt.image.*;
import javax.swing.JPanel;
import org.rcosjava.software.memory.MemoryReturn;

/**
 * Represents a single segment of memory and its current status either being
 * read, being written, allocated and deallocated.
 * <P>
 * @author Andrew Newman
 * @created 28th April 2002
 * @version 1.00 $Date$
 */
public class MemoryGraphic extends JPanel
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
   * The unique id of the memory graphic.
   */
  private int id;

  /**
   * The type of the memory as defined by the memory manager.
   */
  private byte memoryType;

  /**
   * The size in bytes of the memory.
   */
  private int memorySize;

  /**
   * The default width of the memory graphic.  Usually determined by the image.
   */
  private int defaultWidth;

  /**
   * The default height of the memory graphic.  Usually determined by the image.
   */
  private int defaultHeight;

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
   * Whether the graphic is current allocated or not (determines the its
   * colour).
   */
  private boolean allocated = false;

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
    setSize(defaultWidth, defaultHeight);
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
   * Sets the memory graphic as being allocated with the values in the memory
   * return object.
   *
   * @param newMemoryReturn used to set the type, process id, size, etc.
   */
  public void setAllocated(MemoryReturn newMemoryReturn)
  {
    setValues(newMemoryReturn);
    allocated = true;
  }

  /**
   * Sets the memory graphic as being deallocated and resets the values to their
   * defaults.
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
   * Returns if the memory is from the correct id and type given.
   *
   * @param memoryId the id of the memory to be equal.
   * @param memoryType the type of the memory to be equal.
   * @return if the memory is from the correct id and type given.
   */
  public boolean isMemory(int memoryId, byte memoryType)
  {
    return ((id == memoryId) && (memoryType == memoryType));
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

    if (allocated)
    {
      g.setColor(currentColour);
    }
    else
    {
      g.setColor(unallocatedColour);
    }

    g.drawImage(memoryImage, 0, 0, this);
    g.fillRect(((int) defaultWidth / 2) - 10, ((int) defaultHeight / 2) - 10,
        20, 20);

    //When drawing text to the screen the x,y co-ordinate
    //is of the baseline of the text.
    g.setColor(textColour);
    g.setFont(textFont);
    g.drawString(text, x, y);
  }

  /**
   * Sets the text, id, memory type and memory size of the memory object based
   * on the given memory return object.
   *
   * @param memoryReturn memory return object used to set the values of this
   *     memory graphic object.
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
