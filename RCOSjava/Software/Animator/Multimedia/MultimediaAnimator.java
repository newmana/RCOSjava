package Software.Animator.Multimedia;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import Software.Animator.RCOSAnimator;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.NewLabel;
import Software.Animator.Support.RCOSList;
import MessageSystem.Messages.Animator.AnimatorMessageAdapter;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.PostOffices.Animator.AnimatorOffice;
import MessageSystem.PostOffices.Universal.UniversalMessageRecorder;
import MessageSystem.PostOffices.Universal.UniversalMessagePlayer;

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

  public void recordToggle()
  {
    if (!recording)
    {
      this.recorder.recordOn("test");
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
    System.out.println("Play step");
    this.player.sendNextMessage("test");
  }

  public void processMessage(AnimatorMessageAdapter aMsg)
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
}
