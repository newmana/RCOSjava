package MessageSystem.PostOffices.Animator;

import MessageSystem.PostOffices.SimpleMessageHandler;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.PostOffices.PostOffice;
import MessageSystem.Messages.AddHandler;

public class AnimatorMessageRecorder extends AnimatorMessageHandler
{
  public AnimatorMessageRecorder()
  {
  }

  public AnimatorMessageRecorder(String newID, AnimatorOffice mhNewPostOffice)
  {
    super(newID, mhNewPostOffice);
  }

  public void processMessage(AnimatorMessageAdapter mMessage)
  {
    System.out.println("Animator Got Animator Message: " + mMessage);
  }

  public void processMessage(UniversalMessageAdapter mMessage)
  {
    System.out.println("Animator Got Universal Message: " + mMessage);
  }
}
