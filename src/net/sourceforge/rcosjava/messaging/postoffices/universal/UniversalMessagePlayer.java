package net.sourceforge.rcosjava.messaging.postoffices.universal;

import net.sourceforge.rcosjava.messaging.messages.RemoveHandler;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
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
 * A simple player backerer.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd January 2001
 * @see MessageSystem.postoffices.os.OSMessageRecorder
 * @see MessageSystem.postoffices.animator.AnimatorMessageRecorder
 */
public class UniversalMessagePlayer
{
  private static int counter = 0;
  private FileClient myClient;
  private String host;
  private int port;
  private String id;
  private OSOffice osPostOffice;
  private AnimatorOffice animatorPostOffice;
  private String fileName;

  /**
   * @param newHost the local host to connect to.
   * @param newPort the local hosts port to connect to.
   */
  public UniversalMessagePlayer(String newHost, int newPort)
  {
    host = newHost;
    port = newPort;
  }

  /**
   * @param newID the id to register with the offices to.
   * @param newOSPostOffice the new operating system post office to register
   * to.
   * @param newAnimatorOffice the new animator post office to register to.
   */
  public UniversalMessagePlayer(String host, int port, String newId,
    OSOffice newOSPostOffice, AnimatorOffice newAnimatorPostOffice)
  {
    this(host, port);
    animatorPostOffice = newAnimatorPostOffice;
    osPostOffice = newOSPostOffice;
    id = newId;
  }

  /**
   * Reads the next mesage in order and sends it to the appropriate post
   * offices.
   *
   * @param newFileName the base file name to read.
   */
  public void sendNextMessage(String newFileName)
  {
    fileName = newFileName;
    MessageAdapter tmpMessage = (MessageAdapter) readMessage();
    if (tmpMessage.forPostOffice(osPostOffice) &&
      tmpMessage.forPostOffice(animatorPostOffice))
    {
      osPostOffice.localSendMessage((MessageAdapter) tmpMessage);
      animatorPostOffice.localSendMessage((MessageAdapter) tmpMessage);
    }
    else if (tmpMessage.forPostOffice(osPostOffice))
      osPostOffice.localSendMessage((OSMessageAdapter) tmpMessage);
    else if (tmpMessage.forPostOffice(animatorPostOffice))
      animatorPostOffice.localSendMessage((AnimatorMessageAdapter) tmpMessage);
    //System.out.println(counter + "Got Message: " + tmpMessage.getClass().getName());
  }

  /**
   * Saves all the messages in a consistent.  Called by all 4 process methods.
   * Increments the record counter by one.
   */
  private Object readMessage()
  {
    myClient = new FileClient(host, port);
    myClient.openConnection();
    Object tmpObject;
    try
    {
      tmpObject = myClient.getRecFile(java.io.File.separatorChar
        + fileName + java.io.File.separatorChar + (counter) + ".xml");
      counter++;
    }
    catch (Exception e)
    {
      tmpObject = null;
      e.printStackTrace();
    }
    myClient.closeConnection();
    return tmpObject;
  }
}