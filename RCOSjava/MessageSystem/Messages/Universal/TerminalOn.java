//******************************************************/
// FILE     : TerminalOnMessage.java
// PURPOSE  : Message to show the CPU.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Animator.Terminal.TerminalManagerAnimator;
import Software.Terminal.TerminalManager;
import Software.Animator.Process.StartProgram;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.SimpleMessageHandler;

public class TerminalOn extends UniversalMessageAdapter
{
  private int iTerminalNo = 0;

  public TerminalOn(OSMessageHandler theSource, int iNewTerminalNo)
  {
    super(theSource);
    iTerminalNo = iNewTerminalNo;
  }

  public TerminalOn(SimpleMessageHandler theSource)
  {
    super(theSource);
  }

  public void setTerminalNo(int iNewTerminalNo)
  {
    iTerminalNo = iNewTerminalNo;
  }

  public void doMessage(TerminalManager theElement)
  {
    if (iTerminalNo == 0)
    {
      iTerminalNo = theElement.setNextTerminal(getSource().getId());
    }
    else
    {
      if (theElement.addTerminal(iTerminalNo))
			{
		    //Send to animator once finished
				theElement.sendMessage(this);
			}
    }
  }

  public void doMessage(TerminalManagerAnimator theElement)
  {
    if (iTerminalNo != 0)
      theElement.terminalOn(iTerminalNo);
  }
}

