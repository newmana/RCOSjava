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

/**
 * Communicates via messages and via awt Events with the main RCOS frame and
 * other components. It's functions are to set up and comunicate with a
 * remote server for the loading of programs.
 * <P>
 * HISTORY: 31/3/96  DJ Added fifoQueue for filenames.<BR>
 *          30/12/96 Rewritten with frame moved to Animators.<BR>
 *          1/1/97   Section from Program Manager moved here.<BR>
 * <P>
 * @author Andrew Newman.
 * @created 19th March 1996
 * @version 1.00 $Date$
 */
public class ProgramManagerAnimator extends RCOSAnimator
{
  private ProgramManagerFrame pmFrame;
  private String currentFile = "";
  private String currentDirectory = java.io.File.separator;
  private static final String MESSENGING_ID = "ProgramManagerAnimator";

  public ProgramManagerAnimator (AnimatorOffice postOffice,
                                 int x, int y, Image[] pmImages)
  {
    super(MESSENGING_ID, postOffice);
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
    return currentFile;
  }

  public void setCurrentFile(String newFile)
  {
    currentFile = newFile;
  }

  public String getCurrentDirectory()
  {
    return currentDirectory;
  }

  public void setCurrentDirectory(String newDirectory)
  {
    currentDirectory = newDirectory;
  }

  public ProgramManagerFrame getFrame()
  {
    return pmFrame;
  }

  public synchronized void processMessage(AnimatorMessageAdapter message)
  {
  }

  public synchronized void processMessage(UniversalMessageAdapter message)
  {
    try
    {
      message.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error processing: "+e);
      e.printStackTrace();
    }
  }

  public void newProcess(boolean startTerminal)
  {
    // If the box is checked start a terminal before the process
    // is loaded.
    StartProgram myStartProgram = new StartProgram(this, postOffice,
      getCurrentDirectory()+getCurrentFile(), startTerminal);
  }

  public void upDirectory()
  {
    //Calculate the parent directory.

    if (currentDirectory != java.io.File.separator)
    {
      String tmp = currentDirectory;
      int location;

      location = tmp.lastIndexOf(java.io.File.separatorChar,(tmp.length()-2));
      if (location == -1)
      {
        currentDirectory = java.io.File.separator;
      }
      else
      {
        tmp = tmp.substring(0, location+1);
        currentDirectory = tmp.substring(0,location+1);
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

  /**
   * Handle updating the list.  Send the
   * @see MessageSystem.Messages.Universal.UpdateList message.
   */
  public void updateList()
  {
    UpdateList newMsg = new UpdateList(this,
      getCurrentDirectory(), 1);
    sendMessage(newMsg);
  }
}
