//********************************************************************//
// FILE     : TerminalManagerAnimator.java
// PACKAGE  : Animator
// PURPOSE  : Class used to animate Terminal Manager
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/12/96  Created
//            21/02/97  Cleaned up.
//
//********************************************************************//

package Software.Animator.Terminal;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.RCOSFrame;
import MessageSystem.Messages.Message;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.Universal.TerminalToggle;
import MessageSystem.Messages.Universal.TerminalFront;
import MessageSystem.Messages.Universal.TerminalBack;

public class TerminalManagerAnimator extends RCOSAnimator
{
  private TerminalManagerFrame tmFrame;
	private static final String MESSENGING_ID = "TerminalManagerAnimator";

  public TerminalManagerAnimator(AnimatorOffice aPostOffice, int x, int y,
    Image[] tmImages, int mTerm, int mCol, int mRow)
  {
    super(MESSENGING_ID, aPostOffice);
    tmFrame = new TerminalManagerFrame(x, y, tmImages, mTerm, mCol, mRow, this);
    tmFrame.pack();
    tmFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    tmFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    tmFrame.dispose();
  }

  public void showFrame()
  {
    tmFrame.setVisible(true);
  }

  public void hideFrame()
  {
    tmFrame.setVisible(false);
  }

  public void processMessage(AnimatorMessageAdapter aMsg)
  {
  }

  public void processMessage(UniversalMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error processing: "+e);
      e.printStackTrace();
    }
  }

  public void terminalOff(int iTemp)
  {
    tmFrame.terminalOff(iTemp);
  }

  public void terminalOn(int iTemp)
  {
    tmFrame.terminalOn(iTemp);
  }

  public void sendToggleTerminal(int iTemp)
  {
    TerminalToggle ttMessage = new TerminalToggle(this, iTemp);
    sendMessage(ttMessage);
  }

  public void terminalFront(int iTemp)
  {
    tmFrame.terminalFront(iTemp);
  }

  public void sendTerminalFront(int iTemp)
  {
    TerminalFront tfMessage = new TerminalFront(this, iTemp);
    sendMessage(tfMessage);
  }

  public void terminalBack(int iTemp)
  {
    tmFrame.terminalBack(iTemp);
  }

  public void sendTerminalBack(int iTemp)
  {
    TerminalBack tbMessage = new TerminalBack(this, iTemp);
    sendMessage(tbMessage);
  }
}