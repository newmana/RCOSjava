package org.rcosjava.software.animator.support;
import java.applet.Applet;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;
import java.util.*;

/**
 * The active areas on the image are controlled by ImageArea classes that can be
 * dynamically loaded over the net.
 *
 * @author Jim Graham
 * @created 28 April 2002
 * @version 1.7, 04/24/96
 */
public class Overview extends Frame implements Runnable
{

  /**
   * The primary highlight mode to be used.
   */
  final static int BRIGHTER = 0;
  /**
   * Description of the Field
   */
  final static int DARKER = 1;
  /**
   * Description of the Field
   */
  private final static long UPDATERATE = 100;
  /**
   * The unhighlighted image being mapped.
   */
  Image baseImage;

  /**
   * The list of image area handling objects;
   */
  ImageMapArea areas[];

  /**
   * Description of the Field
   */
  int hlmode = BRIGHTER;

  /**
   * The percentage of highlight to apply for the primary highlight mode.
   */
  int hlpercent = 50;

  /**
   * The MediaTracker for loading and constructing the various images.
   */
  MediaTracker tracker;
  /**
   * Description of the Field
   */
  Image[] myImages;
  /**
   * Description of the Field
   */
  String sHighlight;
  /**
   * Description of the Field
   */
  String[] sAreas;
  /**
   * Description of the Field
   */
  Applet theApplet;

  /**
   * Description of the Field
   */
  Thread aniThread = null;
  /**
   * Description of the Field
   */
  String introTune = null;

  /**
   * Make sure that no ImageAreas are highlighted. public boolean
   * mouseExit(java.awt.Event evt, int x, int y) { for (int i = 0; i <
   * areas.length; i++) { areas[i].checkExit(); } return true; }
   */

  /**
   * Description of the Field
   */
  private boolean fullrepaint = false;


  /**
   * Constructor for the Overview object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param images Description of Parameter
   * @param bkImage Description of Parameter
   * @param highlight Description of Parameter
   * @param areas Description of Parameter
   * @param aApplet Description of Parameter
   */
  public Overview(int x, int y, Image images[], Image bkImage,
      String highlight, String areas[],
      Applet aApplet)
  {
    super();
    setTitle("Overview");
    myImages = images;
    theApplet = aApplet;
    sHighlight = highlight;
    sAreas = areas;
    baseImage = bkImage;
    pack();
    setSize(x, y);
  }

  /**
   * Gets the DocumentBase attribute of the Overview object
   *
   * @return The DocumentBase value
   */
  public URL getDocumentBase()
  {
    return (theApplet.getDocumentBase());
  }

  /**
   * Description of the Method
   *
   * @param s Description of Parameter
   */
  public void showStatus(String s)
  {
    theApplet.showStatus(s);
  }

  /**
   * Description of the Method
   *
   * @param u Description of Parameter
   */
  public void showDocument(URL u)
  {
    theApplet.getAppletContext().showDocument(u);
  }

  /**
   * Initialize the applet. Get attributes. Initialize the ImageAreas. Each
   * ImageArea is a subclass of the class ImageArea, and is specified with an
   * attribute of the form: areaN=ImageAreaClassName,arguments.. The
   * ImageAreaClassName is parsed off and a new instance of that class is
   * created. The initializer for that class is passed a reference to the applet
   * and the remainder of the attribute string, from which the class should
   * retrieve any information it needs about the area it controls and the
   * actions it needs to take within that area.
   */
  public void init()
  {
    setBackground(Color.black);
    setForeground(Color.white);

    String s;

    tracker = new MediaTracker(this);
    parseHighlight(sHighlight);

    Vector areaVec = new Vector();
    int num = 0;

    while (sAreas[num] != null)
    {
      ImageMapArea newArea;

      s = sAreas[num];
      try
      {
        int classend = s.indexOf(",");
        String name = s.substring(0, classend);

        newArea = (ImageMapArea) Class.forName(name).newInstance();
        s = s.substring(classend + 1);
        newArea.init(this, s);
        areaVec.addElement(newArea);
      }
      catch (Exception e)
      {
        System.err.println("error processing: " + s);
        e.printStackTrace();
        break;
      }
      num++;
    }
    areas = new ImageMapArea[areaVec.size()];
    areaVec.copyInto(areas);
    checkSize();
  }

  /**
   * Description of the Method
   */
  public void start()
  {
    if (aniThread == null)
    {
      aniThread = new Thread(this);
      aniThread.setName("ImageMap Animator");
      aniThread.start();
    }
  }

  /**
   * Main processing method for the Overview object
   */
  public void run()
  {
    Thread me = Thread.currentThread();

    tracker.checkAll(true);
    for (int i = areas.length; --i >= 0; )
    {
      areas[i].getMedia();
    }
    me.setPriority(Thread.MIN_PRIORITY);
    while (aniThread == me)
    {
      boolean animating = false;

      for (int i = areas.length; --i >= 0; )
      {
        animating = areas[i].animate() || animating;
      }
      try
      {
        synchronized (this)
        {
          wait(animating ? 100 : 0);
        }
      }
      catch (InterruptedException e)
      {
        break;
      }
    }
  }

  /**
   * Description of the Method
   */
  public synchronized void startAnimation()
  {
    notify();
  }

  /**
   * Description of the Method
   */
  public synchronized void stop()
  {
    aniThread = null;
    notify();
    for (int i = 0; i < areas.length; i++)
    {
      areas[i].exit();
    }
  }

  /**
   * Handle updates from images being loaded.
   *
   * @param img Description of Parameter
   * @param infoflags Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param width Description of Parameter
   * @param height Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean imageUpdate(Image img, int infoflags,
      int x, int y, int width, int height)
  {
    if ((infoflags & (WIDTH | HEIGHT)) != 0)
    {
      checkSize();
    }
    if ((infoflags & (SOMEBITS | FRAMEBITS | ALLBITS)) != 0)
    {
      synchronized (this)
      {
        fullrepaint = true;
      }
      repaint(((infoflags & (FRAMEBITS | ALLBITS)) != 0)
           ? 0 : UPDATERATE,
          x, y, width, height);
    }
    return (infoflags & (ALLBITS | ERROR)) == 0;
  }

  /**
   * Paint the image and all active highlights.
   *
   * @param g Description of Parameter
   */
  public void paint(Graphics g)
  {
    synchronized (this)
    {
      fullrepaint = false;
    }
    if (baseImage == null)
    {
      return;
    }
    g.drawImage(baseImage, 0, 0, this);
    if (areas != null)
    {
      for (int i = areas.length; --i >= 0; )
      {
        areas[i].highlight(g);
      }
    }
  }

  /**
   * Update the active highlights on the image.
   *
   * @param g Description of Parameter
   */
  public void update(Graphics g)
  {
    boolean full;

    synchronized (this)
    {
      full = fullrepaint;
    }
    if (full)
    {
      paint(g);
      return;
    }
    if (baseImage == null)
    {
      return;
    }
    g.drawImage(baseImage, 0, 0, this);
    if (areas == null)
    {
      return;
    }
    // First unhighlight all of the deactivated areas
    for (int i = areas.length; --i >= 0; )
    {
      areas[i].highlight(g);
    }
  }

  /**
   * Get a rectangular region of the baseImage highlighted according to the
   * primary highlight specification.
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param w Description of Parameter
   * @param h Description of Parameter
   * @return The Highlight value
   */
  Image getHighlight(int x, int y, int w, int h)
  {
    return getHighlight(x, y, w, h, hlmode, hlpercent);
  }

  /**
   * Get a rectangular region of the baseImage with a specific highlight.
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param w Description of Parameter
   * @param h Description of Parameter
   * @param mode Description of Parameter
   * @param percent Description of Parameter
   * @return The Highlight value
   */
  Image getHighlight(int x, int y, int w, int h, int mode, int percent)
  {
    return getHighlight(x, y, w, h, new HighlightFilter(mode == BRIGHTER,
        percent));
  }

  /**
   * Get a rectangular region of the baseImage modified by an image filter.
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param w Description of Parameter
   * @param h Description of Parameter
   * @param filter Description of Parameter
   * @return The Highlight value
   */
  Image getHighlight(int x, int y, int w, int h, ImageFilter filter)
  {
    ImageFilter cropfilter = new CropImageFilter(x, y, w, h);
    ImageProducer prod = new FilteredImageSource(baseImage.getSource(),
        cropfilter);

    return makeImage(prod, filter, 0);
  }

  /**
   * Make a filtered image based on another image.
   *
   * @param orig Description of Parameter
   * @param filter Description of Parameter
   * @return Description of the Returned Value
   */
  Image makeImage(Image orig, ImageFilter filter)
  {
    return makeImage(orig.getSource(), filter);
  }

  /**
   * Make a filtered image based on another ImageProducer.
   *
   * @param prod Description of Parameter
   * @param filter Description of Parameter
   * @return Description of the Returned Value
   */
  Image makeImage(ImageProducer prod, ImageFilter filter)
  {
    return makeImage(prod, filter,
        (prod == baseImage.getSource()) ? 1 : 0);
  }

  /**
   * Make a filtered image based on another ImageProducer. Add it to the media
   * tracker using the indicated ID.
   *
   * @param prod Description of Parameter
   * @param filter Description of Parameter
   * @param ID Description of Parameter
   * @return Description of the Returned Value
   */
  Image makeImage(ImageProducer prod, ImageFilter filter, int ID)
  {
    Image filtered = createImage(new FilteredImageSource(prod, filter));

    tracker.addImage(filtered, ID);
    return filtered;
  }

  /**
   * Add an image to the list of images to be tracked.
   *
   * @param img The feature to be added to the Image attribute
   */
  void addImage(Image img)
  {
    tracker.addImage(img, 1);
  }

  /**
   * Parse a string representing the desired highlight to be applied.
   *
   * @param s Description of Parameter
   */
  void parseHighlight(String s)
  {
    if (s == null)
    {
      return;
    }
    if (s.startsWith("brighter"))
    {
      hlmode = BRIGHTER;
      if (s.length() > "brighter".length())
      {
        hlpercent = Integer.parseInt(s.substring("brighter".length()));
      }
    }
    else if (s.startsWith("darker"))
    {
      hlmode = DARKER;
      if (s.length() > "darker".length())
      {
        hlpercent = Integer.parseInt(s.substring("darker".length()));
      }
    }
  }

  /**
   * Check the size of this applet while the image is being loaded.
   */
  void checkSize()
  {
    int w = baseImage.getWidth(this);
    int h = baseImage.getHeight(this);

    if (w > 0 && h > 0)
    {
      setSize(w, h);
      synchronized (this)
      {
        fullrepaint = true;
      }
      repaint(0, 0, w, h);
    }
  }


}
