package org.rcosjava.software.animator;

import fr.dyade.koala.serialization.GeneratorInputStream;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import java.io.*;

import java.lang.reflect.Method;

/**
 * Root class used for all animator panels. Also contains default values for the
 * colours and fonts used in all the UI.
 * <P>
 * @author Andrew Newman
 * @created 22nd July 2002
 * @version 1.00 $Date$
 */
public abstract class RCOSPanel extends JPanel implements Serializable
{
  /**
   * Default colour to display on foreground of all radio text colours
   * (Black).
   */
  protected transient static final Color choiceFgColour = Color.lightGray;

  /**
   * Default colour to display on background of all radio text background
   * colours (Light Gray).
   */
  protected transient static final Color choiceBgColour = Color.darkGray;

  /**
   * Default colour of text to display foreground on all drop down
   * menus (Black).
   */
  protected transient static final Color listFgColour = Color.lightGray;

  /**
   * Default colour to display on background of all radio text background
   * colours (Dark Gray).
   */
  protected transient static final Color listBgColour = Color.darkGray;

  /**
   * Default colour to use on foreground of all text boxes (Dark Gray).
   */
  protected transient static final Color textBoxFgColour = Color.darkGray;

  /**
   * Default colour to use on background of all text boxes (Black).
   */
  protected transient static final Color textBoxBgColour = Color.black;

  /**
   * Default background of all windows (Black).
   */
  protected transient static final Color defaultBgColour = Color.black;

  /**
   * Default foreground colour of all text (White).
   */
  protected transient static final Color defaultFgColour = Color.lightGray;

  /**
   * Default font size (TimesRoman, Plain, 12).
   */
  protected transient static final Font defaultFont = new Font("TimesRoman",
      Font.PLAIN, 11);

  /**
   * Default label font size (TimesRoman, Plain, 14).
   */
  protected transient static final Font labelFont = new Font("TimesRoman",
      Font.PLAIN, 11);

  /**
   * Default title font size (TimesRoman, Plain, 16).
   */
  protected transient static final Font titleFont = new Font("TimesRoman",
      Font.PLAIN, 16);

  /**
   * Constructor for the RCOSFrame object
   */
  public RCOSPanel()
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
   * Set up the default layout properties of this panel.
   *
   * @param c Description of Parameter
   */
  public abstract void setupLayout(Component c);

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
}
