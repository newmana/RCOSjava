package org.rcosjava.messaging.messages.os;
import org.rcosjava.hardware.cpu.Interrupt;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.terminal.SoftwareTerminal;

/**
 * Sent by the terminal interrupt handler when the kernal gets a interrupt.
 * <P>
 * @author Andrew Newman.
 * @created 24th of January 1996
 * @version 1.00 $Date$
 */
public class KeyPress extends OSMessageAdapter
{
  /**
   * The generated interrupt.
   */
  private Interrupt interrupt;

  /**
   * Constructor for the KeyPress object
   *
   * @param theSource Description of Parameter
   * @param newInterrupt Description of Parameter
   */
  public KeyPress(OSMessageHandler theSource,
      Interrupt newInterrupt)
  {
    super(theSource);
    interrupt = newInterrupt;
  }

  /**
   * Call key press on the given element.
   *
   * @param theElement software terminal to call method on.
   */
  public void doMessage(SoftwareTerminal theElement)
  {
    theElement.keyPress();
  }

  /**
   * Sets the Interrupt attribute of the KeyPress object
   *
   * @param newInterrupt The new Interrupt value
   */
  private void setInterrupt(Interrupt newInterrupt)
  {
    interrupt = newInterrupt;
  }
}

