package org.rcosjava.software.animator;

import java.awt.Component;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;

/**
 * Root Animator class.
 * <P>
 * <DT><B>History:</B>
 * <DD> </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 10th January 1996
 * @version 1.00 $Date$
 */
public abstract class RCOSAnimator extends AnimatorMessageHandler
{
  /**
   * By default registers to the post office.
   *
   * @param newId Description of Parameter
   * @param newPostOffice Description of Parameter
   */
  public RCOSAnimator(String newId, AnimatorOffice newPostOffice)
  {
    super(newId, newPostOffice);
  }

  /**
   * Sets the default colours and creates the layout of the frame.
   *
   * @param c Description of Parameter
   */
  public abstract void setupLayout(Component c);

  /**
   * Returns the associated panel.
   *
   * @return the associated panel.
   */
  public abstract RCOSPanel getPanel();
}
