package org.rcosjava.pll2;

import java.io.*;
import java.net.*;

/**
 * Provide a network server to enable remote clients to access. Disk resources
 * on the machine the server is running on.
 * <P>
 * HISTORY: 12/01/1997 Moved to Disk package and bugs fixed. AN<BR>
 * 01/01/1998 Removed sun.net.*. Rewritten for JDK 1.1. AN<BR>
 * 11/03/2001 Fixed write problems with sync mehotd. AN<BR>
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @created 19th January 1996
 * @version 1.00 $Date$
 */
public class FileServer
{
  /**
   * The location of the root file system of all executable (PLL) code.
   */
  private static String executableRoot;

  /**
   * The location of the root file system of all the recorded (XML) files.
   */
  private static String recorderRoot;

  /**
   * The port on which to listen on.
   */
  private static int serverPortNumber;

  /**
   * Server socket to listen on.
   */
  private ServerSocket fileServerSocket;

  /**
   * Socket to respond to all requests with.
   */
  private Socket fileServerConnection;

  /**
   * Contains a list of all the message to be used between client and server.
   */
  private FileMessages fileMessage;

  /**
   * Default constructor simply calls serviceRequests().
   */
  public FileServer()
  {
    serviceRequests();
  }

  /**
   * Checks to see that the arguments are correct and then calls new
   * FileServer().
   *
   * @param args Description of Parameter
   */
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

  /**
   * Description of the Method
   *
   * @param directory Description of Parameter
   */
  public synchronized void handleCreateDir(String directory)
  {
    boolean success = false;
    File tmpDirectory = new File(directory);

    success = tmpDirectory.mkdir();
    if (success)
    {
      fileMessage.replyDirectoryCreated(0);
    }
    else
    {
      fileMessage.replyCannotCreateDirectory("");
    }
  }

  /**
   * Handles a single connection from a client.
   */
  private synchronized void serviceRequests()
  {
    // Start the server.
    System.out.println("Starting up server on port " + this.serverPortNumber + "..");
    try
    {
      fileServerSocket = new ServerSocket(this.serverPortNumber);
      System.out.println("Server ready..");
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
        System.out.println("Client " +
            fileServerConnection.getInetAddress().getHostName() + " has connected..");
        // Get Input Stream
        BufferedInputStream inputStream =
            new BufferedInputStream(fileServerConnection.getInputStream());
        // Set Output Stream
        BufferedOutputStream outputStream =
            new BufferedOutputStream(fileServerConnection.getOutputStream());
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
      System.err.println("IO Error encountered: " + theException);
    }
  }

  /**
   * Each message is handled individually.
   *
   * @return Description of the Returned Value
   * @exception IOException Description of Exception
   */
  private synchronized boolean handleMessage()
    throws IOException
  {
    fileMessage.readMessage();

    String path = fileMessage.getLastMessageData();
    //Only used for file data
    String outputData = "";

    if (fileMessage.getLastMessageType().equals(fileMessage.Q_WRITE_FILE_DATA))
    {
      outputData = path.substring(path.indexOf(FileMessages.spacer) + 1, path.length());
      path = path.substring(0, path.indexOf(FileMessages.spacer));
    }

    int directoryIndicator = fileMessage.getLastMessageDirectory();

    if (directoryIndicator == 1)
    {
      path = executableRoot + path;
    }
    else if (directoryIndicator == 2)
    {
      path = recorderRoot + path;
    }

//    System.out.println("Type: " + fileMessage.getLastMessageType());
//    System.out.println("Size: " + fileMessage.getLastMessageSize());
//    System.out.println("Data: " + fileMessage.getLastMessageData());

    if (fileMessage.getLastMessageType()
        .equals(fileMessage.Q_DIRECTORY_LIST))
    {
      handleGetDirectoryListRequest(path);
    }
    else if (fileMessage.getLastMessageType()
        .equals(fileMessage.Q_DIRECTORY_CREATE))
    {
      handleCreateDir(path);
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
          " has disconnected..");
      fileServerConnection.close();
      return false;
    }
    else
    {
      fileMessage.replyInvalidCommandMessage();
    }
    return true;
  }

  /**
   * To correctly handle a GETDIRECTORYLIST command from the client.
   *
   * @param directoryPath Description of Parameter
   */
  private synchronized void handleGetDirectoryListRequest(String directoryPath)
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
      // Put a trailing file separator on directories for identification.
      File tmpFile = new File(theDirectory.getAbsolutePath() +
          java.io.File.separatorChar + theDirectoryList[counter]);

      if (tmpFile.isDirectory())
      {
        theDirectoryList[counter] = theDirectoryList[counter] +
            java.io.File.separatorChar;
      }
    }
    fileMessage.replyDirectoryListing(theDirectoryList);
  }

  /**
   * To correctly handle a GETFILE command from the client.
   *
   * @param filename Description of Parameter
   */
  private synchronized void handleGetFileRequest(String filename)
  {
    // Setup variables.
    FileInputStream inputFile;
    BufferedInputStream inputStream;
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
    inputStream = new BufferedInputStream(inputFile);
    try
    {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[4096];
      int bytesRead;

      while ((bytesRead = inputStream.read(buffer)) != -1)
      {
        outputStream.write(buffer, 0, bytesRead);
      }

      inputStream.close();
      outputStream.close();

      fileData = outputStream.toByteArray();
      fileSize = fileData.length;
    }
    catch (IOException theException)
    {
      theException.printStackTrace();
      fileSize = 0;
      fileData = null;
    }
    fileMessage.replyLoadFileData(fileData);
  }

  /**
   * Assumes that if the file exists it will append to the existing one.
   *
   * @param filename Description of Parameter
   * @param outputData Description of Parameter
   */
  private synchronized void handleWriteFileRequest(String filename,
      String outputData)
  {
    // Open a file and write to it.
    try
    {
      BufferedWriter outputFile = new BufferedWriter(new FileWriter(filename));
      outputFile.write(outputData);
      outputFile.flush();
      outputFile.close();
    }
    catch (IOException theException)
    {
      fileMessage.replyCannotAccessFileMessage(filename);
      return;
    }
    fileMessage.replyWriteFileData();
  }

  /**
   * To correctly handle a STATFILE command from the client.
   *
   * @param filename Description of Parameter
   */
  private synchronized void handleStatFileRequest(String filename)
  {
    // Setup variables.
    File file = new File(filename);

    // Check if file exists - if not return error.
    if (!file.exists())
    {
      fileMessage.replyFileDoesNotExistMessage(filename);
      return;
    }

    // If the file exists get the length and return it.
    fileMessage.replyFileStat(file.length());
  }
}
