package org.rcosjava.software.animator.support;

import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

/**
 * A graphical button class.
 * <P>
 * @author Andrew Newman.
 * @created 22nd July 2002
 * @version 1.00 $Date$
 */
public class GraphicButton extends JComponent
{
  /**
   * The image for greyed out button
   */
  private Image greyPic;

  /**
   * The image for the up button.
   */
  private Image buttonUpPic;

  /**
   * The image for the down button.
   */
  private Image buttonDownPic;

  /**
   * Text to write on the image/
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
   * Width of image.
   */
  private int imageWidth;

  /**
   * Height of image.
   */
  private int imageHeight;

  /**
   * The dimensions of the button used for setting minimum and preferred size.
   */
  private Dimension totalSize;

  /**
   * The font to display the name of the button.
   */
  private Font textFont;

  /**
   * Colour of the text on the button.
   */
  private Color textColour;

  /**
   * Whether to move the text when the button is pressed.
   */
  private boolean depressText;

  /**
   * Whether the button is greyed out.
   */
  private boolean greyedOut = false;

  /**
   * Whether to display the name or the text on the button.
   */
  private boolean showText;

  /**
   * Filter to apply over the image (produces greyed out image).
   */
  private ImageFilter filter;

  /**
   * Produces a new image (greyed out image).
   */
  private ImageProducer producer;

  /**
   * Graphic button's mouse listener.
   */
  private GraphicButtonListener gblMouse = null;

  /**
   * Graphic button's actioner listener.
   */
  private ActionListener actionListener = null;

  /**
   * More explicit way to setup default values of the graphic button rather
   * than in the declarators.
   */
  private GraphicButton()
  {
    super();
    overButton = false;
    pressedButton = false;
    textColour = Color.yellow;
    textFont = new Font("TimesRoman", Font.PLAIN, 12);
    showText = true;
  }

  /**
   * This is the basic constructor.  Shows the text of the button and sets the
   * up and down images to the same.
   *
   * @param pic is the image that is displayed on the background on the button.
   * @param buttonText is the the name of the event to be generated and is also
   *   the label on the button.
   */
  public GraphicButton(Image newPic, String newButtonText)
  {
    this();
    buttonUpPic = newPic;
    buttonDownPic = newPic;
    buttonText = newButtonText;
    imageWidth = buttonUpPic.getWidth(getParent());
    imageHeight = buttonUpPic.getHeight(getParent());
    totalSize = new Dimension(imageWidth, imageHeight);
    setSize(imageWidth, imageHeight);
  }

  /**
   * Sets the up and down images, name of the button, the font size, displays
   * the name of the button and sets whether to move the text on click.
   *
   * @param newButtonUpPic Picture of button to display when up (normal).
   * @param newButtonDownPic Picture of button to display when down (pressed).
   * @param newButtonText Name of the button and used to display on button if
   *   set.
   * @param newTextFont Font to use on the text of button.
   * @param newTextColour Text colour of the text on the button.
   * @param newDepressText Depress the text on the button when pressed.
   */
  public GraphicButton(Image newButtonUpPic, Image newButtonDownPic,
      String newButtonText, Font newTextFont, Color newTextColour,
      boolean newDepressText)
  {
    this(newButtonUpPic, newButtonText);
    buttonDownPic = newButtonDownPic;
    textFont = newTextFont;
    textColour = newTextColour;
    depressText = newDepressText;
  }

  /**
   * Sets the up and down images, name of the button, the font size, whether to
   * display the name of the button and sets whether to move the text on click.
   *
   * @param newButtonUpPic Picture of button to display when up (normal).
   * @param newButtonDownPic Picture of button to display when down (pressed).
   * @param newButtonText Name of the button and used to display on button if
   *   set.
   * @param newTextFont Font to use on the text of button.
   * @param newTextColour Text colour of the text on the button.
   * @param newDepressText Depress the text on the button when pressed.
   * @param newShowText Display the name of the button.
   */
  public GraphicButton(Image newButtonUpPic, Image newButtonDownPic,
      String newButtonText, Font newTextFont, Color newTextColour,
      boolean newDepressText, boolean newShowText)
  {
    this(newButtonUpPic, newButtonDownPic, newButtonText, newTextFont,
      newTextColour, newDepressText);
    showText = newShowText;
  }

  /**
   * Sets the up and down images, name of the button, the font size, whether to
   * display the name of the button and sets whether to move the text on click.
   * Also, creates a greyed out image for when it's disabled.
   *
   * @param newButtonUpPic Picture of button to display when up (normal).
   * @param newButtonDownPic Picture of button to display when down (pressed).
   * @param newButtonText Name of the button and used to display on button
   *   if set.
   * @param newTextFont Font to use on the text of button.
   * @param newTextColour Text colour of the text on the button.
   * @param newDepressText Depress the text on the button when pressed.
   * @param newShowText Display the name of the button.
   * @param newGreyedOut Whether the button is disabled and greyed out.
   */
  public GraphicButton(Image newButtonUpPic, Image newButtonDownPic,
      String newButtonText, Font newTextFont, Color newTextColour,
      boolean newDepressText, boolean newShowText, boolean newGreyedOut)
  {
    this(newButtonUpPic, newButtonDownPic, newButtonText, newTextFont,
      newTextColour, newDepressText, newShowText);
    greyedOut = newGreyedOut;
    ImageFilter filter = new GreyOutImage();
    producer = new FilteredImageSource(buttonUpPic.getSource(), filter);
    greyPic = this.createImage(producer);
    setSize(imageWidth, imageHeight);
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
    return totalSize;
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
   * Returns the name of the button.
   *
   * @return the name of the button.
   */
  public String toString()
  {
    return buttonText;
  }

  /**
   * Toggles the button from being greyed and disabled or being normal and
   * enabled.
   */
  public void toggleGrey()
  {
    greyedOut = !greyedOut;
  }

  /**
   * Paints the graphical button.
   *
   * @param g graphics object to paint to.
   */
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    FontMetrics fm = g.getFontMetrics();

    //Using image width and height and text size
    //center text (the program gets the font from
    //the current Graphics context).
    int x = (imageWidth / 2) - (fm.stringWidth(buttonText) / 2);
    int y = (imageHeight / 2) + (fm.getAscent() / 2);

    if ((greyedOut) && (greyPic != null))
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

    if (showText)
    {
      if (greyedOut)
      {
        g.setColor(Color.lightGray);
      }
      else
      {
        g.setColor(textColour);
      }
      g.setFont(textFont);
      if (depressText && pressedButton)
      {
        g.drawString(buttonText, x + 1, y + 1);
      }
      else
      {
        g.drawString(buttonText, x, y);
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
   * Listens for mouse events on the graphic button.
   */
  class GraphicButtonListener extends MouseAdapter
  {
    /**
     * Mouse enters button.
     *
     * @param evt mouse event generated.
     */
    public void mouseEntered(java.awt.event.MouseEvent evt)
    {
      overButton = true;
    }

    /**
     * Mouse exits button
     *
     * @param e mouse event generated.
     */
    public void mouseExited(java.awt.event.MouseEvent e)
    {
      overButton = false;
    }

    /**
     * Button pressed.
     *
     * @param e mouse event generated.
     */
    public void mousePressed(java.awt.event.MouseEvent e)
    {
      pressedButton = true;
      repaint();
    }

    /**
     * Button released.
     *
     * @param e mouse event generated.
     */
    public void mouseReleased(java.awt.event.MouseEvent e)
    {
      if (pressedButton && overButton && depressText)
      {
        dispatchEvent(new ActionEvent(buttonUpPic, Event.ACTION_EVENT, buttonText));
      }
      pressedButton = false;
      repaint();
    }
  }
}
