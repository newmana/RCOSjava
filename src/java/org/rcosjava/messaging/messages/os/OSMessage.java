package org.rcosjava.messaging.messages.os;

import org.rcosjava.messaging.messages.Message;
import org.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;
import org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;
import org.rcosjava.software.disk.DiskScheduler;
import org.rcosjava.software.disk.DiskManager;
import org.rcosjava.software.filesystem.FileSystemManager;
import org.rcosjava.software.ipc.IPC;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.ProgramManager;
import org.rcosjava.software.terminal.SoftwareTerminal;
import org.rcosjava.software.terminal.TerminalManager;

/**
 * Description of the Interface
 *
 * @author administrator
 * @created 28 April 2002
 */
public abstract interface OSMessage extends Message
{
  /**
   * Gets the AnimatorType attribute of the OSMessage object
   *
   * @return The AnimatorType value
   */
  public abstract String getAnimatorType();

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(DiskScheduler theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(FileSystemManager theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(IPC theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(Kernel theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(MemoryManager theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(ProcessScheduler theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(ProgramManager theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(SoftwareTerminal theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(TerminalManager theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(UniversalMessagePlayer theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(UniversalMessageRecorder theElement);

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public abstract void doMessage(DiskManager theElement);
}
