//*************************************************************************//
// FILENAME : FileSystemManager.java
// PACKAGE  : FileSystem
// PURPOSE  : Implement a central point for file system control.
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 25/03/96  Created.
//            01/01/98  Modified properties
//            08/08/98  Added new messaging system support.
//*************************************************************************//
// Mount point seperator is ":" . Thus, C:Hello.pas yields Hello.pas on device
// "C".

package Software.FileSystem;

import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.Messages.OS.BlockCurrentProcess;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.OS.FileSystemRequest;
import Software.Util.*;
import java.util.Hashtable;

public class FileSystemManager extends OSMessageHandler
{
  private IndexedList cvFIDTable;
  private IndexedList cvRequestTable;
  private Hashtable cvFileSystems;  // Indexed on FStype
  private Hashtable cvMountTable;   // indexed on Mount Point

  //public final static String F_REGISTER = "F_REGISTER";
  //public final static String F_MOUNT = "F_MOUNT";
  public final static String F_ALLOC = "F_ALLOC";
  public final static String F_OPEN = "F_OPEN";
  public final static String F_CLOSE = "F_CLOSE";
  public final static String F_EOF = "F_EOF";
  public final static String F_CREATE = "F_CREATE";
  public final static String F_DEL = "F_DEL";
  public final static String F_READ = "F_READ";
  public final static String F_WRITE = "F_WRITE";
	private static final String MESSENGING_ID = "FileSystemManager";

  public FileSystemManager(String myID, OSOffice myPO)
  {
    super(MESSENGING_ID, myPO);
    cvFIDTable = new IndexedList(100, 10);
    cvRequestTable = new IndexedList(100, 10);
    cvFileSystems = new Hashtable(20);
    cvMountTable = new Hashtable(20);
  }

	
  public void processMessage(OSMessageAdapter aMsg)
  {
    try
    {          
      aMsg.doMessage(this);        
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }  
  }

  public void processMessage(UniversalMessageAdapter aMsg)
  {
    try
    {          
      aMsg.doMessage(this);        
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }  
  }

	
  // Registers and dynamically creates a file system with the specified FS type.
  // File System type is the class name of the file system.
  // e.g. FileSystem.CPM14.CPM14FileSystem
  // File System name is the unique identifier of the file system.  e.g. CPM14.
	// One file system name per file system type.
  public void register(String sFSName, String sFSType)
  { 
    if (!cvFileSystems.containsKey(sFSName))
    {
      try
      {
        FileSystem fsNewFileSystem = (FileSystem) Class.forName(sFSType).newInstance();
        cvFileSystems.put(sFSName, fsNewFileSystem);
      }
      catch (Exception e)
      {
        System.out.println("Error registering file system: "+e);
        e.printStackTrace();
      }
    }
    else
    {
      //Report some error message (already exists).
    }
  }

  // Allocats a slot in the mount table and send the mount request.
  // e.g. sFSName - CPM14, sMountPoint - C (drive), sDeviceName - cdrom/Disk1/Orion/Infinity/etc
  public void mount(String sFSName, String sMountPoint, String sDeviceName)
  {
		FileSystem fsExistingFS = (FileSystem) cvFileSystems.get(sFSName);
		
    if (fsExistingFS == null)
    {
      //No filesystem registered under that name
    }
    else
    {
      // Check if that point is already mounted.
      if (!cvMountTable.containsKey(sMountPoint))
      {
        cvMountTable.put(sMountPoint, sFSName);
        fsExistingFS.mount(sMountPoint, sDeviceName);        
      }
      else
      {
        //Mount point already in use
      }
    }
  }
  
  public void allocate(DiskRequestData drdNewRequest)
  {
		FileSystem fsExistingFS = getFSForFile(drdNewRequest.getFileName());
	  if (fsExistingFS == null)
	  {
			replyError(drdNewRequest.getPID());
	  }
	  else
	  {
			int iRequestID = addRequest(drdNewRequest);
			if (iRequestID == -1)
			{
		    replyError(drdNewRequest.getPID());
 				return;
			}
			else
			{
				fsExistingFS.allocate(iRequestID, drdNewRequest.getFileName());
			}
		}
  }

	public void handleRequest(DiskRequestData drdNewRequest)
	{
		FileSystem fsExistingFS = getFSForFile(drdNewRequest.getFileName());
		if (fsExistingFS == null)
	  {
			replyError(drdNewRequest.getPID());
	  }
	  else
	  {
			if (addRequest(drdNewRequest) == -1)
			{
		    replyError(drdNewRequest.getPID());
 				return;
			}
			else
			{
				drdNewRequest.doRequest(fsExistingFS, 1);
			}
		}
	}
	
  // Handles the return value from requests sent to the file systems.
  synchronized void handleReturnValue(OSMessageAdapter mvTheMessage)
  {
    FileSystemReturnData mvReturnData = 
           (FileSystemReturnData) mvTheMessage.getBody();
    RequestTableData mvRequestData = 
           (RequestTableData) cvRequestTable.getItem(mvReturnData.getRequestID());
    if (mvRequestData == null)
    {
      return;
    }
    int mvToReturn;
    // Fill out return message depending upon request.
    if (mvRequestData.getType().equalsIgnoreCase("F_ALLOC"))
    {
      if (mvReturnData.getReturnValue() >= 0)
      {
        FIDTableData mvNewFID = new FIDTableData();
        mvNewFID.setPID(mvRequestData.getPID());
        mvNewFID.setFSID(mvTheMessage.getSource().getID());
        mvNewFID.setFSFileNo(mvReturnData.getReturnValue());
        mvToReturn = cvFIDTable.add(mvNewFID);
      }
      else
      {
        mvToReturn = -1;
      }
    }
    else if (mvRequestData.getType().equalsIgnoreCase("F_CLOSE"))
    {
      if (mvReturnData.getReturnValue() >= 0)
      {

        RequestTableData mvOriginalRequest =
         (RequestTableData)(cvRequestTable.getItem(mvReturnData.getRequestID()));

        //FileSystemReturnData mvOriginalRequestData = 
        //          (FileMessageData)mvOriginalRequest.getData();

        //int mvFID = mvOriginalRequestData.getFID();
				int mvFID = 0;
        cvFIDTable.remove(mvFID);
        mvToReturn = 0;

      }
      else
      {
        mvToReturn = -1;
      }
    }
    else
    {
      mvToReturn = mvReturnData.getReturnValue();
    }
    reply(mvRequestData.getPID(), mvToReturn);
    // Remove request from queue
    int mvCheck = cvRequestTable.remove(mvReturnData.getRequestID());
  }

  // Using Reply, send the ProcessScheduler an error Message.
  private void replyError(int mvPID)
  {
    reply(mvPID, -1);
  }

  // Sends a RETURNVALUE message to the process scheduler with the specified return value.
  private void reply(int mvPID, int mvRetVal)
  {
    FileSystemReturnData mvToReturn = new FileSystemReturnData(mvPID, mvRetVal);

    //MessageAdapter mvReplyMessage = new MessageAdapter(getID(), 
    //  "ProcessScheduler", "SysCallReturnValue", mvToReturn);
    //sendMessage(mvReplyMessage);
  }

  // Returns a the File System that a request should be sent to. This 
  // essentially  breaks down the filename and checks the mount point
  // table. If an entry is found, it returns the name
  // of the filesystem to handle the request.
  public FileSystem getFSForFile(String sFileName)
  {
    // extract mount point from file name. 
    int mvSearchIndex = sFileName.indexOf(":");
    if (mvSearchIndex == -1)
    {
      return null;
    }    
    String mvMountPoint = sFileName.substring(0, mvSearchIndex);
    // Get the file system name based on mount point name
    String mvTheFSName = (String) cvMountTable.get(mvMountPoint);
    // Get file system type based on the name
    FileSystem fsFileSystem = (FileSystem) cvFileSystems.get(mvTheFSName);
    return fsFileSystem;
  }
	
 	// Returns the Files entry stored in the 
  private FIDTableData getTableData(DiskRequestData drdNewRequest)
	{
		// Get FID table entry
    FIDTableData mvFileData = (FIDTableData) cvFIDTable.getItem(drdNewRequest.getFID());
    if (mvFileData == null)
    {
      replyError(drdNewRequest.getPID());
      return null;
    }
    // Check Matching PID's
    if (mvFileData.getPID() != drdNewRequest.getPID())
    {
      replyError(drdNewRequest.getPID());
      return null;
    }
		return mvFileData;
	}
		
  private int addRequest(DiskRequestData drdNewRequest)
  {
    //RequestTableData rtdNewEntry = new RequestTableData(drdNewRequest);
    //return(cvRequestTable.add(rtdNewEntry));
		return 0;
  }
}