//***************************************************************************
// FILE     : ProcessSchedulerAnimator.java
// PACKAGE  : Animator
// PURPOSE  : Receives messages from Process Scheduler and manipulates
//            processScheduler frame based on messages received.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/1/97  Created.
//
//***************************************************************************/

package Software.Animator.Process;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.RCOSBox;
import Software.Animator.Support.GraphicButton;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.Message;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.Messages.Animator.ShowCPU;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.Messages.Universal.Quantum;
import Software.Process.RCOSProcess;
import Software.Process.ProcessScheduler;

public class ProcessSchedulerAnimator extends RCOSAnimator
{
  private static ProcessSchedulerFrame psFrame;
  private static String sCurrentProcess = new String();
	private static final String MESSENGING_ID = "ProcessSchedulerAnimator";

  public ProcessSchedulerAnimator (AnimatorOffice aPostOffice,
                                  int x, int y, Image[] psImages)
  {
    super(MESSENGING_ID, aPostOffice);
    psFrame = new ProcessSchedulerFrame(x, y, psImages, this);
    psFrame.pack();
    psFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    psFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    psFrame.dispose();
  }

  public void showFrame()
  {
    psFrame.setVisible(true);
  }

  public void hideFrame()
  {
    psFrame.setVisible(false);
  }

  public synchronized void processMessage(AnimatorMessageAdapter aMsg)
  {
  }

  public synchronized void processMessage(UniversalMessageAdapter aMsg)
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

  public void sendQuantum(Integer iQuantum)
  {
    //Create and send Quantum message
    Quantum msg = new Quantum(this, iQuantum.intValue());
    sendMessage(msg);
  }

  private void addQueue(int iQType, int iPID)
  {
    psFrame.addQueue(iQType, iPID);
  }

  private void removeQueue(int iQType, int iPID)
  {
    psFrame.removeQueue(iQType, iPID);
  }

  public void processFinished(int iPID)
  {
    psFrame.processFinished(iPID);
  }

  public void killProcess(int iPID)
  {
    psFrame.killProcess(iPID);
  }

  public void cpuToBlocked(int iPID)
  {
    psFrame.cpuToBlocked(iPID);
    addQueue(ProcessScheduler.BLOCKEDQ, iPID);
  }

  public void blockedToReady(int iPID)
  {
    removeQueue(ProcessScheduler.BLOCKEDQ, iPID);
    psFrame.blockedToReady(iPID);
    addQueue(ProcessScheduler.READYQ, iPID);
  }

  public void cpuToReady(int iPID)
  {
    psFrame.cpuToReady(iPID);
    addQueue(ProcessScheduler.READYQ, iPID);
  }

  public void readyToCPU(int iPID)
  {
    removeQueue(ProcessScheduler.READYQ, iPID);
    psFrame.readyToCPU(iPID);
  }

  public void zombieToReady(int iPID)
  {
    removeQueue(ProcessScheduler.ZOMBIEQ, iPID);
    psFrame.zombieToReady(iPID);
    addQueue(ProcessScheduler.READYQ, iPID);
  }

  public void newProcess(int iPID)
  {
    psFrame.newProcess(iPID);
    addQueue(ProcessScheduler.ZOMBIEQ, iPID);
  }

  public void showCPU()
  {
    ShowCPU scmMsg = new ShowCPU(this);
    localSendMessage(scmMsg);
  }
}

