//**************************************************************************/
// FILE     : Chain.java
// PACKAGE  : Animator.MTGOS
// PURPOSE  : The purpose of this class is to produce a movable text and
//            graphics object (MTGO) that can display text on it of some type.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/01/97  First created.
//
//**************************************************************************/

package net.sourceforge.rcosjava.software.animator.support.mtgos;

import java.awt.*;
import java.awt.image.*;

public class MTGO
{
  public int iX = 0;
  public int iY = 0;
  public int iImageWidth = 0;
  public int iImageHeight = 0;
  public Image iPicture;
  public int iPriority = 0;
  public boolean bVisible = true;
  public boolean bDisplayText = false;
  public String sText = new String();
  public ImageObserver ioObserver;
  public Color colTextColour;

  public MTGO (Image myPic, String myString, boolean myDisplayText)
  {
    iPicture = myPic;
    sText = myString;
    bDisplayText = myDisplayText;
    colTextColour = Color.black;
  }

  public MTGO (Image myPic, String myString, boolean myDisplayText,
    Color col)
  {
    iPicture = myPic;
    sText = myString;
    bDisplayText = myDisplayText;
    colTextColour = col;
  }

  public Graphics paint (Graphics g)
  {
    g.drawImage(iPicture,iX,iY,ioObserver);
    if (sText.length() != 0 && bDisplayText)
    {
      Color origColour = g.getColor();
      FontMetrics fm = g.getFontMetrics();
      int iTextX = (iImageWidth/2) - (fm.stringWidth(sText)/2);
      int iTextY = (iImageHeight/2) + (fm.getAscent()/2);
      g.setColor(colTextColour);
      g.drawString(sText, iTextX+iX, iTextY+iY);
      g.setColor(origColour);
    }
    return g;
  }

  public boolean isInside (int mx, int my)
  {
    return (((mx >= iX) && (mx <= (iX+iImageWidth))) &&
            ((my >= iY) && (my <= (iY+iImageHeight))));
  }
}
