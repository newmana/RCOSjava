//***************************************************************************
// FILE     : RCOSFrame.java
// PACKAGE  : Animator
// PURPOSE  : Root class used for all animator frames.  Also contains
//            default values for the colours and fonts used in all
//            the UI.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 22/01/96  Original (RCOSFrame).
//          : 20/11/98  New Version Created.
//***************************************************************************//

package Software.Animator;

import java.lang.reflect.Method;
import java.awt.*;
import java.awt.event.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.Support.GraphicButton;
import MessageSystem.Messages.MessageAdapter;

public class RCOSFrame extends Frame
{
  public static Color cButtonColour = Color.yellow;
  public static Color cChoiceColour = Color.darkGray;
  public static Color cListColour = Color.darkGray;
  public static Color cTextBoxColour = Color.darkGray;
  public static Color cTerminalColour = Color.green;
  public static Color cDefaultBgColour = Color.black;
  public static Color cDefaultFgColour = Color.white;
  public static Font fDefaultFont = new Font ("TimesRoman", Font.PLAIN, 12);
  public static Font fLabelFont = new Font ("TimesRoman", Font.PLAIN, 14);
  public static Font fButtonFont = new Font("Courier", Font.BOLD, 14);
  public static Font fTerminlFont = new Font("Courier", Font.PLAIN, 10);
  public static Font fTitleFont = new Font ("TimesRoman", Font.PLAIN, 18);
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
    SymWindow aSymWindow = new SymWindow();
    this.addWindowListener(aSymWindow);
    // Set default colours, fonts and layout manager.
    setBackground(cDefaultBgColour);
    setForeground(cDefaultFgColour);
    setFont(fDefaultFont);
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
