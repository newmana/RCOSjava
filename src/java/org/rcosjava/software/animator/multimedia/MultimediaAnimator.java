package org.rcosjava.software.animator.multimedia;

import java.applet.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.net.*;
import java.util.*;
import org.rcosjava.messaging.messages.universal.UpdateList;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;
import org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;
import org.rcosjava.pll2.FileClient;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.util.FIFOQueue;

/**
 * User interface which allows the recording and playback of messages that occur
 * in RCOS.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 2001
 * @version 1.00 $Date$
 */
public class MultimediaAnimator extends RCOSAnimator
{
  /**
   * Description of the Field
   */
  private final static String MESSENGING_ID = "MultimediaAnimator";

  /**
   * Description of the Field
   */
  private MultimediaFrame mmFrame;

  /**
   * Description of the Field
   */
  private UniversalMessageRecorder recorder;

  /**
   * Description of the Field
   */
  private UniversalMessagePlayer player;

  /**
   * Description of the Field
   */
  private boolean recording, playing;

  /**
   * Description of the Field
   */
  private String currentFile;

  /**
   * Description of the Field
   */
  private FileClient myClient;

  /**
   * Constructor for the MultimediaAnimator object
   *
   * @param aPostOffice Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param pmImages Description of Parameter
   * @param newRecorder Description of Parameter
   * @param newPlayer Description of Parameter
   */
  public MultimediaAnimator(AnimatorOffice aPostOffice,
      int x, int y, ImageIcon[] pmImages, UniversalMessageRecorder newRecorder,
      UniversalMessagePlayer newPlayer)
  {
    super(MESSENGING_ID, aPostOffice);
    mmFrame = new MultimediaFrame(x, y, pmImages, this);
    mmFrame.pack();
    mmFrame.setSize(x, y);
    recorder = newRecorder;
    player = newPlayer;
    recording = false;
    playing = false;
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    mmFrame.setupLayout(c);
  }

  /**
   * Sets the CurrentFile attribute of the MultimediaAnimator object
   *
   * @param newFile The new CurrentFile value
   */
  public void setCurrentFile(String newFile)
  {
    currentFile = newFile;
  }

  /**
   * Gets the Recording attribute of the MultimediaAnimator object
   *
   * @return The Recording value
   */
  public boolean getRecording()
  {
    return this.recording;
  }

  /**
   * Description of the Method
   */
  public void disposeFrame()
  {
    mmFrame.dispose();
  }

  /**
   * Description of the Method
   */
  public void showFrame()
  {
    mmFrame.setVisible(true);
  }

  /**
   * Description of the Method
   */
  public void hideFrame()
  {
    mmFrame.setVisible(false);
  }

  /**
   * Description of the Method
   *
   * @param data Description of Parameter
   */
  public void updateDirectoryList(FIFOQueue data)
  {
    mmFrame.updateDirectoryList(data);
  }

  /**
   * Handle updating the list. Send the
   *
   * @see org.rcosjava.messaging.messages.universal.UpdateList
   *      message.
   */
  public void updateList()
  {
    UpdateList newMsg = new UpdateList(this, java.io.File.separator, 2);

    sendMessage(newMsg);
  }

  /**
   * Description of the Method
   */
  public void createDirectory()
  {
    recorder.createDirectory(java.io.File.separatorChar + currentFile);
  }

  /**
   * Description of the Method
   */
  public void recordToggle()
  {
    if (!recording)
    {
      this.recorder.recordOn(currentFile);
      recording = true;
    }
    else
    {
      this.recorder.recordOff();
      recording = false;
    }
  }

  //Basic step for now
  /**
   * Description of the Method
   */
  public void playStep()
  {
    this.player.sendNextMessage(currentFile);
  }
}
