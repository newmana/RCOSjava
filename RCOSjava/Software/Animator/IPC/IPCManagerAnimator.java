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

package Software.Animator.IPC;

import java.awt.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.RCOSList;
import Software.Animator.Support.MTGOS.MTGO;
import Software.Animator.Support.Positions.Position;
import Software.Memory.MemoryRequest;
import Software.Memory.MemoryReturn;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.Messages.Message;
import MessageSystem.PostOffices.Animator.AnimatorOffice;

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

