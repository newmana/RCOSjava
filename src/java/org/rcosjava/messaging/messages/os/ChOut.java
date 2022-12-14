package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.terminal.SoftwareTerminal;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class ChOut extends OSMessageAdapter
{
  /**
   * Description of the Field
   */
  private String terminalId;
  /**
   * Description of the Field
   */
  private char ch;

  /**
   * Constructor for the ChOut object
   *
   * @param newSource Description of Parameter
   * @param newTerminalId Description of Parameter
   * @param newCh Description of Parameter
   */
  public ChOut(OSMessageHandler newSource,
      String newTerminalId, char newCh)
  {
    super(newSource);
    terminalId = newTerminalId;
    ch = newCh;
  }

  /**
   * Sets the TerminalId attribute of the ChOut object
   *
   * @param newTerminalId The new TerminalID value
   */
  public void setTerminalId(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  /**
   * Sets the Character attribute of the ChOut object
   *
   * @param newCh The new Character value
   */
  public void setCharacter(char newCh)
  {
    ch = newCh;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(SoftwareTerminal theElement)
  {
    if (theElement.getId().compareTo(terminalId) == 0)
    {
      theElement.chOut(ch);
    }
  }
}

