//***************************************************************************
// FILE     : DiskSchedulerAnimator.java
// PACKAGE  : Animator.Disk
// PURPOSE  : Class used to animate Disk Scheduler
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/01/96  Created
//
//***************************************************************************/

package net.sourceforge.rcosjava.software.animator.disk;

import java.awt.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;

public class DiskSchedulerAnimator extends RCOSAnimator
{
  private RCOSFrame dsFrame;
	private static final String MESSENGING_ID = "DiskSchedulerAnimator";

  public DiskSchedulerAnimator(AnimatorOffice aPostOffice, int x, int y,
                     Image[] cpuImages)
  {
    super(MESSENGING_ID, aPostOffice);
  }

  public void setupLayout(Component c)
  {
    dsFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    dsFrame.dispose();
  }

  public void showFrame()
  {
    dsFrame.setVisible(true);
  }

  public void hideFrame()
  {
    dsFrame.setVisible(false);
  }

	public void processMessage(AnimatorMessageAdapter aMessage)
	{
	}

	public void processMessage(UniversalMessageAdapter aMessage)
	{
	}
}