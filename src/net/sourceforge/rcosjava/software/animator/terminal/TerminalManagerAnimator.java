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

package net.sourceforge.rcosjava.software.animator.terminal;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalToggle;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalFront;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalBack;

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