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

/**
 * Process is being moved from being executed (on the CPU) to blocked (usually
 * waiting for I/O).
 * <P>
 * <DT><B>History:</B><DD>
 * 24/03/96 Created
 * </DD><DD>
 * 01/07/96 Uses Memory
 * </DD><DD>
 * 03/08/97 Moved to message system
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th of March 1996
 */
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
