package org.rcosjava.software.animator.multimedia;

import java.applet.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.net.*;
import java.util.*;

import org.rcosjava.messaging.messages.universal.CreateNewRecordingMessage;
import org.rcosjava.messaging.messages.universal.PlayNextMessage;
import org.rcosjava.messaging.messages.universal.SetRecordingNameMessage;
import org.rcosjava.messaging.messages.universal.ToggleRecordingMessage;
import org.rcosjava.messaging.messages.universal.UpdateList;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;
import org.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;
import org.rcosjava.messaging.postoffices.universal.EndOfMessagesException;
import org.rcosjava.pll2.FileClient;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.animator.support.ErrorDialog;
import org.rcosjava.software.util.FIFOQueue;

import org.apache.log4j.*;

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
   * The unique identifier of this animator to register with the post office.
   */
  private final static String MESSENGING_ID = "MultimediaAnimator";

  /**
   * Logging class.
   */
  private final static Logger log = Logger.getLogger(MultimediaAnimator.class);

  /**
   * The frame to display the playback interface.
   */
  private MultimediaPlayFrame mmPlayFrame;

  /**
   * The frame to display the recording interface.
   */
  private MultimediaRecordFrame mmRecordFrame;

  /**
   * The recorder of all messages.
   */
  private UniversalMessageRecorder recorder;

  /**
   * The player of all messages.
   */
  private UniversalMessagePlayer player;

  /**
   * Constructor for the MultimediaAnimator object
   *
   * @param postOffice Description of Parameter
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param pmImages Description of Parameter
   * @param newRecorder Description of Parameter
   * @param newPlayer Description of Parameter
   */
  public MultimediaAnimator(AnimatorOffice postOffice,
      int x, int y, UniversalMessageRecorder newRecorder,
      UniversalMessagePlayer newPlayer)
  {
    super(MESSENGING_ID, postOffice);
    mmPlayFrame = new MultimediaPlayFrame(x, y, this);
    mmRecordFrame = new MultimediaRecordFrame(x, y, this);
    recorder = newRecorder;
    player = newPlayer;
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    mmPlayFrame.setupLayout(c);
    mmRecordFrame.setupLayout(c);
  }

  public RCOSPanel getPanel()
  {
    return null;
  }

  /**
   * Description of the Method
   */
  public void disposeFrame()
  {
    mmRecordFrame.dispose();
    mmPlayFrame.dispose();
  }

  /**
   * Description of the Method
   */
  public void showPlayFrame()
  {
    mmPlayFrame.setVisible(true);
  }

  /**
   * Description of the Method
   */
  public void hidePlayFrame()
  {
    mmPlayFrame.setVisible(false);
  }

  /**
   * Description of the Method
   */
  public void showRecordFrame()
  {
    mmRecordFrame.setVisible(true);
  }

  /**
   * Description of the Method
   */
  public void hideRecordFrame()
  {
    mmRecordFrame.setVisible(false);
  }

  /**
   * Description of the Method
   *
   * @param data Description of Parameter
   */
  public void updateDirectoryList(FIFOQueue data)
  {
    if (mmRecordFrame.isVisible())
    {
      mmRecordFrame.updateDirectoryList(data);
    }
    else
    {
      mmPlayFrame.updateDirectoryList(data);
    }
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
   * Sets the name of the current recording.
   *
   * @param newRecordingName the name of the recording to set.
   */
  public void setRecordingName(String newRecordingName)
  {
    SetRecordingNameMessage msg = new SetRecordingNameMessage(this,
        newRecordingName);
    sendMessage(msg);
  }

  /**
   * Creates a new recording.  Must set the recording name first.
   */
  public void createNewRecording()
  {
    CreateNewRecordingMessage msg = new CreateNewRecordingMessage(this);
    sendMessage(msg);
  }

  /**
   * Turn recording of messages on or off.
   */
  public void toggleRecording()
  {
    ToggleRecordingMessage msg = new ToggleRecordingMessage(this);
    sendMessage(msg);
  }

  /**
   * Playback a single recorded message.
   */
  public void playStep()
  {
    PlayNextMessage msg = new PlayNextMessage(this);
    sendMessage(msg);
  }

  /**
   * Indicate to the user that there is no more messages left to playback.
   */
  public void noNextMessage()
  {
    mmPlayFrame.disableStepButton();
  }
}
