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
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;

/**
 * Interface for all animator messages.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 03/07/1998   Used double dispatch
 * </DD></DT>
 * <P>
 * HISTORY:
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