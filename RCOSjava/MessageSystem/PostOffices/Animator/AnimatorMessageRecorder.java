package MessageSystem.PostOffices.Animator;

import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.PostOffices.Universal.UniversalMessageRecorder;

/**
 * Recording facilities for all animator messages.
 * <P>
 * @author Andrew Newman
 * @created 25th February 2001
 * @version 1.00 $Date$
 * @see MessageSystem.PostOffices.Universal.UniversalMessageRecorder
 */
public class AnimatorMessageRecorder extends AnimatorMessageHandler
{
  /**
   * Notified when it receives a message.
   */
  public UniversalMessageRecorder recorder;

  /**
   * Null constructor.  Currently does nothing.
   */
  public AnimatorMessageRecorder()
  {
  }

  /**
   * Constructs a message recorder to listen to all the message of a particular
   * post office.
   *
   * @param newId the string identifier to register as.
   * @param newPostOffice the post office to register to.
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
    recorder.processAnimatorMessage(newMessage);
  }

  /**
   * Accept the universal messages sent to the post office.  This message
   * can come from either an animator or operating system component.
   *
   * @param newMessage the message to accept.
   */
  public void processMessage(UniversalMessageAdapter newMessage)
  {
    //recorder.processAnimatorUniversalMessage(newMessage);
  }
}
