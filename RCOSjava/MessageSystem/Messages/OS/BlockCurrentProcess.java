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

package MessageSystem.Messages.OS;

import Software.Kernel.Kernel;
import Software.FileSystem.FileSystemManager;
import Software.Terminal.SoftwareTerminal;
import MessageSystem.PostOffices.OS.OSMessageHandler;

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

