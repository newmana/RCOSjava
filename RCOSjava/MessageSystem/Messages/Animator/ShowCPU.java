package MessageSystem.Messages.Animator;

import Software.Animator.RCOSAnimator;
import Software.Animator.CPU.CPUAnimator;
import Software.Animator.CPU.CPUFrame;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;
import Software.Animator.Process.ProcessSchedulerAnimator;

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

