package Software.Animator;

import java.awt.*;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.PostOffices.MessageHandler;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;

/**
 * Root Animator class.
 * <P>
 * HISTORY:
 * <P>
 * @author Andrew Newman.
 * @created    10th January 1996
 * @version 1.00 $Date$
 */
public abstract class RCOSAnimator extends AnimatorMessageHandler
{
  //By default registers to the post office.
  public RCOSAnimator (String myId, AnimatorOffice aPostOffice)
  {
    super(myId, aPostOffice);
  }

  //Interface to frame.
  public abstract void disposeFrame();
  public abstract void setupLayout(Component c);
  public abstract void showFrame();
  public abstract void hideFrame();
  public abstract void processMessage(AnimatorMessageAdapter aMsg);
  public abstract void processMessage(UniversalMessageAdapter aMsg);
}

