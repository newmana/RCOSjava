package net.sourceforge.rcosjava.messaging.postoffices.animator;

import net.sourceforge.rcosjava.messaging.messages.AddHandler;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.SimpleMessageHandler;
import java.io.Serializable;

/**
 * Provide sending and receiving facilities for all classes.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 21/03/2001 Fixed local send message to call process message instead of
 * local send message of the post office (class cast exception).
 * </DD><DD>
 * 25/02/2001 Added serialization handlers.
 * </DD></DT>
 * <P>
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @version 1.00 $Date$
 * @created 21 October 2000
 */
public abstract class AnimatorMessageHandler extends SimpleMessageHandler
{
  /**
   *  Description of the Field
   */
  protected AnimatorOffice postOffice;

  /**
   *  Constructor for the AnimatorMessageHandler object
   */
  public AnimatorMessageHandler()
  {
  }

  /**
   * Constructor for the AnimatorMessageHandler object
   *
   * @param newID
   * @param mhNewPostOffice
   */
  public AnimatorMessageHandler(String newId, AnimatorOffice newPostOffice)
  {
    id = newId;
    // Save myId and a pointer the PostOffice
    postOffice = newPostOffice;
    // Tell the PostOffice that I'm alive
    AddHandler newMessage = new AddHandler(this, newId, this);
    postOffice.processMessage(newMessage);
  }

  public void sendMessage(MessageAdapter message)
  {
    postOffice.sendMessage(message);
  }

  /**
   *
   *
   * @param message
   */
  public void sendMessage(AnimatorMessageAdapter message)
  {
    localSendMessage(message);
  }

  /**
   *
   *
   * @param message
   */
  public void sendMessage(UniversalMessageAdapter message)
  {
    postOffice.sendMessage(message);
  }

  public void localSendMessage(MessageAdapter message)
  {
    postOffice.processMessage(message);
  }

  /**
   *
   *
   * @param message
   */
  public void localSendMessage(AnimatorMessageAdapter message)
  {
    postOffice.localSendMessage(message);
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
   * The method that is called by the post office to a registered handler.  This
   * is for Animator Messages only.
   *
   * @param message the message to execute and call.
   */
  public abstract void processMessage(AnimatorMessageAdapter message);

  /**
   * The method that is called by the post office to a registered handler.  This
   * is for Universal Messages only.
   *
   * @param message the message to execute and call.
   */
  public abstract void processMessage(UniversalMessageAdapter message);
}
