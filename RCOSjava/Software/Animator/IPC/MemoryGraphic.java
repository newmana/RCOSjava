// ****************************************************
// FILE     : MemoryGraphic.java
// PACKAGE  : Animator
// PURPOSE  : Graphic display of a section of memory.
// AUTHOR   : Andrew Newman
// MODIFIED : 
// HISTORY  : 01/09/97      
//
// ****************************************************
 
package Software.Animator.IPC;

import java.awt.*;
import java.awt.image.*;
import Hardware.Memory.Memory;
import Software.Memory.MemoryReturn;

public class MemoryGraphic extends Canvas
{
  public static final Color cReading = Color.blue;
  public static final Color cWriting = Color.red;
  public static final Color cAllocated = Color.gray;
  public static final Color cUnAllocColour = Color.black;
  public static final Color cUnallocated = Color.black;

  private int iID;
  private byte bType;
  private int iSize;
  private int iDefaultWidth;
  private int iDefaultHeight;
  private Color cCurrent = Color.gray;
  private Color cTextColour = Color.yellow;
  private Image iMyImage;
  private Font fTheFont = new Font("TimesRoman", Font.PLAIN, 12);
  private boolean bAllocated = false;
  private String sText;
 
  public MemoryGraphic(Image aImage)
  {
    super();
    sText = "";
    iMyImage = aImage;
    iDefaultWidth = iMyImage.getWidth(getParent());
    iDefaultHeight = iMyImage.getHeight(getParent());
    this.setSize(iDefaultWidth,iDefaultHeight);
    repaint();
  }

  public boolean isMemory(int iMemID, byte bMemType)
  {
    return ((iID == iMemID) && (bMemType == bMemType));
  }
  
  public void setCurrentColour(Color cNewColour)
  {
    cCurrent = cNewColour;
    this.repaint();
  }
  
  public void setAllocated(MemoryReturn aMemret)
  {
    bAllocated = true;
  }
  
  public void setDeallocated()
  {
    sText = "";
    bAllocated = false;
    iID = 0;
    bType = 0;
    iSize = 0;
    this.repaint();
  }
  
  private void setValues(MemoryReturn aMemret)
  {
    sText = "P" + aMemret.getPID();
    iID = aMemret.getPID();
    bType = aMemret.getMemoryType();
    iSize = aMemret.getSize();
    this.repaint();
  }

  public Dimension getMinimumSize()
  {
    return setPreferredSize();
  }

  public Dimension setPreferredSize()
  {
    return new Dimension (iDefaultWidth, iDefaultHeight);
  }

  public synchronized void addNotify()
  {
    repaint();
    super.addNotify();
  }

  public void paint (Graphics g)
  {
    update(g);
  }

  public void update (Graphics g)
  {
    FontMetrics fm = g.getFontMetrics();

    //Using image width and height and text size
    //center text (the program gets the font from
    //the current Graphics context).

    int iX = (iDefaultWidth/2) - (fm.stringWidth(sText)/2);
    int iY = (iDefaultHeight/2) + (fm.getAscent()/2);

    if (bAllocated)
    {
      g.setColor(cCurrent);
    }
    else
    {
      g.setColor(cUnAllocColour);
    }

    g.drawImage(iMyImage, 0, 0, this);
    
    g.fillRect(((int) iDefaultWidth/2)-10,((int) iDefaultHeight/2)-10,20,20);

    //When drawing text to the screen the x,y co-ordinate
    //is of the baseline of the text.

    g.setColor(cTextColour);
    g.setFont(fTheFont);
    g.drawString(sText, iX, iY);      
  }
}
