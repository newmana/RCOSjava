package net.sourceforge.rcosjava.software.animator.about;

import java.awt.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;

/**
 * Class used for showing authors.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th of February 1999
 */
public class AboutAnimator extends RCOSAnimator
{
  private AboutFrame myFrame;
  private static final String MESSENGING_ID = "About";

  public AboutAnimator(AnimatorOffice postOffice, int x, int y,
    Image[] images)
  {
    super(MESSENGING_ID, postOffice);
    myFrame = new AboutFrame(x, y, images);
    myFrame.pack();
    myFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    myFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    myFrame.dispose();
  }

  public void showFrame()
  {
    myFrame.setVisible(true);
  }

  public void hideFrame()
  {
    myFrame.setVisible(false);
  }
}