package MessageSystem.Messages.Universal;

import Software.Animator.Terminal.TerminalManagerAnimator;
import Software.Terminal.TerminalManager;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;

/**
 * Called when a user click on the graphical component (or some other reason)
 * to turn off a given terminal.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1996
 */
public class TerminalOff extends UniversalMessageAdapter
{
  private int terminalNo = 0;
  private boolean forAnimator;

  public TerminalOff(OSMessageHandler theSource, int newTerminalNo,
   boolean newForAnimator)
  {
    super(theSource);
    terminalNo = newTerminalNo;
    forAnimator = newForAnimator;
  }

  public TerminalOff(AnimatorMessageHandler theSource, int newTerminalNo,
   boolean newForAnimator)
  {
    super(theSource);
    terminalNo = newTerminalNo;
    forAnimator = newForAnimator;
  }

  public void doMessage(TerminalManager theElement)
  {
    if (!forAnimator)
      theElement.terminalOff(terminalNo);
  }

  public void doMessage(TerminalManagerAnimator theElement)
  {
    if (forAnimator)
      theElement.terminalOff(terminalNo);
  }
}

