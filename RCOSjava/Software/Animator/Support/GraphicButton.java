//**************************************************************************
// FILE     : GraphicButton.java
// PACKAGE  : Animator
// PURPOSE  : An image file is passed to it whereby an event Image type
//            is produced giving a arguement (or a name of the event) passed
//            to it by the string value.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 31/07/96  First created.
//          : 24/08/96  Added fonts and font colours.
//            25/07/98  Changed to Java 1.1.
//
//  @see java.applet.Canvas for original class definition.
//  @author Andrew Newman
//  @version 24/7/1996, 1.00
//
//**************************************************************************/

package Software.Animator.Support;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.applet.*;
import java.io.*;
import java.util.*;

public class GraphicButton extends Canvas
{

//buttonPic is the image.
//onButton and pressButton define when the mouse is over the button area and
//when someone presses the mouse on that area respectively.
//The button is the name or arguement of the object - to uniquely identify
//it.  bDepress text indicates if the text on the button moves.

  public Image imgGreyPic, imgButtonUpPic, imgButtonDownPic;
  public String sTheButton;
  private boolean bOverButton, bPressedButton;
  private int iImageWidth, iImageHeight;
  private Font fTheFont;
  private Color cTextColour;
  private boolean bDepressText;
  private boolean bGreyedOut = false;
  private boolean bShowText;
  private ImageFilter filter;
  private ImageProducer producer;
  private GraphicButtonListener gblMouse = null;
  private ActionListener actionListener = null;

/*
  This is the basic constructor.  @param iPic is the image that is displayed
  on the background on the button.  @param sButton is the the name of the
  event to be generated and is also the label on the button.
*/

  public GraphicButton(Image iPic, String sButton)
  {
    super();
    bOverButton = false;
    bPressedButton = false;
    imgButtonUpPic = iPic;
    imgButtonDownPic = iPic;
    sTheButton = sButton;
    cTextColour = Color.yellow;
    fTheFont = new Font("TimesRoman", Font.PLAIN, 12);
    iImageWidth = imgButtonUpPic.getWidth(getParent());
    iImageHeight = imgButtonUpPic.getHeight(getParent());
    bShowText = true;
    setSize(iImageWidth,iImageHeight);
    repaint();
  }

/*
  This allows a greater control over what is generated.  @param iPic is the
  image that is displayed on the background on the button.  @param sButton
  is the the name of the event to be generated and is also the label on
  the button.  @param fFont is the type of Font to be used.  @cColour
  sets the colour of the text to be displayed.
*/

  public GraphicButton(Image iPicUp, Image iPicDown, String sButton,
                       Font fFont, Color cColour, boolean bDepress)
  {
    super();
    bOverButton = false;
    bPressedButton = false;
    imgButtonUpPic = iPicUp;
    imgButtonDownPic = iPicDown;
    sTheButton = sButton;
    fTheFont = fFont;
    cTextColour = cColour;
    iImageWidth = imgButtonUpPic.getWidth(getParent());
    iImageHeight = imgButtonUpPic.getHeight(getParent());
    bDepressText = bDepress;
    bShowText = true;
    setSize(iImageWidth,iImageHeight);
    repaint();
  }

  public GraphicButton(Image iPicUp, Image iPicDown, String sButton,
                       Font fFont, Color cColour, boolean bDepress,
                       boolean bDisplayText)
  {
    super();
    bOverButton = false;
    bPressedButton = false;
    imgButtonUpPic = iPicUp;
    imgButtonDownPic = iPicDown;
    sTheButton = sButton;
    fTheFont = fFont;
    cTextColour = cColour;
    iImageWidth = imgButtonUpPic.getWidth(getParent());
    iImageHeight = imgButtonUpPic.getHeight(getParent());
    bDepressText = bDepress;
    bShowText = bDisplayText;
    setSize(iImageWidth,iImageHeight);
    repaint();
  }

  public GraphicButton(Image iPicUp, Image iPicDown, String sButton,
                       Font fFont, Color cColour, boolean bDepress,
                       boolean bDisplayText, boolean bGreyed)
  {
    super();
    bOverButton = false;
    bPressedButton = false;
    imgButtonUpPic = iPicUp;
    imgButtonDownPic = iPicDown;
    sTheButton = sButton;
    fTheFont = fFont;
    cTextColour = cColour;
    iImageWidth = imgButtonUpPic.getWidth(getParent());
    iImageHeight = imgButtonUpPic.getHeight(getParent());
    bDepressText = bDepress;
    bShowText = bDisplayText;
    bGreyedOut = bGreyed;
    ImageFilter filter = new GreyOutImage();
    producer = new FilteredImageSource(imgButtonUpPic.getSource(), filter);
    imgGreyPic = createImage(producer);
    MediaTracker tracker = new MediaTracker(this);
    tracker.addImage(imgGreyPic,0);
    try
    {
      tracker.waitForID(0);
    }
    catch (InterruptedException e)
    {
      System.out.println("Error");
    }
    setSize(iImageWidth,iImageHeight);
    repaint();
  }

  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  public Dimension getPreferredSize()
  {
    return new Dimension (iImageWidth, iImageHeight);
  }

  public synchronized void addNotify()
  {
    super.addNotify();
    if (gblMouse == null)
	{
	  gblMouse = new GraphicButtonListener();
  	  addMouseListener(gblMouse);
	}	
    repaint();
  }

  public void paint (Graphics g)
  {
    update(g);
  }
  
  public void addActionListener(ActionListener l)
  {
	actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  public void removeActionListener(ActionListener l)
  {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }
  
  public String toString()
  {
    return sTheButton;
  }

  public void update (Graphics g)
  {
    FontMetrics fm = g.getFontMetrics();

    //Using image width and height and text size
    //center text (the program gets the font from
    //the current Graphics context).

    int iX = (iImageWidth/2) - (fm.stringWidth(sTheButton)/2);
    int iY = (iImageHeight/2) + (fm.getAscent()/2);

    if (bGreyedOut)
    {
      g.drawImage(imgGreyPic, 0, 0, this);
    }
    else
    {
      if (bPressedButton)
      {
        g.drawImage(imgButtonDownPic, 0, 0, this);
      }
      else
      {
        g.drawImage(imgButtonUpPic, 0, 0, this);
      }
    }

    //When drawing text to the screen the x,y co-ordinate
    //is of the baseline of the text.

    if (bShowText)
    {
      if (bGreyedOut)
      {
        g.setColor(Color.lightGray);
      }
      else
      {
        g.setColor(cTextColour);
      }
      g.setFont(fTheFont);
      if (bDepressText && bPressedButton)
      {
        g.drawString(sTheButton, iX+1, iY+1);
      }
      else
      {
        g.drawString(sTheButton, iX, iY);      
      }
    }
  }

  public void processEvent(AWTEvent e)
  {
    repaint();
    super.processEvent(e);
  }

  class GraphicButtonListener extends MouseAdapter
  {
    public void mouseEntered(java.awt.event.MouseEvent evt)
    {
      bOverButton = true;
    }

    public void mouseExited(java.awt.event.MouseEvent e)
    {
      bOverButton = false;
    }

    public void mousePressed(java.awt.event.MouseEvent e)
    {
      bPressedButton = true;
      repaint();
    }
  
    public void mouseReleased(java.awt.event.MouseEvent e)
    {
      if (bPressedButton && bOverButton && bDepressText)
      {
        dispatchEvent(new ActionEvent(imgButtonUpPic, Event.ACTION_EVENT, sTheButton));
      }
      bPressedButton = false;
      repaint();
    }
  }
}