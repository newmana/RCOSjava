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

package pll2;

import java.io.*;
import java.net.*;

public class FileServer
{
  private static String executableRoot;
  private static String recorderRoot;
  private static int serverPortNumber;
  private ServerSocket fileServerSocket;
  private Socket fileServerConnection;
  private DataInputStream inputStream;
  private DataOutputStream outputStream;
  private FileMessages fileMessage;

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
    executableRoot = args[0];
    recorderRoot = args[1];
    try
    {
      serverPortNumber = (new Integer(args[2])).intValue();
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
    System.out.println("Starting up server on port " + this.serverPortNumber + "...");
    try
    {
      fileServerSocket = new ServerSocket(this.serverPortNumber);
      System.out.println("Server ready...");
    }
    catch (IOException exception)
    {
      System.out.println("Unable to start server on port " + this.serverPortNumber);
      return;
    }
    // Use a while loop to process commands. Wrap it in a try loop so that
    // If it fails, the disconnection is standard.
    try
    {
      while (true)
      {
        fileServerConnection = fileServerSocket.accept();
        System.out.println("Client "+
          fileServerConnection.getInetAddress().getHostName() + " has connected...");
        // Get Input Stream
        inputStream = new DataInputStream(fileServerConnection.getInputStream());
        // Set Output Stream
        outputStream = new DataOutputStream(fileServerConnection.getOutputStream());
        fileMessage = new FileMessages(inputStream, outputStream);
        boolean anotherMessage = true;
        while (anotherMessage)
        {
          anotherMessage = handleMessage();
        }
      }
    }
    catch (IOException theException)
    {
      System.out.println("IO Error encountered: " + theException);
    }
  }

  private boolean handleMessage()
    throws IOException
  {
    fileMessage.readMessage();
    String path = fileMessage.getLastMessageData();
    //Only used for file data
    String outputData = new String();

    if (fileMessage.getLastMessageType().equals(fileMessage.Q_WRITE_FILE_DATA))
    {
      outputData = path.substring(path.indexOf(FileMessages.spacer)+1, path.length());
      path = path.substring(0, path.indexOf(FileMessages.spacer));
    }

    int directoryIndicator = fileMessage.getLastMessageDirectory();
    if (directoryIndicator == 1)
      path = executableRoot + path;
    else if (directoryIndicator == 2)
      path = recorderRoot + path;

//    System.out.println("Type: " + fileMessage.getLastMessageType());
//    System.out.println("Size: " + fileMessage.getLastMessageSize());
//    System.out.println("Date: " + fileMessage.getLastMessageData());

    if (fileMessage.getLastMessageType()
      .equals(fileMessage.Q_DIRECTORY_LIST))
    {
      handleGetDirectoryListRequest(path);
    }
    else if (fileMessage.getLastMessageType()
      .equals(fileMessage.Q_READ_FILE_DATA))
    {
      handleGetFileRequest(path);
    }
    else if (fileMessage.getLastMessageType()
      .equals(fileMessage.Q_WRITE_FILE_DATA))
    {
      handleWriteFileRequest(path, outputData);
    }
    else if (fileMessage.getLastMessageType()
      .equals(fileMessage.Q_FILE_STATS))
    {
      handleStatFileRequest(path);
    }
    else if (fileMessage.getLastMessageType()
      .equals(fileMessage.Q_HANGUP))
    {
      System.out.println("Client " +
        fileServerConnection.getInetAddress().getHostName() +
        " has disconnected...");
      outputStream.close();
      inputStream.close();
      fileServerConnection.close();
      return false;
    }
    else
    {
      fileMessage.replyInvalidCommandMessage();
    }
    return true;
  }

  // To correctly handle a GETDIRECTORYLIST command from
  // the client.
  private void handleGetDirectoryListRequest(String directoryPath)
  {
    // Setup variables.
    String[] theDirectoryList;
    File theDirectory = new File(directoryPath);
    // Check that the file exists and is a directory.
    if (!theDirectory.isDirectory())
    {
      fileMessage.replyDirectoryDoesNotExistMessage(directoryPath);
      return;
    }
    // Read the directory
    theDirectoryList = theDirectory.list();
    int counter;
    // Send the data to the client.
    for (counter = 0; counter < theDirectoryList.length; counter++)
    {
      // Put a trailing "/" on directories for identification.
      File tmpFile = new File(theDirectory.getAbsolutePath() + "/" +
        theDirectoryList[counter]);
      if (tmpFile.isDirectory())
      {
        theDirectoryList[counter]  = theDirectoryList[counter] + "/";
      }
    }
    fileMessage.replyDirectoryListing(theDirectoryList);
  }

  // To correctly handle a GETFILE command from
  // the client.
  private void handleGetFileRequest(String filename)
  {
    // Setup variables.
    FileInputStream inputFile;
    DataInputStream inputStream;
    int fileSize;
    byte[] fileData;
    // Open the file and throw an error if it takes exception to it.
    try
    {
      inputFile = new FileInputStream(filename);
    }
    catch (FileNotFoundException exception)
    {
      fileMessage.replyFileDoesNotExistMessage(filename);
      return;
    }
    // Setup the necessary streams and read the file contents.
    inputStream = new DataInputStream(inputFile);
    try
    {
      fileSize = inputStream.available();
      fileData = new byte[fileSize];
      inputStream.readFully(fileData, 0, fileSize);
      inputStream.close();
    }
    catch (IOException theException)
    {
      fileSize = 0;
      fileData = null;
    }
    fileMessage.replyLoadFileData(fileData);
  }

  //Assumes that if the file exists it will append to the existing one.
  private void handleWriteFileRequest(String filename, String outputData)
  {
    FileOutputStream outputFile;
    DataOutputStream outputStream;

    // Open (with append) the file and throw an error if it takes exception to it.
    try
    {
      outputFile = new FileOutputStream(filename, false);
    }
    catch (FileNotFoundException exception)
    {
      fileMessage.replyFileDoesNotExistMessage(filename);
      return;
    }
    // Setup the necessary streams and read the file contents.
    outputStream = new DataOutputStream(outputFile);
    try
    {
      outputStream.write(outputData.getBytes());
      outputStream.close();
    }
    catch (IOException theException)
    {
      fileMessage.replyCannotAccessFileMessage(filename);
      return;
    }
    fileMessage.replyWriteFileData();
  }

  // To correctly handle a STATFILE command from
  // the client.
  private void handleStatFileRequest(String filename)
  {
    // Setup variables.
    FileInputStream inputFile;
    DataInputStream inputStream;
    // Open the file and throw an error if it takes exception to it.
    try
    {
      inputFile = new FileInputStream(filename);
    }
    catch (FileNotFoundException exception)
    {
      fileMessage.replyFileDoesNotExistMessage(filename);
      return;
    }
    // Setup the necessary streams and read the file contents.
    inputStream = new DataInputStream(inputFile);
    int fileSize;
    try
    {
      fileSize = inputStream.available();
      inputStream.close();
    }
    catch (IOException theException)
    {
      fileSize = 0;
    }
    fileMessage.replyFileStat(fileSize);
  }
}