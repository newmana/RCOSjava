package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;

/**
 * Message sent to program manager to set the thread of executing back to start
 * again to begin executing the program at full speed.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th of March 1996
 */
public class Run extends UniversalMessageAdapter
{
  public Run(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  public void doMessage(ProgramManager theElement)
  {
    theElement.startThread();
  }
}

