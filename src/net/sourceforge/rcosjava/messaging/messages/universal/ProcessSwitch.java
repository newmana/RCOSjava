//***************************************************************************
// FILE    : ProcessSwitchMessage.java
// PACKAGE :
// PURPOSE :
// AUTHOR  : Andrew Newman
// MODIFIED:
// HISTORY : 28/03/96  Created
//
//
//***************************************************************************

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class ProcessSwitch extends UniversalMessageAdapter
{
  private RCOSProcess rpProcess;

  public ProcessSwitch(OSMessageHandler theSource, RCOSProcess rpNewProcess)
  {
    super(theSource);
    rpProcess = rpNewProcess;
  }

  public void setProcess(RCOSProcess rpNewProcess)
  {
    rpProcess = rpNewProcess;
  }

  public void doMessage(Kernel theElement)
  {
    theElement.switchProcess(rpProcess);
    theElement.setCurrentProcessTicks();
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.readyToCPU(rpProcess.getPID());
  }
}

