package net.sourceforge.rcosjava.software.animator;

import java.lang.reflect.Method;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;

import fr.dyade.koala.serialization.GeneratorInputStream;

/**
 * Root class used for all animator frames.  Also contains default values for
 * the colours and fonts used in all the UI.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 20/11/1998 New Version Created.<BR>
 * </DD><DD>
 * 25/02/2001 Added serialization support.<BR>
 * </DD></DT>
 * <P>
 * @author Andrew Newman
 * @created 22nd January 1996
 * @version 1.00 $Date$
 */
public class RCOSFrame extends Frame implements Serializable
{
  /**
   * Default colour to display text on all buttons (Yellow).
   */
  public static Color buttonColour = Color.yellow;

  /**
   * Default colour to display on all radio text colours (Dark Gray).
   */
  public static Color choiceColour = Color.darkGray;

  /**
   * Default colour of text to display on all drop down menus (Dark Gray).
   */
  public static Color listColour = Color.darkGray;

  /**
   * Default colour to use on all text boxes (Dark Gray).
   */
  public static Color textBoxColour = Color.darkGray;

  /**
   * Default text colour of all terminals (Green).
   */
  public static Color terminalColour = Color.green;

  /**
   * Default background of all windows (Black).
   */
  public static Color defaultBgColour = Color.black;

  /**
   * Default foreground colour of all text (White).
   */
  public static Color defaultFgColour = Color.white;

  /**
   * Default font size (TimesRoman, Plain, 12).
   */
  public static Font defaultFont = new Font ("TimesRoman", Font.PLAIN, 12);

  /**
   * Default label font size (TimesRoman, Plain, 14).
   */
  public static Font labelFont = new Font ("TimesRoman", Font.PLAIN, 14);

  /**
   * Default button font size (Courier, Bold, 14).
   */
  public static Font buttonFont = new Font("Courier", Font.BOLD, 14);

  /**
   * Default terminal font size (Courier, Plain, 10).
   */
  public static Font terminlFont = new Font("Courier", Font.PLAIN, 10);

  /**
   * Default title font size (TimesRoman, Plain, 18).
   */
  public static Font titleFont = new Font ("TimesRoman", Font.PLAIN, 18);
  protected Button tmpButton;
  protected Component myComponent;

  public RCOSFrame()
  {
    super();
  }

  public void setupLayout(Component c)
  {
    myComponent = c;
    SymWindow theSymWindow = new SymWindow();
    this.addWindowListener(theSymWindow);
    // Set default colours, fonts and layout manager.
    setBackground(defaultBgColour);
    setForeground(defaultFgColour);
    setFont(defaultFont);
    setLayout(new BorderLayout());
  }

  public void addNotify()
  {
    this.repaint();
    super.addNotify();
  }

  /**
   * Added for KOML support.  Does nothing.
   *
   * @out output stream in which to write these objects to.
   */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException
  {
    //Do nothing.
    out.writeObject("Null");
  }

  /**
   * Added for KOML support.  Does nothing.
   */
  private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.readObject();
  }

  /**
   * Koala XML serialization requirements.
   */
  public static void readObject(GeneratorInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
  }

  /**
   * All or most windows will have a close button to hide the current window.
   */
  public class CloseAnimator extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      setVisible(false);
    }
  }

  /**
   * Hide the window from close event.
   */
  public class SymWindow extends java.awt.event.WindowAdapter
  {
    public void windowClosing(java.awt.event.WindowEvent event)
    {
      Object object = event.getSource();
      if (object == RCOSFrame.this)
      {
        setVisible(false);
      }
    }
  }
}
