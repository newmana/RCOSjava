//***************************************************************************
// FILE     : RCOSAnimator.java
// PACKAGE  : Animator
// PURPOSE  : Root Animator class.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/1/97  Created.
//
//***************************************************************************/

package Software.Animator;

import java.awt.*;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.PostOffices.MessageHandler;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;

public abstract class RCOSAnimator extends AnimatorMessageHandler
{
  //By default registers to the post office.
  public RCOSAnimator (String myId, AnimatorOffice aPostOffice)
  {
    super(myId, aPostOffice);
  }

  //Interface to frame.
  public abstract void disposeFrame();
  public abstract void setupLayout(Component c);
  public abstract void showFrame();
  public abstract void hideFrame();
  public abstract void processMessage(AnimatorMessageAdapter aMsg);
  public abstract void processMessage(UniversalMessageAdapter aMsg);
}

