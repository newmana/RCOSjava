package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.software.kernel.Kernel;

/**
 * Kernel receives a number in event tells the software terminal to display
 * the input on screen.
 * <P>
 * @see net.sourceforge.rcosjava.software.kernel.Kernel
 * @see net.sourceforge.rcosjava.software.terminal.SoftwareTerminal
 * @author Andrew Newman.
 * @author David Jones
 * @version 1.00 $Date$
 * @created 21st January 1996
 */
public class NumIn extends OSMessageAdapter
{
  /**
   * The terminal Id that the key press is from.
   */
  private String terminalId;

  /**
   * Create a message from a source with the given terminal id.
   *
   * @param theSource the parent of the console's frame that sent the message.
   * @param newTerminalId the name of the terminal that sent the message.
   */
  public NumIn(OSMessageHandler theSource, String newTerminalId)
  {
    super(theSource);
    terminalId = newTerminalId;
  }

  /**
   * Set a new terminal id for the existing message.
   *
   * @param newTerminalId the new value for the terminal id.
   */
  public void setTerminalID(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  /**
   * If the terminalId matches the receiving software terminal then call the
   * numIn method on the software terminal.
   *
   * @param theElement a software terminal listening to the messages.
   */
  public void doMessage(SoftwareTerminal theElement)
  {
    if (theElement.getId().compareTo(terminalId) == 0)
      theElement.numIn();
  }
}

