//***************************************************************************
// FILE     : FileSystemAnimator.java
// PACKAGE  : Animator.File
// PURPOSE  : Class used to animate File System
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/01/96  Created
//
//***************************************************************************/

package net.sourceforge.rcosjava.software.animator.filesystem;

import java.awt.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;

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
}
