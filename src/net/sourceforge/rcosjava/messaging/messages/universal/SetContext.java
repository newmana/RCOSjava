package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.hardware.cpu.Context;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Sets current CPU status of BP, SP, etc.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st January 1998
 */
public class SetContext extends UniversalMessageAdapter
{
  private Context myContext;

  public SetContext(OSMessageHandler theSource,
    Context newContext)
  {
    super(theSource);
    myContext = (Context) newContext.clone();
  }

  /**
   * Calls setContext on the CPU Animator to display the stack pointer, base
   * pointer, etc.
   *
   * @param theElement the CPU Animator to do the work on.
   */
  public void doMessage(CPUAnimator theElement)
  {
    theElement.setContext(myContext);
  }
}

