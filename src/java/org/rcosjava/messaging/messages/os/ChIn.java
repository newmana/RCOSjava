package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.terminal.SoftwareTerminal;

/**
 * User has type a character.
 *
 * @author Andrew Newman
 * @created 28 April 2002
 */
public class ChIn extends OSMessageAdapter
{
  /**
   * The unique id of the terminal that the character was pressed.
   */
  private String terminalId;

  /**
   * Create a new ChIn message.
   *
   * @param theSource the sender of the message.
   * @param newTerminalID the terminal id where the character orginated.
   */
  public ChIn(OSMessageHandler theSource, String newTerminalID)
  {
    super(theSource);
    setTerminalId(newTerminalID);
  }

  /**
   * Sets a new value of the terminal id.
   *
   * @param newTerminalId The new terminal id value.
   */
  public void setTerminalId(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  /**
   * If the Software Terminal object has the same terminal id then chIn() is
   * called.
   *
   * @param theElement the software terminal.
   */
  public void doMessage(SoftwareTerminal theElement)
  {
    if (theElement.getId().compareTo(terminalId) == 0)
    {
      theElement.chIn();
    }
  }
}