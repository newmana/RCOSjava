//*******************************************************************/
// FILE     : KillProcessMessage.java
// PURPOSE  : Kill a process.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//*******************************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;


import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class KillProcess  extends UniversalMessageAdapter
{
  private int iPID;
  private boolean bHandleInterrupt = false;

  public KillProcess(OSMessageHandler theSource, int iNewPID, boolean bNewHandleInterrupt)
  {
    super(theSource);
    iPID = iNewPID;
    bHandleInterrupt = bNewHandleInterrupt;
  }

  public void setProcessID(int iNewPID)
  {
    iPID = iNewPID;
  }

  public void doMessage(ProcessScheduler theElement)
  {
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
     theElement.killProcess(iPID);
  }

  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.deleteProcess(new Integer(iPID));
  }

  public void doMessage(Kernel theElement)
  {
    if (bHandleInterrupt)
      theElement.handleProcessFinishedInterrupt();
  }
}

