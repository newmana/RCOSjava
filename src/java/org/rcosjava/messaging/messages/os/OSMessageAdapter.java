package org.rcosjava.messaging.messages.os;

import java.io.Serializable;
import org.rcosjava.RCOS;
import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.postoffices.PostOffice;
import org.rcosjava.messaging.postoffices.SimpleMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;
import org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;
import org.rcosjava.software.disk.DiskManager;
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
 * An adapter for OSMessage. This implements the OSMessage interface but with
 * null methods. This is to prevent extending classes from having to implement
 * each method. <P>
 *
 *
 *
 * @author Andrew Newman.
 * @created 24th March 1997
 * @version 1.00 $Date$
 */
public class OSMessageAdapter extends MessageAdapter
    implements OSMessage, Serializable
{
  /**
   * Calls the MessageAdapters constructor.
   *
   * @param newSource Description of Parameter
   * @theSource the object from which the object is from.
   */
  public OSMessageAdapter(OSMessageHandler newSource)
  {
    super(newSource);
  }

  /**
   * Constructor for the OSMessageAdapter object
   *
   * @param newSource Description of Parameter
   */
  public OSMessageAdapter(SimpleMessageHandler newSource)
  {
    super(newSource);
  }

  /**
   * Will return the a string representation of the Animator type.
   *
   * @return The AnimatorType value
   */
  public String getAnimatorType()
  {
    //This depends on the location of the message in the class.
    //Change the class, change how it returns the AnimatorMessage type.
    //return ("Animator." + super.getType());
    return "Animator";
  }

  /**
   * Returns true if the post office is of the right id as the operating system
   * post office in RCOS.
   *
   * @param myPostOffice the post office to check against.
   * @return Description of the Returned Value
   */
  public boolean forPostOffice(PostOffice myPostOffice)
  {
    return (myPostOffice.getId().compareTo(RCOS.OS_POST_OFFICE_ID) == 0);
  }

  /**
   * Execute the command based on the disk scheduler.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(DiskScheduler theElement)
  {
  }

  /**
   * Execute the command on the file system manager.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(FileSystemManager theElement)
  {
  }

  /**
   * Execute the command on the ipc.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(IPC theElement)
  {
  }

  /**
   * Execute the command on the kernel.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(Kernel theElement)
  {
  }

  /**
   * Execute the command on the memory manager.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(MemoryManager theElement)
  {
  }

  /**
   * Execute the command on the process scheduler.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessScheduler theElement)
  {
  }

  /**
   * Execute the command on the program manager.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProgramManager theElement)
  {
  }

  /**
   * Execute the command on the software terminal.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(SoftwareTerminal theElement)
  {
  }

  /**
   * Execute the command on the terminal manager.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(TerminalManager theElement)
  {
  }

  /**
   * Execute the command on the universal message player.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(UniversalMessagePlayer theElement)
  {
  }

  /**
   * Execute the command on the universal message recorder.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(UniversalMessageRecorder theElement)
  {
  }

  /**
   * Execute the command on the disk manager.
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(DiskManager theElement)
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
