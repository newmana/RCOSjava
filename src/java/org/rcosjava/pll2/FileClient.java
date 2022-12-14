package org.rcosjava.pll2;

import fr.dyade.koala.xml.koml.KOMLDeserializer;
import fr.dyade.koala.xml.koml.KOMLSerializer;
import fr.dyade.koala.xml.sax.*;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

import org.rcosjava.hardware.memory.Memory;

import org.apache.log4j.*;

/**
 * Implementation that will contact a server and communicate with it. The
 * server can fetch file listings or actual files.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 20/01/1996 Updated to break down to method calls. </DD>
 * <DD> 12/01/1997 Moved to Disk package and closed connection gracefully. </DD>
 * <DD> 10/01/198 Rewritten for JDK 1.1. </DD> </DT>
 * <P>
 * @author Andrew Newman (based on code by Brett Carter).
 * @created 19th January 1996
 * @version 1.00 $Date$
 */
public class FileClient
{
  /**
   * Logging class.
   */
  private final static Logger log = Logger.getLogger(FileClient.class);

  /**
   * Connection to the server.
   */
  private Socket socketConnection;

  /**
   * Input stream to read from.
   */
  private InputStream inStream;

  /**
   * Output stream to write to.
   */
  private OutputStream outStream;

  /**
   * A list of standard message utility.
   */
  private FileMessages messages;

  /**
   * The host to connect to.
   */
  private String host;

  /**
   * The port to connect to.
   */
  private int port;

  /**
   * Construct a new file client with the required host and port.
   *
   * @param newHost the host to connect to.
   * @param newPort the port on the host to connect to.
   */
  public FileClient(String newHost, int newPort)
  {
    // Connect to server.
    //System.err.print("Connecting to " + host + "..");
    host = newHost;
    port = newPort;
  }

  /**
   * Construct a new file client with a given URL and port..
   *
   * @param newHost the URL of the host (assumes a known protocol).
   * @param newPort the port to connect to.
   */
  public FileClient(URL newHost, int newPort)
  {
    // Connect to server.
    this(String.valueOf(newHost), newPort);
  }

  /**
   * Retrieves a list of the files in the executable directory from the server.
   *
   * @return An array of strings, one for each file in the list.
   */
  public String[] getExeDir()
  {
    return (getDir(1, java.io.File.separator));
  }

  /**
   * Returns a list of the files in the recordable directory from the server.
   *
   * @return An array of strings, one for each file in the list.
   */
  public String[] getRecDir()
  {
    return (getDir(2, java.io.File.separator));
  }

  /**
   * Returns a given list of files in the executable directory.
   *
   * @param directoryName the directory to list relative to the root directory.
   * @return An array of strings, one for each file in the list.
   */
  public String[] getExeDir(String directoryName)
  {
    return (getDir(1, directoryName));
  }

  /**
   * Returns a given list of file in the recordable directory.
   *
   * @param directoryName the directory to list relative to the root directory.
   * @return An array of strings, one of each file in the list.
   */
  public String[] getRecDir(String directoryName)
  {
    return (getDir(2, directoryName));
  }

  /**
   * Gets an executable file from the server.
   *
   * @param filename the file name to get.
   * @return the contents of the executable file.
   */
  public Memory getExeFile(String filename)
  {
    return new Memory(getFile(1, filename));
  }

  /**
   * Get a recordable file from the server and converts it to an object.
   *
   * @param filename the file name to get.
   * @return the object representation of the file.
   */
  public Object getRecFile(String filename)
  {
    String serializedObject = getFile(2, filename);

    try
    {
      ObjectInputStream tmpBuffer = new
         ObjectInputStream(new ByteArrayInputStream(serializedObject.getBytes()));
      Object tmpObject = tmpBuffer.readObject();
      return tmpObject;
    }
    catch (Exception e)
    {
      log.fatal("Failed to deserialize class", e);
    }

    return null;
  }

  /**
   * Attempts to open a socket to the server.
   *
   * @return true if it succeeds.
   */
  public boolean openConnection()
  {
    try
    {
      socketConnection = new Socket(host, port);
      inStream = socketConnection.getInputStream();
      outStream = socketConnection.getOutputStream();
      messages = new FileMessages(inStream, outStream);
      return true;
    }
    catch (IOException exception)
    {
      log.error("Failed to open socket to server", exception);
      return false;
    }
  }

  /**
   * Create a new recording directory.
   *
   * @param directoryName the name of directory.
   * @return true if successfully created.
   */
  public boolean createRecDir(String directoryName)
  {
    int messageSize = 0;

    if (messages.askDirectoryCreate(2, directoryName))
    {
      //Read result
      if (messages.readMessage())
      {
        if (messages.getLastMessageType().equals(messages.A_DIRECTORY_CREATED))
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Write a new object to the record directory.
   *
   * @param fileName the name of the file to store.
   * @param object the object to record.
   * @exception Exception if any error occurred during writing.
   */
  public void writeRecFile(String fileName, Object object)
    throws Exception
  {
    // Byte buffer to write the object to.
    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

    // Write the object to the byte buffer.
    ObjectOutputStream objectBuffer = new ObjectOutputStream(byteBuffer);
    objectBuffer.writeObject(object);

    // Get the resultant serialized object.
    String result = byteBuffer.toString();

    //2 for recorded area
    if (messages.askWriteFileData(2, fileName, result))
    {
      //Read result
      if (messages.readMessage())
      {
        if (messages.getLastMessageType().equals(messages.A_WRITE_FILE_DATA))
        {
          return;
        }
      }
    }
    throw new Exception();
  }

  /**
   * Finds the size of a file in the executable directory.
   *
   * @param fileName the name of the file.
   * @return the size of the file in bytes.
   */
  public long statExeFile(String fileName)
  {
    return statFile(1, fileName);
  }

  /**
   * Finds the size of the file in the recordable directory.
   *
   * @param fileName the name of the file.
   * @return the size of the file in bytes.
   */
  public long statRecFile(String fileName)
  {
    return statFile(2, fileName);
  }

  /**
   * When shutting down the applet we should disconnect the connections to the
   * server gracefully.
   */
  public void closeConnection()
  {
    try
    {
      if (messages != null)
      {
        messages.askHangUp();
      }
      if (socketConnection != null)
      {
        socketConnection.close();
      }
    }
    catch (IOException exception)
    {
      System.err.println("Error! Unable to close connection to server.");
    }
  }

  /**
   * Retrieve a list of the specified directory from the server.
   *
   * @param directoryType Description of Parameter
   * @param directoryName Description of Parameter
   * @return An array of Strings, one for each entry in the directory list.
   */
  private String[] getDir(int directoryType, String directoryName)
  {
    String[] theList = new String[0];

    //Ask for directory listing
    if (messages.askDirectoryListing(directoryType, directoryName))
    {
      //Read result
      if (messages.readMessage())
      {
        String data = messages.getLastMessageData();
        int messageSize = messages.getLastMessageSize();

        if ((messages.getLastMessageType().equals(messages.A_DIRECTORY_LIST)) &&
            (messageSize > 0))
        {
          theList = new String[messageSize];

          StringTokenizer tokenizer = new StringTokenizer(data, " ");
          int count = 0;

          while (tokenizer.hasMoreElements())
          {
            theList[count] = tokenizer.nextToken();
            count++;
          }
        }
      }
      else
      {
        System.err.println(this + " getDir() Couldn't read reply.");
      }
    }
    else
    {
      System.err.println(this + " getDir() Couldn't send command.");
    }
    // Return the lines read in.
    return theList;
  }

  /**
   * Retrieve the specified file from the server.
   *
   * @param directoryType Description of Parameter
   * @param fileName Description of Parameter
   * @return An array of bytes containing the file contents.
   */
  private String getFile(int directoryType, String fileName)
  {
    String fileData = "";

    if (messages.askReadFileData(directoryType, fileName))
    {
      //Read result
      if (messages.readMessage())
      {
        if (messages.getLastMessageType().equals(messages.A_READ_FILE_DATA))
        {
          fileData = messages.getLastMessageData();
        }
      }
    }
    // Return the lines read in.
    return fileData;
  }

  /**
   * Retrieve the sizeof the specified file
   *
   * @param directoryType Description of Parameter
   * @param fileName Description of Parameter
   * @return the size of the file or 0 if there was an error.
   */
  private long statFile(int directoryType, String fileName)
  {
    long messageSize = 0;

    if (messages.askFileStats(directoryType, fileName))
    {
      //Read result
      if (messages.readMessage())
      {
        if (messages.getLastMessageType().equals(messages.A_FILE_STATS))
        {
          try
          {
            messageSize = Long.parseLong(messages.getLastMessageData());
          }
          catch (NumberFormatException e)
          {
            messageSize = 0;
          }
        }
      }
    }
    return messageSize;
  }
}
