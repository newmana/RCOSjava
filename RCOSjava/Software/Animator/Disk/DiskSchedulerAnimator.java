//***************************************************************************
// FILE     : DiskSchedulerAnimator.java
// PACKAGE  : Animator.Disk
// PURPOSE  : Class used to animate Disk Scheduler
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/01/96  Created
//
//***************************************************************************/

package Software.Animator.Disk;

import java.awt.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.RCOSFrame;
import Software.Memory.MemoryRequest;
import MessageSystem.Messages.Message;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;

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