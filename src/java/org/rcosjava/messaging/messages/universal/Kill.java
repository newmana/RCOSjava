package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.process.ProgramManager;

/**
 * Sent from Program Manager Animator to kill a selected process running. Not to
 * be confused with (although linked to) KillProcessMessage.
 * <P>
 * @author Andrew Newman.
 * @created 28th March 1996
 * @version 1.00 $Date$
 */
public class Kill extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private int pid;

  /**
   * Constructor for the Kill object
   *
   * @param theSource Description of Parameter
   * @param newPID Description of Parameter
   */
  public Kill(AnimatorMessageHandler theSource, int newPID)
  {
    super(theSource);
    pid = newPID;
  }

  /**
   * Constructor for the Kill object
   *
   * @param theSource Description of Parameter
   * @param newPID Description of Parameter
   */
  public Kill(OSMessageHandler theSource, int newPID)
  {
    super(theSource);
    pid = newPID;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProgramManager theElement)
  {
    theElement.kill(pid);
  }

  /**
   * Sets the ProcessID attribute of the Kill object
   *
   * @param newPID The new ProcessID value
   */
  private void setProcessID(int newPID)
  {
    pid = newPID;
  }
}

