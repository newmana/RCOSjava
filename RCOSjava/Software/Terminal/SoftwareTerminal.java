package Software.Terminal;

import java.awt.*;
import java.awt.event.*;

import Hardware.CPU.Interrupt;
import Hardware.Terminal.HardwareTerminal;
import Software.Interrupt.TerminalInterruptHandler;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.Messages.OS.BlockCurrentProcess;
import MessageSystem.Messages.OS.HandleInterrupt;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.TerminalToggle;
import MessageSystem.Messages.OS.RegisterInterruptHandler;
import MessageSystem.Messages.OS.ReturnValue;
import MessageSystem.Messages.Universal.KillProcess;
import MessageSystem.Messages.Universal.BlockedToReady;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import Software.Interrupt.InterruptHandler;
import Software.Util.LIFOQueue;

/**
 * OS software that manages a hardware terminal.  The basic idea is that there
 * is a hardware terminal representing the physical properties of the screen
 * and the software terminal representing any logic processing involved.
 * <P>
 * HISTORY: 23/03/96  Moved into package Terminal -DJ.<BR>
 *          24/03/96  Modified to reverse membership<BR>
 *                    Terminal now extends Frame and
 *                    has a MessageHandler as a member -DJ.<BR>
 *          29/03/96  Separated into SoftwareTerminal -DJ.<BR>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 24th of January 1996
 */
public class SoftwareTerminal extends OSMessageHandler
{
  HardwareTerminal hardwareTerminal;
  TerminalInterruptHandler terminalIH;

  // Holds all key pressed events.
  public LIFOQueue softwareBuffer;

  // PID of process that is blocked waiting for input
  public int blockedProcess;
  public int currentProcess;

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

  public void chOut(char cOut)
  {
    hardwareTerminal.printChr(cOut);
  }

  public void numOut(byte bOut)
  {
    hardwareTerminal.printNum(bOut);
  }

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

  public void numIn()
  {
    if (!softwareBuffer.queueEmpty())
    {
      int newNum = 0, number = 0;
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
        number = toNumber((KeyEvent) softwareBuffer.retrieveCurrent());

      // Print the output.
      hardwareTerminal.printNum((short) number);

      // return character back
      ReturnValue msg = new ReturnValue(this,
        (short) number);
      sendMessage(msg);
    }
    else
    {
      BlockCurrentProcess msg = new
        BlockCurrentProcess(this);
      sendMessage(msg);
    }
  }

  public void keyPress()
  {
    if (!hardwareTerminal.bufferEmpty())
    {
      KeyEvent theEvent = hardwareTerminal.getKeyFromBuffer();

      //**************************************
      // perform checks on type of key
      // this should eventually be moved into a seperate function

      if (theEvent.getKeyChar() == 3)
      {
        // hey, the use hit CTRL-C, time to kill the process
        // make sure we've been allocated first

        if (currentProcess != -1)
        {
          KillProcess msg = new KillProcess(this,
            currentProcess,false);
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

  public void sendInterrupt(Interrupt theInt)
  {
    HandleInterrupt msg = new HandleInterrupt(this, theInt);
    sendMessage(msg);
  }

  public void terminalClose()
  {
    int iTerminalNo = Integer.parseInt(hardwareTerminal.getTitle().substring(9));
    TerminalToggle msg = new TerminalToggle(this, iTerminalNo);
    sendMessage(msg);
  }

  public void processMessage(OSMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }

  public void processMessage(UniversalMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }

  /**
   * Check to see if the key that was pressed was a numeric key
   *
   * @return TRUE if ASCII value is between 48 & 57 inclusive
   */
  boolean isNumber(KeyEvent evt)
  {
    return ((evt.getKeyChar() > 47) && (evt.getKeyChar() < 58));
  }

  /**
   * take KeyEvent (keypress) and return Integer value
   */
  int toNumber(KeyEvent evt)
  {
    return (evt.getKeyChar() - 48);
  }
}
