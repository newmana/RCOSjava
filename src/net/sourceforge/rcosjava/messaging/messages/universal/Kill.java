package net.sourceforge.rcosjava.messaging.messages.universal;


import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Sent from Program Manager Animator to kill a selected process running.  Not
 * to be confused with (although linked to) KillProcessMessage.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1996
 */
public class Kill extends UniversalMessageAdapter
{
  private int pid;

  public Kill(AnimatorMessageHandler theSource, int newPID)
  {
    super(theSource);
    pid = newPID;
  }

  public Kill(OSMessageHandler theSource, int newPID)
  {
    super(theSource);
    pid = newPID;
  }

  private void setProcessID(int newPID)
  {
    pid = newPID;
  }

  public void doMessage(ProgramManager theElement)
  {
    theElement.kill(pid);
  }
}

