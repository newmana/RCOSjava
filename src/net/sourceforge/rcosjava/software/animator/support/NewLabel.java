//**************************************************************************
// FILE     : NewLabel.java
// PACKAGE  : Animator
// PURPOSE  : A less buggy version of Label
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/02/96  First created.
//
//**************************************************************************/

package net.sourceforge.rcosjava.software.animator.support;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;

public class NewLabel extends Canvas
{
  public String sMyString;
  public Font fMyFont;
  private boolean bCentered;

  public NewLabel (String sString, Font fFont)
  {
    super();
    sMyString = sString;
    fMyFont = fFont;
    bCentered = true;
    repaint();
  }

  public NewLabel (String sString, Font fFont, boolean bCenter)
  {
    super();
    sMyString = sString;
    fMyFont = fFont;
    bCentered = bCenter;
    repaint();
  }

  public void setText (String sString)
  {
    sMyString = sString;
    repaint();
  }

  public String getText ()
  {
    return sMyString;
  }

  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  public Dimension getPreferredSize()
  {
    FontMetrics fm = getFontMetrics(fMyFont);
    return new Dimension ((fm.stringWidth(sMyString)+5), (fm.getHeight()+5));
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
    Dimension size = this.getSize();
    FontMetrics fm = getFontMetrics(fMyFont);
    g.setColor(Color.black);
    g.fillRect(0,0,size.width, size.height);
    g.setColor(Color.white);
    g.setFont(fMyFont);
    if (bCentered)
    {
      int x = (size.width/2) - (fm.stringWidth(sMyString)/2);
      int y = (size.height/2) + (fm.getAscent()/2);
      g.drawString(sMyString, x, y);
    }
    else
      g.drawString(sMyString, 0, fm.getAscent());
  }
}
