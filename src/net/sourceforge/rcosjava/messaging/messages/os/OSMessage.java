//******************************************************/
// FILE     : OSMessage.java
// PURPOSE  : Operating System message class
// AUTHOR   : Bruce Jamieson
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 03/07/98   Used double dispatch
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.disk.DiskScheduler;
import net.sourceforge.rcosjava.software.filesystem.FileSystemManager;
import net.sourceforge.rcosjava.software.ipc.IPC;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.messaging.messages.Message;

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