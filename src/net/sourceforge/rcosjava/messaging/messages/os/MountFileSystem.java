package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.filesystem.FileSystemManager;
import net.sourceforge.rcosjava.software.filesystem.cpm14.CPM14FileSystem;
import net.sourceforge.rcosjava.software.animator.filesystem.FileSystemAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * TBD
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 8th of August 1998
 */
public class MountFileSystem extends OSMessageAdapter
{
  private String fileSystemName;
  private String mountPoint;
  private String deviceName;

  public MountFileSystem(OSMessageHandler theSource, String newFileSystemName,
    String newMountPoint, String newDeviceName)
  {
    super(theSource);
    fileSystemName = newFileSystemName;
    mountPoint = newMountPoint;
    deviceName = newDeviceName;
  }

  public void setMountType(String newFileSystemName, String newMountPoint,
    String newDeviceName)
  {
    fileSystemName = newFileSystemName;
    mountPoint = newMountPoint;
    deviceName = newDeviceName;
  }

  public void doMessage(FileSystemManager theElement)
  {
    theElement.mount(fileSystemName, mountPoint, deviceName);
  }
}
