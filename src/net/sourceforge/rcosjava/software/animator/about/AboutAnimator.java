//***************************************************************************
// FILE     : AboutAnimator.java
// PACKAGE  : Software.animator.About
// PURPOSE  : Class used for showing authors.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/02/99 Created
//
//***************************************************************************/

package net.sourceforge.rcosjava.software.animator.about;

import java.awt.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;

public class AboutAnimator extends RCOSAnimator
{
  private AboutFrame aFrame;
	private static final String MESSENGING_ID = "About";

  public AboutAnimator(AnimatorOffice aPostOffice, int x, int y,
                     Image[] cpuImages)
  {
    super(MESSENGING_ID, aPostOffice);
    aFrame = new AboutFrame(x, y, cpuImages);
    aFrame.pack();
    aFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    aFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    aFrame.dispose();
  }

  public void showFrame()
  {
    aFrame.setVisible(true);
  }

  public void hideFrame()
  {
    aFrame.setVisible(false);
  }

	public void processMessage(AnimatorMessageAdapter aMessage)
	{
	}

	public void processMessage(UniversalMessageAdapter aMessage)
	{
	}
}