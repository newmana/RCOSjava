package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;

/**
 * Move terminal to back.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
 public class TerminalBack extends UniversalMessageAdapter
{
  private int terminalNo = 0;
  private boolean forAnimator = false;

  public TerminalBack(OSMessageHandler theSource, int newTerminalNo,
    boolean newForAnimator)
  {
    super(theSource);
    terminalNo = newTerminalNo;
    forAnimator = newForAnimator;
  }

  public TerminalBack(OSMessageHandler theSource, int newTerminalNo)
  {
    super(theSource);
    terminalNo = newTerminalNo;
  }

  public TerminalBack(AnimatorMessageHandler theSource, int newTerminalNo)
  {
    super(theSource);
    terminalNo = newTerminalNo;
  }

  public void setTerminalNumber(int newTerminalNo)
  {
    terminalNo = newTerminalNo;
  }

  public void doMessage(TerminalManager theElement)
  {
    if (!forAnimator)
      theElement.terminalBack(terminalNo);
  }

  public void doMessage(TerminalManagerAnimator theElement)
  {
    if (forAnimator)
      theElement.terminalBack(terminalNo);
  }
}