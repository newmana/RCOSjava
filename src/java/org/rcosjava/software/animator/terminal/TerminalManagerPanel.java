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
  private Image images[];

  /**
   * The calling animator.
   */
  private TerminalManagerAnimator terminalManager;

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
  public TerminalManagerPanel(int x, int y, ImageIcon[] newImages,
      int noTerminals, int noTerminalCols, int noTerminalRows,
      TerminalManagerAnimator thisTerminalManager)
  {
    super();

    maxTerminals = noTerminals + 1;
    maxTerminalCols = noTerminalCols;
    maxTerminalRows = noTerminalRows;

    images = new Image[newImages.length];
    for (int index = 0; index < images.length; index++)
    {
      images[index] = newImages[index].getImage();
    }
    terminalManager = thisTerminalManager;

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

    Panel main = new Panel();
    main.setBackground(defaultBgColour);
    main.setForeground(defaultFgColour);

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();

    main.setLayout(gridBag);

    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;

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
        Icon upIcon = new ImageIcon(images[1]);
        Icon downIcon = new ImageIcon(images[2]);
        terminals[currentTerminal] = new GraphicButton(images[1],
            images[1], "Terminal #" + currentTerminal, defaultFont,
            defaultFgColour, true, false);
        gridBag.setConstraints(terminals[currentTerminal], constraints);
        main.add(terminals[currentTerminal]);
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
        views[currentTerminal] = new GraphicButton(images[2],
            images[3], "#" + currentTerminal + " off", defaultFont,
            buttonColour, true, true);
        gridBag.setConstraints(views[currentTerminal], constraints);
        main.add(views[currentTerminal]);
        views[currentTerminal].addMouseListener(new TerminalHandler());
      }
    }

    setLayout(new BorderLayout());
    add("Center", main);
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
    terminals[terminalNo].setButtonUpPic(images[0]);
    terminals[terminalNo].setButtonDownPic(images[0]);
    views[terminalNo].setButtonText("Hide #" + terminalNo);
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
    terminals[terminalNo].setButtonUpPic(images[1]);
    terminals[terminalNo].setButtonDownPic(images[1]);
    views[terminalNo].setButtonText("#" + terminalNo + " off");
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
    views[terminalNo].setButtonText("Hide #" + terminalNo);
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
    views[terminalNo].setButtonText("View #" + terminalNo);
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
        terminalManager.sendToggleTerminal(iTerminalNumber);
      }
      if (whichObject.startsWith("View #"))
      {
        iTerminalNumber = (Integer.parseInt(whichObject.substring(6)));
        terminalManager.sendTerminalFront(iTerminalNumber);
        terminalFront(iTerminalNumber);
      }
      if (whichObject.startsWith("Hide #"))
      {
        iTerminalNumber = (Integer.parseInt(whichObject.substring(6)));
        terminalManager.sendTerminalBack(iTerminalNumber);
        terminalBack(iTerminalNumber);
      }
    }
  }
}
