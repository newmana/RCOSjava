package net.sourceforge.rcosjava.messaging.messages.os;

import java.io.Serializable;
import net.sourceforge.rcosjava.RCOS;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;
import net.sourceforge.rcosjava.software.disk.DiskManager;
import net.sourceforge.rcosjava.software.disk.DiskScheduler;
import net.sourceforge.rcosjava.software.filesystem.FileSystemManager;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;

/**
 * An adapter for OSMessage.  This implements the OSMessage interface but with
 * null methods.  This is to prevent extending classes from having to implement
 * each method.
 * <P>
 *
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1997
 */
public class OSMessageAdapter extends MessageAdapter
  implements OSMessage, Serializable
{
  /**
   * Calls the MessageAdapters constructor.
   *
   * @theSource the object from which the object is from.
   */
  public OSMessageAdapter(OSMessageHandler newSource)
  {
    super(newSource);
  }

  public OSMessageAdapter(SimpleMessageHandler newSource)
  {
    super(newSource);
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
    //return ("Animator." + super.getType());
    return "Animator";
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

  public boolean undoableMessage()
  {
    return true;
  }
}