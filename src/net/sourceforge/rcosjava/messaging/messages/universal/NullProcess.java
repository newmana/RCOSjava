//******************************************************/
// FILE     : NullProcessMessage.java
// PURPOSE  :
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;


import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

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

