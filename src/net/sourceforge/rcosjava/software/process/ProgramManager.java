package net.sourceforge.rcosjava.software.process;

import java.awt.*;
import java.net.*;
import java.lang.Thread;

import net.sourceforge.rcosjava.messaging.*;
import net.sourceforge.rcosjava.messaging.messages.os.*;
import net.sourceforge.rcosjava.messaging.messages.universal.*;
import net.sourceforge.rcosjava.messaging.postoffices.os.*;
import net.sourceforge.rcosjava.messaging.postoffices.*;
import net.sourceforge.rcosjava.messaging.messages.*;
import net.sourceforge.rcosjava.hardware.cpu.Interrupt;
import net.sourceforge.rcosjava.pll2.FileClient;
import net.sourceforge.rcosjava.software.interrupt.*;
import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.hardware.memory.*;
import net.sourceforge.rcosjava.software.memory.*;
import net.sourceforge.rcosjava.software.util.*;

/**
 * To communicate via messages and via awt Events with the main RCOS frame
 * and other components. It's functions are to set up and comunicate with a
 * remote server for the loading of programs.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 31/03/96 Added fifoQueue for filenames. DJ
 * </DD><DD>
 * <DD>
 * 30/12/96 Rewrote manager with frame moved to Animators. AN
 * </DD><DD>
 * <DD>
 * 01/01/97 Changed it to be a client.  No tracking here at all. AN
 * </DD><DD>
 * <DD>
 * 15/01/97 Added ability to kill processes, step and run. AN
 * </DD></DT>
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
   * The file client which is used to talk to the server to load files, etc.
   */
  private FileClient theFileClient;

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
   * The name of the manager to register as with the post office.
   */
  private static final String MESSENGING_ID = "ProgramManager";

  /**
   * Sets up internal data and registers with the PostOffice.
   *
   * @param newPostOffice the post office to register to.
   * @param host the host that this should connect to.
   * @param port the port to connect to.
   * @param newKernel link to RCOS to control some of the threading stuff
   * directly.
   */
  public ProgramManager(OSOffice newPostOffice, String host, int port,
    Kernel newKernel)
  {
    super(MESSENGING_ID, newPostOffice);
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
   * Start loading and creating a new process based on a file name of an
   * executable.
   */
  public void newFile(String filename)
  {
    setNewFilename(filename);
    // Create a new message body to send to Process Scheduler.
    // Contains file information and code
    open();
    Memory fileContents = getFileContents(filename);
    int fileSize = getFileSize(filename);
    close();

    //Send the message with the given cotents.
    NewProcess newMsg = new NewProcess(this, filename, fileContents, fileSize);
    sendMessage(newMsg);

    Interrupt intInterrupt = new Interrupt(-1, "NewProcess");
    HandleInterrupt newMsg2 = new HandleInterrupt(
      this, intInterrupt);
    sendMessage(newMsg2);
  }

  public String getNewFilename()
  {
    return fileName;
  }

  public void setNewFilename(String newFilename)
  {
    fileName = newFilename;
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
    KillProcess newMsg = new KillProcess(this,pid);
    sendMessage(newMsg);
  }

  /**
   * Open a connection using the file client.
   */
  public boolean open()
  {
    return theFileClient.openConnection();
  }

  /**
   * @return the file size. This method will return -1 if no file was selected.
   */
  public int getFileSize(String theFileName)
  {
    return theFileClient.statExeFile(theFileName);
  }

  /**
   * @returns a Memory object containing the file data.
   */
  public Memory getFileContents(String theFileName)
  {
    return theFileClient.getExeFile(theFileName);
  }

  /**
   * Close the connection using the file client.
   */
  public void close()
  {
    theFileClient.closeConnection();
  }

  /**
   * Update the directory list.
   *
   * @param the directory name to list of.
   * @param 1 or 2 for the executable and recording directories respectively.
   */
  public void updateList(String directoryName, int directoryType)
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
          directoryList.insert(new String("."));
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
