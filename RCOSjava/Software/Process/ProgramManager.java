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
import MessageSystem.Messages.OS.HandleInterrupt;
import MessageSystem.PostOffices.MessageHandler;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.Universal.NewProcess;
import Hardware.CPU.Interrupt;
import pll2.FileClient;
import Software.Interrupt.InterruptHandler;
import Software.Interrupt.ProgManInterruptHandler;
import Hardware.Memory.*;
import Software.Memory.*;
import Software.Util.*;
import RCOS;

/**
 * To communicate via messages and via awt Events with the main RCOS frame
 * and other components. It's functions are to set up and comunicate with a
 * remote server for the loading of programs.
 * <P>
 * HISTORY: 31/03/96 Added fifoQueue for filenames. DJ<BR>
 *          30/12/96 Rewrote manager with frame moved to Animators. AN<BR>
 *          01/01/97 Changed it to be a client.  No tracking here at all. AN<BR>
 *          15/01/97 Added ability to kill processes, step and run. AN<BR>
 * <P>
 *
 * @author Andrew Newman
 * @author Brett Carter
 * @author David Jones
 * @created 19th March 1996
 * @version 1.00 $Date$
 */
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

  /**
   * Sets up internal data and registers with the PostOffice.
   *
   * @param newPostOffice the post office to register to.
   * @param host the host that this should connect to.
   * @param port the port to connect to.
   * @param newRCOS link to RCOS to control some of the threading stuff
   * directly.
   */
  public ProgramManager(OSOffice newPostOffice, String host, int port,
    RCOS newRCOS)
  {
    super(MESSENGING_ID, newPostOffice);
    myRCOS = newRCOS;

    // Create a new client to get files and directory
    // information from the server.

    theFileClient = new FileClient(host, port);

    // create and register InterruptHandler
    theIH = new ProgManInterruptHandler("ProgManInterruptHandler",
      newPostOffice, "NewProcess");
    RegisterInterruptHandler newMsg = new
      RegisterInterruptHandler((InterruptHandler) theIH);
    sendMessage(newMsg);
  }

  /**
   * Start loading and creating a new process based on a file name of an
   * executable.
   */
  public void newFile(String filename)
  {
    setNewFilename(filename);
    // Create a new message body to send to Process Scheduler.
    // Contains file information and code
    open();
    NewProcess newMsg = new NewProcess(this, filename,
      getFileContents(filename), getFileSize(filename));
    close();
    sendMessage(newMsg);

    Interrupt intInterrupt = new Interrupt(-1, "NewProcess");
    HandleInterrupt newMsg2 = new HandleInterrupt(
      this, intInterrupt);
    sendMessage(newMsg2);
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

  /**
   * Returns the file size. This method will return -1 if no file was selected.
   */
  public synchronized int getFileSize(String theFileName)
  {
    return theFileClient.statExeFile(theFileName);
  }

  /**
   * This function returns a Memory object containing the file data.
   */
  public synchronized Memory getFileContents(String theFileName)
  {
    return theFileClient.getExeFile(theFileName);
  }

  public synchronized void close()
  {
    theFileClient.closeConnection();
  }

  public synchronized void updateList(String directoryName, int directoryType)
  {
    String[] dataArray;
    directoryList = new FIFOQueue(10,1);
    fileList = new FIFOQueue(10,1);

    // Gets listing of the current directory.
    // Current directory is the messages body.
    if (open())
    {
      if (directoryType == 1)
        dataArray = theFileClient.getExeDir(directoryName);
      else
        dataArray = theFileClient.getRecDir();
      if (dataArray != null)
      {
        if ((directoryName.compareTo(java.io.File.separator) != 0) &&
          (directoryType ==1))
        {
          directoryList.insert(new String("."));
          directoryList.insert(new String(".."));
        }

        for (int counter = 0; counter < dataArray.length; counter++)
        {
          if (dataArray[counter].endsWith(java.io.File.separator))
          {
            directoryList.insert(dataArray[counter]);
          }
          else
          {
            fileList.insert(dataArray[counter]);
          }
        }
      }
      close();
      // Sends updated list to animator.
      UpdateFileList newMsg = new UpdateFileList(this,
        fileList, directoryList, directoryType);
      sendMessage(newMsg);
    }
    else
    {
      RCOS.updateStatusBar("Error! Unable to connect to server.");
    }
  }
}
