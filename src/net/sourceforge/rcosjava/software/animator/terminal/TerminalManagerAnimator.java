package net.sourceforge.rcosjava.software.animator.terminal;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalToggle;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalFront;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalBack;

/**
 * Terminal Manager Animator receives responses back from the operating system
 * to be interpretted and sent to the Terminal Manager Frame.  This controls
 * the message received and the actions carried out on the Terminal Manager
 * Frame.  It also sends message generated by user input
 * (assuming it's correct).
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 21/02/97  Cleaned up.
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 24th of December 1996
 */
public class TerminalManagerAnimator extends RCOSAnimator
{
  private TerminalManagerFrame myFrame;
  private static final String MESSENGING_ID = "TerminalManagerAnimator";

  /**
   * Create an animator office, register with the animator office, set the size
   * of the frame and the images to use to represent the processes and the
   * buttons.  The terminals may not fit on a even number so the number of
   * terminals and columns and rows is passed instead of just columns an rows.
   * e.g. 2 rows, 4 columns but a maximum of 7 terminals.
   *
   * @param postOffice the post office to register to.
   * @param x width of frame
   * @param y height of frame
   * @param noTerminals maximum number of terminals to display
   * @param noTerminalColumns number of columns of terminals to display
   * @param noTerminalRows number of rows of terminals to display
   * @param images the images to use for process and buttons.
   */
  public TerminalManagerAnimator(AnimatorOffice postOffice, int x, int y,
    Image[] images, int noTerminals, int noTerminalColumns, int noTerminalRows)
  {
    super(MESSENGING_ID, postOffice);
    myFrame = new TerminalManagerFrame(x, y, images, noTerminals,
      noTerminalColumns, noTerminalRows, this);
    myFrame.pack();
    myFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    myFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    myFrame.dispose();
  }

  public void showFrame()
  {
    myFrame.setVisible(true);
  }

  public void hideFrame()
  {
    myFrame.setVisible(false);
  }

  public void processMessage(AnimatorMessageAdapter message)
  {
  }

  public void processMessage(UniversalMessageAdapter message)
  {
    try
    {
      message.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error processing: "+e);
      e.printStackTrace();
    }
  }

  /**
   * Indicate that a terminal is off (show the off terminal image).  Calls
   * the frame's terminal off.
   *
   * @param terminalNo the index to the terminal to turn off.
   */
  public void terminalOff(int terminalNo)
  {
    myFrame.terminalOff(terminalNo);
  }

  /**
   * Indicate that a terminal is to be turned on.  Calls the frame's terminal
   * on.
   */
  public void terminalOn(int terminalNo)
  {
    myFrame.terminalOn(terminalNo);
  }

  /**
   * Indicate that a terminal is being toggled (switching states).  Sends the
   * terminal toggle message to the Terminal Manager.
   */
  public void sendToggleTerminal(int terminalNo)
  {
    TerminalToggle message = new TerminalToggle(this, terminalNo);
    sendMessage(message);
  }

  /**
   * Show the terminal.  Calls the terminalFront method in the frame.
   */
  public void terminalFront(int terminalNo)
  {
    myFrame.terminalFront(terminalNo);
  }

  /**
   * Send the terminal front message.
   */
  public void sendTerminalFront(int terminalNo)
  {
    TerminalFront message = new TerminalFront(this, terminalNo);
    sendMessage(message);
  }

  /**
   * Hide the terminal.  Calls the terminalBack method in the frame.
   */
  public void terminalBack(int terminalNo)
  {
    myFrame.terminalBack(terminalNo);
  }

  /**
   * Sends the terminal back message.
   */
  public void sendTerminalBack(int terminalNo)
  {
    TerminalBack message = new TerminalBack(this, terminalNo);
    sendMessage(message);
  }
}