// *************************************************************************
// FILE     : RCOSQueue.java
// PACKAGE  : Animator
// PURPOSE  : Display the queues in a graphical fashion
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 1/7/97  Created.
//
// *************************************************************************

package net.sourceforge.rcosjava.software.animator.support;

import java.awt.*;
import net.sourceforge.rcosjava.software.util.LIFOQueue;

public class RCOSQueue extends Canvas
{
  private LIFOQueue queueMembers;
  private int iBoxWidth, iBoxHeight, iNoBoxes;
  private Color bgColour = Color.black;
  private Color fgColour = Color.white;
  private Font fMyFont;
  private int iPadX = 5;
  private int iPadY = 5;

  public RCOSQueue(int iNoBoxes, Font fFont)
  {
    fMyFont = fFont;
    this.iNoBoxes = iNoBoxes;
    FontMetrics fm = getFontMetrics(fMyFont);
    iBoxWidth = fm.stringWidth("XXX");
    iBoxHeight = fm.getHeight();
    queueMembers = new LIFOQueue(iNoBoxes,0);
  }

  public void addToQueue (String newMember)
  {
    queueMembers.insert(newMember);
  }

  public void removeFromQueue ()
  {
    queueMembers.retrieve();
  }

  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  public Dimension getPreferredSize()
  {
    return new Dimension ((iBoxWidth*iNoBoxes)+(iPadX*2), iBoxHeight+(iPadY*2));
  }

  public void addNotify()
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
    Dimension size = getSize();
    FontMetrics fm = getFontMetrics(fMyFont);
    g.setColor(Color.black);
    g.fillRect(0,0,size.width, size.height);
    g.setColor(Color.white);
    g.setFont(fMyFont);
    String sTmpString;
    int iTmpX, count;
    for (count = 0; count < iNoBoxes; count++)
    {
      iTmpX = count * iBoxWidth;
      g.draw3DRect(iTmpX,0,iBoxWidth,iBoxHeight,true);
      try
      {
        sTmpString = (String) queueMembers.peek(count);
        g.drawString(sTmpString, iTmpX+iPadX, (iBoxHeight/2) + (fm.getAscent()/2) - 1);
      }
      catch (Exception e)
      {
      }
    }
  }
}