package org.rcosjava.software.animator.disk;

import java.awt.*;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.RCOSPanel;

/**
 * Description of the Class
 *
 * @author administrator
 * @created 28 April 2002
 */
public class DiskSchedulerAnimator extends RCOSAnimator
{
  /**
   * Description of the Field
   */
  private final static String MESSENGING_ID = "DiskSchedulerAnimator";
  /**
   * Description of the Field
   */
  private RCOSFrame dsFrame;

  /**
   * Constructor for the DiskSchedulerAnimator object
   *
   * @param aPostOffice Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param cpuImages Description of Parameter
   */
  public DiskSchedulerAnimator(AnimatorOffice aPostOffice, int x, int y,
      Image[] cpuImages)
  {
    super(MESSENGING_ID, aPostOffice);
  }

  public RCOSPanel getPanel()
  {
    return null;
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    dsFrame.setupLayout(c);
  }

  /**
   * Description of the Method
   */
  public void disposeFrame()
  {
    dsFrame.dispose();
  }

  /**
   * Description of the Method
   */
  public void showFrame()
  {
    dsFrame.setVisible(true);
  }

  /**
   * Description of the Method
   */
  public void hideFrame()
  {
    dsFrame.setVisible(false);
  }
}
