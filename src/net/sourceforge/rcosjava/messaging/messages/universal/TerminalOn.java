package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import net.sourceforge.rcosjava.software.terminal.TerminalManager;

/**
 * Request that a specific terminal be set to on.
 * <P>
 * Usage example:
 * <CODE>
 *      TerminalOn tmpOn = new UpdateList(this, terminalNo);
 * </CODE>
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1997
 */
public class TerminalOn extends UniversalMessageAdapter
{
  private int terminalNo = 0;
  boolean forAnimator = false;

  /**
   * Create the terminal on message given the source and terminal number to set.
   */
  public TerminalOn(OSMessageHandler theSource, int newTerminalNo,
    boolean newForAnimator)
  {
    super(theSource);
    terminalNo = newTerminalNo;
    forAnimator = newForAnimator;
  }

  public TerminalOn(AnimatorMessageHandler theSource, int newTerminalNo,
    boolean newForAnimator)
  {
    super(theSource);
    terminalNo = newTerminalNo;
    forAnimator = newForAnimator;
  }

  /**
   * Create a terminal message but don't allocate a terminal number.  The
   * default is 0 indicating allocate the next free terminal.
   */
  public TerminalOn(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * @param newTerminalNo Set the terminal to the new value given.
   */
  public void setTerminalNo(int newTerminalNo)
  {
    terminalNo = newTerminalNo;
  }

  /**
   * If the terminal number terminal number is zero the call setNextTerminal
   * on Terminal Manager.  Otherwise, tried to allocate the given terminal
   * number.
   */
  public void doMessage(TerminalManager theElement)
  {
    if (!forAnimator)
      theElement.terminalOn(terminalNo);
  }

  /**
   * Will call the terminalOn call of the animator if the terminal number is
   * not zero.
   */
  public void doMessage(TerminalManagerAnimator theElement)
  {
    if (forAnimator)
      theElement.terminalOn(terminalNo);
  }
}

