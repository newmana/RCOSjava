//***************************************************************************
// FILE     : IPCManagerAnimator.java
// PACKAGE  : Animator
// PURPOSE  : Receives messages from MMU and IPC and manipulates
//            memoryManagerFrame based on messages received.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/1/97  Created.
//
//***************************************************************************/

package net.sourceforge.rcosjava.software.animator.ipc;

import java.awt.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.RCOSList;
import net.sourceforge.rcosjava.software.animator.support.mtgos.MTGO;
import net.sourceforge.rcosjava.software.animator.support.positions.Position;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;

public class IPCManagerAnimator extends RCOSAnimator
{
  private Message msg;
  private IPCManagerFrame ipcFrame;
	private static final String MESSENGING_ID = "IPCManagerAnimator";

  public IPCManagerAnimator (AnimatorOffice aPostOffice,
                             int x, int y, Image[] mmImages)
  {
    super(MESSENGING_ID, aPostOffice);
    ipcFrame = new IPCManagerFrame(x, y, mmImages, this);
    ipcFrame.pack();
    ipcFrame.setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    ipcFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    ipcFrame.dispose();
  }

  public void showFrame()
  {
    ipcFrame.setVisible(true);
  }

  public void hideFrame()
  {
    ipcFrame.setVisible(false);
  }

  public void allocatedPages(MemoryReturn mrReturn)
  {
    ipcFrame.allocatedPages(mrReturn);
  }

  public void deallocatedPages(MemoryReturn mrReturn)
  {
    ipcFrame.deallocatedPages(mrReturn);
  }

  public void readingMemory(MemoryRequest mrRequest)
  {
    ipcFrame.readingMemory(mrRequest.getPID(), mrRequest.getMemoryType());
  }

  public void writingMemory(MemoryRequest mrRequest)
  {
    ipcFrame.writingMemory(mrRequest.getPID(), mrRequest.getMemoryType());
  }

  public void finishedReadingMemory(MemoryRequest mrRequest)
  {
    ipcFrame.finishedReadingMemory(mrRequest.getPID(), mrRequest.getMemoryType());
  }

  public void finishedWritingMemory(MemoryRequest mrRequest)
  {
    ipcFrame.finishedWritingMemory(mrRequest.getPID(), mrRequest.getMemoryType());
  }

  public void semaphoreCreated()
  {
    //Expects process ID
    ipcFrame.semQueueAdd("");
  }

  public void semaphoreClosed()
  {
   ipcFrame.semQueueRemove();
  }

  public void semaphoreWaiting()
  {
  }

  public void sempahoreSignalled()
  {
  }

  public void sharedMemoryOpen()
  {
  }

  public void sharedMemoryClosed()
  {
  }

  public void sharedMemoryRead()
  {
  }

  public void sharedMemoryWrite()
  {
  }

  public synchronized void processMessage (AnimatorMessageAdapter aMsg)
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
      System.err.println(this + "- exception: "+e);
    }
  }
}

