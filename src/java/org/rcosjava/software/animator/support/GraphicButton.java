package org.rcosjava.software.animator.support;

import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * A graphical button class.
 * <P>
 * @author Andrew Newman.
 * @created 22nd July 2002
 * @version 1.00 $Date$
 */
public class GraphicButton extends Canvas
{

//buttonPic is the image.
//onButton and pressButton define when the mouse is over the button area and
//when someone presses the mouse on that area respectively.
//The button is the name or arguement of the object - to uniquely identify
//it.  bDepress text indicates if the text on the button moves.

  /**
   * The images for greyed out, up and down.
   */
  private Image greyPic, buttonUpPic, buttonDownPic;

  /**
   * Text to write on the image
   */
  private String buttonText;

  /**
   * Mouse over.
   */
  private boolean overButton;

  /**
   * Mouse pressed.
   */
  private boolean pressedButton;

  /**
   * Dimensions of image.
   */
  private int imageWidth, imageHeight;

  /**
   * The font to display the name of the button.
   */
  private Font textFont;

  /**
   * Description of the Field
   */
  private Color cTextColour;

  /**
   * Description of the Field
   */
  private boolean bDepressText;

  /**
   * Description of the Field
   */
  private boolean bGreyedOut = false;

  /**
   * Description of the Field
   */
  private boolean bShowText;

  /**
   * Description of the Field
   */
  private ImageFilter filter;
  /**
   * Description of the Field
   */
  private ImageProducer producer;
  /**
   * Description of the Field
   */
  private GraphicButtonListener gblMouse = null;
  /**
   * Description of the Field
   */
  private ActionListener actionListener = null;

  /*
  This is the basic constructor.  @param iPic is the image that is displayed
  on the background on the button.  @param sButton is the the name of the
  event to be generated and is also the label on the button.
*/
  /**
   * Constructor for the GraphicButton object
   *
   * @param iPic Description of Parameter
   * @param sButton Description of Parameter
   */
  public GraphicButton(Image iPic, String sButton)
  {
    super();
    overButton = false;
    pressedButton = false;
    buttonUpPic = iPic;
    buttonDownPic = iPic;
    buttonText = sButton;
    cTextColour = Color.yellow;
    textFont = new Font("TimesRoman", Font.PLAIN, 12);
    imageWidth = buttonUpPic.getWidth(getParent());
    imageHeight = buttonUpPic.getHeight(getParent());
    bShowText = true;
    setSize(imageWidth, imageHeight);
    repaint();
  }

  /*
  This allows a greater control over what is generated.  @param iPic is the
  image that is displayed on the background on the button.  @param sButton
  is the the name of the event to be generated and is also the label on
  the button.  @param fFont is the type of Font to be used.  @cColour
  sets the colour of the text to be displayed.
*/
  /**
   * Constructor for the GraphicButton object
   *
   * @param iPicUp Description of Parameter
   * @param iPicDown Description of Parameter
   * @param sButton Description of Parameter
   * @param fFont Description of Parameter
   * @param cColour Description of Parameter
   * @param bDepress Description of Parameter
   */
  public GraphicButton(Image iPicUp, Image iPicDown, String sButton,
      Font fFont, Color cColour, boolean bDepress)
  {
    super();
    overButton = false;
    pressedButton = false;
    buttonUpPic = iPicUp;
    buttonDownPic = iPicDown;
    buttonText = sButton;
    textFont = fFont;
    cTextColour = cColour;
    imageWidth = buttonUpPic.getWidth(getParent());
    imageHeight = buttonUpPic.getHeight(getParent());
    bDepressText = bDepress;
    bShowText = true;
    setSize(imageWidth, imageHeight);
    repaint();
  }

  /**
   * Constructor for the GraphicButton object
   *
   * @param iPicUp Description of Parameter
   * @param iPicDown Description of Parameter
   * @param sButton Description of Parameter
   * @param fFont Description of Parameter
   * @param cColour Description of Parameter
   * @param bDepress Description of Parameter
   * @param bDisplayText Description of Parameter
   */
  public GraphicButton(Image iPicUp, Image iPicDown, String sButton,
      Font fFont, Color cColour, boolean bDepress, boolean bDisplayText)
  {
    super();
    overButton = false;
    pressedButton = false;
    buttonUpPic = iPicUp;
    buttonDownPic = iPicDown;
    buttonText = sButton;
    textFont = fFont;
    cTextColour = cColour;
    imageWidth = buttonUpPic.getWidth(getParent());
    imageHeight = buttonUpPic.getHeight(getParent());
    bDepressText = bDepress;
    bShowText = bDisplayText;
    setSize(imageWidth, imageHeight);
    repaint();
  }

  /**
   * Constructor for the GraphicButton object
   *
   * @param iPicUp Description of Parameter
   * @param iPicDown Description of Parameter
   * @param sButton Description of Parameter
   * @param fFont Description of Parameter
   * @param cColour Description of Parameter
   * @param bDepress Description of Parameter
   * @param bDisplayText Description of Parameter
   * @param bGreyed Description of Parameter
   */
  public GraphicButton(Image iPicUp, Image iPicDown, String sButton,
      Font fFont, Color cColour, boolean bDepress, boolean bDisplayText,
      boolean bGreyed)
  {
    super();
    overButton = false;
    pressedButton = false;
    buttonUpPic = iPicUp;
    buttonDownPic = iPicDown;
    buttonText = sButton;
    textFont = fFont;
    cTextColour = cColour;
    imageWidth = buttonUpPic.getWidth(getParent());
    imageHeight = buttonUpPic.getHeight(getParent());
    bDepressText = bDepress;
    bShowText = bDisplayText;
    bGreyedOut = bGreyed;

    ImageFilter filter = new GreyOutImage();

    producer = new FilteredImageSource(buttonUpPic.getSource(), filter);
    greyPic = this.createImage(producer);
    setSize(imageWidth, imageHeight);
    repaint();
  }

  /**
   * Gets the MinimumSize attribute of the GraphicButton object
   *
   * @return The MinimumSize value
   */
  public Dimension getMinimumSize()
  {
    return getPreferredSize();
  }

  /**
   * Gets the PreferredSize attribute of the GraphicButton object
   *
   * @return The PreferredSize value
   */
  public Dimension getPreferredSize()
  {
    return new Dimension(imageWidth, imageHeight);
  }

  /**
   * Adds a feature to the Notify attribute of the GraphicButton object
   */
  public void addNotify()
  {
    super.addNotify();
    if (gblMouse == null)
    {
      gblMouse = new GraphicButtonListener();
      addMouseListener(gblMouse);
    }
    repaint();
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
   * Adds a feature to the ActionListener attribute of the GraphicButton object
   *
   * @param l The feature to be added to the ActionListener attribute
   */
  public void addActionListener(ActionListener l)
  {
    actionListener = AWTEventMulticaster.add(actionListener, l);
  }

  /**
   * Description of the Method
   *
   * @param l Description of Parameter
   */
  public void removeActionListener(ActionListener l)
  {
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public String toString()
  {
    return buttonText;
  }

  /**
   * Description of the Method
   */
  public void toggleGrey()
  {
    bGreyedOut = !bGreyedOut;
  }

  /**
   * Description of the Method
   *
   * @param g Description of Parameter
   */
  public void update(Graphics g)
  {
    FontMetrics fm = g.getFontMetrics();

    //Using image width and height and text size
    //center text (the program gets the font from
    //the current Graphics context).

    int iX = (imageWidth / 2) - (fm.stringWidth(buttonText) / 2);
    int iY = (imageHeight / 2) + (fm.getAscent() / 2);

    if ((bGreyedOut) && (greyPic != null))
    {
      g.drawImage(greyPic, 0, 0, this);
    }
    else
    {
      if (pressedButton)
      {
        g.drawImage(buttonDownPic, 0, 0, this);
      }
      else
      {
        g.drawImage(buttonUpPic, 0, 0, this);
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
      g.setFont(textFont);
      if (bDepressText && pressedButton)
      {
        g.drawString(buttonText, iX + 1, iY + 1);
      }
      else
      {
        g.drawString(buttonText, iX, iY);
      }
    }
  }

  /**
   * Set the button down picture.
   *
   * @param newButton the new button picture
   */
  public void setButtonDownPic(Image newButton)
  {
    buttonDownPic = newButton;
  }

  /**
   * Returns the button down picture.
   *
   * @return the button down picture.
   */
  public Image getButtonDownPic()
  {
    return buttonDownPic;
  }

  /**
   * Set the button up picture.
   *
   * @param newButton the new button picture
   */
  public void setButtonUpPic(Image newButton)
  {
    buttonUpPic = newButton;
  }

  /**
   * Returns the button up picture.
   *
   * @return the button up picture.
   */
  public Image getButtonUpPic()
  {
    return buttonUpPic;
  }

  /**
   * Set the text to display on the button.
   *
   * @param newText the text to display.
   */
  public void setButtonText(String newText)
  {
    buttonText = newText;
  }

  /**
   * Returns the text displayed on the button.
   *
   * @return the text displayed on the button.
   */
  public String getButtonText()
  {
    return buttonText;
  }

  /**
   * Description of the Method
   *
   * @param e Description of Parameter
   */
  public void processEvent(AWTEvent e)
  {
    repaint();
    super.processEvent(e);
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class GraphicButtonListener extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param evt Description of Parameter
     */
    public void mouseEntered(java.awt.event.MouseEvent evt)
    {
      overButton = true;
    }

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseExited(java.awt.event.MouseEvent e)
    {
      overButton = false;
    }

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mousePressed(java.awt.event.MouseEvent e)
    {
      pressedButton = true;
      repaint();
    }

    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseReleased(java.awt.event.MouseEvent e)
    {
      if (pressedButton && overButton && bDepressText)
      {
        dispatchEvent(new ActionEvent(buttonUpPic, Event.ACTION_EVENT, buttonText));
      }
      pressedButton = false;
      repaint();
    }
  }
}
