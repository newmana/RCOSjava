package org.rcosjava.software.animator.process;
import java.applet.*;

import java.awt.*;
import javax.swing.ImageIcon;
import java.net.*;
import java.util.*;
import org.rcosjava.messaging.messages.universal.NewFile;
import org.rcosjava.messaging.messages.universal.TerminalOn;
import org.rcosjava.messaging.messages.universal.UpdateList;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.util.FIFOQueue;

/**
 * Communicates via messages and via awt Events with the main RCOS frame and
 * other components. It's functions are to set up and comunicate with a remote
 * server for the loading of programs.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 *   31/3/96 DJ Added fifoQueue for filenames.
 * </DD>
 *   30/12/96 Rewritten with frame moved to Animators.
 * <DD>
 * </DD>
 * <DD>
 *   1/1/97 Section from Program Manager moved here.
 * </DD>
 * </DT>
 * <P>
 * @author Andrew Newman.
 * @created 19th March 1996
 * @version 1.00 $Date$
 */
public class ProgramManagerAnimator extends RCOSAnimator
{
  /**
   * Description of the Field
   */
  private final static String MESSENGING_ID = "ProgramManagerAnimator";

  /**
   * Description of the Field
   */
  private ProgramManagerFrame pmFrame;
  /**
   * Description of the Field
   */
  private String currentFile = "";
  /**
   * Description of the Field
   */
  private String currentDirectory = java.io.File.separator;

  /**
   * Constructor for the ProgramManagerAnimator object
   *
   * @param postOffice Description of Parameter
   * @param pmImages Description of Parameter
   */
  public ProgramManagerAnimator(AnimatorOffice postOffice,
      ImageIcon[] pmImages)
  {
    super(MESSENGING_ID, postOffice);
    pmFrame = new ProgramManagerFrame(200, 200, pmImages, this);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    pmFrame.setupLayout(c);
  }

  /**
   * Returns the panel of this component.
   *
   * @return the panel of this component.
   */
  public RCOSPanel getPanel()
  {
    return null;
  }

  /**
   * Sets the CurrentFile attribute of the ProgramManagerAnimator object
   *
   * @param newFile The new CurrentFile value
   */
  public void setCurrentFile(String newFile)
  {
    currentFile = newFile;
  }

  /**
   * Sets the CurrentDirectory attribute of the ProgramManagerAnimator object
   *
   * @param newDirectory The new CurrentDirectory value
   */
  public void setCurrentDirectory(String newDirectory)
  {
    currentDirectory = newDirectory;
  }

  /**
   * Gets the CurrentFile attribute of the ProgramManagerAnimator object
   *
   * @return The CurrentFile value
   */
  public String getCurrentFile()
  {
    return currentFile;
  }

  /**
   * Gets the CurrentDirectory attribute of the ProgramManagerAnimator object
   *
   * @return The CurrentDirectory value
   */
  public String getCurrentDirectory()
  {
    return currentDirectory;
  }

  /**
   * Description of the Method
   *
   * @param startTerminal Description of Parameter
   */
  public void newProcess(boolean startTerminal)
  {
    // If the box is checked start a terminal before the process
    // is loaded.
//    StartProgram myStartProgram = new StartProgram(this, postOffice,
//       getCurrentDirectory()+getCurrentFile(), startTerminal);
    if (startTerminal)
    {
      TerminalOn msg = new TerminalOn(this);

      sendMessage(msg);
    }

    NewFile msg = new NewFile(this,
        getCurrentDirectory() + getCurrentFile());

    sendMessage(msg);
  }

  /**
   * Description of the Method
   */
  public void upDirectory()
  {
    //Calculate the parent directory.

    if (currentDirectory != java.io.File.separator)
    {
      String tmp = currentDirectory;
      int location;

      location = tmp.lastIndexOf(java.io.File.separatorChar, (tmp.length() - 2));
      if (location == -1)
      {
        currentDirectory = java.io.File.separator;
      }
      else
      {
        tmp = tmp.substring(0, location + 1);
        currentDirectory = tmp.substring(0, location + 1);
      }
    }
    updateList();
  }

  /**
   * Description of the Method
   *
   * @param data Description of Parameter
   */
  public void updateFileList(FIFOQueue data)
  {
    pmFrame.updateFileList(data);
  }

  /**
   * Description of the Method
   *
   * @param data Description of Parameter
   */
  public void updateDirectoryList(FIFOQueue data)
  {
    pmFrame.updateDirectoryList(data);
  }

  /**
   * Handle updating the list. Send the
   *
   * @see org.rcosjava.messaging.messages.universal.UpdateList
   *      message.
   */
  public void updateList()
  {
    UpdateList newMsg = new UpdateList(this,
        getCurrentDirectory(), 1);

    sendMessage(newMsg);
  }
}
