package org.rcosjava.software.animator;

import fr.dyade.koala.serialization.GeneratorInputStream;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import java.lang.reflect.Method;

/**
 * Root class used for all animator frames. Also contains default values for the
 * colours and fonts used in all the UI.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 20/11/1998 New Version Created.<BR>
 * </DD>
 * <DD> 25/02/2001 Added serialization support.<BR>
 * </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @created 22nd January 1996
 * @version 1.00 $Date$
 */
public class RCOSFrame extends JFrame implements Serializable
{
  /**
   * Default colour to display text on all buttons (Yellow).
   */
  protected transient static final Color buttonColour = Color.yellow;

  /**
   * Default colour to display on all radio text colours (Dark Gray).
   */
  protected transient static final Color choiceColour = Color.darkGray;

  /**
   * Default colour of text to display on all drop down menus (Dark Gray).
   */
  protected transient static final Color listColour = Color.darkGray;

  /**
   * Default colour to use on all text boxes (Dark Gray).
   */
  protected transient static final Color textBoxColour = Color.darkGray;

  /**
   * Default background of all windows (Black).
   */
  public static final Color DEFAULT_BG_COLOUR = Color.black;

  /**
   * Default foreground colour of all text (White).
   */
  public static final Color DEFAULT_FG_COLOUR = Color.white;

  /**
   * Default font size (TimesRoman, Plain, 12).
   */
  protected transient static final Font defaultFont = new Font("TimesRoman", Font.PLAIN, 11);

  /**
   * Default label font size (TimesRoman, Plain, 14).
   */
  protected transient static final Font labelFont = new Font("TimesRoman", Font.PLAIN, 11);

  /**
   * Default button font size (Courier, Bold, 14).
   */
  protected transient static final Font buttonFont = new Font("Courier", Font.BOLD, 11);

  /**
   * Default title font size (TimesRoman, Plain, 18).
   */
  protected transient static final Font titleFont = new Font("TimesRoman", Font.PLAIN, 16);

  /**
   * Description of the Field
   */
  protected JButton tmpButton;

  /**
   * Description of the Field
   */
  protected JComponent myComponent;

  /**
   * Constructor for the RCOSFrame object
   */
  public RCOSFrame()
  {
    super();
  }

  /**
   * Koala XML serialization requirements.
   *
   * @param in Description of Parameter
   * @exception IOException Description of Exception
   * @exception ClassNotFoundException Description of Exception
   */
  public static void readObject(GeneratorInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(JComponent c)
  {
    myComponent = c;

    SymWindow theSymWindow = new SymWindow();

    this.addWindowListener(theSymWindow);
    // Set default colours, fonts and layout manager.
    setBackground(DEFAULT_BG_COLOUR);
    setForeground(DEFAULT_FG_COLOUR);
    setFont(defaultFont);
    getContentPane().setLayout(new BorderLayout());
  }

  /**
   * Adds a feature to the Notify attribute of the RCOSFrame object
   */
  public void addNotify()
  {
    this.repaint();
    super.addNotify();
  }

  /**
   * Added for KOML support. Does nothing.
   *
   * @param out Description of Parameter
   * @exception IOException Description of Exception
   * @out output stream in which to write these objects to.
   */
  private void writeObject(java.io.ObjectOutputStream out)
    throws IOException
  {
    //Do nothing.
    out.writeObject("Null");
  }

  /**
   * Added for KOML support. Does nothing.
   *
   * @param in Description of Parameter
   * @exception IOException Description of Exception
   * @exception ClassNotFoundException Description of Exception
   */
  private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.readObject();
  }

  /**
   * All or most windows will have a close button to hide the current window.
   *
   * @author administrator
   * @created 28 April 2002
   */
  public class CloseAnimator extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      setVisible(false);
    }
  }

  /**
   * Hide the window from close event.
   *
   * @author administrator
   * @created 28 April 2002
   */
  public class SymWindow extends java.awt.event.WindowAdapter
  {
    /**
     * Description of the Method
     *
     * @param event Description of Parameter
     */
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
