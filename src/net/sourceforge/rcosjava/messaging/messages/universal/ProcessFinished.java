//******************************************************/
// FILE     : ProcessFinishedMessage.java
// PURPOSE  : What to do when the process finishes.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.RCOSProcess;

public class ProcessFinished extends UniversalMessageAdapter
{
  private RCOSProcess rpOldCurrent;

  public ProcessFinished(OSMessageHandler theSource, RCOSProcess oldProcess)
  {
    super(theSource);
    setProcess(oldProcess);
  }

  public void setProcess(RCOSProcess rpNewProcess)
  {
    rpOldCurrent = rpNewProcess;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.processFinished(rpOldCurrent);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.processFinished(rpOldCurrent.getPID());
  }

  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.deleteProcess(new Integer(rpOldCurrent.getPID()));
  }
}

