package org.rcosjava.software.process;

import java.awt.*;
import java.lang.Thread;
import java.io.*;
import java.net.*;

import org.rcosjava.hardware.cpu.Interrupt;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.messaging.messages.os.HandleInterrupt;
import org.rcosjava.messaging.messages.os.RegisterInterruptHandler;
import org.rcosjava.messaging.messages.universal.KillProcess;
import org.rcosjava.messaging.messages.universal.NewProcess;
import org.rcosjava.messaging.messages.universal.UpdateFileList;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.pll2.FileClient;
import org.rcosjava.software.interrupt.InterruptHandler;
import org.rcosjava.software.interrupt.ProgManInterruptHandler;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.process.RCOSProcess;
import org.rcosjava.software.util.FIFOQueue;

/**
 * To communicate via messages and via awt Events with the main RCOS frame and
 * other components. It's functions are to set up and comunicate with a remote
 * server for the loading of programs.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 31/03/96 Added fifoQueue for filenames. DJ </DD>
 * <DD> 30/12/96 Rewrote manager with frame moved to Animators. AN </DD>
 * <DD> 01/01/97 Changed it to be a client. No tracking here at all. AN </DD>
 * <DD> 15/01/97 Added ability to kill processes, step and run. AN </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @author Brett Carter
 * @author David Jones
 * @created 19th March 1996
 * @version 1.00 $Date$
 */
public class ProgramManager extends OSMessageHandler
{
  /**
   * The name of the manager to register as with the post office.
   */
  private final static String MESSENGING_ID = "ProgramManager";

  /**
   * The file client which is used to talk to the server to load files, etc.
   */
  private transient FileClient theFileClient;

  /**
   * A simple list of strings of all the available directories excluding . and
   * ..
   */
  private FIFOQueue directoryList;

  /**
   * A simple list of string of all the file in the current directory.
   */
  private FIFOQueue fileList;

  /**
   * The interrupt handler used to handle all the program manager interrupts.
   */
  private ProgManInterruptHandler theIH;

  /**
   * A reference to the kernel.
   */
  private Kernel myKernel;

  /**
   * The name of the currently selected file.
   */
  private String fileName;

  /**
   * The host to connect to.
   */
  private String host;

  /**
   * The port to connect to.
   */
  private int port;

  /**
   * Sets up internal data and registers with the PostOffice.
   *
   * @param newPostOffice the post office to register to.
   * @param newHost the host that this should connect to.
   * @param newPort the port to connect to.
   * @param newKernel link to RCOS to control some of the threading stuff
   *      directly.
   */
  public ProgramManager(OSOffice newPostOffice, String newHost, int newPort,
      Kernel newKernel)
  {
    super(MESSENGING_ID, newPostOffice);

    host = newHost;
    port = newPort;
    myKernel = newKernel;

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
   * Sets the filename to load.
   *
   * @param newFilename The filename to load.
   */
  public void setNewFilename(String newFilename)
  {
    fileName = newFilename;
  }

  /**
   * Returns the file name to load or has loaded.
   *
   * @return the file name to load or has loaded.
   */
  public String getNewFilename()
  {
    return fileName;
  }

  /**
   * Gets the file's size in bytes for a given file name.
   *
   * @param fileName file name to read.
   * @return the file size. This method will return -1 if no file was selected.
   */
  public int getFileSize(String fileName)
  {
    return theFileClient.statExeFile(fileName);
  }

  /**
   * Gets the file's contents for a given file name.
   *
   * @param fileName file name to read.
   * @return a Memory object containing the file data.
   */
  public Memory getFileContents(String fileName)
  {
    return theFileClient.getExeFile(fileName);
  }

  /**
   * Start loading and creating a new process based on a file name of an
   * executable.
   *
   * @param filename Description of Parameter
   */
  public void newFile(String fileName)
  {
    setNewFilename(fileName);
    // Create a new message body to send to Process Scheduler.
    // Contains file information and code
    open();

    Memory fileContents = getFileContents(fileName);
    int fileSize = getFileSize(fileName);

    close();

    //Send the message with the given cotents.
    NewProcess newMsg = new NewProcess(this, fileName, fileContents, fileSize);
    sendMessage(newMsg);

    Interrupt intInterrupt = new Interrupt(-1, "NewProcess");
    HandleInterrupt newMsg2 = new HandleInterrupt(this, intInterrupt);
    sendMessage(newMsg2);
  }

  /**
   * Start the program execution thread.
   */
  public void startThread()
  {
    myKernel.unpause();
  }

  /**
   * Step the program execution thread one place forward.
   */
  public void stopThread()
  {
    myKernel.pause();
    //myKernel.processStep();
  }

  /**
   * Send a message to the kernel to kill the given process.
   *
   * @param pid the process id to kill.
   */
  public void kill(int pid)
  {
    // send message to Kernel with Process number
    KillProcess newMsg = new KillProcess(this, new RCOSProcess(pid, ""));
    sendMessage(newMsg);
  }

  /**
   * Open a connection using the file client.
   *
   * @return Description of the Returned Value
   */
  public boolean open()
  {
    return theFileClient.openConnection();
  }

  /**
   * Close the connection using the file client.
   */
  public void close()
  {
    theFileClient.closeConnection();
  }

  /**
   * Handle the creation of non-serializable components.
   *
   * @param is stream that is being read.
   */
  private void readObject(ObjectInputStream is) throws IOException,
      ClassNotFoundException
  {
    // Deserialize the document
    is.defaultReadObject();

    // Create new connection
    theFileClient = new FileClient(host, port);
  }

  /**
   * Update the directory list.
   *
   * @param directoryName Description of Parameter
   * @param directoryType Description of Parameter
   */
  public void updateList(String directoryName, int directoryType)
  {
    String[] dataArray;

    directoryList = new FIFOQueue(10, 1);
    fileList = new FIFOQueue(10, 1);

    // Gets listing of the current directory.
    // Current directory is the messages body.
    if (open())
    {
      if (directoryType == 1)
      {
        dataArray = theFileClient.getExeDir(directoryName);
      }
      else
      {
        dataArray = theFileClient.getRecDir();
      }
      if (dataArray != null)
      {
        if ((directoryName.compareTo(java.io.File.separator) != 0) &&
            (directoryType == 1))
        {
          directoryList.insert(".");
          directoryList.insert("..");
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
//      RCOS.updateStatusBar("Error! Unable to connect to server.");
    }
  }
}
