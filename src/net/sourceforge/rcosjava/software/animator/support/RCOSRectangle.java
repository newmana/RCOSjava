//**************************************************************************
// FILE     : RCOSRectangle.java
// PACKAGE  : Animator
// PURPOSE  : Rectangle component
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/02/96  First created.
//
//**************************************************************************/

package net.sourceforge.rcosjava.software.animator.support;

import java.awt.*;

public class RCOSRectangle extends Canvas
{
  private Color bgColour;
  private Color boxColour;
  private int iTopR, iLeftR, iBottomR, iRightR;

  public RCOSRectangle (int iTop, int iLeft, int iBottom, int iRight,
                        Color bColour, Color bxColour)
  {
    super();
    iTopR = iTop;
    iLeftR = iLeft;
    iBottomR = iBottom;
    iRightR = iRight;
    bgColour = bColour;
    boxColour = bxColour;
    repaint();
  }

  public Dimension getMinimumSize()
  {
	return getPreferredSize();
  }

  public Dimension getPreferredSize()
  {
    return new Dimension (Math.abs(iTopR - iBottomR),
                          Math.abs(iLeftR - iRightR));
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
    g.setColor(bgColour);
    g.fillRect(iTopR,iLeftR,iBottomR,iRightR);
    g.setColor(boxColour);
    g.drawRect(iTopR,iLeftR,iBottomR-1,iRightR-1);
  }
}
