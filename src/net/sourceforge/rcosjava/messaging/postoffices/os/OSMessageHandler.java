package net.sourceforge.rcosjava.messaging.postoffices.os;

import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.PostOffice;
import net.sourceforge.rcosjava.messaging.messages.AddHandler;

import java.lang.reflect.*;

/**
 * Provide sending and receiving facilities for all classes.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 21/03/2001 Fixed local send message to call process message instead of
 * local send message of the post office (class cast exception).
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @created 21 October 2000
 * @version 1.00 $Date$
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
   * @param newId the unique identifier used to register to the post office.
   * @param newPostOffice the post office to register to.
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

  public void sendMessage(MessageAdapter adapter)
  {
    postOffice.sendMessage(adapter);
  }

  /**
   * Call the post office's sendMessage.
   *
   * @param adapter the message being sent and determines the method called.
   */
  public void sendMessage(UniversalMessageAdapter adapter)
  {
    postOffice.sendMessage(adapter);
  }

  /**
   * Call the post office's sendMessage.
   *
   * @param adapter the message being sent and determines the method called.
   */
  public void sendMessage(OSMessageAdapter adapter)
  {
    localSendMessage(adapter);
  }

  public void localSendMessage(MessageAdapter adapter)
  {
    postOffice.processMessage(adapter);
  }

  /**
   * Call the post office's process message, meaning that it will only be
   * processed locally by the post office and not sent on anywhere.
   *
   * @param adapter the message being sent and determines the method called.
   */
  public void localSendMessage(OSMessageAdapter adapter)
  {
     postOffice.localSendMessage(adapter);
  }

  public void processMessage(MessageAdapter message)
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
   * The method that is called by the post office of a registered handler of
   * the message.  This is for only OS Messages.
   */
  public void processMessage(OSMessageAdapter message)
  {
    try
    {
      Class[] classes = {this.getClass()};
      Method method = message.getClass().getMethod(
        "doMessage", classes);

      Object[] args = {this};
      method.invoke(message, args);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * The method that is called by the post office of a registered handler of
   * the message.  This is for only Universal Messages.
   */
  public void processMessage(UniversalMessageAdapter message)
  {
    try
    {
      Class[] classes = {this.getClass()};

      Method method = message.getClass().getSuperclass().getMethod(
        "doMessage", classes);

      Object[] args = {this};
      method.invoke(message, args);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
