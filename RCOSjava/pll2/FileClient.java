// *************************************************************************
// FILE     : FileClient.java
// PACKAGE  : Disk
// PURPOSE  : Implement a class that will contact a server and communicate
//            with it. The server can fetch file listings or actual files.
// AUTHOR   : Andrew Newman (based on code by Brett Carter)
// MODIFIED :
// HISTORY  : 19/1/96 Created.
//            20/1/96 Updated to break down to method calls.
//            12/1/97 Moved to Disk package and closed connection
//                    gracefully. AN
//            10/1/98 Rewritten for JDK 1.1. AN
// *************************************************************************

package pll2;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import Software.Animator.Support.GraphicButton;
import Hardware.Memory.Memory;
import fr.dyade.koala.xml.koml.*;
import fr.dyade.koala.xml.sax.*;

public class FileClient
{
  private Socket sktConnection;
  private DataInputStream disInStream;
  private DataOutputStream dosOutStream;
  private FileMessages fmMessenger;
  private String sHost;
  private int iPort;

  public FileClient(String host, int port)
  {
    // Connect to server.
    //System.err.print("Connecting to " + host + "...");
    sHost = host;
    iPort = port;
  }

  public FileClient(URL host, int port)
  {
    // Connect to server.
    //System.err.print("Connecting to " + host + "...");
    this(String.valueOf(host), port);
  }

  public boolean openConnection()
  {
    try
    {
      sktConnection = new Socket(sHost, iPort);
      disInStream = new DataInputStream(sktConnection.getInputStream());
      dosOutStream = new DataOutputStream(sktConnection.getOutputStream());
      fmMessenger = new FileMessages(disInStream, dosOutStream);
      return true;
    }
    catch (IOException exception)
    {
      return false;
    }
  }

  // Purpose:  To retrieve a list of the root document directory from
  //           the server
  // Returns:  An array of strings, one for each file in the list.
  public String[] getExeDir()
  {
    return(getDir(1, "/"));
  }

  public String[] getRecDir()
  {
    return(getDir(2, "/"));
  }

  public String[] getExeDir(String sDirectory)
  {
    return(getDir(1, sDirectory));
  }

  public String[] getRecDir(String sDirectory)
  {
    return(getDir(2, sDirectory));
  }

  // Purpose:  Retrieve a list of the specified directory from the
  //           server.
  // Returns:  An array of Strings, one for each entry in the directory list.
  private String[] getDir(int iDirectory, String sDirectory)
  {
    String[] sTheList = new String[0];
    //Ask for directory listing
    if(fmMessenger.askDirectoryListing(iDirectory, sDirectory))
    {
      //Read result
      if (fmMessenger.readMessage())
      {
        String sData = fmMessenger.getLastMessageData();
        int iMessageSize = fmMessenger.getLastMessageSize();
        if (fmMessenger.getLastMessageType().equals(fmMessenger.A_DIRECTORY_LIST))
        {
          sTheList = new String[iMessageSize];
          StringTokenizer stTokenizer = new StringTokenizer(sData, " ");
          int iCount = 0;
          while(stTokenizer.hasMoreElements())
          {
            sTheList[iCount] = stTokenizer.nextToken();
            iCount++;
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
    return sTheList;
  }

  public Memory getExeFile(String filename)
  {
    return new Memory(getFile(1, filename));
  }

  public Object getRecFile(String filename)
  {
    String serializedObject = getFile(2, filename);
    ByteArrayInputStream tmpBuffer = new ByteArrayInputStream(serializedObject.getBytes());
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

  // Purpose:  Retrieve the specified file from the server.
  // Returns:  An array of bytes containing the file contents.
  private String getFile(int iDirectory, String sFileName)
  {
    String mFileData = new String();
    if(fmMessenger.askReadFileData(iDirectory, sFileName))
    {
      //Read result
      if (fmMessenger.readMessage())
      {
        if (fmMessenger.getLastMessageType().equals(fmMessenger.A_READ_FILE_DATA))
        {
          mFileData = fmMessenger.getLastMessageData();
        }
      }
    }
    // Return the lines read in.
    return mFileData;
  }

  public void writeRecFile(String sFileName, Object object)
    throws Exception
  {
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
    if(fmMessenger.askWriteFileData(2, sFileName, tmpBuffer.toString()))
    {
      //Read result
      if (fmMessenger.readMessage())
      {
        if (fmMessenger.getLastMessageType().equals(fmMessenger.A_WRITE_FILE_DATA))
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

  // Function: statFile(String sFileName)
  // Purpose:  Retrieve the sizeof the specified file
  // Returns:  an integer.
  private int statFile(int iDirectory, String sFileName)
  {
    int iMessageSize = 0;
    if(fmMessenger.askFileStats(iDirectory, sFileName))
    {
      //Read result
      if (fmMessenger.readMessage())
      {
        if (fmMessenger.getLastMessageType().equals(fmMessenger.A_FILE_STATS))
        {
          try
          {
            iMessageSize = Integer.parseInt(fmMessenger.getLastMessageData());
          }
          catch (NumberFormatException e)
          {
            iMessageSize = 0;
          }
        }
      }
    }
    return iMessageSize;
  }

  // Function: close ()
  // Purpose : When shutting down the applet we should disconnect the
  //           connections to the server gracefully.
  // Returns : Nothing.
  public void closeConnection()
  {
    try
    {
      if (fmMessenger != null)
        fmMessenger.askHangUp();
      if (sktConnection != null)
        sktConnection.close();
    }
    catch (IOException exception)
    {
      System.err.println("Error! Unable to close connection to server.");
    }
  }
}
