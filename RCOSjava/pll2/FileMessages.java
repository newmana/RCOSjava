// *************************************************************************
// FILE     : FileMessages.java
// PACKAGE  : Disk
// PURPOSE  : Provides support for FileClient and FileServer.  Based
//            on Brett Carter's original design.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/1/98 Created.
//
// *************************************************************************

package pll2;

import java.io.*;
import java.net.*;

public class FileMessages
{
  public final static char spacer = ' ';
  public final static String EOF = "[EOF]";

  //Possible requests
  public final static String Q_HANGUP = "Q000";
  public final static String Q_DIRECTORY_LIST = "Q100";
  public final static String Q_DIRECTORY_CREATE = "Q101";
  public final static String Q_READ_FILE_DATA = "Q200";
  public final static String Q_WRITE_FILE_DATA = "Q300";
  public final static String Q_FILE_STATS = "Q400";

  //Possible replies
  //Errors
  public final static String A_INVALID_COMMAND = "A001";
  public final static String A_INCORRECT_USAGE = "A002";
  public final static String A_CANNOT_ACCESS_DIRECTORY = "A003";
  public final static String A_DIRECTORY_DOES_NOT_EXIST = "A004";
  public final static String A_CANNOT_ACCESS_FILE = "A005";
  public final static String A_FILE_DOES_NOT_EXIST = "A006";
  public final static String A_CANNOT_CREATE_DIRECTORY = "A007";
  private final static String padding = spacer + "0" + spacer + "1" + spacer;
  private final static String A_DESC_INVALID_COMMAND = "Invalid Command";
  public final static String A_DESC_INCORRECT_USAGE = "Incorrect Usage";
  public final static String A_DESC_CANNOT_ACCESS_DIRECTORY = "Cannot access directory";
  public final static String A_DESC_DIRECTORY_DOES_NOT_EXIST = "Directory doesn't exist";
  public final static String A_DESC_CANNOT_ACCESS_FILE = "Cannot access file";
  public final static String A_DESC_FILE_DOES_NOT_EXIST = "File doesn't exist";
  public final static String A_DESC_CANNOT_CREATE_DIRECTORY = "Couldn't create directory";

  //Success
  public final static String A_DIRECTORY_LIST = "A100";
  public final static String A_READ_FILE_DATA = "A200";
  public final static String A_WRITE_FILE_DATA = "A300";
  public final static String A_FILE_STATS = "A400";
  public final static String A_DIRECTORY_CREATED = "A500";

  private DataOutputStream outputStream;
  private DataInputStream inputStream;
  private String message, messageData, messageType, previousRequestMessage;
  private int previousRequestSize, messageSize, messageDirectory;

  public FileMessages(DataInputStream newInputStream,
    DataOutputStream newOutputStream)
  {
    inputStream = newInputStream;
    outputStream = newOutputStream;
  }

  //Try to read the next message in the stream.
  public boolean readMessage()
  {
    messageData = null;
    messageDirectory = 0;
    messageType = null;
    try
    {
      boolean end = false;
      StringBuffer tmpMessage = new StringBuffer();
      String strTmpMessage;
      while (!end)
      {
        strTmpMessage = inputStream.readUTF();
        end = strTmpMessage.endsWith(EOF);
        tmpMessage.append(strTmpMessage);
      }
//      System.out.println("Raw message: " + tmpMessage);
      tmpMessage.setLength(tmpMessage.length() - EOF.length());
      message = tmpMessage.toString();

      if (message != null)
      {
        messageType = message.substring(0, 4);
        messageDirectory = Integer.parseInt(message.substring(5, 6));
        messageData = message.substring(7, message.length());
        if (messageData.indexOf(" ") != -1)
        {
          messageSize = (Integer.parseInt(messageData.substring(0, messageData.indexOf(spacer))));
          messageData = messageData.substring(messageData.indexOf(spacer)+1, messageData.length());
        }
//        System.out.println("Message Type: " + messageType);
//        System.out.println("Valid Message Type: " + validMessageType(messageType));
        if (validMessageType(messageType))
        {
          if (messageData.length() > 0)
            return true;
          else
            replyIncorrectUsageMessage();
        }
        else
        {
          replyInvalidCommandMessage();
        }
      }
      else
      {
        replyInvalidCommandMessage();
      }
    }
    catch (IOException ioe)
    {
      System.err.println("Error reading data from server: " + ioe);
      ioe.printStackTrace();
    }
    catch (Exception e)
    {
      System.err.println("Error: " + e);
      e.printStackTrace();
    }
    return false;
  }

  //Return the last message type returned by read message
  public String getLastMessageType()
  {
    return messageType;
  }

  //Return the last message directory by read message
  public int getLastMessageDirectory()
  {
    return messageDirectory;
  }

  //Return the last message data returned by read message
  public String getLastMessageData()
  {
    return messageData;
  }

  //Return the size of the last message
  public int getLastMessageSize()
  {
    return messageSize;
  }

  //Is the message a valid type?
  private boolean validMessageType(String messageType)
  {
    if (messageType.equals(Q_HANGUP) ||
        messageType.equals(Q_DIRECTORY_LIST) ||
        messageType.equals(Q_DIRECTORY_CREATE) ||
        messageType.equals(Q_READ_FILE_DATA) ||
	messageType.equals(Q_WRITE_FILE_DATA) ||
        messageType.equals(Q_FILE_STATS) ||
        messageType.equals(A_DIRECTORY_LIST) ||
        messageType.equals(A_READ_FILE_DATA) ||
	messageType.equals(A_WRITE_FILE_DATA) ||
        messageType.equals(A_FILE_STATS) ||
        messageType.equals(A_DIRECTORY_CREATED) ||
	messageType.equals(A_INVALID_COMMAND) ||
        messageType.equals(A_INCORRECT_USAGE) ||
        messageType.equals(A_CANNOT_ACCESS_DIRECTORY) ||
        messageType.equals(A_DIRECTORY_DOES_NOT_EXIST) ||
        messageType.equals(A_CANNOT_ACCESS_FILE) ||
        messageType.equals(A_FILE_DOES_NOT_EXIST) ||
        messageType.equals(A_CANNOT_CREATE_DIRECTORY))
    {
      return true;
    }
    return false;
  }

  //Questions - requests
  public boolean askDirectoryListing(int directoryIndicator,
    String directoryName)
  {
    if (directoryName != null)
    {
      return(writeMessage(Q_DIRECTORY_LIST + spacer + directoryIndicator + spacer +
        directoryName));
    }
    else
    {
      return false;
    }
  }

  public boolean askDirectoryCreate(int directoryIndicator,
    String directoryName)
  {
    if (directoryName != null)
    {
      return(writeMessage(Q_DIRECTORY_CREATE + spacer + directoryIndicator + spacer +
        directoryName));
    }
    else
    {
      return false;
    }
  }

  public boolean askReadFileData(int directoryIndicator, String filename)
  {
    if (filename != null)
    {
      return(writeMessage(Q_READ_FILE_DATA + spacer + directoryIndicator + spacer +
        filename));
    }
    else
    {
      return false;
    }
  }

  public boolean askWriteFileData(int directoryIndicator, String filename,
    String fileData)
  {
    if (filename != null)
    {
      return(writeMessage(Q_WRITE_FILE_DATA + spacer + directoryIndicator + spacer +
        "1" + spacer + filename + spacer + fileData));
    }
    else
    {
      return false;
    }
  }

  public boolean askFileStats(int directoryIndicator, String filename)
  {
    if (filename != null)
    {
      return(writeMessage(Q_FILE_STATS + spacer + directoryIndicator + spacer +
        filename));
    }
    else
    {
      return false;
    }
  }

  public void askHangUp()
  {
    writeMessage(Q_HANGUP + spacer + "0" + spacer + "NOW");
  }

  //Answers - replies
  public boolean replyDirectoryListing(String[] directoryList)
  {
    previousRequestSize = directoryList.length;
    if (!writeMessage(A_DIRECTORY_LIST + spacer + "0" + spacer +
      previousRequestSize, false))
    {
      return false;
    }
    int count;
    for (count = 0; count < previousRequestSize; count++)
    {
      if(!writeMessage((spacer + directoryList[count]), false))
      {
        return false;
      }
    }
    flushOutStream();
    return true;
  }

  public boolean replyLoadFileData(byte[] fileData)
  {
    previousRequestSize = fileData.length;
    if (!writeMessage(A_READ_FILE_DATA + spacer + "0" + spacer +
      previousRequestSize + spacer, false))
    {
      return false;
    }
    if (!writeMessage(fileData, true))
    {
      return false;
    }
    return true;
  }

  public boolean replyWriteFileData()
  {
    return(writeMessage(A_WRITE_FILE_DATA + spacer + "0" + spacer + "OK"));
  }

  public boolean replyFileStat(int fileSize)
  {
    return(writeMessage(A_FILE_STATS + spacer + "0" + spacer + fileSize));
  }

  public boolean replyDirectoryCreated(int fileSize)
  {
    return(writeMessage(A_DIRECTORY_CREATED + spacer + "0" + spacer + "0"));
  }

  public boolean replyInvalidCommandMessage()
  {
    return(replyErrorMessage(A_INVALID_COMMAND));
  }

  public boolean replyIncorrectUsageMessage()
  {
    return(replyErrorMessage(A_INCORRECT_USAGE));
  }

  public boolean replyCannotAccessDirectoryMessage()
  {
    return(replyErrorMessage(A_CANNOT_ACCESS_DIRECTORY + padding +
      A_DESC_CANNOT_ACCESS_DIRECTORY));
  }

  public boolean replyCannotAccessFileMessage(String filename)
  {
    return(replyErrorMessage(A_CANNOT_ACCESS_FILE + padding +
      A_DESC_CANNOT_ACCESS_FILE + ": " + filename));
  }

  public boolean replyDirectoryDoesNotExistMessage(String directory)
  {
    return(replyErrorMessage(A_DIRECTORY_DOES_NOT_EXIST + padding +
      A_DESC_DIRECTORY_DOES_NOT_EXIST + ": " + directory));
  }

  public boolean replyFileDoesNotExistMessage(String filename)
  {
    return(replyErrorMessage(A_FILE_DOES_NOT_EXIST + padding +
      A_DESC_FILE_DOES_NOT_EXIST + ": " + filename));
  }

  public boolean replyCannotCreateDirectory(String filename)
  {
    return(replyErrorMessage(A_CANNOT_CREATE_DIRECTORY + padding +
      A_DESC_CANNOT_CREATE_DIRECTORY + ": " + filename));
  }

  private boolean replyErrorMessage(String message)
  {
    System.err.println("Error: " + message);
    return(writeMessage(message));
  }

  private boolean writeMessage(String message)
  {
    return(writeMessage(message, true));
  }

  private boolean writeMessage(byte[] message, boolean flush)
  {
    return(writeMessage(new String(message), flush));
  }

  private boolean writeMessage(String message, boolean flush)
  {
    try
    {
      previousRequestMessage = message;
      outputStream.writeUTF(message);
      if (flush)
      {
        flushOutStream();
      }
    }
    catch (IOException ioe)
    {
      System.err.println(ioe + " Error when trying to write: " + message);
      return false;
    }
    return true;
  }

  private void flushOutStream()
  {
    try
    {
      outputStream.writeUTF(EOF);
      outputStream.flush();
    }
    catch (IOException ioe)
    {
      System.err.println("Error when trying to flush");
    }
  }
}
