package MessageSystem.Messages.OS;

import java.lang.Object;
import Software.Disk.DiskScheduler;
import Software.FileSystem.FileSystemManager;
import Software.IPC.IPC;
import Software.Kernel.Kernel;
import Software.Memory.MemoryManager;
import MessageSystem.Messages.MessageAdapter;
import Software.Animator.RCOSFrame;
import Software.Process.ProcessScheduler;
import Software.Process.ProgramManager;
import RCOS;
import Software.Terminal.SoftwareTerminal;
import Software.Terminal.TerminalManager;
import Software.Disk.DiskManager;
import MessageSystem.PostOffices.MessageHandler;
import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.PostOffice;
import Software.Animator.Process.StartProgram;
import java.io.Serializable;

/**
 * An adapter for OSMessage.  This implements the OSMessage interface but with
 * null methods.  This is to prevent extending classes from having to implement
 * each method.
 * <P>
 *
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1997
 **/
 public class OSMessageAdapter extends MessageAdapter
  implements OSMessage, Serializable
{
  /**
   * Calls the MessageAdapters constructor.
   *
   * @theSource the object from which the object is from.
   */
  public OSMessageAdapter(OSMessageHandler theSource)
  {
    super((SimpleMessageHandler) theSource);
  }

  /**
   * Returns true if the post office is of the right id as the operating system
   * post office in RCOS.
   *
   * @param myPostOffice the post office to check against.
   */
  public boolean forPostOffice(PostOffice myPostOffice)
  {
    return (myPostOffice.getId().compareTo(RCOS.osPostOfficeId) == 0);
  }

  /**
   * Will return the a string representation of the Animator type.
   */
  public String getAnimatorType()
  {
    //This depends on the location of the message in the class.
    //Change the class, change how it returns the AnimatorMessage type.
    return ("Animator." + super.getType());
  }

  /**
   * Execute the command based on the disk scheduler.
   */
  public void doMessage(DiskScheduler theElement)
  {
  }

  /**
   * Execute the command on the file system manager.
   */
  public void doMessage(FileSystemManager theElement)
  {
  }

  /**
   * Execute the command on the ipc.
   */
  public void doMessage(IPC theElement)
  {
  }

  /**
   * Execute the command on the kernel.
   */
  public void doMessage(Kernel theElement)
  {
  }

  /**
   * Execute the command on the memory manager.
   */
  public void doMessage(MemoryManager theElement)
  {
  }

  /**
   * Execute the command on the process scheduler.
   */
  public void doMessage(ProcessScheduler theElement)
  {
  }

  /**
   * Execute the command on the program manager.
   */
  public void doMessage(ProgramManager theElement)
  {
  }

  /**
   * Execute the command on the software terminal.
   */
  public void doMessage(SoftwareTerminal theElement)
  {
  }

  /**
   * Execute the command on the terminal manager.
   */
  public void doMessage(TerminalManager theElement)
  {
  }

  /**
   * Execute the command on the disk manager.
   */
  public void doMessage(DiskManager theElement)
  {
  }
}