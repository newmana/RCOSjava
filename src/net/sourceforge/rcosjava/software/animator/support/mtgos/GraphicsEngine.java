//**************************************************************************/
// FILE     : GraphicsEngine.java
// PACKAGE  : Animator.MTGOS
// PURPOSE  : This is the basic engine which uses the MTGO objects.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/01/97  First created.
//
//**************************************************************************/

package net.sourceforge.rcosjava.software.animator.support.mtgos;

import java.awt.*;
import java.awt.image.*;

public class GraphicsEngine extends Canvas
{
  Chain mobs = null;
  public Image background;
  public Image buffer;
  private Component myComponent;
  public Color backgroundColour = Color.white;
  public int iCenterX = 0;
  public int iCenterY = 0;
  public int iWidth = 0;
  public int iHeight = 0;
  public Graphics pad;

  //See graphicsconfiguration

  public GraphicsEngine(Component c)
  {
    super();
    if (c == null)
      throw new java.lang.IllegalArgumentException();
    setGraphicsEngine(c, c.getSize().width, c.getSize().height);
  }

  public GraphicsEngine(Component c, int x, int y)
  {
    super();
    if (c == null)
      throw new java.lang.IllegalArgumentException();
    setGraphicsEngine(c, x, y);
  }

  private void setGraphicsEngine(Component c, int x, int y)
  {
    myComponent = c;
    iWidth = x;
    iHeight = y;
    buffer = myComponent.createImage(iWidth, iHeight);
    pad = buffer.getGraphics();
    setSize(iWidth, iHeight);
    repaint();
    iCenterX = (int) (iWidth / 2);
    iCenterY = (int) (iHeight / 2);
  }

  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  public Dimension getPreferredSize()
  {
    return new Dimension (iWidth, iHeight);
  }

  public synchronized void addNotify()
  {
    repaint();
    super.addNotify();
  }

  public void addMTGO(MTGO new_mob, ImageObserver imob)
  {
    mobs = new Chain(new_mob, mobs);
    new_mob.iImageHeight = new_mob.iPicture.getHeight(imob);
    new_mob.iImageWidth = new_mob.iPicture.getWidth(imob);
  }

  //Remove MTGO based on sText

  public void removeMTGO(String sText)
  {
    Chain temp_mobs = mobs;
    Chain new_mobs = temp_mobs;
    boolean bFound = false;

    while (!bFound)
    {
      // if the current one is the one we are looking
      // for, skip the current one.
      if (temp_mobs.mob.sText.compareTo(sText)==0)
      {
        new_mobs.rest = temp_mobs.rest;
        bFound = true;
      }
      else
      {
        new_mobs.mob = temp_mobs.mob;
        new_mobs.rest = temp_mobs.rest;
        temp_mobs = temp_mobs.rest;
      }
    }
    mobs = new_mobs;
  }

  public MTGO returnMTGO (String sText)
  {
    Chain temp_mobs = mobs;
    boolean bFound = false;

    while (!bFound)
    {
      if (temp_mobs.mob.sText.compareTo(sText)==0)
      {
        bFound = true;
        return temp_mobs.mob;
      }
      if (temp_mobs.rest != null)
      {
        temp_mobs = temp_mobs.rest;
      }
      else
      {
        return null;
      }
    }
    return null;
  }

  public void drawBackground()
  {
    if (background != null)
    {
      pad.drawImage(background, 0, 0, this);
    }
    else
    {
      pad.setColor(backgroundColour);
      pad.fillRect(0,0,buffer.getWidth(this), buffer.getHeight(this));
    }
  }

  public void paint(Graphics g)
  {
    update(g);
  }

  public void update(Graphics g)
  {
    /* Sort MTGOs by priority */
    Chain temp_mobs = sortMTGOsAscending();
    MTGO mob;

    /* Draw MTGOs ms */

    while (temp_mobs != null)
    {
      mob = temp_mobs.mob;
      if (mob.bVisible)
      {
        pad = mob.paint(pad);
      }
      temp_mobs = temp_mobs.rest;
    }

    /* Draw completed buffer to g */
    g.drawImage(buffer,0,0,this);
  }

  public Chain sortMTGOsAscending()
  {
    Chain temp_mobs = new Chain(mobs.mob, null);
    Chain ordered = temp_mobs;
    Chain unordered = mobs.rest;
    MTGO mob;

    while (unordered != null)
    {
      mob = unordered.mob;
      unordered = unordered.rest;
      ordered = temp_mobs;
      while (ordered != null)
      {
        if (mob.iPriority < ordered.mob.iPriority)
        {
          ordered.rest = new Chain(ordered.mob, ordered.rest);
          ordered.mob = mob;
          ordered = null;
        }
        else if (ordered.rest == null)
        {
          ordered.rest = new Chain(mob,null);
          ordered = null;
        }
        else
        {
          ordered = ordered.rest;
        }
      }
    }
    return temp_mobs;
  }

  public Chain sortMTGOsDescending()
  {
    Chain temp_mobs = new Chain(mobs.mob, null);
    Chain ordered = temp_mobs;
    Chain unordered = mobs.rest;
    MTGO mob;

    while (unordered != null)
    {
      mob = unordered.mob;
      unordered = unordered.rest;
      ordered = temp_mobs;
      while (ordered != null)
      {
        if (mob.iPriority > ordered.mob.iPriority)
        {
          ordered.rest = new Chain(ordered.mob, ordered.rest);
          ordered.mob = mob;
          ordered = null;
        }
        else if (ordered.rest == null)
        {
          ordered.rest = new Chain(mob,null);
          ordered = null;
        }
        else
        {
          ordered = ordered.rest;
        }
      }
    }
    return temp_mobs;
  }

  public int CenterX(MTGO theObject)
  {
    float fTemp = theObject.iImageWidth / 2;
    return ((int) (iCenterX - fTemp));
  }

  public int CenterY (MTGO theObject)
  {
    float fTemp = theObject.iImageHeight / 2;
    return ((int) (iCenterY - fTemp));
  }

  public String isInside (int mx, int my)
  {
    Chain temp_mobs = sortMTGOsDescending();
    MTGO mob;

    //find the MTGO with the highest priority in the
    //given x and y co-ordinate.
    try
    {
      while (temp_mobs != null)
      {
        mob = temp_mobs.mob;
        if (mob.bVisible)
        {
          if (mob.isInside(mx,my))
          {
            return mob.sText;
          }
        }
        temp_mobs = temp_mobs.rest;
      }
    }
    catch (Exception e)
    {
    }
    return " ";
  }
}
