package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.terminal.SoftwareTerminal;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class ChIn extends OSMessageAdapter
{
  /**
   * Description of the Field
   */
  private String terminalId;

  /**
   * Constructor for the ChIn object
   *
   * @param theSource Description of Parameter
   * @param newTerminalID Description of Parameter
   */
  public ChIn(OSMessageHandler theSource, String newTerminalID)
  {
    super(theSource);
    setTerminalId(newTerminalID);
  }

  /**
   * Sets the TerminalId attribute of the ChIn object
   *
   * @param newTerminalId The new TerminalId value
   */
  public void setTerminalId(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(SoftwareTerminal theElement)
  {
    System.out.println("Element id:" + theElement.getId());
    System.out.println("My id:" + terminalId);
    if (theElement.getId().compareTo(terminalId) == 0)
    {
      theElement.chIn();
    }
  }
}