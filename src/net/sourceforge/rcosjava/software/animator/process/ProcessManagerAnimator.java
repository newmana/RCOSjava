//***************************************************************************
// FILE     : ProcessManagerAnimator.java
// PACKAGE  : Animator
// PURPOSE  : Interface which sends messages to the Process Manager to
//            execute a command given by the user.  Receives messages
//            from the Process Manager about which processes are currently
//            being run.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/1/97  Created.
//
//***************************************************************************/

package net.sourceforge.rcosjava.software.animator.process;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;
import net.sourceforge.rcosjava.software.animator.support.RCOSList;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.Run;
import net.sourceforge.rcosjava.messaging.messages.universal.Step;
import net.sourceforge.rcosjava.messaging.messages.universal.Kill;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.util.LIFOQueue;

public class ProcessManagerAnimator extends RCOSAnimator
{
  private LIFOQueue currentProcesses;
  private MessageAdapter maMsg;
  private ProcessManagerFrame pmFrame;
	private static final String MESSENGING_ID = "ProcessManagerAnimator";

  public ProcessManagerAnimator (AnimatorOffice aPostOffice,
                                 int x, int y, Image[] pmImages)
  {
    super(MESSENGING_ID, aPostOffice);
    currentProcesses = new LIFOQueue(5,5);
    pmFrame = new ProcessManagerFrame(x, y, pmImages, this);
    pmFrame.pack();
    pmFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    pmFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    pmFrame.dispose();
  }

  public void showFrame()
  {
    pmFrame.setVisible(true);
  }

  public void hideFrame()
  {
    pmFrame.setVisible(false);
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

  //add new process to list
  public void newProcess(Integer iProcess)
  {
    currentProcesses.insert(iProcess);
    updateProcessList();
  }

  //delete process from list
  public void deleteProcess(Integer iProcess)
  {
    currentProcesses.goToHead();
    while (!currentProcesses.atTail())
    {
      if (((Integer) currentProcesses.peek()).intValue() ==
        (iProcess).intValue())
      {
        int tmp = ((Integer) currentProcesses.retrieveCurrent()).intValue();
      }
      currentProcesses.goToNext();
    }
    updateProcessList();
  }

  public void updateProcessList()
  {
    ((ProcessManagerFrame) pmFrame).clearProcesses();
    if (!currentProcesses.queueEmpty())
    {
      currentProcesses.goToHead();
      while(!currentProcesses.atTail())
      {
        Integer tmp = (Integer) currentProcesses.peek();
        try
        {
          ((ProcessManagerFrame) pmFrame).addProcess(
            String.valueOf(tmp.intValue()));
        }
        catch (Exception e)
        {
          System.out.println(this + "- exception: " + e);
        }
        currentProcesses.goToNext();
      }
    }
  }

  public void run()
  {
    Run newMsg = new Run(this);
    sendMessage(newMsg);
  }

  public void step()
  {
    Step newMsg = new Step(this);
    sendMessage(newMsg);
  }

  public void kill(int iProcessID)
  {
    Kill newMsg = new Kill(this, iProcessID);
    sendMessage(newMsg);
  }
}
