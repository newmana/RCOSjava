package org.rcosjava.software.animator.support.mtgos;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 * This is the basic engine which uses the MTGO objects.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 23/04/2001 Fixed remove MTGO bug and updated style. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1997
 * @version 1.00 $Date$
 */
public class GraphicsEngine extends JPanel
{
  /**
   * Offscreen bufer
   */
  private Image buffer;

  /**
   * Painting object
   */
  private Graphics pad;

  /**
   * Background colour.
   */
  private Color backgroundColour = Color.white;

  /**
   * Middle of screen X position.
   */
  private int centerX = 0;

  /**
   * Middle of screen Y position.
   */
  private int centerY = 0;

  /**
   * Objects to display.
   */
  Chain objectChain = null;

  /**
   * If we have setup the graphics objects (done at render time).
   */
  private boolean graphicsSetup = false;

  /**
   * Returns the center X co-ordinate.
   *
   * @return the center X co-ordinate.
   */
  public int getCenterX()
  {
    return centerX;
  }

  /**
   * Returns the center Y co-ordinate.
   *
   * @return the center Y co-ordinate.
   */
  public int getCenterY()
  {
    return centerY;
  }

  /**
   * Determines if an object is inside the give x and y co-ordinates.
   *
   * @param mx x position of object.
   * @param my y position of object
   * @return the name of the object at the given co-ordinates.
   */
  public String isInside(int mx, int my)
  {
    Chain tempObjectChain = sortMTGOsDescending();
    MTGO mob;

    // Find the MTGO with the highest priority in the
    // given x and y co-ordinate.
    try
    {
      while (tempObjectChain != null)
      {
        mob = tempObjectChain.object;
        if (mob.isVisible)
        {
          if (mob.isInside(mx, my))
          {
            return mob.text;
          }
        }
        tempObjectChain = tempObjectChain.nextChain;
      }
    }
    catch (Exception e)
    {
    }
    return " ";
  }

  /**
   * Adds a feature to the Notify attribute of the GraphicsEngine object
   */
  public void addNotify()
  {
    repaint();
    super.addNotify();
  }

  /**
   * Adds a new graphic object to the chain of graphical objects.
   *
   * @param newMTGO the graphical object to add.
   * @param observer an object that implements ImageObserver for obtaining the
   *      graphical objects dimensions.
   */
  public void addMTGO(MTGO newMTGO, ImageObserver observer)
  {
    objectChain = new Chain(newMTGO, objectChain);
    newMTGO.imageHeight = newMTGO.picture.getHeight(observer);
    newMTGO.imageWidth = newMTGO.picture.getWidth(observer);
  }

  /**
   * Remove MTGO based on its name
   *
   * @param mtgoText the text label to find in the chain and remove.
   */
  public void removeMTGO(String mtgoText)
  {
    Chain tempObjectChain = new Chain(objectChain.object, objectChain.nextChain);
    Chain newObjectChain = new Chain(objectChain.object, objectChain.nextChain);
    Chain startObjectChain = newObjectChain;
    MTGO mob = tempObjectChain.object;

    // Check to see whether the first one is the one we want
    if (mob.text.compareTo(mtgoText) == 0)
    {
      startObjectChain = tempObjectChain.nextChain;
      tempObjectChain = tempObjectChain.nextChain;
    }

    while (tempObjectChain != null)
    {
      // If the current one is the one we are looking
      // for, skip the current one.
      if (mob.text.compareTo(mtgoText) == 0)
      {
        if (tempObjectChain.nextChain != null)
        {
          newObjectChain.nextChain = tempObjectChain.nextChain;
        }
        else
        {
          newObjectChain.nextChain = null;
        }
      }
      else
      {
        newObjectChain = tempObjectChain;
      }

      tempObjectChain = tempObjectChain.nextChain;
      if (tempObjectChain != null)
      {
        mob = tempObjectChain.object;
      }
    }
    objectChain = startObjectChain;
  }

  /**
   * Find a MTGO and return it. Does not remove it from the list of objects.
   *
   * @param mtgoText the text label to find in the chain and return.
   * @return Description of the Returned Value
   */
  public MTGO returnMTGO(String mtgoText)
  {
    Chain tempObjectChain = objectChain;
    boolean found = false;

    while (!found)
    {
      if (tempObjectChain.object.text.compareTo(mtgoText) == 0)
      {
        found = true;
        return tempObjectChain.object;
      }
      if (tempObjectChain.nextChain != null)
      {
        tempObjectChain = tempObjectChain.nextChain;
      }
      else
      {
        return null;
      }
    }
    return null;
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    if (!graphicsSetup)
    {
      setGraphicsEngine();
    }

    // Sort MTGOs by priority
    if (objectChain != null)
    {

      Chain tempObjectChain = sortMTGOsAscending();
      MTGO mob;

      // Draw MTGOs ms
      while (tempObjectChain != null)
      {
        mob = tempObjectChain.object;
        if (mob.isVisible)
        {
          pad = mob.paint(pad);
        }
        tempObjectChain = tempObjectChain.nextChain;
      }
    }

    // Draw completed buffer to g
    g.drawImage(buffer, 0, 0, this);
  }

  /**
   * Returns a chain object where they are sorted in ascending orber by
   * priority.
   *
   * @return a chain object where they are sorted in ascending order by
   *      priority.
   */
  public Chain sortMTGOsAscending()
  {
    Chain tempObjectChain = new Chain(objectChain.object, null);
    Chain ordered = tempObjectChain;
    Chain unordered = objectChain.nextChain;
    MTGO mob;

    while (unordered != null)
    {
      mob = unordered.object;
      unordered = unordered.nextChain;
      ordered = tempObjectChain;
      while (ordered != null)
      {
        if (mob.priority < ordered.object.priority)
        {
          ordered.nextChain = new Chain(ordered.object, ordered.nextChain);
          ordered.object = mob;
          ordered = null;
        }
        else if (ordered.nextChain == null)
        {
          ordered.nextChain = new Chain(mob, null);
          ordered = null;
        }
        else
        {
          ordered = ordered.nextChain;
        }
      }
    }
    return tempObjectChain;
  }

  /**
   * Returns a chain object where they are sorted in descending order by
   * priority.
   *
   * @return a chain object where they are sorted in descending order by
   *      priority.
   */
  public Chain sortMTGOsDescending()
  {
    Chain tempObjectChain = new Chain(objectChain.object, null);
    Chain ordered = tempObjectChain;
    Chain unordered = objectChain.nextChain;
    MTGO mob;

    while (unordered != null)
    {
      mob = unordered.object;
      unordered = unordered.nextChain;
      ordered = tempObjectChain;
      while (ordered != null)
      {
        if (mob.priority > ordered.object.priority)
        {
          ordered.nextChain = new Chain(ordered.object, ordered.nextChain);
          ordered.object = mob;
          ordered = null;
        }
        else if (ordered.nextChain == null)
        {
          ordered.nextChain = new Chain(mob, null);
          ordered = null;
        }
        else
        {
          ordered = ordered.nextChain;
        }
      }
    }
    return tempObjectChain;
  }

  /**
   * Returns the X position to place an object determined by its size.
   *
   * @param mgto the object to center.
   * @return the X position to place the object.
   */
  public int centerX(MTGO mtgo)
  {
    float temp = mtgo.imageWidth / 2;
    return ((int) (centerX - temp));
  }

  /**
   * Returns the Y position to place an object determined by its size.
   *
   * @param mtgo the object to center.
   * @return the Y position to place the object.
   */
  public int centerY(MTGO mtgo)
  {
    float temp = mtgo.imageHeight / 2;
    return ((int) (centerY - temp));
  }

  /**
   * Get the graphics object from the repaint manager for offscreen painting.
   */
  private synchronized void setGraphicsEngine()
  {
    RepaintManager rm = RepaintManager.currentManager(this);
    buffer = rm.getOffscreenBuffer(this, getWidth(), getHeight());
    pad = buffer.getGraphics();

    centerX = (int) (getWidth() / 2);
    centerY = (int) (getHeight() / 2);

    graphicsSetup = true;
  }

  /**
   * Sets the new background colour of the entire area.
   *
   * @param newBackgroundColour the new colour to set the background to.
   */
  public void setBackgroundColour(Color newBackgroundColour)
  {
    backgroundColour = newBackgroundColour;
  }

  /**
   * Returns the offscreen graphics object or pad.
   *
   * @return the offscreen graphics object or pad.
   */
  public synchronized Graphics getPad()
  {
    if (pad == null) return null;
    return pad;
  }
}
