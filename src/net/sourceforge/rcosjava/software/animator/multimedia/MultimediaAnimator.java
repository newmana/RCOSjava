package net.sourceforge.rcosjava.software.animator.multimedia;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;
import net.sourceforge.rcosjava.software.animator.support.RCOSList;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.postoffices.universal.UniversalMessageRecorder;
import net.sourceforge.rcosjava.messaging.postoffices.universal.UniversalMessagePlayer;
import net.sourceforge.rcosjava.messaging.messages.universal.UpdateList;
import net.sourceforge.rcosjava.software.util.FIFOQueue;
import net.sourceforge.rcosjava.pll2.FileClient;

/**
 * User interface which allows the recording and playback of messages that
 * occur in RCOS.
 * <P>
 * <DT><B>History:</B>
 * </DT>
 *
 * @author Andrew Newman.
 * @created 1st January 2001
 * @version 1.00 $Date$
 */
public class MultimediaAnimator extends RCOSAnimator
{
  private MultimediaFrame mmFrame;
  private static final String MESSENGING_ID = "MultimediaAnimator";
  private UniversalMessageRecorder recorder;
  private UniversalMessagePlayer player;
  private boolean recording, playing;
  private String currentFile;
  private FileClient myClient;

  public MultimediaAnimator (AnimatorOffice aPostOffice,
    int x, int y, Image[] pmImages, UniversalMessageRecorder newRecorder,
    UniversalMessagePlayer newPlayer)
  {
    super(MESSENGING_ID, aPostOffice);
    mmFrame = new MultimediaFrame(x, y, pmImages, this);
    mmFrame.pack();
    mmFrame.setSize(x,y);
    recorder = newRecorder;
    player = newPlayer;
    recording = false;
    playing = false;
  }

  public void setupLayout(Component c)
  {
    mmFrame.setupLayout(c);
  }

  public void disposeFrame()
  {
    mmFrame.dispose();
  }

  public void showFrame()
  {
    mmFrame.setVisible(true);
  }

  public void hideFrame()
  {
    mmFrame.setVisible(false);
  }

  public boolean getRecording()
  {
    return this.recording;
  }

  public void updateDirectoryList(FIFOQueue data)
  {
    mmFrame.updateDirectoryList(data);
  }

  /**
   * Handle updating the list.  Send the
   * @see MessageSystem.messages.universal.UpdateList message.
   */
  public void updateList()
  {
    UpdateList newMsg = new UpdateList(this, java.io.File.separator, 2);
    sendMessage(newMsg);
  }

  public void setCurrentFile(String newFile)
  {
    currentFile = newFile;
  }

  public void createDirectory()
  {
    recorder.createDirectory(java.io.File.separatorChar + currentFile);
  }

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
  public void playStep()
  {
    this.player.sendNextMessage(currentFile);
  }

  public void processMessage(AnimatorMessageAdapter message)
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

  public void processMessage(UniversalMessageAdapter message)
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
}
