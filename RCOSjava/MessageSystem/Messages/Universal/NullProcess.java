//******************************************************/
// FILE     : NullProcessMessage.java
// PURPOSE  :
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Kernel.Kernel;
import Software.Animator.CPU.CPUAnimator;
import Software.Process.ProcessScheduler;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class NullProcess extends UniversalMessageAdapter
{
  public NullProcess(OSMessageHandler theSource)
  {
    super(theSource);
  }

  public void doMessage(Kernel theElement)
  {
    theElement.setCurrentProcessNull();
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.setCurrentProcessNull();
  }

  public void doMessage(CPUAnimator theElement)
  {
    theElement.screenReset();
  }
}

