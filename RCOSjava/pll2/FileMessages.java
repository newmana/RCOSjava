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
  public final static char cSpacer = ' ';
  public final static String EOF = "[EOF]";

  //Possible requests
  public final static String Q_HANGUP = "Q000";
  public final static String Q_DIRECTORY_LIST = "Q100";
  public final static String Q_READ_FILE_DATA = "Q200";
  public final static String Q_OPEN_FILE_DATA = "Q300";
  public final static String Q_WRITE_FILE_DATA = "Q301";
  public final static String Q_CLOSE_FILE_DATA = "Q302";
  public final static String Q_FILE_STATS = "Q400";

  //Possible replies
  //Errors
  public final static String A_INVALID_COMMAND = "A001" + cSpacer + "Invalid Command";
  public final static String A_INCORRECT_USAGE = "A002" + cSpacer + "Incorrect Usage";
  public final static String A_CANNOT_ACCESS_DIRECTORY = "A003" + cSpacer + "Cannot access directory";
  public final static String A_DIRECTORY_DOES_NOT_EXIST = "A004" + cSpacer + "Directory doesn't exist";
  public final static String A_CANNOT_ACCESS_FILE = "A005" + cSpacer + "Cannot access file";
  public final static String A_FILE_DOES_NOT_EXIST = "A006" + cSpacer + "File doesn't exist";

  //Success
  public final static String A_DIRECTORY_LIST = "A100";
  public final static String A_READ_FILE_DATA = "A200";
  public final static String A_OPEN_FILE_DATA = "A300";
  public final static String A_WRITE_FILE_DATA = "A301";
	public final static String A_CLOSE_FILE_DATA = "A302";
  public final static String A_FILE_STATS = "A400";

  private DataOutputStream dosOutStream;
  private DataInputStream disInStream;
  private String sMessage, sMessageData, sMessageType, sPreviousRequest;
  private int iPreviousSize, iMessageSize, iMessageDirectory;

  public FileMessages(DataInputStream disStream, DataOutputStream dosStream)
  {
    this.disInStream = disStream;
    this.dosOutStream = dosStream;
  }

  //Try to read the next message in the stream.
  public boolean readMessage()
  {
    sMessageData = null;
    iMessageDirectory = 0;
    sMessageType = null;
    try
    {
      boolean bEnd = false;
      StringBuffer sbTmpMessage = new StringBuffer();
      String sTmpMessage;
      while (!bEnd)
      {
        sTmpMessage = disInStream.readUTF();
        bEnd = sTmpMessage.endsWith(EOF);
        sbTmpMessage.append(sTmpMessage);
      }
      sbTmpMessage.setLength(sbTmpMessage.length() - EOF.length());
      sMessage = sbTmpMessage.toString();

      if (sMessage != null)
      {
        sMessageType = sMessage.substring(0, 4);
        iMessageDirectory = Integer.parseInt(sMessage.substring(5, 6));
        sMessageData = sMessage.substring(7, sMessage.length());
        if (sMessageData.indexOf(" ") != -1)
        {
          iMessageSize = (Integer.parseInt(sMessageData.substring(0, sMessageData.indexOf(cSpacer))));
          sMessageData = sMessageData.substring(sMessageData.indexOf(cSpacer)+1, sMessageData.length());
        }
        if (validMessageType(sMessageType))
        {
          if (sMessageData.length() > 0)
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
      System.out.println("Error reading data from server: " + ioe);
		}
    catch (Exception e)
    {
      System.out.println("Error: " + e);
			e.printStackTrace();
    }
    return false;
  }

  //Return the last message type returned by read message
  public String getLastMessageType()
  {
    return sMessageType;
  }

  //Return the last message directory by read message
  public int getLastMessageDirectory()
  {
          return iMessageDirectory;
  }

  //Return the last message data returned by read message
  public String getLastMessageData()
  {
    return sMessageData;
  }

  //Return the size of the last message
  public int getLastMessageSize()
  {
    return iMessageSize;
  }

  //Is the message a valid type?
  private boolean validMessageType(String sMessageType)
  {
    if (sMessageType.equals(Q_HANGUP) ||
        sMessageType.equals(Q_DIRECTORY_LIST) ||
        sMessageType.equals(Q_READ_FILE_DATA) ||
	sMessageType.equals(Q_OPEN_FILE_DATA) ||
	sMessageType.equals(Q_WRITE_FILE_DATA) ||
	sMessageType.equals(Q_CLOSE_FILE_DATA) ||
        sMessageType.equals(Q_FILE_STATS) ||
        sMessageType.equals(A_DIRECTORY_LIST) ||
        sMessageType.equals(A_READ_FILE_DATA) ||
	sMessageType.equals(A_OPEN_FILE_DATA) ||
	sMessageType.equals(A_WRITE_FILE_DATA) ||
	sMessageType.equals(A_CLOSE_FILE_DATA) ||
        sMessageType.equals(A_FILE_STATS) ||
	sMessageType.equals(A_INVALID_COMMAND) ||
        sMessageType.equals(A_INCORRECT_USAGE) ||
        sMessageType.equals(A_CANNOT_ACCESS_DIRECTORY) ||
        sMessageType.equals(A_DIRECTORY_DOES_NOT_EXIST) ||
        sMessageType.equals(A_CANNOT_ACCESS_FILE) ||
        sMessageType.equals(A_FILE_DOES_NOT_EXIST))
    {
      return true;
    }
    return false;
  }

  //Questions - requests
  public boolean askDirectoryListing(int iDirectory, String sDirectoryName)
  {
    if (sDirectoryName != null)
      return(writeMessage(Q_DIRECTORY_LIST + cSpacer + iDirectory + cSpacer + sDirectoryName));
    return false;
  }

  public boolean askReadFileData(int iDirectory, String sFileName)
  {
    if (sFileName != null)
      return(writeMessage(Q_READ_FILE_DATA + cSpacer + iDirectory + cSpacer + sFileName));
    return false;
  }

  public boolean askOpenFileData(int iDirectory, String sFileName)
  {
  if (sFileName != null)
    return(writeMessage(Q_OPEN_FILE_DATA + cSpacer + iDirectory + cSpacer + sFileName));
  return false;
  }

  public boolean askWriteFileData(int iDirectory, String sFileName)
  {
    if (sFileName != null)
      return(writeMessage(Q_WRITE_FILE_DATA + cSpacer + iDirectory + cSpacer + sFileName));
    return false;
  }

  public boolean askCloseFileData(int iDirectory, String sFileName)
  {
    if (sFileName != null)
      return(writeMessage(Q_CLOSE_FILE_DATA + cSpacer + iDirectory + cSpacer + sFileName));
    return false;
  }

  public boolean askFileStats(int iDirectory, String sFileName)
  {
    if (sFileName != null)
      return(writeMessage(Q_FILE_STATS + cSpacer + iDirectory + cSpacer + sFileName));
    return false;
  }

  public void askHangUp()
  {
    writeMessage(Q_HANGUP + cSpacer + "0" + cSpacer + "NOW");
  }

  //Answers - replies
  public boolean replyDirectoryListing(int iDirectory, String[] sDirectoryList)
  {
    iPreviousSize = sDirectoryList.length;
    int iCount;
    if (!writeMessage(A_DIRECTORY_LIST + cSpacer + iDirectory + cSpacer + iPreviousSize, false))
      return false;
    for (iCount = 0; iCount < iPreviousSize; iCount++)
    {
      if(!writeMessage(cSpacer + sDirectoryList[iCount], false))
        return false;
    }
    flushOutStream();
    return true;
  }

  public boolean replyLoadFileData(int iDirectory, byte[] bFileData)
  {
    iPreviousSize = bFileData.length;
    if (!writeMessage(A_READ_FILE_DATA + cSpacer + iDirectory + cSpacer + iPreviousSize + cSpacer, false))
      return false;
    if (!writeMessage(bFileData, true))
      return false;
    return true;
  }

  public boolean replyFileStat(int iDirectory, int iSize)
  {
    return(writeMessage(A_FILE_STATS + cSpacer + iDirectory + cSpacer + iSize));
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
    return(replyErrorMessage(A_CANNOT_ACCESS_DIRECTORY));
  }

  public boolean replyCannotAccessFileMessage()
  {
    return(replyErrorMessage(A_CANNOT_ACCESS_FILE));
  }

  public boolean replyDirectoryDoesNotExistMessage()
  {
    return(replyErrorMessage(A_DIRECTORY_DOES_NOT_EXIST));
  }

  public boolean replyFileDoesNotExistMessage()
  {
    return(replyErrorMessage(A_FILE_DOES_NOT_EXIST));
  }

  private boolean replyErrorMessage(String sMessage)
  {
    System.err.println(sMessage + ": " + sPreviousRequest);
    return(writeMessage(sMessage));
  }

  private boolean writeMessage(String sMessage)
  {
    return(writeMessage(sMessage, true));
  }

  private boolean writeMessage(byte[] bMessage, boolean bFlush)
  {
    return(writeMessage(new String(bMessage), bFlush));
  }

  private boolean writeMessage(String sMessage, boolean bFlush)
  {
    try
    {
      sPreviousRequest = sMessage;
      dosOutStream.writeUTF(sMessage);
      if (bFlush)
        flushOutStream();
    }
    catch (IOException ioe)
    {
      System.out.println(ioe + " Error when trying to write: " + sMessage);
      return false;
    }
    return true;
  }

  private void flushOutStream()
  {
    try
    {
      dosOutStream.writeUTF(EOF);
      dosOutStream.flush();
    }
    catch (IOException ioe)
    {
      System.out.println("Error when trying to flush");
    }
  }
}
