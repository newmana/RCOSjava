package MessageSystem.Messages.Animator;

import java.lang.Object;
import Software.Animator.CPU.CPUAnimator;
import Software.Animator.Disk.DiskSchedulerAnimator;
import Software.Animator.FileSystem.FileSystemAnimator;
import Software.Animator.IPC.IPCManagerAnimator;
import Software.Animator.Multimedia.MultimediaAnimator;
import Software.Animator.Process.ProcessManagerAnimator;
import Software.Animator.Process.ProcessSchedulerAnimator;
import Software.Animator.Process.ProgramManagerAnimator;
import Software.Animator.Terminal.TerminalManagerAnimator;
import MessageSystem.Messages.Message;
import MessageSystem.PostOffices.MessageHandler;

/**
 * Interface for all animator messages.
 * <P>
 * HISTORY:  03/07/1998   Used double dispatch
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1997
 */
public abstract interface AnimatorMessage extends Message
{
  public abstract String getOSType();
  public abstract void doMessage(CPUAnimator theElement);
  public abstract void doMessage(DiskSchedulerAnimator theElement);
  public abstract void doMessage(FileSystemAnimator theElement);
  public abstract void doMessage(IPCManagerAnimator theElement);
  public abstract void doMessage(MultimediaAnimator theElement);
  public abstract void doMessage(ProcessManagerAnimator theElement);
  public abstract void doMessage(ProcessSchedulerAnimator theElement);
  public abstract void doMessage(ProgramManagerAnimator theElement);
  public abstract void doMessage(TerminalManagerAnimator theElement);
}