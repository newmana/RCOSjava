//***************************************************************************
// FILE     : ProcessManagerAnimator.java
// PACKAGE  : Animator
// PURPOSE  : Interface which allows the recording and playback of messages
//            that occur in RCOS.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/1/97  Created.
//
//***************************************************************************/

package Software.Animator.Multimedia;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.NewLabel;
import Software.Animator.Support.RCOSList;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.PostOffices.Animator.AnimatorOffice;

public class MultimediaAnimator extends RCOSAnimator
{
  private MultimediaFrame mmFrame;
	private static final String MESSENGING_ID = "MultimediaAnimator";

  public MultimediaAnimator (AnimatorOffice aPostOffice,
                             int x, int y, Image[] pmImages)
  {
    super(MESSENGING_ID, aPostOffice);
    mmFrame = new MultimediaFrame(x, y, pmImages, this);
    mmFrame.pack();
    mmFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    mmFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    mmFrame.dispose();
  }

  public void showFrame()
  {
    mmFrame.setVisible(true);
  }

  public void hideFrame()
  {
    mmFrame.setVisible(false);
  }

  public void processMessage(AnimatorMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing: "+e);
      e.printStackTrace();
    }
  }

	public void processMessage(UniversalMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing: "+e);
      e.printStackTrace();
    }
  }
}
