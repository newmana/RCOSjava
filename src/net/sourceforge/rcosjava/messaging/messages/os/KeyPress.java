package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.hardware.cpu.Interrupt;
import net.sourceforge.rcosjava.software.interrupt.TerminalInterruptHandler;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Sent by the terminal interrupt handler when the kernal gets a interrupt.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th of January 1996
 */
public class KeyPress extends OSMessageAdapter
{
  /**
   * The generated interrupt.
   */
  private Interrupt interrupt;

  public KeyPress(OSMessageHandler theSource,
    Interrupt newInterrupt)
  {
    super(theSource);
    interrupt = newInterrupt;
  }

  private void setInterrupt(Interrupt intNewInterrupt)
  {
    interrupt = intNewInterrupt;
  }

  public void doMessage(SoftwareTerminal theElement)
  {
    theElement.keyPress();
  }
}

