package net.sourceforge.rcosjava.hardware.terminal;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import net.sourceforge.rcosjava.hardware.cpu.Interrupt;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.messages.os.HandleInterrupt;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.software.util.FIFOQueue;

/**
 * A simulation of a "real" terminal (keyboard and screen).  This is basically
 * the I/O keyboard section.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 23/03/1996  Moved into package Terminal<BR>
 * </DD><DD>
 * 24/03/1996  Modified to reverse membership Terminal now extends Frame and
 * has a MessageHandler as a member.
 * </DD><DD>
 * 29/03/1996 Separated into Hardware|SoftwareTerminal.
 * </DD><DD>
 * 01/12/1996 Some bugs to do with displaying on the screen are fixed.  Text
 * nolonger has a box at the end and updating is done by rows instead of each
 * character.
 * </DD><DD>
 * 03/12/1996 Double buffering for flicker free animation and you can set the
 * screen colours too.
 * </DD><DD>
 * 14/04/1997 Split Hardware Terminal into HardwareTerminal and and
 * HardwareTerminalScreen
 * </DD><DD>
 * 28/11/1998 Converted to Java 1.1.
 * </DD><DD>
 * 29/11/1998 Removed any message passing.
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.software.animator.RCOSFrame
 * @see net.sourceforge.rcosjava.hardware.terminal.HardwareTerminalScreen
 * @see net.sourceforge.rcosjava.software.terminal.SoftwareTerminal
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th January 1996
 */

public class HardwareTerminal extends RCOSFrame
{
  /**
   * The physical output terminal display hardware (the screen).
   */
  private HardwareTerminalScreen myScreen;

  /**
   * The software side of terminal linking the hardware to the operating system.
   */
  private SoftwareTerminal softwareTerminal;

  /**
   * The name of the terminal to display as the title on the window.
   */
  private String theTitle;

  /**
   * A buffer of hardware events such as key presses.
   */
  private FIFOQueue hardwareBuffer;

  /**
   * The number of characters in the buffer.
   */
  private int numChars;

  /**
   * Registers the software terminal with the post office for the operating
   * system.
   */
  public HardwareTerminal(String myId, OSOffice aPostOffice)
  {
    softwareTerminal = new SoftwareTerminal(myId, aPostOffice, this);
    theTitle = myId;

    hardwareBuffer = new FIFOQueue(10,0);
    numChars = 0;

    setupLayout();
  }

  /**
   * @return the title of the screen.
   */
  public String getTitle()
  {
    return theTitle;
  }

  /**
   * Set Process ID using this terminal
   */
  public void setCurrentProcessId(int iCurrentProcess)
  {
    softwareTerminal.currentProcess = iCurrentProcess;
  }

  /**
   * @return is there a keyevent waiting for processing?
   */
  public boolean bufferEmpty()
  {
    return hardwareBuffer.queueEmpty();
  }

  /**
   * @return keyevent from queue.
   */
  public KeyEvent getKeyFromBuffer()
  {
    return ((KeyEvent) hardwareBuffer.retrieve());
  }

  //Setup screen layout.

  /**
   * Setup the screen layout by setting the title, default colours of the screen
   * and the default font.  Adds the KeyHandler to the entire screen to get any
   * key presses that the user makes.
   */
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

  /**
   * Called by the software terminal when a character is to be written to the
   * screen.
   *
   * @param ch the character to display in the screen.
   */
  public void printChr (char ch)
  {
    myScreen.printChr(ch);
  }

  /**
   * Called by the software terminal when a number is to be written to the
   * screen.
   *
   * @param num the number to display on the screen.
   */
  public void printNum (short num)
  {
    myScreen.printNum(num);
  }

  /**
   * Attaches itself to the screen and adds the character to the hardware
   * buffer.  It then generates a hardware interrupt of KeyPress and uses the
   * SoftwareTerminal to send this message.
   *
   * @see net.sourceforge.rcosjava.hardware.cpu.Interrupt
   */
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
