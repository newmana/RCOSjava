package org.rcosjava.messaging.postoffices.universal;

import org.rcosjava.RCOS;
import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.messages.universal.NoNextMessage;
import org.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.pll2.FileClient;

import java.util.*;

/**
 * This deserializes the messages recorded in order that they were saved.
 * <P>
 * @author Andrew Newman.
 * @created 2nd January 2001
 * @see org.rcosjava.messaging.postoffices.os.OSMessageRecorder
 * @see org.rcosjava.messaging.postoffices.animator.AnimatorMessageRecorder
 * @version 1.00 $Date$
 */
public class UniversalMessagePlayer extends OSMessageHandler
{
  /**
   * Current message being playbacked.
   */
  private static int messageCounter = 0;

  /**
   * Client to communicate with.
   */
  private FileClient myClient;

  /**
   * Host to connect to to read results from.
   */
  private String host;

  /**
   * Port to connect to to read results from.
   */
  private int port;

  /**
   * Unique name to register reader as.
   */
  private String id;

  /**
   * OS post office that the reader is registered to.
   */
  private OSOffice osPostOffice;

  /**
   * Animator post office that the recorder is registered to.
   */
  private AnimatorOffice animatorPostOffice;

  /**
   * The name of the recording to playback.
   */
  private String recordingName;

  /**
   * The identifier of the scheduler to the post office.
   */
  private final static String MESSENGING_ID = "UniversalMessagePlayer";

  /**
   * Creates file client with default RCOS properties. Requires the hostname and
   * port to connect to in order to save/load the recordings.
   *
   * @param postOffice the post office to register the universal message player
   *     to and the one that sends messages to it.
   * @param newHost the local host to connect to.
   * @param newPort the local hosts port to connect to.
   */
  public UniversalMessagePlayer(OSOffice postOffice, String newHost,
      int newPort)
  {
    super(MESSENGING_ID, postOffice);
    host = newHost;
    port = newPort;
  }

  /**
  * Creates the Universal Message Player. Call sendNextMessage to go through the
  * messages.
  *
  * @param newAnimatorPostOffice the new animator post office to register to.
  * @param newOSPostOffice the post office to register the universal message player
  *     to and the one that sends messages to it.
  * @param newHost the local host to connect to.
  * @param newPort the host's port to connect to.
  * @param newId the unique id to register to the post offices with.
  */
  public UniversalMessagePlayer(AnimatorOffice newAnimatorPostOffice,
      OSOffice newOSPostOffice, String newHost, int newPort, String newId)
  {
    this(newOSPostOffice, newHost, newPort);
    animatorPostOffice = newAnimatorPostOffice;
    osPostOffice = newOSPostOffice;
    id = newId;
  }

  /**
   * Sets the name of the recording.
   *
   * @param newRecordingName the name to set the recording to.
   */
  public void setRecordingName(String newRecordingName)
  {
    recordingName = newRecordingName;
  }

  /**
   * Returns true if there is another message left to read.
   *
   * @return true if there is another message left to read.
   */
  public boolean hasNextMessage()
  {
    return hasMessage(messageCounter + 1);
  }

  /**
   * Increment the message counter.
   */
  public void incMessageCounter()
  {
    messageCounter++;
  }

  /**
   * Reads the next mesage in order and sends it to the appropriate post
   * offices.  If it comes to the end of the messages it will do nothing.
   *
   * @param newFileName the base file name to read.
   * @throws EndOfMessagesException if there are no more messages left to read.
   */
  public void playNextMessage() throws EndOfMessagesException
  {
    // If the object is null then we can assume we're at the end.
    if (hasMessage(messageCounter))
    {
      if (messageCounter == 0)
      {
        // Playback first message.  Assume first message is list of RCOS
        // components.
        List components = (List) readMessage();
        RCOS.setRCOSComponents(components);
      }
      else
      {
        MessageAdapter tmpMessage = (MessageAdapter) readMessage();

        if (tmpMessage.forPostOffice(osPostOffice) &&
            tmpMessage.forPostOffice(animatorPostOffice))
        {
          osPostOffice.localSendMessage((MessageAdapter) tmpMessage);
          animatorPostOffice.localSendMessage((MessageAdapter) tmpMessage);
        }
        else if (tmpMessage.forPostOffice(osPostOffice))
        {
          osPostOffice.localSendMessage((OSMessageAdapter) tmpMessage);
        }
        else if (tmpMessage.forPostOffice(animatorPostOffice))
        {
          animatorPostOffice.localSendMessage((AnimatorMessageAdapter) tmpMessage);
        }
      }

      // Check to see if this is the last message.
      if (hasNextMessage())
      {
        // If it's not the last message increment the message counter.
        incMessageCounter();
      }
      else
      {
        // If there is no next message let the multimedia animator know.
        NoNextMessage msg = new NoNextMessage(this);
        sendMessage(msg);
      }
    }
    else
    {
      throw new EndOfMessagesException("Message number: " + messageCounter +
          " Was not found.");
    }
  }

  /**
   * Returns true if there are no more messages left to read.
   *
   * @param messageIndex the message index into the number of messages to
   *   determine if it exists.
   * @return true if there are no more messages left to read.
   */
  private boolean hasMessage(int messageNumber)
  {
    // Open connection to client.
    myClient = new FileClient(host, port);
    myClient.openConnection();

    // Ask for the file size of the file.
    int size = myClient.statRecFile(java.io.File.separatorChar +
        recordingName + java.io.File.separatorChar + messageNumber +
        ".xml");

    // Close the connection
    myClient.closeConnection();

    // A size of 0 indicates not found - ie. no more messages.
    return (size != 0);
  }

  /**
   * Saves all the messages in a consistent. Called by all 4 process methods.
   * Increments the record counter by one.
   *
   * @return Description of the Returned Value
   */
  private Object readMessage()
  {
    myClient = new FileClient(host, port);
    myClient.openConnection();

    Object tmpObject;

    try
    {
      tmpObject = myClient.getRecFile(java.io.File.separatorChar +
          recordingName + java.io.File.separatorChar + (messageCounter) +
          ".xml");
    }
    catch (Exception e)
    {
      tmpObject = null;
      e.printStackTrace();
    }
    finally
    {
      myClient.closeConnection();
    }
    return tmpObject;
  }
}
