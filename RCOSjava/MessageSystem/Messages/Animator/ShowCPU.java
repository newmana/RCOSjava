//******************************************************/
// FILE     : ShowCPUMessage.java
// PURPOSE  : Message to show the CPU.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//******************************************************/

package MessageSystem.Messages.Animator;

import Software.Animator.RCOSAnimator;
import Software.Animator.CPU.CPUAnimator;
import Software.Animator.CPU.CPUFrame;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;
import Software.Animator.Process.ProcessSchedulerAnimator;

public class ShowCPU extends AnimatorMessageAdapter
{
  public ShowCPU(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  public void doMessage(CPUAnimator theElement)
  {    
    theElement.showCPU();
  }
}

