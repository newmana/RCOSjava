// *************************************************************************
// FILE     : FileServer.java
// PACKAGE  : Disk
// PURPOSE  : Provide a network server to enable remote clients to access
//            Disk resources on the machine the server is running on.
// AUTHOR   : Brett Carter
// MODIFIED : Andrew Newman
// HISTORY  : 19/01/96 Created.
//            12/01/97 Moved to Disk package and bugs fixed. AN
//            1/1/98   Removed sun.net.*.  Rewritten for JDK 1.1. AN
//
// *************************************************************************

package PLL2;

import java.io.*;
import java.net.*;

public class FileServer
{
  private static String sExecutableRoot;
  private static String sRecorderRoot;
  private static int iServerPort;
  private ServerSocket ssktServer;
  private Socket sktConnection;
  private DataInputStream disInStream;
  private DataOutputStream dosOutStream;
  private FileMessages fmMessenger;

  public FileServer()
  {
    serviceRequests();
  }

  public static void main(String args[])
  {
    if (args.length != 3)
    {
      System.err.println(" Correct usage is : fileserver <exe directory> <rec directory> <port>");
      System.err.println(" Where:");
      System.err.println("     <exe directory> is the directory containing compiled PLL code,");
      System.err.println("     <rec directory> is the directory containing recorder XML files, and");
      System.err.println("     <port> is the port to listen on.");
      return;
    }
    sExecutableRoot = args[0];
    sRecorderRoot = args[1];
    try
    {
      iServerPort = (new Integer(args[2])).intValue();
    }
    catch (Exception e)
    {
      System.err.println("Error in Server port!");
      return;
    }
    new FileServer();
  }

  // Handle a single connection from a client.
  private void serviceRequests()
  {
    // Start the server.
    System.out.println("Starting up server on port " + this.iServerPort + "...");
    try
    {
      ssktServer = new ServerSocket(this.iServerPort);
      System.out.println("Server ready...");
    }
    catch (IOException exception)
    {
      System.out.println("Unable to start server on port " + this.iServerPort);
      return;
    }
    // Use a while loop to process commands. Wrap it in a try loop so that
    // If it fails, the disconnection is standard.
    try
    {
      while (true)
      {
        sktConnection = ssktServer.accept();
        System.out.println("Client "+
          sktConnection.getInetAddress().getHostName() + " has connected...");
        // Get Input Stream
        disInStream = new DataInputStream(sktConnection.getInputStream());
        // Set Output Stream
        dosOutStream = new DataOutputStream(sktConnection.getOutputStream());
        fmMessenger = new FileMessages(disInStream, dosOutStream);
        while (fmMessenger.readMessage())
        {
          if (fmMessenger.getLastMessageType()
            .equals(fmMessenger.Q_DIRECTORY_LIST))
          {
            handleGetDirectoryListRequest(fmMessenger.getLastMessageDirectory(),
              fmMessenger.getLastMessageData());
          }
          else if (fmMessenger.getLastMessageType()
            .equals(fmMessenger.Q_READ_FILE_DATA))
          {
            handleGetFileRequest(fmMessenger.getLastMessageDirectory(),
              fmMessenger.getLastMessageData());
          }
          else if (fmMessenger.getLastMessageType()
            .equals(fmMessenger.Q_OPEN_FILE_DATA))
          {
            //handleOpenFileRequest(fmMessenger.getLastMessageDirectory(),
            //fmMessenger.getLastMessageData());
          }
          else if (fmMessenger.getLastMessageType()
            .equals(fmMessenger.Q_WRITE_FILE_DATA))
          {
            //handleWriteFileRequest(fmMessenger.getLastMessageDirectory(),
            //fmMessenger.getLastMessageData());
          }
          else if (fmMessenger.getLastMessageType()
            .equals(fmMessenger.Q_CLOSE_FILE_DATA))
          {
            //handleCloseFileRequest(fmMessenger.getLastMessageDirectory(),
            //fmMessenger.getLastMessageData());
          }
          else if (fmMessenger.getLastMessageType()
            .equals(fmMessenger.Q_FILE_STATS))
          {
            handleStatFileRequest(fmMessenger.getLastMessageDirectory(),
              fmMessenger.getLastMessageData());
          }
          else if (fmMessenger.getLastMessageType()
            .equals(fmMessenger.Q_HANGUP))
          {
            System.out.println("Client " +
              sktConnection.getInetAddress().getHostName() +
              " has disconnected...");
            dosOutStream.close();
            disInStream.close();
            sktConnection.close();
            break;
          }
          else
          {
            fmMessenger.replyInvalidCommandMessage();
          }
        }
      }
    }
    catch (IOException theException)
    {
      System.out.println("IO Error encountered: " + theException);
    }
  }

  // To correctly handle a GETDIRECTORYLIST command from
  // the client.
  private void handleGetDirectoryListRequest(int iDirectory, String sDirectory)
  {
    // Setup variables.
    String[] sDirectoryList;
    File fDirectory;
    if (iDirectory == 1)
      sDirectory = sExecutableRoot + sDirectory;
    else if (iDirectory == 2)
      sDirectory = sRecorderRoot + sDirectory;
    // Setup the directory File object.
    fDirectory = new File(sDirectory);
    // Check that the file exists and is a directory.
    if (!fDirectory.isDirectory())
    {
      fmMessenger.replyDirectoryDoesNotExistMessage();
      return;
    }
    // Read the directory
    sDirectoryList = fDirectory.list();
    int iCounter;
    // Send the data to the client.
    for (iCounter = 0; iCounter < sDirectoryList.length; iCounter++)
    {
      // Put a trailing "/" on directories for identification.
      File tmpFile = new File(fDirectory.getAbsolutePath() + "/" + sDirectoryList[iCounter]);
      if (tmpFile.isDirectory())
      {
        sDirectoryList[iCounter]  = sDirectoryList[iCounter] + "/";
      }
    }
    fmMessenger.replyDirectoryListing(iDirectory, sDirectoryList);
  }

  // To correctly handle a GETFILE command from
  // the client.
  private void handleGetFileRequest(int iDirectory, String sFileName)
  {
    // Setup variables.
    FileInputStream fisFile;
    DataInputStream disFile;
    int iSize;
    byte[] bFileData;
    if (iDirectory == 1)
      sFileName = sExecutableRoot + sFileName;
    else if (iDirectory == 2)
      sFileName = sRecorderRoot + sFileName;
    // Open the file and throw an error if it takes exception to it.
    try
    {
      fisFile = new FileInputStream(sFileName);
    }
    catch (FileNotFoundException exception)
    {
      fmMessenger.replyFileDoesNotExistMessage();
      return;
    }
    // Setup the necessary streams and read the file contents.
    disFile = new DataInputStream(fisFile);
    try
    {
      iSize = disFile.available();
      bFileData = new byte[iSize];
      disFile.readFully(bFileData, 0, iSize);
      disFile.close();
    }
    catch (IOException theException)
    {
      iSize = 0;
      bFileData = null;
    }
    fmMessenger.replyLoadFileData(iDirectory, bFileData);
  }

  // To correctly handle a STATFILE command from
  // the client.
  private void handleStatFileRequest(int iDirectory, String sFileName)
  {
    // Setup variables.
    FileInputStream fisFile;
    DataInputStream disFile;
    int iSize;
    if (iDirectory == 1)
      sFileName = sExecutableRoot + sFileName;
    else if (iDirectory == 2)
      sFileName = sRecorderRoot + sFileName;
    // Open the file and throw an error if it takes exception to it.
    try
    {
      fisFile = new FileInputStream (sFileName);
    }
    catch (FileNotFoundException exception)
    {
      fmMessenger.replyFileDoesNotExistMessage();
      return;
    }
    // Setup the necessary streams and read the file contents.
    disFile = new DataInputStream(fisFile);
    try
    {
      iSize = disFile.available();
      disFile.close();
    }
    catch (IOException theException)
    {
      iSize = 0;
    }
    fmMessenger.replyFileStat(iDirectory, iSize);
  }
}