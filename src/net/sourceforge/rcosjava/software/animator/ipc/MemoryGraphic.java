// ****************************************************
// FILE     : MemoryGraphic.java
// PACKAGE  : Animator
// PURPOSE  : Graphic display of a section of memory.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/09/97
//
// ****************************************************

package net.sourceforge.rcosjava.software.animator.ipc;

import java.awt.*;
import java.awt.image.*;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;

public class MemoryGraphic extends Canvas
{
  public static final Color readingColour = Color.blue;
  public static final Color writingColour = Color.red;
  public static final Color allocatedColour = Color.gray;
  public static final Color unallocatedColour = Color.black;

  private int id;
  private byte memoryType;
  private int memorySize;
  private int defaultWidth;
  private int defaultHeight;
  private Color currentColour = Color.gray;
  private Color textColour = Color.yellow;
  private Image memoryImage;
  private Font textFont = new Font("TimesRoman", Font.PLAIN, 12);
  private boolean allocated = false;
  private String text;

  public MemoryGraphic(Image newImage)
  {
    super();
    text = "";
    memoryImage = newImage;
    defaultWidth = memoryImage.getWidth(getParent());
    defaultHeight = memoryImage.getHeight(getParent());
    this.setSize(defaultWidth,defaultHeight);
    //repaint();
  }

  public boolean isMemory(int memoryId, byte memoryType)
  {
    return ((id == memoryId) && (memoryType == memoryType));
  }

  public void setCurrentColour(Color newColour)
  {
    currentColour = newColour;
    this.repaint();
  }

  public void setAllocated(MemoryReturn newMemoryReturn)
  {
    setValues(newMemoryReturn);
    allocated = true;
  }

  public void setDeallocated()
  {
    text = "";
    allocated = false;
    id = 0;
    memoryType = 0;
    memorySize = 0;
  }

  private void setValues(MemoryReturn aMemret)
  {
    text = "P" + aMemret.getPID();
    id = aMemret.getPID();
    memoryType = aMemret.getMemoryType();
    memorySize = aMemret.getSize();
    this.repaint();
  }

  public void paint(Graphics g)
  {
    FontMetrics fm = g.getFontMetrics();

    //Using image width and height and text size
    //center text (the program gets the font from
    //the current Graphics context).

    int iX = (defaultWidth/2) - (fm.stringWidth(text)/2);
    int iY = (defaultHeight/2) + (fm.getAscent()/2);

    if (allocated)
    {
      g.setColor(currentColour);
    }
    else
    {
      g.setColor(unallocatedColour);
    }

    g.drawImage(memoryImage, 0, 0, this);

    g.fillRect(((int) defaultWidth/2)-10,((int) defaultHeight/2)-10,20,20);

    //When drawing text to the screen the x,y co-ordinate
    //is of the baseline of the text.

    g.setColor(textColour);
    g.setFont(textFont);
    g.drawString(text, iX, iY);
  }
}
