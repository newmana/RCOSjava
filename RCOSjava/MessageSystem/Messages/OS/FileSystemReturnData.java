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

package MessageSystem.Messages.OS;

import MessageSystem.PostOffices.OS.OSMessageHandler;

public class FileSystemReturnData extends OSMessageAdapter
{
  private int iFSMRequestID;
  private int iReturnValue;

  public FileSystemReturnData(OSMessageHandler theSource,
		int iNewFSMRequestID, int iNewReturnValue)
  {
		super(theSource);
    iFSMRequestID = iNewFSMRequestID;
    iReturnValue = iNewReturnValue;
  }

  public FileSystemReturnData(OSMessageHandler theSource)
  {
		super(theSource);
    iFSMRequestID = -1;
    iReturnValue = -1;
  }

  public int getFSMRequestID()
  {
    return iFSMRequestID;
  }
  
  public void setFSMRequestID(int iNewFSMRequestID)
  {
    iFSMRequestID = iNewFSMRequestID;
  }
  
  public int getReturnValue()
  {
    return iReturnValue;
  }
  
  public void setReturnValue(int iNewReturnValue)
  {
    iReturnValue = iNewReturnValue;
  }
}