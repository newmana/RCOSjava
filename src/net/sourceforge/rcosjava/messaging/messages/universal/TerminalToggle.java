package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalOff;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalOn;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;

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