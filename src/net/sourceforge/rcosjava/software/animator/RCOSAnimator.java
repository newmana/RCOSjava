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
 * <DT><B>History:</B>
 * <DD></DD></DT>
 * <P>
 * @author Andrew Newman.
 * @created 10th January 1996
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

  /**
   * Calls dispose frame on the internal frame object.
   */
  public abstract void disposeFrame();

  /**
   * Sets the default colours and creates the layout of the frame.
   */
  public abstract void setupLayout(Component c);

  /**
   * Calls show frame on the internal frame object.
   */
  public abstract void showFrame();

  /**
   * Calls hide frame on the internal frame object.
   */
  public abstract void hideFrame();
  public abstract void processMessage(AnimatorMessageAdapter message);
  public abstract void processMessage(UniversalMessageAdapter message);
}
