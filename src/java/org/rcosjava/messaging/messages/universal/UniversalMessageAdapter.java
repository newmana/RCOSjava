package org.rcosjava.messaging.messages.universal;
import java.io.Serializable;
import org.rcosjava.RCOS;
import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.messages.animator.AnimatorMessage;
import org.rcosjava.messaging.messages.os.OSMessage;
import org.rcosjava.messaging.postoffices.PostOffice;
import org.rcosjava.messaging.postoffices.SimpleMessageHandler;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.about.AboutAnimator;
import org.rcosjava.software.animator.cpu.CPUAnimator;
import org.rcosjava.software.animator.disk.DiskSchedulerAnimator;
import org.rcosjava.software.animator.filesystem.FileSystemAnimator;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;
import org.rcosjava.software.animator.multimedia.MultimediaAnimator;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.animator.process.ProgramManagerAnimator;
import org.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import org.rcosjava.software.disk.DiskScheduler;
import org.rcosjava.software.filesystem.FileSystemManager;
import org.rcosjava.software.ipc.IPC;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.ProgramManager;
import org.rcosjava.software.terminal.SoftwareTerminal;
import org.rcosjava.software.terminal.TerminalManager;

/**
 * Adapter for OSMessage and AnimatorMessage for messages bound to both OS and
 * Animator. <P>
 *
 * HISTORY: 03/07/98 Used double dispatch<BR>
 * <P>
 *
 *
 *
 * @author Andrew Newman
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public class UniversalMessageAdapter extends MessageAdapter
     implements OSMessage, AnimatorMessage, Serializable
{
  /**
   * Constructor for the UniversalMessageAdapter object
   *
   * @param theSource Description of Parameter
   */
  public UniversalMessageAdapter(OSMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Constructor for the UniversalMessageAdapter object
   *
   * @param theSource Description of Parameter
   */
  public UniversalMessageAdapter(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Constructor for the UniversalMessageAdapter object
   *
   * @param theSource Description of Parameter
   */
  public UniversalMessageAdapter(SimpleMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Gets the AnimatorType attribute of the UniversalMessageAdapter object
   *
   * @return The AnimatorType value
   */
  public String getAnimatorType()
  {
    return "Animator";
  }

  /**
   * Gets the OSType attribute of the UniversalMessageAdapter object
   *
   * @return The OSType value
   */
  public String getOSType()
  {
    //This depends on the location of the message in the class.
    //Change the class, change how it returns the OSMessage type.
    return (getType().substring(getType().indexOf(".") + 1));
  }

  /**
   * Description of the Method
   *
   * @param myPostOffice Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean forPostOffice(PostOffice myPostOffice)
  {
    String postOfficeId = myPostOffice.getId();

    return ((postOfficeId.compareTo(RCOS.ANIMATOR_POST_OFFICE_ID) == 0) ||
        (postOfficeId.compareTo(RCOS.OS_POST_OFFICE_ID) == 0));
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(DiskScheduler theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(FileSystemManager theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(IPC theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(Kernel theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(MemoryManager theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessScheduler theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProgramManager theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(SoftwareTerminal theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(TerminalManager theElement)
  {
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(AboutAnimator theElement)
  {
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
