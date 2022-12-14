package org.rcosjava.messaging.postoffices.animator;
import org.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import org.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;

/**
 * Recording facilities for all animator messages.
 * <P>
 * @author Andrew Newman
 * @created 25th February 2001
 * @see org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder
 * @version 1.00 $Date$
 */
public class AnimatorMessageRecorder extends AnimatorMessageHandler
{
  /**
   * Notified when it receives a message.
   */
  public UniversalMessageRecorder recorder;

  /**
   * Constructs a message recorder to listen to all the message of a particular
   * post office.
   *
   * @param newId the string identifier to register as.
   * @param newPostOffice the post office to register to.
   * @param newRecorder the parent recorder object.
   */
  public AnimatorMessageRecorder(String newId, AnimatorOffice newPostOffice,
      UniversalMessageRecorder newRecorder)
  {
    super(newId, newPostOffice);
    recorder = newRecorder;
  }

  /**
   * Processes any and all message of animator message type (animator to
   * animator in this case).
   *
   * @param newMessage the message to accept.
   */
  public void processMessage(AnimatorMessageAdapter newMessage)
  {
    if (newMessage.undoableMessage())
    {
      recorder.processAnimatorMessage(newMessage);
    }
  }

  /**
   * Accept the universal messages sent to the post office. This message can
   * come from either an animator or operating system component.
   *
   * @param newMessage the message to accept.
   */
  public void processMessage(UniversalMessageAdapter newMessage)
  {
    //recorder.processAnimatorUniversalMessage(newMessage);
  }
}
