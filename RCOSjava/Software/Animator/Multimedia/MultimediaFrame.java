package Software.Animator.Multimedia;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.NewLabel;
import Software.Animator.Support.RCOSList;
import MessageSystem.Messages.Message;
import MessageSystem.PostOffices.MessageHandler;
import Software.Process.RCOSProcess;

/**
 * It is the interface that allows users to turn on/off or playback
 * pre-recorded messages.
 * <P>
 * <DT><B>History:</B>
 * </DT>
 *
 * @author Andrew Newman.
 * @created 1st January 2001
 * @version 1.00 $Date$
 */
public class MultimediaFrame extends RCOSFrame
{
  private MultimediaAnimator myMultimediaAnimator;
  private Image myImages[];
  private Message msg;
  private RCOSList rMovies;
  private GraphicButton recordButton, loadButton, pauseButton, playButton;

  public MultimediaFrame (int x, int y, Image[] mmImages,
    MultimediaAnimator newMultimediaAnimator)
  {
    setTitle("Multimedia Tour");
    myImages = mmImages;
    myMultimediaAnimator = newMultimediaAnimator;
    setSize(x,y);
  }

  public synchronized void addNotify()
  {
    repaint();
    super.addNotify();
  }

  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    Panel pMain = new Panel();
    Panel pClose = new Panel();
    NewLabel lTmpLabel;

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    pMain.setLayout(gridBag);
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;
    constraints.insets= new Insets(3,1,3,1);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    lTmpLabel = new NewLabel("Existing Recordings", titleFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pMain.add(lTmpLabel);

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    rMovies = new RCOSList(this,3,false);
    gridBag.setConstraints(rMovies,constraints);
    pMain.add(rMovies);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    loadButton = new GraphicButton (myImages[0], myImages[1],
      "Load", defaultFont, buttonColour, true);
    gridBag.setConstraints(loadButton,constraints);
    pMain.add(loadButton);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    lTmpLabel = new NewLabel("Commands", titleFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pMain.add(lTmpLabel);

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    playButton = new GraphicButton (myImages[0], myImages[1],
      "Play", defaultFont, buttonColour, true);
    gridBag.setConstraints(playButton,constraints);
    pMain.add(playButton);
    playButton.addMouseListener(new PlayStep());

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    pauseButton = new GraphicButton (myImages[0], myImages[1],
      "Pause", defaultFont, buttonColour, true);
    gridBag.setConstraints(pauseButton,constraints);
    pMain.add(pauseButton);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    recordButton = new GraphicButton (myImages[0], myImages[1],
      "Record", defaultFont, buttonColour, true, true, false);
    gridBag.setConstraints(recordButton,constraints);
    pMain.add(recordButton);
    recordButton.addMouseListener(new RecordToggle());

/*    tmpButton = new Button ("Open");
    pClose.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());

    tmpButton = new Button ("Save");
    pClose.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());*/

    pClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new Button ("Close");
    pClose.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());

    add("Center", pMain);
    add("South", pClose);
  }

  class RecordToggle extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      myMultimediaAnimator.recordToggle();
      recordButton.toggleGrey();
    }
  }

  class PlayStep extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      myMultimediaAnimator.playStep();
    }
  }
/*
  class StepProcess extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      myProcessManager.step();
    }
  }

  class RunProcess extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      myProcessManager.run();
    }
  }*/
}
