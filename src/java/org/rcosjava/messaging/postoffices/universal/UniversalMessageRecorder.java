package org.rcosjava.messaging.postoffices.universal;

import org.rcosjava.messaging.messages.RemoveHandler;
import org.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageRecorder;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSMessageRecorder;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.pll2.FileClient;

/**
 * This is simply contains a OS Message Recorder and a Animator Message
 * Recorder. It will record all messages and the relay them to the file system
 * to be saved. This will enable the system to read these files and reconstruct
 * what occurred. It current has no error handling and will probably fail badly
 * in certain circumstances.
 * <P>
 * <DT> <B>Usage example:</B>
 * <DD> <CODE>
 *      To be done.
 * </CODE> </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 2nd January 2001
 * @see org.rcosjava.messaging.postoffices.os.OSMessageRecorder
 * @see org.rcosjava.messaging.postoffices.animator.AnimatorMessageRecorder
 * @version 1.00 $Date$
 */
public class UniversalMessageRecorder extends OSMessageHandler
{
  /**
   * Current message being recorded.
   */
  private static int messagesRecorded = 0;

  /**
   * Message recorder for all OS messages.
   */
  private OSMessageRecorder osRecorder;

  /**
   * Message recorder for all Animator messages.
   */
  private AnimatorMessageRecorder animatorRecorder;

  /**
   * Client to communicate to.
   */
  private FileClient myClient;

  /**
   * Host to connect to to send results to.
   */
  private String host;

  /**
   * Port to connect to to send results to.
   */
  private int port;

  /**
   * Unique name to register recorder as.
   */
  private String id;

  /**
   * OS post office that the recorder is registered to.
   */
  private OSOffice osPostOffice;

  /**
   * Animator post office that the recorder is registerd to.
   */
  private AnimatorOffice animatorPostOffice;

  /**
   * The name to use to store all messages under.
   */
  private String recordingName;

  /**
   * Whether it is currently recording.
   */
  private boolean recordingOn;

  /**
   * The identifier of the scheduler to the post office.
   */
  private final static String MESSENGING_ID = "UniversalMessageRecorder";

  /**
   * Creates file client with default RCOS properties. Requires the hostname and
   * port to connect to in order to save/load the recordings.
   *
   * @param postOffice the post office to register the universal message player
   *     to and the one that sends messages to it.
   * @param newHost the host to connect to.
   * @param newPort the host's port to connect to.
   */
  public UniversalMessageRecorder(OSOffice postOffice, String newHost,
      int newPort)
  {
    super(MESSENGING_ID, postOffice);
    host = newHost;
    port = newPort;
    recordingOn = false;
  }

  /**
   * Creates the Universal Message Recorder. It does not automatically listen to
   * the messages. You must call recordOn to start recording the messages.
   *
   * @param newAnimatorPostOffice the new animator post office to register to.
   * @param newOSPostOffice the new operating system post office to register to.
   * @param newHost the local host to connect to.
   * @param newPort the host's port to connect to.
   * @param newId the unique id to register to the post offices with.
   */
  public UniversalMessageRecorder(AnimatorOffice newAnimatorPostOffice,
      OSOffice newOSPostOffice, String newHost, int newPort, String newId)
  {
    this(newOSPostOffice, newHost, newPort);
    animatorPostOffice = newAnimatorPostOffice;
    osPostOffice = newOSPostOffice;
    id = newId;
  }

  /**
   * Toggles the recording of messages on or off.  Assumes that the current
   * directory to write to is already set.
   */
  public void toggleRecording()
  {
    if (!recordingOn)
    {
      recordOn();
    }
    else
    {
      recordOff();
    }
  }

  /**
   * Sets the name of the recording.
   *
   * @param newRecordingName the name to set the recording to.
   */
  public void setRecordingName(String newRecordingName)
  {
    recordingName = newRecordingName;
    System.err.println("Recording name: " + recordingName);
  }

  /**
   * Create a new connection, create the recording area with the previously
   * given name, reset the message count and close the connection.  Assumes
   * a recording name is set.
   *
   * @param directory name of the directory to create.
   */
  public void createNewRecording()
  {
    myClient = new FileClient(host, port);
    myClient.openConnection();
    myClient.createRecDir(java.io.File.separatorChar + recordingName);
    messagesRecorded = 0;
    myClient.closeConnection();
  }

  /**
   * Saves animator messages.
   *
   * @param newMessage the message to save permanently.
   */
  public void processAnimatorMessage(AnimatorMessageAdapter newMessage)
  {
    saveMessage(newMessage);
  }

  /**
   * Save OS message.
   *
   * @param newMessage the message to save permanently.
   */
  public void processOSMessage(OSMessageAdapter newMessage)
  {
    saveMessage(newMessage);
  }

  /**
   * Save Universal message.
   *
   * @param newMessage the message to save permanently.
   */
  public void processOSUniversalMessage(UniversalMessageAdapter newMessage)
  {
    saveMessage(newMessage);
  }

  /**
   * Start recording all the messages received. The file name has a numeric
   * counter added to the end of it for each message. What happens can simply
   * start at 0 and continue till there are none left.
   *
   * @param newFileName the base file name to save all the messages to.
   */
  private void recordOn()
  {
    recordingOn = true;
    osRecorder = new OSMessageRecorder(id, osPostOffice, this);
    animatorRecorder = new AnimatorMessageRecorder(id, animatorPostOffice,
        this);
  }

  /**
   * Remove the OS and Animator recorders from their relevant post offices. As
   * we don't want to process the messages anymore.
   */
  private void recordOff()
  {
    osRecorder.localSendMessage(new RemoveHandler(osRecorder));
    animatorRecorder.localSendMessage(new RemoveHandler(animatorRecorder));
    recordingOn = false;
  }

  /**
   * Saves all the messages in a consistent. Called by all 3 process methods.
   * Increments the record counter by one.
   *
   * @param newMessage Description of Parameter
   */
  private void saveMessage(Object newMessage)
  {
    myClient = new FileClient(host, port);
    myClient.openConnection();
    try
    {
      myClient.writeRecFile(java.io.File.separatorChar + recordingName +
          java.io.File.separatorChar + messagesRecorded + ".xml", newMessage);
      messagesRecorded++;
    }
    catch (Exception e)
    {
    }
    myClient.closeConnection();
  }
}
