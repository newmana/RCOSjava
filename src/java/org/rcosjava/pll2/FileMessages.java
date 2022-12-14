package org.rcosjava.pll2;

import java.io.*;
import java.net.*;

/**
 * Provides support for FileClient and FileServer. Based on Brett Carter's
 * original design.
 * <P>
 * @author Andrew Newman (based on code by Brett Carter).
 * @created 10th January 1998
 * @version 1.00 $Date$
 */
public class FileMessages
{
  /**
   * Description of the Field
   */
  public final static char spacer = ' ';

  /**
   * Description of the Field
   */
  public final static String EOF = "[EOF]";

  /**
   * Description of the Field
   */
  public final static String Q_HANGUP = "Q000";

  /**
   * Description of the Field
   */
  public final static String Q_DIRECTORY_LIST = "Q100";

  /**
   * Description of the Field
   */
  public final static String Q_DIRECTORY_CREATE = "Q101";

  /**
   * Description of the Field
   */
  public final static String Q_READ_FILE_DATA = "Q200";

  /**
   * Description of the Field
   */
  public final static String Q_WRITE_FILE_DATA = "Q300";

  /**
   * Description of the Field
   */
  public final static String Q_FILE_STATS = "Q400";

  /**
   * Description of the Field
   */
  public final static String A_INVALID_COMMAND = "A001";

  /**
   * Description of the Field
   */
  public final static String A_INCORRECT_USAGE = "A002";

  /**
   * Description of the Field
   */
  public final static String A_CANNOT_ACCESS_DIRECTORY = "A003";

  /**
   * Description of the Field
   */
  public final static String A_DIRECTORY_DOES_NOT_EXIST = "A004";

  /**
   * Description of the Field
   */
  public final static String A_CANNOT_ACCESS_FILE = "A005";

  /**
   * Description of the Field
   */
  public final static String A_FILE_DOES_NOT_EXIST = "A006";

  /**
   * Description of the Field
   */
  public final static String A_CANNOT_CREATE_DIRECTORY = "A007";

  /**
   * Description of the Field
   */
  public final static String A_DESC_INCORRECT_USAGE = "Incorrect Usage";

  /**
   * Description of the Field
   */
  public final static String A_DESC_CANNOT_ACCESS_DIRECTORY = "Cannot access directory";

  /**
   * Description of the Field
   */
  public final static String A_DESC_DIRECTORY_DOES_NOT_EXIST = "Directory doesn't exist";

  /**
   * Description of the Field
   */
  public final static String A_DESC_CANNOT_ACCESS_FILE = "Cannot access file";

  /**
   * Description of the Field
   */
  public final static String A_DESC_FILE_DOES_NOT_EXIST = "File doesn't exist";

  /**
   * Description of the Field
   */
  public final static String A_DESC_CANNOT_CREATE_DIRECTORY = "Couldn't create directory";

  /**
   * Description of the Field
   */
  public final static String A_DIRECTORY_LIST = "A100";

  /**
   * Description of the Field
   */
  public final static String A_READ_FILE_DATA = "A200";

  /**
   * Description of the Field
   */
  public final static String A_WRITE_FILE_DATA = "A300";

  /**
   * Description of the Field
   */
  public final static String A_FILE_STATS = "A400";

  /**
   * Description of the Field
   */
  public final static String A_DIRECTORY_CREATED = "A500";

  /**
   * Description of the Field
   */
  private final static String padding = spacer + "0" + spacer + "1" + spacer;

  /**
   * Description of the Field
   */
  private final static String A_DESC_INVALID_COMMAND = "Invalid Command";

  /**
   * Description of the Field
   */
  private BufferedOutputStream writer;

  /**
   * Description of the Field
   */
  private BufferedInputStream reader;

  /**
   * Description of the Field
   */
  private String message, messageData, messageType, previousRequestMessage;

  /**
   * Description of the Field
   */
  private int previousRequestSize, messageSize, messageDirectory;

  /**
   * Constructor for the FileMessages object
   *
   * @param newInputStream Description of Parameter
   * @param newOutputStream Description of Parameter
   */
  public FileMessages(InputStream newInputStream, OutputStream newOutputStream)
  {
    reader = new BufferedInputStream(newInputStream);
    writer = new BufferedOutputStream(newOutputStream);
  }

  /**
   * @return the last message type returned by read message
   */
  public String getLastMessageType()
  {
    return messageType;
  }

  /**
   * @return the last message directory by read message
   */
  public int getLastMessageDirectory()
  {
    return messageDirectory;
  }

  /**
   * @return the last message data returned by read message
   */
  public String getLastMessageData()
  {
    return messageData;
  }

  /**
   * @return the size of the last message
   */
  public int getLastMessageSize()
  {
    return messageSize;
  }

  /**
   * Try to read the next message in the stream.
   *
   * @return Description of the Returned Value
   */
  public boolean readMessage()
  {
    messageData = null;
    messageDirectory = 0;
    messageType = null;
    try
    {
      boolean end = false;
      byte[] buffer = new byte[1];
      StringBuffer tmpMessage = new StringBuffer();
      String strTmpMessage = "";
      int result;

      // Read the message until we get to the end of stream or the end of file.
      do
      {
        result = reader.read(buffer);
        if (result != -1)
        {
          strTmpMessage += new String(buffer);
          end = strTmpMessage.endsWith(EOF);
        }
      } while (!end && result != -1);

      message = strTmpMessage.substring(0, strTmpMessage.length() -
          EOF.length());

      if (message != null)
      {
        messageType = message.substring(0, 4);
        messageDirectory = Integer.parseInt(message.substring(5, 6));
        messageData = message.substring(7, message.length());
        if (messageData.indexOf(" ") != -1)
        {
          messageSize = (Integer.parseInt(messageData.substring(0, messageData.indexOf(spacer))));
          messageData = messageData.substring(messageData.indexOf(spacer) + 1, messageData.length());
        }
//        System.out.println("Message Type: " + messageType);
//        System.out.println("Message Data: " + messageData);
//        System.out.println("Valid Message Type: " + validMessageType(messageType));
        if (validMessageType(messageType))
        {
          if (messageData.length() > 0)
          {
            return true;
          }
          else
          {
            replyIncorrectUsageMessage();
          }
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

  /**
   * Description of the Method
   *
   * @param directoryIndicator Description of Parameter
   * @param directoryName Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean askDirectoryListing(int directoryIndicator,
    String directoryName)
  {
    if (directoryName != null)
    {
      return (writeMessage(Q_DIRECTORY_LIST + spacer + directoryIndicator +
          spacer + directoryName));
    }
    else
    {
      return false;
    }
  }

  /**
   * Description of the Method
   *
   * @param directoryIndicator Description of Parameter
   * @param directoryName Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean askDirectoryCreate(int directoryIndicator,
    String directoryName)
  {
    if (directoryName != null)
    {
      return (writeMessage(Q_DIRECTORY_CREATE + spacer + directoryIndicator + spacer +
          directoryName));
    }
    else
    {
      return false;
    }
  }

  /**
   * Description of the Method
   *
   * @param directoryIndicator Description of Parameter
   * @param filename Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean askReadFileData(int directoryIndicator, String filename)
  {
    if (filename != null)
    {
      return (writeMessage(Q_READ_FILE_DATA + spacer + directoryIndicator +
          spacer + filename));
    }
    else
    {
      return false;
    }
  }

  /**
   * Description of the Method
   *
   * @param directoryIndicator Description of Parameter
   * @param filename Description of Parameter
   * @param fileData Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean askWriteFileData(int directoryIndicator, String filename,
    String fileData)
  {
    if (filename != null)
    {
      return (writeMessage(Q_WRITE_FILE_DATA + spacer + directoryIndicator + spacer +
          "1" + spacer + filename + spacer + fileData));
    }
    else
    {
      return false;
    }
  }

  /**
   * Description of the Method
   *
   * @param directoryIndicator Description of Parameter
   * @param filename Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean askFileStats(int directoryIndicator, String filename)
  {
    if (filename != null)
    {
      return (writeMessage(Q_FILE_STATS + spacer + directoryIndicator + spacer +
                           filename));
    }
    else
    {
      return false;
    }
  }

  /**
   * Description of the Method
   */
  public void askHangUp()
  {
    writeMessage(Q_HANGUP + spacer + "0" + spacer + "NOW");
  }

  /**
   * Description of the Method
   *
   * @param directoryList Description of Parameter
   * @return Description of the Returned Value
   */
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
      if (!writeMessage((spacer + directoryList[count]), false))
      {
        return false;
      }
    }
    flushOutStream();
    return true;
  }

  /**
   * Description of the Method
   *
   * @param fileData Description of Parameter
   * @return Description of the Returned Value
   */
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

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean replyWriteFileData()
  {
    return (writeMessage(A_WRITE_FILE_DATA + spacer + "0" + spacer + "OK"));
  }

  /**
   * Writer the reply to the file stat message with the length in bytes of the
   * file.
   *
   * @param fileSize length in bytes of the file.
   * @return if it was successful.
   */
  public boolean replyFileStat(long fileSize)
  {
    return (writeMessage(A_FILE_STATS + spacer + "0" + spacer + fileSize));
  }

  /**
   * Description of the Method
   *
   * @param fileSize Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean replyDirectoryCreated(int fileSize)
  {
    return (writeMessage(A_DIRECTORY_CREATED + spacer + "0" + spacer + "0"));
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean replyInvalidCommandMessage()
  {
    return (replyErrorMessage(A_INVALID_COMMAND));
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean replyIncorrectUsageMessage()
  {
    return (replyErrorMessage(A_INCORRECT_USAGE));
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean replyCannotAccessDirectoryMessage()
  {
    return (replyErrorMessage(A_CANNOT_ACCESS_DIRECTORY + padding +
        A_DESC_CANNOT_ACCESS_DIRECTORY));
  }

  /**
   * Description of the Method
   *
   * @param filename Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean replyCannotAccessFileMessage(String filename)
  {
    return (replyErrorMessage(A_CANNOT_ACCESS_FILE + padding +
        A_DESC_CANNOT_ACCESS_FILE + ": " + filename));
  }

  /**
   * Description of the Method
   *
   * @param directory Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean replyDirectoryDoesNotExistMessage(String directory)
  {
    return (replyErrorMessage(A_DIRECTORY_DOES_NOT_EXIST + padding +
        A_DESC_DIRECTORY_DOES_NOT_EXIST + ": " + directory));
  }

  /**
   * Description of the Method
   *
   * @param filename Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean replyFileDoesNotExistMessage(String filename)
  {
    return (replyErrorMessage(A_FILE_DOES_NOT_EXIST + padding +
        A_DESC_FILE_DOES_NOT_EXIST + ": " + filename));
  }

  /**
   * Description of the Method
   *
   * @param filename Description of Parameter
   * @return Description of the Returned Value
   */
  public boolean replyCannotCreateDirectory(String filename)
  {
    return (replyErrorMessage(A_CANNOT_CREATE_DIRECTORY + padding +
        A_DESC_CANNOT_CREATE_DIRECTORY + ": " + filename));
  }

  /**
   * Is the message a valid type?
   *
   * @param messageType Description of Parameter
   * @return Description of the Returned Value
   */
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

  /**
   * Description of the Method
   *
   * @param message Description of Parameter
   * @return Description of the Returned Value
   */
  private boolean replyErrorMessage(String message)
  {
    System.err.println("Error: " + message);
    return (writeMessage(message));
  }

  /**
   * Write a message, flushing the buffer automatically.
   *
   * @param message the message to write.
   * @return whether it was sucessful.
   */
  private boolean writeMessage(String message)
  {
    return (writeMessage(message, true));
  }

  /**
   * Write the message to the output stream and flush if appropriate.
   *
   * @param message the message as to be converted to a string.
   * @param flush whether to flush or not.
   * @return if it was successful.
   */
  private boolean writeMessage(byte[] message, boolean flush)
  {
    return (writeMessage(new String(message), flush));
  }

  /**
   * Write the message to the output stream and flush if appropriate.
   *
   * @param message the message to write.
   * @param flush whether to flush the stream or not.
   * @return if it was successful.
   */
  private boolean writeMessage(String message, boolean flush)
  {
    try
    {
      previousRequestMessage = message;
      writer.write(message.getBytes());

      if (flush)
      {
        flushOutStream();
      }
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
      System.err.println(ioe + " Error when trying to write: " + message);
      return false;
    }
    return true;
  }

  /**
   * Write the EOF bytes and flush.
   */
  private void flushOutStream()
  {
    try
    {
      writer.write(EOF.getBytes());
      writer.flush();
    }
    catch (IOException ioe)
    {
      System.err.println("Error when trying to flush");
    }
  }
}