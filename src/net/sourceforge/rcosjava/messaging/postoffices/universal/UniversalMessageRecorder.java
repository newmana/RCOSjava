package net.sourceforge.rcosjava.messaging.postoffices.universal;

import net.sourceforge.rcosjava.messaging.messages.RemoveHandler;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;

import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageRecorder;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageRecorder;

import net.sourceforge.rcosjava.RCOS;

import net.sourceforge.rcosjava.pll2.FileClient;

/**
 * This is simply contains a OS Message Recorder and a Animator Message
 * Recorder.  It will record all messages and the relay them to the file system
 * to be saved.  This will enable the system to read these files and reconstruct
 * what occurred.  It current has no error handling and will probably fail
 * badly in certain circumstances.
 * <P>
 * <DT><B>Usage example:</B><DD>
 * <CODE>
 *      To be done.
 * </CODE>
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageRecorder
 * @see net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageRecorder
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd January 2001
 */
public class UniversalMessageRecorder
{
  private OSMessageRecorder osRecorder;
  private AnimatorMessageRecorder animatorRecorder;
  private static int counter = 0;
  private FileClient myClient;
  private String host;
  private int port;
  private String id;
  private OSOffice osPostOffice;
  private AnimatorOffice animatorPostOffice;
  private String fileName;

  /**
   * Creates file client with default RCOS properties.  Requires the hostname
   * and port to connect to in order to save/load the recordings.
   *
   * @param newHost the local host to connect to.
   * @param newPort the local hosts port to connect to.
   */
  public UniversalMessageRecorder(String newHost, int newPort)
  {
    host = newHost;
    port = newPort;
  }

  /**
   * Creates the Universal Message Recorder.  It does not automatically listen
   * to the messages.  You must call recordOn to start recording the messages.
   *
   * @param newID the id to register with the offices to.
   * @param newOSPostOffice the new operating system post office to register
   * to.
   * @param newAnimatorOffice the new animator post office to register to.
   */
  public UniversalMessageRecorder(String host, int port, String newId,
    OSOffice newOSPostOffice, AnimatorOffice newAnimatorPostOffice)
  {
    this(host, port);
    animatorPostOffice = newAnimatorPostOffice;
    osPostOffice = newOSPostOffice;
    id = newId;
  }

  /**
   * Start recording all the messages received.  The file name has a numeric
   * counter added to the end of it for each message.  What happens can simply
   * start at 0 and continue till there are none left.
   *
   * @param newFileName the base file name to save all the messages to.
   */
  public void recordOn(String newFileName)
  {
    fileName = newFileName;
    System.out.println("Set filename: " + fileName);
    osRecorder = new OSMessageRecorder(id, osPostOffice, this);
    animatorRecorder = new AnimatorMessageRecorder(id, animatorPostOffice,
      this);
  }

  /**
   * Remove the OS and Animator recorders from their relevant post offices.  As
   * we don't want to process the messages anymore.
   */
  public void recordOff()
  {
    osRecorder.localSendMessage(new RemoveHandler(osRecorder.getId()));
    animatorRecorder.localSendMessage(new RemoveHandler(animatorRecorder.getId()));
  }

  public void createDirectory(String directory)
  {
    myClient = new FileClient(host, port);
    myClient.openConnection();
    myClient.createRecDir(directory);
    myClient.closeConnection();
  }

  /**
   * Saves all animator messages from the Animators.
   *
   * @param newMessage the message to save permanently.
   */
  public void processAnimatorMessage(AnimatorMessageAdapter newMessage)
  {
    saveMessage(newMessage);
  }

  /**
   * Saves all OS message from the OS components.
   *
   * @param newMessage the message to save permanently.
   */
  public void processOSMessage(OSMessageAdapter newMessage)
  {
    saveMessage(newMessage);
  }

  /**
   * Process the universal messages received by the OS components.
   *
   * @param newMessage the message to save permanently.
   */
  public void processOSUniversalMessage(UniversalMessageAdapter newMessage)
  {
    saveMessage(newMessage);
  }

  /**
   * Process the universal messages received by the Animators.
   *
   * @param newMessage the message to save permanently.
   */
  /*public void processAnimatorUniversalMessage(
    UniversalMessageAdapter newMessage)
  {
    saveMessage(newMessage);
  }*/

  /**
   * Saves all the messages in a consistent.  Called by all 4 process methods.
   * Increments the record counter by one.
   */
  private void saveMessage(Object newMessage)
  {
    myClient = new FileClient(host, port);
    myClient.openConnection();
    try
    {
      myClient.writeRecFile(java.io.File.separatorChar + fileName +
        java.io.File.separatorChar + counter + ".xml", newMessage);
      counter++;
    }
    catch (Exception e)
    {
    }
    myClient.closeConnection();
  }
}