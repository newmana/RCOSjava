//******************************************************/
// FILE     : TerminalFront
// PURPOSE  : Move terminal to front.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Animator.Terminal.TerminalManagerAnimator;
import Software.Terminal.TerminalManager;
import MessageSystem.PostOffices.SimpleMessageHandler;

public class TerminalFront extends UniversalMessageAdapter
{
  private int iTerminalNo = 0;
  private boolean bOkay = false;

  public TerminalFront(SimpleMessageHandler theSource, int iNewTerminalNo)
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
    bOkay = theElement.viewTerminal(iTerminalNo, true);
    //Send to animator once finished
    if (bOkay)
    {
      theElement.sendMessage(this);
    }
  }

  public void doMessage(TerminalManagerAnimator theElement)
  {
    if (bOkay)
      theElement.terminalFront(iTerminalNo);
  }
}

