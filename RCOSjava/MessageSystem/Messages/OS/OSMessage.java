//******************************************************/
// FILE     : OSMessage.java
// PURPOSE  : Operating System message class
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 03/07/98   Used double dispatch
//******************************************************/

package MessageSystem.Messages.OS;

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

public abstract interface OSMessage extends Message
{
  public abstract String getAnimatorType();
  public abstract void doMessage(DiskScheduler theElement);
  public abstract void doMessage(FileSystemManager theElement);
  public abstract void doMessage(IPC theElement);
  public abstract void doMessage(Kernel theElement);
  public abstract void doMessage(MemoryManager theElement);
  public abstract void doMessage(ProcessScheduler theElement);
  public abstract void doMessage(ProgramManager theElement);
  public abstract void doMessage(SoftwareTerminal theElement);
  public abstract void doMessage(TerminalManager theElement);
}