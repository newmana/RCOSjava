package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import org.rcosjava.software.terminal.TerminalManager;

/**
 * Called when a user click on the graphical component (or some other reason) to
 * turn off a given terminal.
 * <P>
 * @author Andrew Newman.
 * @created 28th March 1996
 * @version 1.00 $Date$
 */
public class TerminalOff extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private int terminalNo = 0;
  /**
   * Description of the Field
   */
  private boolean forAnimator;

  /**
   * Constructor for the TerminalOff object
   *
   * @param theSource Description of Parameter
   * @param newTerminalNo Description of Parameter
   * @param newForAnimator Description of Parameter
   */
  public TerminalOff(OSMessageHandler theSource, int newTerminalNo,
      boolean newForAnimator)
  {
    super(theSource);
    terminalNo = newTerminalNo;
    forAnimator = newForAnimator;
  }

  /**
   * Constructor for the TerminalOff object
   *
   * @param theSource Description of Parameter
   * @param newTerminalNo Description of Parameter
   * @param newForAnimator Description of Parameter
   */
  public TerminalOff(AnimatorMessageHandler theSource, int newTerminalNo,
      boolean newForAnimator)
  {
    super(theSource);
    terminalNo = newTerminalNo;
    forAnimator = newForAnimator;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(TerminalManager theElement)
  {
    if (!forAnimator)
    {
      theElement.terminalOff(terminalNo);
    }
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(TerminalManagerAnimator theElement)
  {
    if (forAnimator)
    {
      theElement.terminalOff(terminalNo);
    }
  }
}

