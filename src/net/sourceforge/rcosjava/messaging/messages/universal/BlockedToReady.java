//******************************************************/
// FILE     : BlockedToReadyMessage.java
// PURPOSE  : Process moves from blocked to ready.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class BlockedToReady extends UniversalMessageAdapter
{
  private int iPID;

  public BlockedToReady(OSMessageHandler theSource,
    int iProcessID)
  {
    super(theSource);
    iPID = iProcessID;
  }

  public void setValues(int iProcessID)
  {
    iPID = iProcessID;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.blockedToReady(iPID);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.blockedToReady(iPID);
  }
}