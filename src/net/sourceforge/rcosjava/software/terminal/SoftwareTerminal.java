package net.sourceforge.rcosjava.software.terminal;

import java.awt.*;
import java.awt.event.*;

import net.sourceforge.rcosjava.hardware.cpu.Interrupt;
import net.sourceforge.rcosjava.hardware.terminal.HardwareTerminal;
import net.sourceforge.rcosjava.software.interrupt.TerminalInterruptHandler;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.messages.os.BlockCurrentProcess;
import net.sourceforge.rcosjava.messaging.messages.os.HandleInterrupt;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalToggle;
import net.sourceforge.rcosjava.messaging.messages.os.RegisterInterruptHandler;
import net.sourceforge.rcosjava.messaging.messages.os.ReturnValue;
import net.sourceforge.rcosjava.messaging.messages.universal.Kill;
import net.sourceforge.rcosjava.messaging.messages.universal.BlockedToReady;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.software.interrupt.InterruptHandler;
import net.sourceforge.rcosjava.software.util.LIFOQueue;

/**
 * OS software that manages a hardware terminal.  The basic idea is that there
 * is a hardware terminal representing the physical properties of the screen
 * and the software terminal representing any logic processing involved.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 23/03/96  Moved into package Terminal -DJ.
 * </DD><DD>
 * 24/03/96  Modified to reverse membership.  Terminal now extends Frame and
 * has a MessageHandler as a member -DJ.
 * </DD><DD>
 * 29/03/96  Separated into SoftwareTerminal -DJ.
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 24th of January 1996
 */
public class SoftwareTerminal extends OSMessageHandler
{
  /**
   * The physical hardware terminal screen to receive and display input/output.
   */
  HardwareTerminal hardwareTerminal;

  /**
   * The handler which handles interrupts generated.
   */
  TerminalInterruptHandler terminalIH;

  /**
   * Holds all key pressed events.
   */
  public LIFOQueue softwareBuffer;

  /**
   * PID of process that is blocked waiting for input
   */
  public int blockedProcess;

  /**
   * The PID of the process that is currently using this terminal
   */
  public int currentProcess;

  /**
   * Creates a new software terminal ready to be allocated a process.  Set all
   * values to their default set-ups.  The blocked and current process values
   * are set to -1 to indicate not allocated.  It also registers the terminal
   * interrupt handler.
   *
   * @param myId the string to register to the post office with.
   * @param aPostOffice the post office to register with.
   * @param terminal the hardware terminal to associate myself with.
   */
  public SoftwareTerminal(String myId, OSOffice aPostOffice,
                          HardwareTerminal terminal)
  {
    super(myId, aPostOffice);

    hardwareTerminal = terminal;
    blockedProcess = -1;
    currentProcess = -1;
    softwareBuffer = new LIFOQueue(10, 10);

    // create and register a terminal Interrupt handler
    terminalIH = new TerminalInterruptHandler(getId(), aPostOffice,
      getId() + "KeyPress");
    RegisterInterruptHandler msg = new
      RegisterInterruptHandler((InterruptHandler) terminalIH);
    sendMessage(msg);
  }

  /**
   * Print the character to the screen.  Currently, calls printChr on the
   * hardware terminal's screen.
   *
   * @param out the character to print.
   */
  public void chOut(char out)
  {
    hardwareTerminal.printChr(out);
  }

  /**
   * Print the number to the screen.  Currently, calls printNum on the
   * hardware terminal's screen.
   *
   * @param out the number to print.
   */
  public void numOut(byte out)
  {
    hardwareTerminal.printNum(out);
  }

  /**
   * From the buffer take the next key event from it.  This is called when a
   * character event has happened.  Calls the hardware terminal to display the
   * character and send the character back.  If there is no character in the
   * buffer then the process is blocked.
   */
  public void chIn()
  {
    if (!softwareBuffer.queueEmpty())
    {
      KeyEvent theEvent = (KeyEvent) softwareBuffer.retrieve();

      // Print the output.
      hardwareTerminal.printChr(theEvent.getKeyChar());

      // return character back
      ReturnValue msg = new ReturnValue(this,
        (short) theEvent.getKeyChar());
      sendMessage(msg);
    }
    else
    {
      BlockCurrentProcess msg = new BlockCurrentProcess(this);
      sendMessage(msg);
    }
  }

  /**
   * Exactly the same as the chIn except a number is used instead.
   */
  public void numIn()
  {
    if (!softwareBuffer.queueEmpty())
    {
      int newNum = 0;
      int number = 0;
      boolean found = false;

      softwareBuffer.goToHead();

      while (!softwareBuffer.atTail())
      {
        if (isNumber((KeyEvent) softwareBuffer.peek()))
        {
          found = true;
          newNum = toNumber((KeyEvent) softwareBuffer.retrieveCurrent());
          number = number * 10 + newNum;
        }
        else
          break;
      }

      if (!found)
      {
        number = toNumber((KeyEvent) softwareBuffer.retrieveCurrent());
      }

      // Print the output.
      hardwareTerminal.printNum((short) number);

      // return character back
      ReturnValue msg = new ReturnValue(this, (short) number);
      sendMessage(msg);
    }
    else
    {
      BlockCurrentProcess msg = new
        BlockCurrentProcess(this);
      sendMessage(msg);
    }
  }

  /**
   * If a keypress event has occurred it checks the bueffer and if it's not
   * empty awakens the process that was blocked.  If the key is a Ctrl-C then
   * it kills this process.
   */
  public void keyPress()
  {
    if (!hardwareTerminal.bufferEmpty())
    {
      KeyEvent theEvent = hardwareTerminal.getKeyFromBuffer();

      // perform checks on type of key
      // this should eventually be moved into a seperate function

      if (theEvent.getKeyChar() == 3)
      {
        // hey, the use hit CTRL-C, time to kill the process
        // make sure we've been allocated first
        if (currentProcess != -1)
        {
          Kill msg = new Kill(this, currentProcess);
          sendMessage(msg);
        }
      }
      else
        softwareBuffer.insert(theEvent);

      if (blockedProcess != - 1)
      {
        BlockedToReady msg = new BlockedToReady(this,
          blockedProcess);
        sendMessage(msg);
        // process is no longer blocked
        blockedProcess = -1;
      }
    }
  }

  /**
   * Creates a new interrupt and sends a handle interrupt message.
   *
   * @param newInterrupt the interrupt to send.
   */
  public void sendInterrupt(Interrupt newInterrupt)
  {
    HandleInterrupt msg = new HandleInterrupt(this, newInterrupt);
    sendMessage(msg);
  }

  /**
   * Sends a terminal toggle message as the user has set the terminal to close.
   */
  public void terminalClose()
  {
    int terminalNo = Integer.parseInt(hardwareTerminal.getTitle().substring(9));
    TerminalToggle msg = new TerminalToggle(this, terminalNo);
    sendMessage(msg);
  }

  /**
   * Check to see if the key that was pressed was a numeric key
   *
   * @return true if ASCII value is between 48 & 57 inclusive
   */
  boolean isNumber(KeyEvent evt)
  {
    return ((evt.getKeyChar() > 47) && (evt.getKeyChar() < 58));
  }

  /**
   * Take KeyEvent (keypress) and return Integer value
   */
  int toNumber(KeyEvent evt)
  {
    return (evt.getKeyChar() - 48);
  }
}
