package MessageSystem.Messages.Animator;

import Software.Animator.CPU.CPUAnimator;
import Software.Animator.Disk.DiskSchedulerAnimator;
import Software.Animator.FileSystem.FileSystemAnimator;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Animator.Multimedia.MultimediaAnimator;
import Software.Animator.Process.ProcessManagerAnimator;
import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Animator.Process.ProgramManagerAnimator;
import Software.Animator.Terminal.TerminalManagerAnimator;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.PostOffices.PostOffice;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;
import RCOS;
import java.io.Serializable;

/**
 * AnimatorMessageAdapter which implements AnimatorMessage.
 * <P>
 * HISTORY: 03/07/98 Used double dispatch
 *
 * @author Andrew Newman
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public class AnimatorMessageAdapter extends MessageAdapter
  implements AnimatorMessage, Serializable
{
  public AnimatorMessageAdapter()
  {
    super();
  }

  public AnimatorMessageAdapter(AnimatorMessageHandler theSource)
  {
    super(theSource);
  }

  public String getOSType()
  {
    return "OS";
  }

  public boolean forPostOffice(PostOffice myPostOffice)
  {
    return (myPostOffice.getId().compareTo(RCOS.animatorPostOfficeId) == 0);
  }

  public void doMessage(CPUAnimator theElement)
  {
  }

  public void doMessage(DiskSchedulerAnimator theElement)
  {
  }

  public void doMessage(FileSystemAnimator theElement)
  {
  }

  public void doMessage(IPCManagerAnimator theElement)
  {
  }

  public void doMessage(MultimediaAnimator theElement)
  {
  }

  public void doMessage(ProcessManagerAnimator theElement)
  {
  }

  public void doMessage(ProcessSchedulerAnimator theElement)
  {
  }

  public void doMessage(ProgramManagerAnimator theElement)
  {
  }

  public void doMessage(TerminalManagerAnimator theElement)
  {
  }
}