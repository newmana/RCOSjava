//******************************************************/
// FILE     : OSMessage.java
// PURPOSE  : Operating System message class
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
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
import Software.Process.ProcessScheduler;
import Software.Process.ProgramManager;
import Software.Terminal.SoftwareTerminal;
import Software.Terminal.TerminalManager;
import MessageSystem.Messages.Message;

public interface OSMessage extends Message
{
	public String getAnimatorType();
  public void doMessage(DiskScheduler theElement);
  public void doMessage(FileSystemManager theElement);
  public void doMessage(IPC theElement);
  public void doMessage(Kernel theElement);
  public void doMessage(MemoryManager theElement);
  public void doMessage(ProcessScheduler theElement);
  public void doMessage(ProgramManager theElement);
  public void doMessage(SoftwareTerminal theElement);
  public void doMessage(TerminalManager theElement);
}