package org.rcosjava.messaging.postoffices.universal;
import org.rcosjava.messaging.messages.RemoveHandler;
import org.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageRecorder;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
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
public class UniversalMessageRecorder
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
   * File name to use to write the messages.
   */
  private String fileName;

  /**
   * Creates file client with default RCOS properties. Requires the hostname and
   * port to connect to in order to save/load the recordings.
   *
   * @param newHost the host to connect to.
   * @param newPort the host's port to connect to.
   */
  public UniversalMessageRecorder(String newHost, int newPort)
  {
    host = newHost;
    port = newPort;
  }

  /**
   * Creates the Universal Message Recorder. It does not automatically listen to
   * the messages. You must call recordOn to start recording the messages.
   *
   * @param newHost the local host to connect to.
   * @param newPort the host's port to connect to.
   * @param newId the unique id to register to the post offices with.
   * @param newOSPostOffice the new operating system post office to register to.
   * @param newAnimatorPostOffice the new animator post office to register to.
   */
  public UniversalMessageRecorder(String newHost, int newPort, String newId,
      OSOffice newOSPostOffice, AnimatorOffice newAnimatorPostOffice)
  {
    this(newHost, newPort);
    animatorPostOffice = newAnimatorPostOffice;
    osPostOffice = newOSPostOffice;
    id = newId;
  }

  /**
   * Start recording all the messages received. The file name has a numeric
   * counter added to the end of it for each message. What happens can simply
   * start at 0 and continue till there are none left.
   *
   * @param newFileName the base file name to save all the messages to.
   */
  public void recordOn(String newFileName)
  {
    fileName = newFileName;
    osRecorder = new OSMessageRecorder(id, osPostOffice, this);
    animatorRecorder = new AnimatorMessageRecorder(id, animatorPostOffice,
        this);
  }

  /**
   * Remove the OS and Animator recorders from their relevant post offices. As
   * we don't want to process the messages anymore.
   */
  public void recordOff()
  {
    osRecorder.localSendMessage(new RemoveHandler(osRecorder));
    animatorRecorder.localSendMessage(new RemoveHandler(animatorRecorder));
  }

  /**
   * Create a new connection, create the directory with the given name and
   * close the connection.
   *
   * @param directory name of the directory to create.
   */
  public void createDirectory(String directory)
  {
    myClient = new FileClient(host, port);
    myClient.openConnection();
    myClient.createRecDir(directory);
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
      myClient.writeRecFile(java.io.File.separatorChar + fileName +
          java.io.File.separatorChar + messagesRecorded + ".xml", newMessage);
      messagesRecorded++;
    }
    catch (Exception e)
    {
    }
    myClient.closeConnection();
  }
}
