//***************************************************************************
// FILE    : BlockCurrentProcessMessage.java
// PACKAGE : Terminal
// PURPOSE : Sent from various sources to block the current process
//           usually due to I/O bound function (keyboard, disk, etc).
//
// AUTHOR  : Andrew Newman
// MODIFIED:
// HISTORY : 28/03/96  Created
//
//
//***************************************************************************

package net.sourceforge.rcosjava.messaging.messages.os;


import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.filesystem.FileSystemManager;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class BlockCurrentProcess extends OSMessageAdapter
{
  public BlockCurrentProcess(OSMessageHandler theSource)
  {
    super(theSource);
  }

  public void doMessage(Kernel theElement)
  {
    theElement.blockCurrentProcess();
  }
}

