//******************************************************/
// FILE     : TerminalOffMessage
// PURPOSE  : Message to turn terminal off.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Animator.Terminal.TerminalManagerAnimator;
import Software.Terminal.TerminalManager;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class TerminalOff extends UniversalMessageAdapter
{
  private int iTerminalNo = 0;
  private boolean bOkay;

  public TerminalOff(OSMessageHandler theSource, int iNewTerminalNo,
   boolean bNewOkay)
  {
    super(theSource);
    iTerminalNo = iNewTerminalNo;
    bOkay = bNewOkay;
  }

  public void doMessage(TerminalManager theElement)
  {
    boolean bOkay;
    //Sets an internal boolean to true if it was
    //removed successfully.  Then resends the message.
    bOkay = theElement.removeTerminal(iTerminalNo);
    theElement.sendMessage(this);
  }

  public void doMessage(TerminalManagerAnimator theElement)
  {
    if (bOkay)
    {
      theElement.terminalOff(iTerminalNo);
    }
  }
}

