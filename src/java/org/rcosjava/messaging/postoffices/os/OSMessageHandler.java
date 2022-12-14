package org.rcosjava.messaging.postoffices.os;

import java.lang.reflect.*;
import org.rcosjava.messaging.messages.AddHandler;
import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.messages.os.OSMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.messaging.postoffices.SimpleMessageHandler;

import org.apache.log4j.*;

/**
 * Provide sending and receiving facilities for all classes.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 21/03/2001 Fixed local send message to call process message instead of
 * local send message of the post office (class cast exception). </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @created 21 October 2000
 * @see org.rcosjava.messaging.postoffices.SimpleMessageHandler
 * @version 1.00 $Date$
 */
public class OSMessageHandler extends SimpleMessageHandler
{
  /**
   * Serial id.
   */
  private static final long serialVersionUID = -3208076811777456137L;

  /**
   * Logging class.
   */
  private final static Logger log = Logger.getLogger(OSMessageHandler.class);

  /**
   * The operating system post office that will handle all messages.
   */
  protected OSOffice postOffice;

  /**
   * Register the handler with the given Id to the given postoffice. Adds a
   * handler to the post office to handle passing messages to this component.
   *
   * @param newId the unique identifier used to register to the post office.
   * @param newPostOffice the post office to register to.
   */
  public OSMessageHandler(String newId, OSOffice newPostOffice)
  {
    super(newId);
    register(newId, newPostOffice);
  }

  /**
   * Register with the OS office.
   *
   * @param newId the unique string identifier for the handler.
   * @param newPostOffice the post office to register to.
   */
  protected void register(String newId, OSOffice newPostOffice)
  {
    // Set the current post office
    postOffice = newPostOffice;

    // Tell the PostOffice that I'm alive
    AddHandler newMessage = new AddHandler(this, newId, this);
    newPostOffice.processMessage(newMessage);
  }

  /**
   * Description of the Method
   *
   * @param adapter Description of Parameter
   */
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
    postOffice.sendMessage(adapter);
  }

  /**
   * Description of the Method
   *
   * @param adapter Description of Parameter
   */
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

  /**
   * Description of the Method
   *
   * @param message Description of Parameter
   */
  public void processMessage(MessageAdapter message)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Processing message type: " + message.getType());
      log.debug("Processing message processing: " + this.getClass().getName());
    }

    try
    {
      processMessage((UniversalMessageAdapter) message);
    }
    catch (Exception e)
    {
      log.error("Error invoking processMessage on: " +
          this.getClass().getName(), e);
    }
  }

  /**
   * The method that is called by the post office of a registered handler of the
   * message. This is for only OS Messages.
   *
   * @param message Description of Parameter
   */
  public void processMessage(OSMessageAdapter message)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Processing message type: " + message.getType());
      log.debug("Processing message processing: " + this.getClass().getName());
    }

    try
    {
      Class[] classes = {this.getClass()};
      Method method = message.getClass().getMethod("doMessage", classes);
      Object[] args = {this};
      method.invoke(message, args);
    }
    catch (Exception e)
    {
      log.error("Error processing OS Message, invoking doMessage on: " +
          this.getClass().getName(), e);
    }
  }

  /**
   * The method that is called by the post office of a registered handler of the
   * message. This is for only Universal Messages.
   *
   * @param message Description of Parameter
   */
  public void processMessage(UniversalMessageAdapter message)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Processing message type: " + message.getType());
      log.debug("Processing message processing: " + this.getClass().getName());
    }

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
      log.error("Error processing Universal Message, invoking doMessage on: " +
          this.getClass().getName(), e);
    }
  }
}
