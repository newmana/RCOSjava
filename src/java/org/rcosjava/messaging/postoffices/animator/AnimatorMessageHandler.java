package org.rcosjava.messaging.postoffices.animator;

import java.io.Serializable;
import java.lang.reflect.*;
import org.rcosjava.messaging.messages.AddHandler;
import org.rcosjava.messaging.messages.MessageAdapter;
import org.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.messaging.postoffices.SimpleMessageHandler;

/**
 * Provide sending and receiving facilities for all classes.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 21/03/2001 Fixed local send message to call process message instead of
 * local send message of the post office (class cast exception). </DD>
 * <DD> 25/02/2001 Added serialization handlers. </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @created 21 October 2000
 * @version 1.00 $Date$
 */
public class AnimatorMessageHandler extends SimpleMessageHandler
{
  /**
   * The animator post office that all message are sent to.
   */
  protected AnimatorOffice postOffice;

  /**
   * Constructor for the AnimatorMessageHandler object
   *
   * @param newId Description of Parameter
   * @param newPostOffice Description of Parameter
   */
  public AnimatorMessageHandler(String newId, AnimatorOffice newPostOffice)
  {
    super(newId);

    postOffice = newPostOffice;

    // Tell the PostOffice that I'm alive
    AddHandler newMessage = new AddHandler(this, newId, this);

    newPostOffice.processMessage(newMessage);
  }

  /**
   * Description of the Method
   *
   * @param message Description of Parameter
   */
  public void sendMessage(MessageAdapter message)
  {
    postOffice.sendMessage(message);
  }

  /**
   * @param message
   */
  public void sendMessage(AnimatorMessageAdapter message)
  {
    localSendMessage(message);
  }

  /**
   * @param message
   */
  public void sendMessage(UniversalMessageAdapter message)
  {
    postOffice.sendMessage(message);
  }

  /**
   * Description of the Method
   *
   * @param message Description of Parameter
   */
  public void localSendMessage(MessageAdapter message)
  {
    postOffice.processMessage(message);
  }

  /**
   * @param message
   */
  public void localSendMessage(AnimatorMessageAdapter message)
  {
    postOffice.localSendMessage(message);
  }

  /**
   * Description of the Method
   *
   * @param message Description of Parameter
   */
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
   * The method that is called by the post office to a registered handler. This
   * is for Animator Messages only.
   *
   * @param message the message to execute and call.
   */
  public void processMessage(AnimatorMessageAdapter message)
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
   * The method that is called by the post office to a registered handler. This
   * is for Universal Messages only.
   *
   * @param message the message to execute and call.
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
