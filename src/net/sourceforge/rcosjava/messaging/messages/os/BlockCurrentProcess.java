package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.filesystem.FileSystemManager;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Sent from various sources to block the current process usually due to I/O
 * bound function (keyboard, disk, etc).
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1998
 */
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

