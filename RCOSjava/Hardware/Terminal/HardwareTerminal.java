package Hardware.Terminal;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import Hardware.CPU.Interrupt;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.Messages.OS.HandleInterrupt;
import Software.Animator.RCOSFrame;
import Software.Terminal.SoftwareTerminal;
import Software.Util.FIFOQueue;

/**
 * A simulation of a "real" terminal (keyboard and screen).  This is basically
 * the I/O keyboard section.
 * <P>
 * HISTORY: 23/03/1996  Moved into package Terminal<BR>
 *          24/03/1996  Modified to reverse membership
 *                      Terminal now extends Frame and
 *                      has a MessageHandler as a member<BR>
 *           29/03/1996 Separated into Hardware|SoftwareTerminal
 *           01/12/1996 Some bugs to do with displaying on the
 *                      screen are fixed.  Text nolonger has
 *                      a box at the end and updating is done
 *                      by rows instead of each character.<BR>
 *           03/12/1996 Double buffering for flicker free animation
 *                      and you can set the screen colours too.<BR>
 *           14/04/1997 Split Hardware Terminal into HardwareTerminal and
 *                      and HardwareTerminalScreen<BR>
 *           28/11/1998 Converted to Java 1.1.<BR>
 *           29/11/1998 Removed any message passing.<BR>
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th January 1996
 */public class HardwareTerminal extends RCOSFrame
{
  private HardwareTerminalScreen myScreen;
  private SoftwareTerminal softwareTerminal;
  private String theTitle;
  private FIFOQueue hardwareBuffer;
  private int numChars;

  public HardwareTerminal(String myId, OSOffice aPostOffice)
  {
    softwareTerminal = new SoftwareTerminal(myId, aPostOffice, this);
    theTitle = myId;

    hardwareBuffer = new FIFOQueue(10,0);
    numChars = 0;

    setupLayout();
  }

  //Title of terminal
  public String getTitle()
  {
    return theTitle;
  }

  //Set Process ID using this terminal
  public void setCurrentProcessId(int iCurrentProcess)
  {
    softwareTerminal.currentProcess = iCurrentProcess;
  }

  //Is there a keyevent waiting for processing?
  public boolean bufferEmpty()
  {
    return hardwareBuffer.queueEmpty();
  }

  //Return keyevent from queue.
  public KeyEvent getKeyFromBuffer()
  {
    return ((KeyEvent) hardwareBuffer.retrieve());
  }

  //Setup screen layout.

  public void setupLayout()
  {
    setTitle(theTitle);
    setBackground(defaultBgColour);
    setForeground(defaultFgColour);
    setFont(defaultFont);

    SymWindow2 aSymWindow2 = new SymWindow2();
	  this.addWindowListener(aSymWindow2);
    this.addKeyListener(new KeyHandler());

    setLayout(new BorderLayout(5,5));
    Panel pMain = new Panel();
    setVisible(true);
    myScreen = new HardwareTerminalScreen(this);
    pMain.add(myScreen);
    add("Center", pMain);
    pack();
    setSize(myScreen.getWidth(),myScreen.getHeight());
    setResizable(false);
    repaint();
  }

  public void printChr (char ch)
  {
    myScreen.printChr(ch);
  }

  public void printNum (short num)
  {
    myScreen.printNum(num);
  }

  class KeyHandler extends KeyAdapter
  {
    public void keyTyped(KeyEvent e)
    {
      // if buffer is full, take one event off and discard

      if (numChars == 10)
      {
        KeyEvent tmp = (KeyEvent) hardwareBuffer.retrieve();
      }

      // insert the new key press into the buffer

      numChars++;
      hardwareBuffer.insert(e);

      // Send msg to CPU to generate Interrupt
      Interrupt theInt = new Interrupt(-1, theTitle + "KeyPress");
      softwareTerminal.sendInterrupt(theInt);
    }
  }

  class SymWindow2 extends java.awt.event.WindowAdapter
  {
    public void windowClosing(java.awt.event.WindowEvent event)
	  {
  	  Object object = event.getSource();
	    if (object == HardwareTerminal.this)
	    {
        // If user is trying to remove a terminal simulate
        // the user pressing the terminal off in the Terminal
        // Manager screen.

        softwareTerminal.terminalClose();
	    }
    }
  }
}
