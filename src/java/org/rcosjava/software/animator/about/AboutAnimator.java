package org.rcosjava.software.animator.about;

import java.awt.*;
import javax.swing.ImageIcon;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.RCOSAnimator;

/**
 * Class used for showing authors.
 * <P>
 * @author Andrew Newman.
 * @created 24th of February 1999
 * @version 1.00 $Date$
 */
public class AboutAnimator extends RCOSAnimator
{
  /**
   * Description of the Field
   */
  private final static String MESSENGING_ID = "About";

  /**
   * Description of the Field
   */
  private AboutFrame myFrame;

  /**
   * Constructor for the AboutAnimator object
   *
   * @param postOffice Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param images Description of Parameter
   */
  public AboutAnimator(AnimatorOffice postOffice, int x, int y,
      ImageIcon[] images)
  {
    super(MESSENGING_ID, postOffice);
    myFrame = new AboutFrame(x, y, images);
    myFrame.pack();
    myFrame.setSize(x, y);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    myFrame.setupLayout(c);
  }

  /**
   * Description of the Method
   */
  public void disposeFrame()
  {
    myFrame.dispose();
  }

  /**
   * Description of the Method
   */
  public void showFrame()
  {
    myFrame.setVisible(true);
  }

  /**
   * Description of the Method
   */
  public void hideFrame()
  {
    myFrame.setVisible(false);
  }
}
