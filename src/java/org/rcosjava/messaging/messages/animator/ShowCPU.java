package org.rcosjava.messaging.messages.animator;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.animator.cpu.CPUAnimator;

/**
 * Display the CPU in the CPU animator.
 *
 * @author Andrew Newman.
 * @created 1st July 1996
 * @version 1.00 $Date$
 */
public class ShowCPU extends AnimatorMessageAdapter
{
  /**
   * Create the message sender.
   *
   * @param theSource Description of Parameter
   */
  public ShowCPU(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Call the CPUAnimator's showCPU method call.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(CPUAnimator theElement)
  {
//    theElement.showCPU();
  }
}

