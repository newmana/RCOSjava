package org.rcosjava.software.animator.terminal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.ImageIcon;
import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.software.animator.support.GraphicButton;

/**
 * A simple array of terminals are displayed to the user. It allows the user to
 * turn on (allocate to the OS) or otherwise manipulate the basic terminals.
 * <P>
 * @author Andrew Newman
 * @created 22nd July 2002
 * @version 1.00 $Date$
 */
public class TerminalManagerPanel extends RCOSPanel
{
  /**
   * Maximum number of terminals
   */
  private int maxTerminals;

  /**
   * Maximum number of columns of terminals to display.
   */
  private int maxTerminalCols;

  /**
   * Maximum number of rows of terminal to display.
   */
  private int maxTerminalRows;

  /**
   * Buttons to display terminals.
   */
  private GraphicButton[] terminals;

  /**
   * Buttons to display view/show.
   */
  private GraphicButton[] views;

  /**
   * Images to display in button.
   */
  private Image myImages[];

  /**
   * The calling animator.
   */
  private TerminalManagerAnimator myTerminalManager;

  /**
   * Create an animator frame, set the size of it and the images to use to
   * represent the processes and the buttons.
   *
   * @param x width of frame
   * @param y height of frame
   * @param noTerminals maximum number of terminals to display
   * @param noTerminalRows number of rows of terminals to display
   * @param thisTerminalManager my parent terminal manager
   * @param images Description of Parameter
   * @param noTerminalCols Description of Parameter
   */
  public TerminalManagerPanel(int x, int y, ImageIcon[] images,
      int noTerminals, int noTerminalCols, int noTerminalRows,
      TerminalManagerAnimator thisTerminalManager)
  {
    super();

    maxTerminals = noTerminals + 1;
    maxTerminalCols = noTerminalCols;
    maxTerminalRows = noTerminalRows;

    myImages = new Image[images.length];
    for (int index = 0; index < images.length; index++)
    {
      myImages[index] = images[index].getImage();
    }
    myTerminalManager = thisTerminalManager;

    terminals = new GraphicButton[maxTerminals];
    views = new GraphicButton[maxTerminals];

    setSize(x, y);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
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

    for (int countRows = 1; countRows <= maxTerminalRows; countRows++)
    {
      for (int countCols = 1; countCols <= maxTerminalCols; countCols++)
      {
        if (countCols == maxTerminalCols)
        {
          constraints.gridwidth = GridBagConstraints.REMAINDER;
        }
        else if (countCols == maxTerminalCols - 1)
        {
          constraints.gridwidth = GridBagConstraints.RELATIVE;
        }
        else
        {
          constraints.gridwidth = 1;
        }

        currentTerminal++;
        terminals[currentTerminal] = new GraphicButton(myImages[1],
            myImages[1], "Terminal #" + currentTerminal, defaultFont,
            defaultFgColour, true, false);
        gridBag.setConstraints(terminals[currentTerminal], constraints);
        pMain.add(terminals[currentTerminal]);
        terminals[currentTerminal].addMouseListener(new TerminalHandler());
      }
      currentTerminal -= maxTerminalCols;
      for (int countCols = 1; countCols <= maxTerminalCols; countCols++)
      {
        if (countCols == maxTerminalCols)
        {
          constraints.gridwidth = GridBagConstraints.REMAINDER;
        }
        else if (countCols == maxTerminalCols - 1)
        {
          constraints.gridwidth = GridBagConstraints.RELATIVE;
        }
        else
        {
          constraints.gridwidth = 1;
        }

        currentTerminal++;
        views[currentTerminal] = new GraphicButton(myImages[2],
            myImages[3], "#" + currentTerminal + " off", defaultFont,
            buttonColour, true, true);
        gridBag.setConstraints(views[currentTerminal], constraints);
        pMain.add(views[currentTerminal]);
        views[currentTerminal].addMouseListener(new TerminalHandler());
      }
    }

    add("Center", pMain);
  }

  /**
   * The terminal was turned on (clicked on in terminal manager animator or an
   * OS event occurred). The image is changed, he button beneath it is change to
   * Hide so that it can be clicked on to hide the button.
   *
   * @param terminalNo the terminal number is assumed to be a valid number
   *      between the range of existing terminals.
   */
  void terminalOn(int terminalNo)
  {
    terminals[terminalNo].imgButtonUpPic = myImages[0];
    terminals[terminalNo].imgButtonDownPic = myImages[0];
    views[terminalNo].sTheButton = "Hide #" + terminalNo;
    terminals[terminalNo].repaint();
    views[terminalNo].repaint();
  }

  /**
   * The terminal was turned off (clicked on or os event). The image is changed
   * and the terminal name is set to off.
   *
   * @param terminalNo the terminal number is assumed to be a valid number
   *      between the range of existing terminals.
   */
  void terminalOff(int terminalNo)
  {
    terminals[terminalNo].imgButtonUpPic = myImages[1];
    terminals[terminalNo].imgButtonDownPic = myImages[1];
    views[terminalNo].sTheButton = "#" + terminalNo + " off";
    terminals[terminalNo].repaint();
    views[terminalNo].repaint();
  }

  /**
   * The terminal front button was hit (i.e. view) which sets the view button to
   * hide.
   *
   * @param terminalNo the terminal number is assumed to be a valid number
   *      between the range of existing terminals.
   */
  void terminalFront(int terminalNo)
  {
    views[terminalNo].sTheButton = "Hide #" + terminalNo;
    views[terminalNo].repaint();
  }

  /**
   * The terminal back button was hit (i.e. hide) which sets the hide button to
   * view.
   *
   * @param terminalNo the terminal number is assumed to be a valid number
   *      between the range of existing terminals.
   */
  void terminalBack(int terminalNo)
  {
    views[terminalNo].sTheButton = "View #" + terminalNo;
    views[terminalNo].repaint();
  }

  /**
   * Attached to each terminal screen and terminal button. If a terminal is
   * clicked on it will toggle the terminal off/on. If the button was view it
   * will bring it to the front and fouce and the hide will do the opposite.
   *
   * @author administrator
   * @created 28 April 2002
   */
  class TerminalHandler extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
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
