package MessageSystem.Messages.Universal;

import Software.Animator.Terminal.TerminalManagerAnimator;
import Software.Terminal.TerminalManager;
import MessageSystem.Messages.Universal.TerminalOff;
import MessageSystem.Messages.Universal.TerminalOn;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;

/**
 * Toggle the terminal off or on.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1996
 */
public class TerminalToggle extends UniversalMessageAdapter
{
  private int iTerminalNo = 0;

  public TerminalToggle(OSMessageHandler theSource, int iNewTerminalNo)
  {
    super(theSource);
    iTerminalNo = iNewTerminalNo;
  }

  public TerminalToggle(AnimatorMessageHandler theSource, int iNewTerminalNo)
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
    theElement.terminalToggle(iTerminalNo);
  }
}