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
   * The X Position of the MTGO object.
   */
  private int xPosition = 0;

  /**
   * The Y Position of the MTGO object.
   */
  private int yPosition = 0;

  /**
   * The width of the object.
   */
  private int imageWidth = 0;

  /**
   * The height of the object.
   */
  private int imageHeight = 0;

  /**
   * The picture to display.
   */
  private Image picture;

  /**
   * The priority of the image.  The lower the priority the sooner it is
   * painted.  A priority of 1 will be underneath a object with a priority of 2.
   */
  private int priority = 0;

  /**
   * Whether the object should be painted or not.
   */
  private boolean isVisible = true;

  /**
   * Whether the name should be displayed ontop of the image.
   */
  private boolean displayName = false;

  /**
   * The unique name of the MTGO object.
   */
  private String name;

  /**
   * The observer of the image.
   */
  public ImageObserver observer;

  /**
   * The colour to use to paint the text onto the image.
   */
  public Color textColour;

  /**
   * Create a new MTGO object with the given picture, name of the object and
   * whether to display.  The default colour of the text is yellow.
   *
   * @param newPicture the picture to use for the object.
   * @param newName the unique name of the MTGO
   * @param newDisplayName whether to display the name ontop of the picture.
   */
  public MTGO(Image newPicture, String newName, boolean newDisplayName)
  {
    picture = newPicture;
    name = newName;
    displayName = newDisplayName;
    textColour = Color.yellow;
  }

  /**
   * Create a new MTGO object with the given picture, name of the object,
   * whether to display and the text colour.
   *
   * @param newPicture the picture to use for the object.
   * @param newName the unique name of the MTGO
   * @param newDisplayName whether to display the name ontop of the picture.
   * @param col Description of Parameter
   */
  public MTGO(Image newPicture, String newName, boolean newDisplayName,
      Color newTextColour)
  {
    picture = newPicture;
    name = newName;
    displayName = newDisplayName;
    textColour = newTextColour;
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
   * Returns the X position of the object.
   *
   * @return the X position of the object.
   */
  public int getXPosition()
  {
    return xPosition;
  }

  /**
   * Sets the X position of the object.
   *
   * @param newXPosition the new X position of the object.
   */
  public void setXPosition(int newXPosition)
  {
    xPosition = newXPosition;
  }

  /**
   * Returns the Y position of the object.
   *
   * @return the Y position of the object.
   */
  public int getYPosition()
  {
    return yPosition;
  }

  /**
   * Sets the Y position of the object.
   *
   * @param newYPosition the new Y position of the object.
   */
  public void setYPosition(int newYPosition)
  {
    yPosition = newYPosition;
  }

  /**
   * Paint the MTGO object.
   *
   * @param g Description of Parameter
   * @return Description of the Returned Value
   */
  public Graphics paint(Graphics g)
  {
    g.drawImage(picture, xPosition, yPosition, observer);
    if (name.length() != 0 && displayName)
    {
      Color origColour = g.getColor();
      FontMetrics fm = g.getFontMetrics();
      int textX = (imageWidth / 2) - (fm.stringWidth(name) / 2);
      int textY = (imageHeight / 2) + (fm.getAscent() / 2);

      g.setColor(textColour);
      g.drawString(name, textX + xPosition, textY + yPosition);
      g.setColor(origColour);
    }
    return g;
  }

  /**
   * Returns if the object should be drawn or not.
   *
   * @return if the object should be drawn or not.
   */
  public boolean isVisible()
  {
    return isVisible;
  }

  /**
   * Sets the MTGO object invisible.
   */
  public void setInvisible()
  {
    isVisible = false;
  }

  /**
   * Returns the name of the object.
   *
   * @return the name of the object.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the height of the image.
   *
   * @return the height of the image.
   */
  public int getImageHeight()
  {
    return imageHeight;
  }

  /**
   * Returns the width of the image.
   *
   * @return the width of the image.
   */
  public int getImageWidth()
  {
    return imageWidth;
  }

  /**
   * Returns the priority of the MTGO.
   *
   * @returns the priority of the MTGO.
   */
  public int getPriority()
  {
    return priority;
  }

  /**
   * Sets the new priority of the object.
   *
   * @param newPriority the new priority of the object.
   */
  public void setPriority(int newPriority)
  {
    priority = newPriority;
  }

  /**
   * Sets the dimensions of the MTGO based on an observer.  Sets the observer
   * to the new one passed.
   *
   * @param newObserver the observer to set the dimensions of the object with.
   */
  public void setDimensions(ImageObserver newObserver)
  {
    observer = newObserver;
    imageHeight = picture.getHeight(observer);
    imageWidth = picture.getWidth(observer);
  }
}