package net.sourceforge.rcosjava.software.animator.support.mtgos;

import java.awt.*;
import java.awt.image.*;

/**
 * The purpose of this class is to produce a movable text and graphics object
 * (MTGO) that can display text on it of some type.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st January 1997
 */
public class MTGO
{
  public int xPosition = 0;
  public int yPosition = 0;
  public int imageWidth = 0;
  public int imageHeight = 0;
  public Image picture;
  public int priority = 0;
  public boolean isVisible = true;
  public boolean displayText = false;
  public String text = new String();
  public ImageObserver observer;
  public Color textColour;

  public MTGO (Image myPic, String myString, boolean myDisplayText)
  {
    picture = myPic;
    text = myString;
    displayText = myDisplayText;
    textColour = Color.black;
  }

  public MTGO (Image myPic, String myString, boolean myDisplayText,
    Color col)
  {
    picture = myPic;
    text = myString;
    displayText = myDisplayText;
    textColour = col;
  }

  public Graphics paint (Graphics g)
  {
    g.drawImage(picture,xPosition,yPosition,observer);
    if (text.length() != 0 && displayText)
    {
      Color origColour = g.getColor();
      FontMetrics fm = g.getFontMetrics();
      int iTextX = (imageWidth/2) - (fm.stringWidth(text)/2);
      int iTextY = (imageHeight/2) + (fm.getAscent()/2);
      g.setColor(textColour);
      g.drawString(text, iTextX+xPosition, iTextY+yPosition);
      g.setColor(origColour);
    }
    return g;
  }

  public boolean isInside (int mx, int my)
  {
    return (((mx >= xPosition) && (mx <= (xPosition+imageWidth))) &&
            ((my >= yPosition) && (my <= (yPosition+imageHeight))));
  }
}
