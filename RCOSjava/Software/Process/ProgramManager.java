// *************************************************************************
// FILE     : ProgramManager.java
// PACKAGE  : Process
// PURPOSE  : To communicate via messages and via awt Events with the main
//            RCOS frame and other components. It's functions are to set up
//            and comunicate with a remote server for the loading of
//            programs.
// AUTHOR   : Brett Carter
// MODIFIED : David Jones, Andrew Newman
// HISTORY  : 19/03/96 Created. BC
//          : 31/03/96 Added fifoQueue for filenames. DJ
//          : 30/12/96 Rewrote manager with frame moved to Animators. AN
//          : 01/01/97 Changed it to be a client.  No tracking here at all. AN
//          : 15/01/97 Added ability to kill processes, step and run. AN
//
// *************************************************************************

package Software.Process;

import java.awt.*;
import java.net.*;
import java.lang.Thread;
import RCOS;
import MessageSystem.*;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.OS.RegisterInterruptHandler;
import MessageSystem.Messages.Universal.UpdateFileList;
import MessageSystem.Messages.Universal.KillProcess;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.PostOffices.MessageHandler;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import Hardware.CPU.Interrupt;
import PLL2.FileClient;
import Software.Interrupt.InterruptHandler;
import Software.Interrupt.ProgManInterruptHandler;
import Hardware.Memory.*;
import Software.Memory.*;
import Software.Util.*;
import RCOS;

public class ProgramManager extends OSMessageHandler
{
  // Variables
  private FileClient theFileClient;
  private FIFOQueue directoryList;
  private FIFOQueue fileList;
  private ProgManInterruptHandler theIH;
  private RCOS myRCOS;
  private String sNewFilename;
	private static final String MESSENGING_ID = "ProgramManager";

  // Parameters  : String myid - the ID of this object as registered with
  //                             the PO.
  //               MessageHandler aPostOffice - the Post Office object.
  //               String host  - The URL.
  // Return Type : none
  // Description : Sets up internal data and registers with the PostOffice.
  public ProgramManager(OSOffice aPostOffice, String host,
                        int port, RCOS thisRCOS)
  {
    super(MESSENGING_ID, aPostOffice);
    myRCOS = thisRCOS;

    // Create a new client to get files and directory
    // information from the server.

    theFileClient = new FileClient(host, port);

    // create and register InterruptHandler
    theIH = new ProgManInterruptHandler("ProgManInterruptHandler", aPostOffice,
              "NewProcess", getID());
    RegisterInterruptHandler newMsg = new
      RegisterInterruptHandler(this, (InterruptHandler) theIH);
    sendMessage(newMsg);
  }

  public String getNewFilename()
  {
    return this.sNewFilename;
  }

  public void setNewFilename(String sFilename)
  {
    this.sNewFilename = sFilename;
  }

  public synchronized void processMessage(OSMessageAdapter aMsg )
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing: "+e.getMessage());
      e.printStackTrace();
    }
  }

	public synchronized void processMessage(UniversalMessageAdapter aMsg )
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing: "+e.getMessage());
      e.printStackTrace();
    }
  }

  public void startThread()
  {
    myRCOS.startThread();
  }

  public void stepThread()
  {
    myRCOS.stepThread();
  }

  public void killProgram(int iProcess)
  {
    // send message to Kernel with Process number
    KillProcess newMsg = new KillProcess(this,iProcess,false);
    sendMessage(newMsg);
  }

  public synchronized boolean open()
  {
    return theFileClient.openConnection();
  }

  // Returns the file size. This method will return -1 if no
  // file was selected.
  public synchronized int getFileSize(String theFileName)
  {
    return theFileClient.statExeFile(theFileName);
  }

  // This function returns a Memory object containing the file data.
  public synchronized Memory getFileContents(String theFileName)
  {
    return theFileClient.getExeFile(theFileName);
  }

  public synchronized void close()
  {
    theFileClient.closeConnection();
  }

  public synchronized void updateList(String sDirectoryName)
  {
    String[] sDataArray;
    directoryList = new FIFOQueue(10,1);
    fileList = new FIFOQueue(10,1);

    // Gets listing of the current directory.
    // Current directory is the messages body.
    if (open())
    {
      sDataArray = theFileClient.getExeDir(sDirectoryName);
      if (sDataArray != null)
      {
        if (sDirectoryName.compareTo("/") != 0)
        {
          directoryList.insert(new String("."));
          directoryList.insert(new String(".."));
        }

        for (int counter = 0; counter < sDataArray.length; counter++)
        {
          if (sDataArray[counter].endsWith("/"))
          {
            directoryList.insert(sDataArray[counter]);
          }
          else
          {
            fileList.insert(sDataArray[counter]);
          }
        }
      }
      close();
      // Sends updated list to animator.
      UpdateFileList newMsg = new UpdateFileList(this,
        fileList, directoryList);
      sendMessage(newMsg);
    }
    else
    {
      RCOS.updateStatusBar("Error! Unable to connect to server.");
    }
  }
}
