package org.rcosjava.software.animator.support.mtgos;

import java.awt.*;
import java.awt.image.*;

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
public class GraphicsEngine extends Canvas
{
  /**
   * Description of the Field
   */
  public Image background;
  /**
   * Description of the Field
   */
  public Image buffer;
  /**
   * Description of the Field
   */
  public Color backgroundColour = Color.white;
  /**
   * Description of the Field
   */
  public int centerX = 0;
  /**
   * Description of the Field
   */
  public int centerY = 0;
  /**
   * Description of the Field
   */
  public int width = 0;
  /**
   * Description of the Field
   */
  public int height = 0;
  /**
   * Description of the Field
   */
  public Graphics pad;
  /**
   * Description of the Field
   */
  Chain objectChain = null;
  /**
   * Description of the Field
   */
  private Component myComponent;

  /**
   * Creates a graphic engine. It requires a component to determine its size.
   *
   * @param c the component that has previously been created. This is what the
   *      graphicsEngine lives in.
   * @exception java.lang.IllegalArgumentException Description of Exception
   * @throws java.lang.IllegalArgumentException when c is null.
   */
  public GraphicsEngine(Component c)
    throws java.lang.IllegalArgumentException
  {
    super();
    if (c == null)
    {
      throw new java.lang.IllegalArgumentException();
    }
    setGraphicsEngine(c, c.getSize().width, c.getSize().height);
  }

  /**
   * Constructor for the GraphicsEngine object
   *
   * @param c Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   */
  public GraphicsEngine(Component c, int x, int y)
  {
    super();
    if (c == null)
    {
      throw new java.lang.IllegalArgumentException();
    }
    setGraphicsEngine(c, x, y);
  }

  /**
   * Gets the MinimumSize attribute of the GraphicsEngine object
   *
   * @return The MinimumSize value
   */
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  /**
   * Gets the PreferredSize attribute of the GraphicsEngine object
   *
   * @return The PreferredSize value
   */
  public Dimension getPreferredSize()
  {
    return new Dimension(width, height);
  }

  /**
   * Gets the Inside attribute of the GraphicsEngine object
   *
   * @param mx Description of Parameter
   * @param my Description of Parameter
   * @return The Inside value
   */
  public String isInside(int mx, int my)
  {
    Chain tempObjectChain = sortMTGOsDescending();
    MTGO mob;

    //find the MTGO with the highest priority in the
    //given x and y co-ordinate.
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
   * Remove MTGO based on sText.
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
      // if the current one is the one we are looking
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
   */
  public void drawBackground()
  {
    if (background != null)
    {
      pad.drawImage(background, 0, 0, this);
    }
    else
    {
      pad.setColor(backgroundColour);
      pad.fillRect(0, 0, buffer.getWidth(this), buffer.getHeight(this));
    }
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
  public void paint(Graphics g)
  {
    update(g);
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
  public void update(Graphics g)
  {
    /* Sort MTGOs by priority */
    Chain tempObjectChain = sortMTGOsAscending();
    MTGO mob;

    /* Draw MTGOs ms */
    while (tempObjectChain != null)
    {
      mob = tempObjectChain.object;
      if (mob.isVisible)
      {
        pad = mob.paint(pad);
      }
      tempObjectChain = tempObjectChain.nextChain;
    }

    /* Draw completed buffer to g */
    g.drawImage(buffer, 0, 0, this);
  }

  /**
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
   * Description of the Method
   *
   * @param theObject Description of Parameter
   * @return Description of the Returned Value
   */
  public int CenterX(MTGO theObject)
  {
    float fTemp = theObject.imageWidth / 2;

    return ((int) (centerX - fTemp));
  }

  /**
   * Description of the Method
   *
   * @param theObject Description of Parameter
   * @return Description of the Returned Value
   */
  public int CenterY(MTGO theObject)
  {
    float fTemp = theObject.imageHeight / 2;

    return ((int) (centerY - fTemp));
  }

  /**
   * Sets the GraphicsEngine attribute of the GraphicsEngine object
   *
   * @param c The new GraphicsEngine value
   * @param x The new GraphicsEngine value
   * @param y The new GraphicsEngine value
   */
  private void setGraphicsEngine(Component c, int x, int y)
  {
    myComponent = c;
    width = x;
    height = y;
    buffer = myComponent.createImage(width, height);
    pad = buffer.getGraphics();
    setSize(width, height);
    repaint();
    centerX = (int) (width / 2);
    centerY = (int) (height / 2);
  }
}
