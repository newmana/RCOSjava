package pll2;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import Software.Animator.Support.GraphicButton;
import Hardware.Memory.Memory;
import fr.dyade.koala.xml.koml.*;
import fr.dyade.koala.xml.sax.*;

/**
 * Implement a class that will contact a server and communicate with it.
 * The server can fetch file listings or actual files.
 * <P>
 * HISTORY: 20/01/1996 Updated to break down to method calls.<BR>
 *          12/01/1997 Moved to Disk package and closed connection
 *          gracefully. AN<BR>
 *          10/01/198  Rewritten for JDK 1.1. AN<BR>
 * <P>
 * @author Andrew Newman (based on code by Brett Carter).
 * @version 1.00 $Date$
 * @created 19th January 1996
 */
public class FileClient
{
  private Socket socketConnection;
  private DataInputStream dataInStream;
  private DataOutputStream dataOutStream;
  private FileMessages messages;
  private String host;
  private int port;

  public FileClient(String newHost, int newPort)
  {
    // Connect to server.
    //System.err.print("Connecting to " + host + "...");
    host = newHost;
    port = newPort;
  }

  public FileClient(URL newHost, int newPort)
  {
    // Connect to server.
    //System.err.print("Connecting to " + host + "...");
    this(String.valueOf(newHost), newPort);
  }

  public boolean openConnection()
  {
    try
    {
      socketConnection = new Socket(host, port);
      dataInStream = new DataInputStream(socketConnection.getInputStream());
      dataOutStream = new DataOutputStream(socketConnection.getOutputStream());
      messages = new FileMessages(dataInStream, dataOutStream);
      return true;
    }
    catch (IOException exception)
    {
      exception.printStackTrace();
      return false;
    }
  }

  /**
   * To retrieve a list of the root document directory from the server.
   *
   * @return An array of strings, one for each file in the list.
   */
  public String[] getExeDir()
  {
    return(getDir(1, java.io.File.separator));
  }

  public String[] getRecDir()
  {
    return(getDir(2, java.io.File.separator));
  }

  public String[] getExeDir(String directoryName)
  {
    return(getDir(1, directoryName));
  }

  public String[] getRecDir(String directoryName)
  {
    return(getDir(2, directoryName));
  }

  public boolean createRecDir(String directoryName)
  {
    int messageSize = 0;
    if(messages.askDirectoryCreate(2, directoryName))
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
   * Retrieve a list of the specified directory from the server.
   *
   * @return An array of Strings, one for each entry in the directory list.
   */
  private String[] getDir(int directoryType, String directoryName)
  {
    String[] theList = new String[0];
    //Ask for directory listing
    if(messages.askDirectoryListing(directoryType, directoryName))
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
          while(tokenizer.hasMoreElements())
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

  public Memory getExeFile(String filename)
  {
    return new Memory(getFile(1, filename));
  }

  public Object getRecFile(String filename)
  {
    String serializedObject = getFile(2, filename);
    System.out.println("Filename: " + filename);
    System.out.println("Got: " + serializedObject);
    ByteArrayInputStream tmpBuffer = new
      ByteArrayInputStream(serializedObject.getBytes());
    KOMLDeserializer deserializer = null;
    try
    {
      deserializer = new KOMLDeserializer(tmpBuffer, false);
      return deserializer.readObject();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        deserializer.close();
      }
      catch (Exception e)
      {
        // ignore
      }
    }
    return null;
  }

  /**
   * Retrieve the specified file from the server.
   *
   * @return An array of bytes containing the file contents.
   */
  private String getFile(int directoryType, String sFileName)
  {
    String mFileData = new String();
    if(messages.askReadFileData(directoryType, sFileName))
    {
      //Read result
      if (messages.readMessage())
      {
        if (messages.getLastMessageType().equals(messages.A_READ_FILE_DATA))
        {
          mFileData = messages.getLastMessageData();
        }
      }
    }
    // Return the lines read in.
    return mFileData;
  }

  public void writeRecFile(String sFileName, Object object)
    throws Exception
  {
//    System.out.println("Serializing: " + object.getClass());
    KOMLSerializer serializer = null;
    ByteArrayOutputStream tmpBuffer = new ByteArrayOutputStream();
    try
    {
      serializer = new KOMLSerializer(tmpBuffer, false);
      serializer.addObject(object);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        serializer.close();
      }
      catch (Exception e)
      {
        // ignore
      }
    }
    //2 for recorded area
    if(messages.askWriteFileData(2, sFileName, tmpBuffer.toString()))
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

  public int statExeFile(String sFileName)
  {
    return statFile(1, sFileName);
  }

  public int statRecFile(String sFileName)
  {
    return statFile(2, sFileName);
  }

  /**
   * Retrieve the sizeof the specified file
   */
  private int statFile(int directoryType, String sFileName)
  {
    int messageSize = 0;
    if(messages.askFileStats(directoryType, sFileName))
    {
      //Read result
      if (messages.readMessage())
      {
        if (messages.getLastMessageType().equals(messages.A_FILE_STATS))
        {
          try
          {
            messageSize = Integer.parseInt(messages.getLastMessageData());
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

  /**
   * When shutting down the applet we should disconnect the connections to the
   * server gracefully.
   */
  public void closeConnection()
  {
    try
    {
      if (messages != null)
        messages.askHangUp();
      if (socketConnection != null)
        socketConnection.close();
    }
    catch (IOException exception)
    {
      System.err.println("Error! Unable to close connection to server.");
    }
  }
}
