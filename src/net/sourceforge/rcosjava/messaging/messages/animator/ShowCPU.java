package net.sourceforge.rcosjava.messaging.messages.animator;

import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.software.animator.cpu.CPUFrame;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;

/**
 * Display the CPU in the CPU animator.
 *
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 1st July 1996
 */
public class ShowCPU extends AnimatorMessageAdapter
{
  /**
   * Create the message sender.
   */
  public ShowCPU(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Call the CPUAnimator's showCPU method call.
   */
  public void doMessage(CPUAnimator theElement)
  {
    theElement.showCPU();
  }
}

