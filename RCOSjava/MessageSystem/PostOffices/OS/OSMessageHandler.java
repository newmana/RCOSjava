package MessageSystem.PostOffices.OS;

import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.PostOffices.PostOffice;
import MessageSystem.Messages.AddHandler;

/**
 * Provide sending and receiving facilities for all classes.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 21/03/2001 Fixed local send message to call process message instead of
 * local send message of the post office (class cast exception).
 * </DD></DT>
 *
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @created 21 October 2000
 * @version 1.00 $Date$
 * @see MessageSystem.PostOffices.SimpleMessageHandler
 */
public abstract class OSMessageHandler extends SimpleMessageHandler
{
  /**
   *  The operating system post office that will handle all messages.
   */
  protected OSOffice postOffice;

  /**
   *  Do nothing constructor.
   */
  public OSMessageHandler()
  {
  }

  /**
   * Register the handler with the given Id to the given postoffice.  Adds a
   * handler to the post office to handle passing messages to this component.
   *
<<<<<<< OSMessageHandler.java
   * @param newId the unique identifier used to register to the post office.
   * @param newPostOffice the post office to register to.
=======
   * @param newId
   * @param newPostOffice
>>>>>>> 1.6
   */
  public OSMessageHandler(String newId, OSOffice newPostOffice)
  {
    id = newId;
    // Save myId and a pointer the PostOffice
    postOffice = newPostOffice;
    // Tell the PostOffice that I'm alive
    AddHandler newMessage = new AddHandler(this, newId, this);
    newPostOffice.processMessage(newMessage);
  }

<<<<<<< OSMessageHandler.java
  public void sendMessage(MessageAdapter adapter)
=======
  /**
   *  Description of the Method
   *
   * @param  adapter  Description of Parameter
   */
  public void sendMessage(MessageAdapter adapter)
>>>>>>> 1.6
  {
    postOffice.sendMessage(adapter);
  }

  /**
   * Call the post office's sendMessage.
   *
<<<<<<< OSMessageHandler.java
   * @param adapter the message being sent and determines the method called.
=======
   * @param  adapter  Description of Parameter
>>>>>>> 1.6
   */
  public void sendMessage(UniversalMessageAdapter adapter)
  {
    postOffice.sendMessage(adapter);
  }

  /**
   * Call the post office's sendMessage.
   *
<<<<<<< OSMessageHandler.java
   * @param adapter the message being sent and determines the method called.
=======
   * @param  adapter  Description of Parameter
>>>>>>> 1.6
   */
  public void sendMessage(OSMessageAdapter adapter)
  {
    localSendMessage(adapter);
  }

<<<<<<< OSMessageHandler.java
  public void localSendMessage(MessageAdapter adapter)
=======
  /**
   *  Description of the Method
   *
   * @param  adapter  Description of Parameter
   */
  public void localSendMessage(MessageAdapter adapter)
>>>>>>> 1.6
  {
    postOffice.processMessage(adapter);
  }

  /**
   * Call the post office's process message, meaning that it will only be
   * processed locally by the post office and not sent on anywhere.
   *
<<<<<<< OSMessageHandler.java
   * @param adapter the message being sent and determines the method called.
=======
   * @param  adapter  Description of Parameter
>>>>>>> 1.6
   */
  public void localSendMessage(OSMessageAdapter adapter)
  {
    postOffice.localSendMessage(adapter);
  }

<<<<<<< OSMessageHandler.java
  public void processMessage(MessageAdapter message)
=======
  /**
   *  Description of the Method
   *
   * @param  message  Description of Parameter
   */
  public void processMessage(MessageAdapter message)
>>>>>>> 1.6
  {
    try
    {
      processMessage((UniversalMessageAdapter) message);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
<<<<<<< OSMessageHandler.java
   * The method that is called by the post office of a registered handler of
   * the message.  This is for only OS Messages.
=======
   *  Description of the Method
   *
   * @param  message  Description of Parameter
>>>>>>> 1.6
   */
  public abstract void processMessage(OSMessageAdapter message);

  /**
<<<<<<< OSMessageHandler.java
   * The method that is called by the post office of a registered handler of
   * the message.  This is for only Universal Messages.
=======
   *  Description of the Method
   *
   * @param  message  Description of Parameter
>>>>>>> 1.6
   */
  public abstract void processMessage(UniversalMessageAdapter message);
}
