package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;

/**
 * Message to stop the execution of code.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th of March 1998
 */
public class Stop extends UniversalMessageAdapter
{
  public Stop(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  public void doMessage(ProgramManager theElement)
  {
    theElement.stopThread();
  }
}

