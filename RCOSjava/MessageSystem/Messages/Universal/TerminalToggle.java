//******************************************************/
// FILE     : TerminalToggleMessage
// PURPOSE  : Toggle terminal from on/off or vice versa.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Animator.Terminal.TerminalManagerAnimator;
import Software.Terminal.TerminalManager;
import MessageSystem.Messages.Universal.TerminalOff;
import MessageSystem.Messages.Universal.TerminalOn;
import MessageSystem.PostOffices.SimpleMessageHandler;

public class TerminalToggle extends UniversalMessageAdapter
{
  private int iTerminalNo = 0;

  public TerminalToggle(SimpleMessageHandler theSource,
    int iNewTerminalNo)
  {
    super(theSource);
    iTerminalNo = iNewTerminalNo;
  }
  
  public void setTerminalNumber(int iNewTerminalNo)
  {
    iTerminalNo = iNewTerminalNo;
  }

  public void doMessage(TerminalManager theElement)
  {
    if (theElement.terminalOn(iTerminalNo))
    {
      if (theElement.removeTerminal(iTerminalNo))
      {
        //Terminal Off
        TerminalOff toMessage = new TerminalOff(
          theElement, iTerminalNo, false);
        theElement.sendMessage(toMessage);
      }
    }
    else
    {
      if (theElement.addTerminal(iTerminalNo))
      {
        //Terminal On
        TerminalOn toMessage = new TerminalOn(
          theElement, iTerminalNo);
        theElement.sendMessage(toMessage);
      }
    }
  }
}

