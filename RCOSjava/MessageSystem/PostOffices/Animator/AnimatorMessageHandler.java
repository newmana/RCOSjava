package MessageSystem.PostOffices.Animator;

import MessageSystem.Messages.AddHandler;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.PostOffices.SimpleMessageHandler;
import java.io.Serializable;

/**
 * Provide sending and receiving facilities for all classes.
 *
 * HISTORY:    25/02/2001   Added serialization handlers.
 *
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

  /**
   *
   *
   * @param message the message to send
   */
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

  /**
   *
   *
   * @param message
   */
  public void localSendMessage(MessageAdapter message)
  {
    postOffice.localSendMessage(message);
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

  /**
   *
   *
   * @param message
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
   *
   *
   * @param message
   */
  public abstract void processMessage(AnimatorMessageAdapter message);

  /**
   *
   *
   * @param message
   */
  public abstract void processMessage(UniversalMessageAdapter message);
}
