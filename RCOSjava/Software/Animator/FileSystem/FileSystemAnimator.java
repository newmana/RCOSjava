//***************************************************************************
// FILE     : FileSystemAnimator.java
// PACKAGE  : Animator.File
// PURPOSE  : Class used to animate File System
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/01/96  Created
//
//***************************************************************************/

package Software.Animator.FileSystem;

import java.awt.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.RCOSFrame;
import Software.Memory.MemoryRequest;
import MessageSystem.Messages.Message;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;

public class FileSystemAnimator extends RCOSAnimator
{
  private RCOSFrame fsFrame;
	private static final String MESSENGING_ID = "FileSystemAnimator";

  public FileSystemAnimator(AnimatorOffice aPostOffice, int x, int y,
                            Image[] cpuImages)
  {
    super(MESSENGING_ID, aPostOffice);
  }

  public void setupLayout(Component c)
  {
    fsFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    fsFrame.dispose();
  }

  public void showFrame()
  {
    fsFrame.setVisible(true);
  }

  public void hideFrame()
  {
    fsFrame.setVisible(false);
  }

	public void processMessage(AnimatorMessageAdapter aMessage)
	{
	}

	public void processMessage(UniversalMessageAdapter aMessage)
	{
	}
}
