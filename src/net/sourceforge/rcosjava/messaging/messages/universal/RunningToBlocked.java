//*******************************************************************/
// FILE     : RunningToBlockedMessage.java
// PURPOSE  : Process moves from running (CPU) to
//            blocked.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//*******************************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerFrame;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class RunningToBlocked extends UniversalMessageAdapter
{
  private RCOSProcess myProcess;

  public RunningToBlocked(OSMessageHandler theSource, RCOSProcess newProcess)
  {
    super(theSource);
    myProcess = newProcess;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.runningToBlocked(myProcess);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.cpuToBlocked(myProcess.getPID());
  }
}

