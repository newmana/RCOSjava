package org.rcosjava.software.animator.process;

import java.applet.*;
import java.awt.*;
import javax.swing.*;
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
   * Uniquely identifies the program manager animator to the post office.
   */
  private final static String MESSENGING_ID = "ProgramManagerAnimator";

  /**
   * Displays the view or user interface.
   */
  private ProgramManagerFrame pmFrame;

  /**
   * Currently selected file name.
   */
  private String currentFile = "";

  /**
   * Directory separator.
   */
  private String currentDirectory = java.io.File.separator;

  /**
   * Create an animator office, register with the animator office, set the size
   * of the frame and the images to use to represent the processes and the
   * buttons.
   *
   * @param postOffice the post office to register to.
   * @param pmImages the images to use for process and buttons.
   */
  public ProgramManagerAnimator(AnimatorOffice postOffice,
      ImageIcon[] pmImages)
  {
    super(MESSENGING_ID, postOffice);
    pmFrame = new ProgramManagerFrame(325, 300, pmImages, this);
  }

  /**
   * Setup the layout of the frame (menus, etc).
   *
   * @param c the parent component.
   */
  public void setupLayout(Component c)
  {
    pmFrame.setupLayout(c);
  }

  /**
   * Display the frame to the user.
   */
  public void showFrame()
  {
    pmFrame.setVisible(true);
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
   * Change the currently selected file to the given value.
   *
   * @param newFile new value of the directory.
   */
  public void setCurrentFile(String newFile)
  {
    currentFile = newFile;
  }

  /**
   * Change the currently selected directory to the given value.
   *
   * @param newDirectory new value of the directory.
   */
  public void setCurrentDirectory(String newDirectory)
  {
    currentDirectory = newDirectory;
  }

  /**
   * Returns the currently selected file.
   *
   * @return the current selected file.
   */
  public String getCurrentFile()
  {
    return currentFile;
  }

  /**
   * Returns the currently selected directory.
   *
   * @return the currently selected directory.
   */
  public String getCurrentDirectory()
  {
    return currentDirectory;
  }

  /**
   * New process has been selected by the user so create a new terminal if
   * selected and send the new file message.
   *
   * @param startTerminal whether a new terminal should be created before
   *   creating the process.
   */
  public void newProcess(boolean startTerminal)
  {
    // If the box is checked start a terminal before the process
    // is loaded.
    if (startTerminal)
    {
      TerminalOn msg = new TerminalOn(this);
      sendMessage(msg);
    }

    NewFile msg = new NewFile(this, getCurrentDirectory() + getCurrentFile());
    sendMessage(msg);
  }

  /**
   * Change the current directory up one (to its parent).
   */
  public void upDirectory()
  {
    //Calculate the parent directory.
    if (currentDirectory != java.io.File.separator)
    {
      String tmp = currentDirectory;
      int location;

      location = tmp.lastIndexOf(java.io.File.separatorChar,
        (tmp.length() - 2));

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
   * Updates the list of files that are selectable.
   *
   * @param data queue containing string representing the files.
   */
  public void updateFileList(FIFOQueue data)
  {
    pmFrame.updateFileList(data);
  }

  /**
   * Updates the list of directories that are selectable.
   *
   * @param data queue containing strings representing the directories.
   */
  public void updateDirectoryList(FIFOQueue data)
  {
    pmFrame.updateDirectoryList(data);
  }

  /**
   * Handle updating the list. Send the update list message.
   *
   * @see org.rcosjava.messaging.messages.universal.UpdateList
   *      message.
   */
  public void updateList()
  {
    UpdateList newMsg = new UpdateList(this, getCurrentDirectory(), 1);
    sendMessage(newMsg);
  }
}
