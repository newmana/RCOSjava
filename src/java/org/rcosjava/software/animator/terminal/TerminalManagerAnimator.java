package org.rcosjava.software.animator.terminal;

import java.applet.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.io.*;
import java.net.*;
import java.util.*;
import org.rcosjava.messaging.messages.universal.TerminalBack;
import org.rcosjava.messaging.messages.universal.TerminalFront;
import org.rcosjava.messaging.messages.universal.TerminalToggle;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.animator.RCOSPanel;

/**
 * Terminal Manager Animator receives responses back from the operating system
 * to be interpretted and sent to the Terminal Manager Frame. This controls the
 * message received and the actions carried out on the Terminal Manager Frame.
 * It also sends message generated by user input (assuming it's correct).
 * <P>
 * <DT> <B>History:</B>
 * <DD> 21/02/97 Cleaned up. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 24th of December 1996
 * @version 1.00 $Date$
 */
public class TerminalManagerAnimator extends RCOSAnimator
{
  /**
   * The id to register with the animator office.
   */
  private final static String MESSENGING_ID = "TerminalManagerAnimator";

  /**
   * Images to display.
   */
  private ImageIcon[] images;

  /**
   * Total number of terminal columns.
   */
  private int noTerminalColumns;

  /**
   * Total number of terminal rows.
   */
  private int noTerminalRows;

  /**
   * Total number of terminals.
   */
  private int noTerminals;

  /**
   * Terminals and if they are on or not.
   */
  private boolean[] terminalsOn;

  /**
   * Terminals and if they are in front or not.
   */
  private boolean[] terminalsFront;

  /**
   * Panel to display the results.
   */
  private transient TerminalManagerPanel panel;

  /**
   * Create an animator office, register with the animator office, set the size
   * of the frame and the images to use to represent the processes and the
   * buttons. The terminals may not fit on a even number so the number of
   * terminals and columns and rows is passed instead of just columns an rows.
   * e.g. 2 rows, 4 columns but a maximum of 7 terminals.
   *
   * @param postOffice the post office to register to.
   * @param noTerminals maximum number of terminals to display
   * @param noTerminalColumns number of columns of terminals to display
   * @param noTerminalRows number of rows of terminals to display
   * @param images the images to use for process and buttons.
   */
  public TerminalManagerAnimator(AnimatorOffice postOffice, ImageIcon[] images,
      int newNoTerminals, int newNoTerminalColumns, int newNoTerminalRows)
  {
    super(MESSENGING_ID, postOffice);

    noTerminals = newNoTerminals + 1;
    noTerminalColumns = newNoTerminalColumns;
    noTerminalRows = newNoTerminalRows;

    terminalsFront = new boolean[noTerminals];
    terminalsOn = new boolean[noTerminals];

    panel = new TerminalManagerPanel(images, noTerminals, noTerminalColumns,
        noTerminalRows, this);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    panel.setupLayout(c);
  }

  /**
   * Returns the panel of this component.
   *
   * @return the panel of this component.
   */
  public RCOSPanel getPanel()
  {
    return panel;
  }

  /**
   * Terminal is front.
   *
   * @param terminalNo the number of the terminal.
   */
  void setTerminalFront(int terminalNo)
  {
    terminalsFront[terminalNo] = true;
  }

  /**
   * Terminal is back.
   *
   * @param terminalNo the number of the terminal.
   */
  void setTerminalBack(int terminalNo)
  {
    terminalsFront[terminalNo] = false;
  }

  /**
   * Terminal is on.
   *
   * @param terminalNo the number of the terminal.
   */
  void setTerminalOn(int terminalNo)
  {
    terminalsOn[terminalNo] = true;
  }

  /**
   * Terminal is off.
   *
   * @param terminalNo the number of the terminal.
   */
  void setTerminalOff(int terminalNo)
  {
    terminalsOn[terminalNo] = false;
  }

  /**
   * Indicate that a terminal is off (show the off terminal image). Calls the
   * frame's terminal off.
   *
   * @param terminalNo the index to the terminal to turn off.
   */
  public void terminalOff(int terminalNo)
  {
    panel.terminalOff(terminalNo);
  }

  /**
   * Indicate that a terminal is to be turned on. Calls the frame's terminal on.
   *
   * @param terminalNo Description of Parameter
   */
  public void terminalOn(int terminalNo)
  {
    panel.terminalOn(terminalNo);
  }

  /**
   * Indicate that a terminal is being toggled (switching states). Sends the
   * terminal toggle message to the Terminal Manager.
   *
   * @param terminalNo Description of Parameter
   */
  public void sendToggleTerminal(int terminalNo)
  {
    TerminalToggle message = new TerminalToggle(this, terminalNo);
    sendMessage(message);
  }

  /**
   * Show the terminal. Calls the terminalFront method in the frame.
   *
   * @param terminalNo Description of Parameter
   */
  public void terminalFront(int terminalNo)
  {
    panel.terminalFront(terminalNo);
  }

  /**
   * Send the terminal front message.
   *
   * @param terminalNo Description of Parameter
   */
  public void sendTerminalFront(int terminalNo)
  {
    TerminalFront message = new TerminalFront(this, terminalNo);
    sendMessage(message);
  }

  /**
   * Hide the terminal. Calls the terminalBack method in the frame.
   *
   * @param terminalNo Description of Parameter
   */
  public void terminalBack(int terminalNo)
  {
    panel.terminalBack(terminalNo);
  }

  /**
   * Sends the terminal back message.
   *
   * @param terminalNo Description of Parameter
   */
  public void sendTerminalBack(int terminalNo)
  {
    TerminalBack message = new TerminalBack(this, terminalNo);
    sendMessage(message);
  }

  /**
   * Handle the creation of non-serializable components.
   *
   * @param is stream that is being read.
   */
  private void readObject(ObjectInputStream is) throws IOException,
      ClassNotFoundException
  {
    // Deserialize the document
    is.defaultReadObject();

    // Create new connection
    panel = new TerminalManagerPanel(images, noTerminals, noTerminalColumns,
        noTerminalRows, this);

    // Restore the state of the terminals
    for (int terminals = 0; terminals < noTerminals; terminals++)
    {
      // Restore terminals on state
      if (terminalsFront[terminals])
      {
        panel.terminalOn(terminals);
      }
      else
      {
        panel.terminalOff(terminals);
      }

      // Restore terminal front state.
      if (terminalsFront[terminals])
      {
        panel.terminalFront(terminals);
      }
      else
      {
        panel.terminalBack(terminals);
      }
    }
  }
}
