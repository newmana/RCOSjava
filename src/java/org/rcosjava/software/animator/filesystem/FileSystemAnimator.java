package org.rcosjava.software.animator.filesystem;

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
public class FileSystemAnimator extends RCOSAnimator
{
  /**
   * Description of the Field
   */
  private final static String MESSENGING_ID = "FileSystemAnimator";
  /**
   * Description of the Field
   */
  private RCOSFrame fsFrame;

  /**
   * Constructor for the FileSystemAnimator object
   *
   * @param aPostOffice Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param cpuImages Description of Parameter
   */
  public FileSystemAnimator(AnimatorOffice aPostOffice, int x, int y,
      Image[] cpuImages)
  {
    super(MESSENGING_ID, aPostOffice);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    fsFrame.setupLayout(c);
  }

  public RCOSPanel getPanel()
  {
    return null;
  }

  /**
   * Description of the Method
   */
  public void disposeFrame()
  {
    fsFrame.dispose();
  }

  /**
   * Description of the Method
   */
  public void showFrame()
  {
    fsFrame.setVisible(true);
  }

  /**
   * Description of the Method
   */
  public void hideFrame()
  {
    fsFrame.setVisible(false);
  }
}
