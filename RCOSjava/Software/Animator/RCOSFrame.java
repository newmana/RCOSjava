package Software.Animator;

import java.lang.reflect.Method;
import java.awt.*;
import java.awt.event.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.Support.GraphicButton;
import MessageSystem.Messages.MessageAdapter;

/**
 * Root class used for all animator frames.  Also contains default values for
 * the colours and fonts used in all the UI.
 * <P>
 * HISTORY         : 20/11/98  New Version Created.
 *
 * @author Andrew Newman
 * @created 22nd January 1996
 * @version 1.00 $Date$
 */
public class RCOSFrame extends Frame
{
  public static Color buttonColour = Color.yellow;
  public static Color choiceColour = Color.darkGray;
  public static Color listColour = Color.darkGray;
  public static Color textBoxColour = Color.darkGray;
  public static Color terminalColour = Color.green;
  public static Color defaultBgColour = Color.black;
  public static Color defaultFgColour = Color.white;
  public static Font defaultFont = new Font ("TimesRoman", Font.PLAIN, 12);
  public static Font labelFont = new Font ("TimesRoman", Font.PLAIN, 14);
  public static Font buttonFont = new Font("Courier", Font.BOLD, 14);
  public static Font terminlFont = new Font("Courier", Font.PLAIN, 10);
  public static Font titleFont = new Font ("TimesRoman", Font.PLAIN, 18);
  private MessageAdapter msg;
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

  public synchronized void addNotify()
  {
    this.repaint();
    super.addNotify();
  }

  // All or most windows will have a close button to hide the
  // current window.
  public class CloseAnimator extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      setVisible(false);
    }
  }

  // Hide the window from close event.
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
