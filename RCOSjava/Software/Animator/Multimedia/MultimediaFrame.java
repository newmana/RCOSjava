//***************************************************************************
// FILE     : MultimediaFrame.java
// PACKAGE  : Animator
// PURPOSE  : It is the interface that allows users to turn on/off or playback
//            pre-recorded messages.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 10/1/97  Created. AN
//            23/11/98 Converted to Java 1.1. AN
//
//***************************************************************************/

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

public class MultimediaFrame extends RCOSFrame
{
  private MultimediaAnimator myMultimediaAnimator;
  private Image myImages[];
  private Message msg;
  private RCOSList rMovies;

  public MultimediaFrame (int x, int y, Image[] mmImages,
                          MultimediaAnimator newMultimediaAnimator)
  {
    setTitle("Process Manager");
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

    GraphicButton tmpGButton;

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
    tmpGButton = new GraphicButton (myImages[0], myImages[1],
      "Load", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton,constraints);
    pMain.add(tmpGButton);
    //tmpButton.addMouseListener(new KillProcess());

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    lTmpLabel = new NewLabel("Commands", titleFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pMain.add(lTmpLabel);

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton (myImages[0], myImages[1],
      "Play", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton,constraints);
    pMain.add(tmpGButton);
		//tmpButton.addMouseListener(new StepProcess());

		constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton (myImages[0], myImages[1],
      "Pause", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton,constraints);
    pMain.add(tmpGButton);
    //tmpButton.addMouseListener(new StepProcess());

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton (myImages[0], myImages[1],
      "Record", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton,constraints);
    pMain.add(tmpGButton);
    //tmpButton.addMouseListener(new RunProcess());

    pClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new Button ("Open");
    pClose.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());

	  tmpButton = new Button ("Save");
    pClose.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());

		tmpButton = new Button ("Close");
    pClose.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());

    add("Center",pMain);
    add("South",pClose);
  }

/*  class KillProcess extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      if (rProcesses.getSelectedItem() != null)
      {
        int iProcess = Integer.parseInt(rProcesses.getSelectedItem());
        myProcessManager.kill(iProcess);
      }
    }
  }

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
