//*******************************************************************/
// FILE     : ProcessToTerminalAllocationMessage.java
// PURPOSE  : Used to assign a terminal to the next
//            waiting process.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//*******************************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class ProcessToTerminalAllocation extends UniversalMessageAdapter
{
  private String sTerminalID;
  private int iPID;

  public ProcessToTerminalAllocation(OSMessageHandler theSource, String sTerminalID, int iPID)
  {
    super(theSource);
    this.sTerminalID = sTerminalID;
    this.iPID = iPID;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.processAllocatedTerminal(iPID, sTerminalID);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
    theElement.zombieToReady(iPID);
  }
}

