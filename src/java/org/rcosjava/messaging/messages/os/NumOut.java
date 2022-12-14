package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.terminal.SoftwareTerminal;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class NumOut extends OSMessageAdapter
{
  /**
   * Description of the Field
   */
  private String terminalId;
  /**
   * Description of the Field
   */
  private short num;

  /**
   * Constructor for the NumOut object
   *
   * @param theSource Description of Parameter
   * @param newTerminalId Description of Parameter
   * @param newNum Description of Parameter
   */
  public NumOut(OSMessageHandler theSource,
      String newTerminalId, short newNum)
  {
    super(theSource);
    terminalId = newTerminalId;
    num = newNum;
  }

  /**
   * Sets the TerminalID attribute of the NumOut object
   *
   * @param newTerminalId The new TerminalID value
   */
  public void setTerminalId(String newTerminalId)
  {
    terminalId = newTerminalId;
  }

  /**
   * Sets the NewNum attribute of the NumOut object
   *
   * @param newNum The new NewNum value
   */
  public void setNewNum(short newNum)
  {
    num = newNum;
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
      theElement.numOut((byte) num);
    }
  }
}

