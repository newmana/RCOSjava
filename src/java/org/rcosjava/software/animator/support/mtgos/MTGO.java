package org.rcosjava.software.animator.support.mtgos;

import java.awt.*;
import java.awt.image.*;

/**
 * The purpose of this class is to produce a movable text and graphics object
 * (MTGO) that can display text on it of some type.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1997
 * @version 1.00 $Date$
 */
public class MTGO
{
  /**
   * Description of the Field
   */
  public int xPosition = 0;
  /**
   * Description of the Field
   */
  public int yPosition = 0;
  /**
   * Description of the Field
   */
  public int imageWidth = 0;
  /**
   * Description of the Field
   */
  public int imageHeight = 0;
  /**
   * Description of the Field
   */
  public Image picture;
  /**
   * Description of the Field
   */
  public int priority = 0;
  /**
   * Description of the Field
   */
  public boolean isVisible = true;
  /**
   * Description of the Field
   */
  public boolean displayText = false;
  /**
   * Description of the Field
   */
  public String text = new String();
  /**
   * Description of the Field
   */
  public ImageObserver observer;
  /**
   * Description of the Field
   */
  public Color textColour;

  /**
   * Constructor for the MTGO object
   *
   * @param myPic Description of Parameter
   * @param myString Description of Parameter
   * @param myDisplayText Description of Parameter
   */
  public MTGO(Image myPic, String myString, boolean myDisplayText)
  {
    picture = myPic;
    text = myString;
    displayText = myDisplayText;
    textColour = Color.black;
  }

  /**
   * Constructor for the MTGO object
   *
   * @param myPic Description of Parameter
   * @param myString Description of Parameter
   * @param myDisplayText Description of Parameter
   * @param col Description of Parameter
   */
  public MTGO(Image myPic, String myString, boolean myDisplayText,
      Color col)
  {
    picture = myPic;
    text = myString;
    displayText = myDisplayText;
    textColour = col;
  }

  /**
   * Gets the Inside attribute of the MTGO object
   *
   * @param mx Description of Parameter
   * @param my Description of Parameter
   * @return The Inside value
   */
  public boolean isInside(int mx, int my)
  {
    return (((mx >= xPosition) && (mx <= (xPosition + imageWidth))) &&
        ((my >= yPosition) && (my <= (yPosition + imageHeight))));
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   * @return Description of the Returned Value
   */
  public Graphics paint(Graphics g)
  {
    g.drawImage(picture, xPosition, yPosition, observer);
    if (text.length() != 0 && displayText)
    {
      Color origColour = g.getColor();
      FontMetrics fm = g.getFontMetrics();
      int iTextX = (imageWidth / 2) - (fm.stringWidth(text) / 2);
      int iTextY = (imageHeight / 2) + (fm.getAscent() / 2);

      g.setColor(textColour);
      g.drawString(text, iTextX + xPosition, iTextY + yPosition);
      g.setColor(origColour);
    }
    return g;
  }
}
