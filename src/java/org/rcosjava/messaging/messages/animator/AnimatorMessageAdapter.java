package org.rcosjava.messaging.messages.animator;
import java.io.Serializable;
import org.rcosjava.RCOS;
import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.postoffices.PostOffice;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.animator.cpu.CPUAnimator;
import org.rcosjava.software.animator.disk.DiskSchedulerAnimator;
import org.rcosjava.software.animator.filesystem.FileSystemAnimator;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;
import org.rcosjava.software.animator.multimedia.MultimediaAnimator;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.animator.process.ProgramManagerAnimator;
import org.rcosjava.software.animator.terminal.TerminalManagerAnimator;

/**
 * AnimatorMessageAdapter which implements AnimatorMessage. <P>
 *
 * HISTORY: 03/07/98 Used double dispatch
 *
 * @author Andrew Newman
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public class AnimatorMessageAdapter extends MessageAdapter
     implements AnimatorMessage, Serializable
{
  /**
   * Constructor for the AnimatorMessageAdapter object
   *
   * @param theSource Description of Parameter
   */
  public AnimatorMessageAdapter(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Gets the OSType attribute of the AnimatorMessageAdapter object
   *
   * @return The OSType value
   */
  public String getOSType()
  {
    return "OS";
  }

  /**
   * Description of the Method
   *
   * @param myPostOffice Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean forPostOffice(PostOffice myPostOffice)
  {
    return (myPostOffice.getId().compareTo(RCOS.ANIMATOR_POST_OFFICE_ID) == 0);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(CPUAnimator theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(DiskSchedulerAnimator theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(FileSystemAnimator theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(IPCManagerAnimator theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(MultimediaAnimator theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessManagerAnimator theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessSchedulerAnimator theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProgramManagerAnimator theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(TerminalManagerAnimator theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return true;
  }
}
