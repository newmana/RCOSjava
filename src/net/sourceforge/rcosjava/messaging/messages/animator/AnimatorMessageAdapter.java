package net.sourceforge.rcosjava.messaging.messages.animator;

import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.software.animator.disk.DiskSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.filesystem.FileSystemAnimator;
import net.sourceforge.rcosjava.software.animator.ipc.IPCManagerAnimator;
import net.sourceforge.rcosjava.software.animator.multimedia.MultimediaAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessManagerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.animator.process.ProgramManagerAnimator;
import net.sourceforge.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.RCOS;
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

  public boolean undoableMessage()
  {
    return true;
  }
}