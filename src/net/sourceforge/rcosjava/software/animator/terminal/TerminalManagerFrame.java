//***************************************************************************
// FILE     : TerminalManagerAnimator.java
// PACKAGE  : Animator
// PURPOSE  : Class used to animate CPU
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 24/12/96  Created
//            21/02/97  Repaint bugs fixed.
//            25/08/98  Converted to Java 1.1.
//
//***************************************************************************/

package net.sourceforge.rcosjava.software.animator.terminal;

import java.awt.*;
import java.awt.event.*;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;

public class TerminalManagerFrame extends RCOSFrame
{
  private int MaxTerminals;
  private int MaxTerminalCols, MaxTerminalRows;
  private GraphicButton[] terminals;
  private GraphicButton[] views;
  private Image myImages[];
  private TerminalManagerAnimator myTerminalManager;

  public TerminalManagerFrame (int x, int y, Image[] tmImages, int mTerm,
                               int mCols, int mRows,
                               TerminalManagerAnimator thisTerminalManager)
  {
    super();
    setTitle("Terminal Manager");

    MaxTerminals = mTerm+1;
    MaxTerminalCols = mCols;
    MaxTerminalRows = mRows;

    myImages = tmImages;
    myTerminalManager = thisTerminalManager;

    terminals = new GraphicButton[MaxTerminals];
    views = new GraphicButton[MaxTerminals];

    setSize(x,y);
  }

  public void setupLayout(Component c)
  {
    super.setupLayout(c);
    Panel pMain = new Panel();
    Panel pClose = new Panel();

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    pMain.setLayout(gridBag);

    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

    pClose.setLayout(new FlowLayout(FlowLayout.CENTER));

    int currentTerminal = 0;

    for (int countRows = 1; countRows <= MaxTerminalRows; countRows++)
    {
      for (int countCols = 1; countCols <= MaxTerminalCols; countCols++)
      {
        if (countCols == MaxTerminalCols)
        {
          constraints.gridwidth = GridBagConstraints.REMAINDER;
        }
        else if (countCols == MaxTerminalCols - 1)
        {
          constraints.gridwidth = GridBagConstraints.RELATIVE;
        }
        else
        {
          constraints.gridwidth = 1;
        }

        currentTerminal++;
        terminals[currentTerminal] = new GraphicButton (myImages[1],
          myImages[1], "Terminal #" + currentTerminal, defaultFont,
          defaultFgColour, true, false);
        gridBag.setConstraints(terminals[currentTerminal], constraints);
        pMain.add(terminals[currentTerminal]);
        terminals[currentTerminal].addMouseListener(new TerminalHandler());
      }
      currentTerminal -= MaxTerminalCols;
      for (int countCols = 1; countCols <= MaxTerminalCols; countCols++)
      {
        if (countCols == MaxTerminalCols)
        {
          constraints.gridwidth = GridBagConstraints.REMAINDER;
        }
        else if (countCols == MaxTerminalCols - 1)
        {
          constraints.gridwidth = GridBagConstraints.RELATIVE;
        }
        else
        {
          constraints.gridwidth = 1;
        }

        currentTerminal++;
        views[currentTerminal] = new GraphicButton (myImages[2],
          myImages[3], "#" + currentTerminal + " off", defaultFont,
          buttonColour, true, true);
        gridBag.setConstraints(views[currentTerminal], constraints);
        pMain.add(views[currentTerminal]);
        views[currentTerminal].addMouseListener(new TerminalHandler());
      }
    }

    pClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new Button("Close");
    pClose.add(tmpButton);
    tmpButton.addMouseListener(new RCOSFrame.CloseAnimator());

    add("Center", pMain);
    add("South", pClose);
  }

  void terminalOn (int termNo)
  {
    terminals[termNo].imgButtonUpPic = myImages[0];
    terminals[termNo].imgButtonDownPic = myImages[0];
    views[termNo].sTheButton = "Hide #" + termNo;
    terminals[termNo].repaint();
    views[termNo].repaint();
  }

  void terminalOff (int termNo)
  {
    terminals[termNo].imgButtonUpPic = myImages[1];
    terminals[termNo].imgButtonDownPic = myImages[1];
    views[termNo].sTheButton = "#" + termNo + " off";
    terminals[termNo].repaint();
    views[termNo].repaint();
  }

  void terminalFront (int termNo)
  {
    views[termNo].sTheButton = "Hide #" + termNo;
    views[termNo].repaint();
  }

  void terminalBack (int termNo)
  {
    views[termNo].sTheButton = "View #" + termNo;
    views[termNo].repaint();
  }

  class TerminalHandler extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      int iTerminalNumber;
      String whichObject = e.getSource().toString();
      if (whichObject.startsWith("Terminal #"))
      {
        iTerminalNumber = (Integer.parseInt(whichObject.substring(10)));
        myTerminalManager.sendToggleTerminal(iTerminalNumber);
      }
      if (whichObject.startsWith("View #"))
      {
        iTerminalNumber = (Integer.parseInt(whichObject.substring(6)));
        myTerminalManager.sendTerminalFront(iTerminalNumber);
        terminalFront(iTerminalNumber);
      }
      if (whichObject.startsWith("Hide #"))
      {
        iTerminalNumber = (Integer.parseInt(whichObject.substring(6)));
        myTerminalManager.sendTerminalBack(iTerminalNumber);
        terminalBack(iTerminalNumber);
      }
    }
  }
}
