//*************************************************************************
//FILE     : ProgramManagerAnimator.java
//PACKAGE  : Animator
//PURPOSE  : To communicate via messages and via awt Events with the main
//           RCOS frame and other components. It's functions are to set up
//           and comunicate with a remote server for the loading of
//           programs.
//AUTHOR   : Andrew Newman, Brett Carter
//MODIFIED :
//HISTORY  : 19/3/96  Created.
//         : 31/3/96  DJ Added fifoQueue for filenames
//         : 30/12/96 Rewritten with frame moved to Animators.
//         : 1/1/97   Section from Program Manager moved here.
//
//*************************************************************************

package Software.Animator.Process;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Util.FIFOQueue;
import Software.Memory.MemoryRequest;
import MessageSystem.Messages.Message;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.Messages.Universal.UpdateList;

public class ProgramManagerAnimator extends RCOSAnimator
{
  private ProgramManagerFrame pmFrame;
  private boolean fDone = false;
  private String sCurrentFile = "";
  private String sCurrentDirectory = "/";
  private int iCounter;
	private static final String MESSENGING_ID = "ProgramManagerAnimator";

  public ProgramManagerAnimator (AnimatorOffice aPostOffice,
                                 int x, int y, Image[] pmImages)
  {
    super(MESSENGING_ID, aPostOffice);
    pmFrame = new ProgramManagerFrame(x, y, pmImages, this);
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

  public String getCurrentFile()
  {
    return sCurrentFile;
  }

  public void setCurrentFile(String sNewFile)
  {
    sCurrentFile = sNewFile;
  }

  public String getCurrentDirectory()
  {
    return sCurrentDirectory;
  }

  public void setCurrentDirectory(String sNewDirectory)
  {
    sCurrentDirectory = sNewDirectory;
  }

  public ProgramManagerFrame getFrame()
  {
    return pmFrame;
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
      System.out.println("Error processing: "+e);
      e.printStackTrace();
    }
  }

  public void newProcess(boolean bStartTerminal)
  {
    // If the box is checked start a terminal before the process
    // is loaded.
    StartProgram myStartProgram = new StartProgram(this, mhThePostOffice,
			getCurrentDirectory()+getCurrentFile(), bStartTerminal);
  }

  public void upDirectory()
  {
    //Calculate the parent directory.

    if (sCurrentDirectory != "/")
    {
      String tmp = sCurrentDirectory;
      int location;

      location = tmp.lastIndexOf('/',(tmp.length()-2));
      if (location == -1)
      {
        sCurrentDirectory = "/";
      }
      else
      {
        tmp = tmp.substring(0, location+1);
        sCurrentDirectory = tmp.substring(0,location+1);
      }
    }
    updateList();
  }

  public void updateFileList(FIFOQueue data)
  {
    pmFrame.updateFileList(data);
  }

  public void updateDirectoryList(FIFOQueue data)
  {
    pmFrame.updateDirectoryList(data);
  }

  public void updateList()
  {
    UpdateList newMsg = new UpdateList(this,
      getCurrentDirectory());
    sendMessage(newMsg);
  }
}
