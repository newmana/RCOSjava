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

/**
 * Sent by the user (control-C on the terminal) or by the process manager to
 * kill a given process with a given process Id.  Currently, no security is
 * in place - everyone is root!
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th of March 1996
 */
public class KillProcess extends UniversalMessageAdapter
{
  private int pid;

  public KillProcess(OSMessageHandler theSource, int newPID)
  {
    super(theSource);
    pid = newPID;
  }

  public void setProcessID(int newPID)
  {
    pid = newPID;
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.killProcess(pid);
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
     theElement.killProcess(pid);
  }

  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.deleteProcess(new Integer(pid));
  }

  public void doMessage(Kernel theElement)
  {
    if (theElement.runningProcess() && theElement.getCurrentProcess().getPID() == pid)
      theElement.handleProcessFinishedInterrupt();
  }
}

