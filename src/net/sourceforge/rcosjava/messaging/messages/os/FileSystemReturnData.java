//*******************************************************************/
// FILENAME : FileSystemReturnData.java
// PACKAGE  : FileSystem
// PURPOSE  : A container for data being passed to the
//            FileSystemManager
//            containg the return information
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 28/3/96 Created.
//
//*******************************************************************/

package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

public class FileSystemReturnData extends OSMessageAdapter
{
  private int fileSystemRequestId;
  private int returnValue;

  public FileSystemReturnData(OSMessageHandler theSource,
    int newFileSystemRequestId, int newReturnValue)
  {
    super(theSource);
    fileSystemRequestId = newFileSystemRequestId;
    returnValue = newReturnValue;
  }

  public FileSystemReturnData(OSMessageHandler theSource)
  {
    super(theSource);
    fileSystemRequestId = -1;
    returnValue = -1;
  }

  public int getFSMRequestID()
  {
    return fileSystemRequestId;
  }

  public void setFSMRequestID(int newFileSystemRequestId)
  {
    fileSystemRequestId = newFileSystemRequestId;
  }

  public int getReturnValue()
  {
    return returnValue;
  }

  public void setReturnValue(int newReturnValue)
  {
    returnValue = newReturnValue;
  }
}