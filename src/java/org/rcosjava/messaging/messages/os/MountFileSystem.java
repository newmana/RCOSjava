package org.rcosjava.messaging.messages.os;import org.rcosjava.messaging.postoffices.os.OSMessageHandler;import org.rcosjava.software.filesystem.FileSystemManager;/** * TBD <P> * * * * @author Andrew Newman. * @created 8th of August 1998 * @version 1.00 $Date$ */public class MountFileSystem extends OSMessageAdapter{  /**   * Description of the Field   */  private String fileSystemName;  /**   * Description of the Field   */  private String mountPoint;  /**   * Description of the Field   */  private String deviceName;  /**   * Constructor for the MountFileSystem object   *   * @param theSource Description of Parameter   * @param newFileSystemName Description of Parameter   * @param newMountPoint Description of Parameter   * @param newDeviceName Description of Parameter   */  public MountFileSystem(OSMessageHandler theSource, String newFileSystemName,      String newMountPoint, String newDeviceName)  {    super(theSource);    fileSystemName = newFileSystemName;    mountPoint = newMountPoint;    deviceName = newDeviceName;  }  /**   * Sets the MountType attribute of the MountFileSystem object   *   * @param newFileSystemName The new MountType value   * @param newMountPoint The new MountType value   * @param newDeviceName The new MountType value   */  public void setMountType(String newFileSystemName, String newMountPoint,      String newDeviceName)  {    fileSystemName = newFileSystemName;    mountPoint = newMountPoint;    deviceName = newDeviceName;  }  /**   * Description of the Method   *   * @param theElement Description of Parameter   */  public void doMessage(FileSystemManager theElement)  {    theElement.mount(fileSystemName, mountPoint, deviceName);  }}