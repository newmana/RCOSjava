//******************************************************/
// FILE     : OSMessageAdapter.java
// PURPOSE  : Adapter for OSMessage
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/03/96   Created
//          : 03/07/98   Used double dispatch
//******************************************************/

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

public class OSMessageAdapter extends MessageAdapter implements OSMessage
{
  public OSMessageAdapter(OSMessageHandler theSource)
  {
    super((SimpleMessageHandler) theSource);
  }

  public boolean forPostOffice(PostOffice myPostOffice)
  {
    return (myPostOffice.getID().compareTo(RCOS.sOSPostOfficeID) == 0);
  }

  public String getAnimatorType()
  {
    //This depends on the location of the message in the class.
    //Change the class, change how it returns the AnimatorMessage type.
    return ("Animator." + super.getType());
  }

  public void doMessage(DiskScheduler theElement)
  {
  }

  public void doMessage(FileSystemManager theElement)
  {
  }

  public void doMessage(IPC theElement)
  {
  }

  public void doMessage(Kernel theElement)
  {
  }

  public void doMessage(MemoryManager theElement)
  {
  }

  public void doMessage(ProcessScheduler theElement)
  {
  }

  public void doMessage(ProgramManager theElement)
  {
  }

  public void doMessage(SoftwareTerminal theElement)
  {
  }

  public void doMessage(TerminalManager theElement)
  {
  }

  public void doMessage(DiskManager theElement)
  {
  }
}