package org.rcosjava.messaging.messages.animator;

import org.rcosjava.messaging.messages.Message;
import org.rcosjava.software.animator.cpu.CPUAnimator;
import org.rcosjava.software.animator.disk.DiskSchedulerAnimator;
import org.rcosjava.software.animator.filesystem.FileSystemAnimator;
import org.rcosjava.software.animator.ipc.IPCManagerAnimator;
import org.rcosjava.software.animator.memory.MemoryManagerAnimator;
import org.rcosjava.software.animator.multimedia.MultimediaAnimator;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;
import org.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import org.rcosjava.software.animator.process.ProgramManagerAnimator;
import org.rcosjava.software.animator.terminal.TerminalManagerAnimator;

/**
 * Interface for all animator messages.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 03/07/1998 Used double dispatch </DD> </DT>
 * <P>
 * HISTORY:
 * <P>
 * @author Andrew Newman.
 * @created 24th March 1997
 * @version 1.00 $Date$
 */
public abstract interface AnimatorMessage extends Message
{
  /**
   * Gets the OSType attribute of the AnimatorMessage object
   *
   * @return The OSType value
   */
  public abstract String getOSType();

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(CPUAnimator theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(DiskSchedulerAnimator theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(FileSystemAnimator theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(IPCManagerAnimator theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(MemoryManagerAnimator theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(MultimediaAnimator theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(ProcessManagerAnimator theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(ProcessSchedulerAnimator theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(ProgramManagerAnimator theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(TerminalManagerAnimator theElement);
}
