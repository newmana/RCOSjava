package net.sourceforge.rcosjava.software.animator;

import java.awt.*;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;

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
  /**
   * By default registers to the post office.
   */
  public RCOSAnimator (String newId, AnimatorOffice newPostOffice)
  {
    super(newId, newPostOffice);
  }

  //Interface to frame.
  public abstract void disposeFrame();
  public abstract void setupLayout(Component c);
  public abstract void showFrame();
  public abstract void hideFrame();
  public abstract void processMessage(AnimatorMessageAdapter aMsg);
  public abstract void processMessage(UniversalMessageAdapter aMsg);
}
