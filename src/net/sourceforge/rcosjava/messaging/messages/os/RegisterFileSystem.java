//***************************************************************************
// FILE    : RegisterFileSystemMessage.java
// PACKAGE : MessageSystem.OS
// PURPOSE :
// AUTHOR  : Andrew Newman
// MODIFIED:
// HISTORY : 08/08/98  Created
//
//***************************************************************************

package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.filesystem.FileSystemManager;
import net.sourceforge.rcosjava.software.animator.filesystem.FileSystemAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class RegisterFileSystem extends OSMessageAdapter
{
  private String sFSType;
  private String sFSName;

  public RegisterFileSystem(OSMessageHandler theSource, String sNewFSType,
    String sNewFSName)
  {
    super(theSource);
    sFSType = sNewFSType;
		sFSName = sNewFSName;
  }

  public void setFileSystemIDs(String sNewFSType, String sNewFSName)
  {
    sFSType = sNewFSType;
    sFSName = sNewFSName;
  }

  public void doMessage(FileSystemManager theElement)
  {
    theElement.register(sFSType, sFSName);
  }
}

