//***************************************************************************
// FILE    : MountFileSystemMessage.java
// PACKAGE : MessageSystem.OS
// PURPOSE : 
// AUTHOR  : Andrew Newman
// MODIFIED: 
// HISTORY : 08/08/98  Created
//***************************************************************************

package MessageSystem.Messages.OS;

import Software.FileSystem.FileSystemManager;
import Software.FileSystem.CPM14.CPM14FileSystem;
import Software.Animator.FileSystem.FileSystemAnimator;
import MessageSystem.PostOffices.OS.OSMessageHandler;

public class MountFileSystem extends OSMessageAdapter
{
  private String sFSName;
  private String sMountPoint;
  private String sDeviceName;

  public MountFileSystem(OSMessageHandler theSource, String sNewFSName,
    String sNewMountPoint, String sNewDeviceName)
  {
    super(theSource);
    sFSName = sNewFSName;
    sMountPoint = sNewMountPoint;
    sDeviceName = sNewDeviceName;
  }
  
  public void setMountType(String sNewFSName, String sNewMountPoint,  
    String sNewDeviceName)
  {
    sFSName = sNewFSName;
    sMountPoint = sNewMountPoint;
    sDeviceName = sNewDeviceName;
  }
  
  public void doMessage(FileSystemManager theElement)
  {
    theElement.mount(sFSName, sMountPoint, sDeviceName);
  }  
}
