package MessageSystem.PostOffices.Universal;

import MessageSystem.Messages.RemoveHandler;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;

import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.PostOffices.Animator.AnimatorMessageRecorder;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.PostOffices.OS.OSMessageRecorder;

import RCOS;

import pll2.FileClient;

/**
 * A simple player backerer.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 2nd January 2001
 * @see MessageSystem.PostOffices.OS.OSMessageRecorder
 * @see MessageSystem.PostOffices.Animator.AnimatorMessageRecorder
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
    System.out.println("Got Message: " + tmpMessage.getClass().getName());
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
      tmpObject = myClient.getRecFile("/" + fileName + (counter) + ".xml");
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