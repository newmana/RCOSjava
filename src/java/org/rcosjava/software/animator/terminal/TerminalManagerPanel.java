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
   * Serial id.
   */
  private static final long serialVersionUID = 1033792888032824673L;

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
  private JButton[] views;

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
   * @param noTerminals maximum number of terminals to display
   * @param noTerminalRows number of rows of terminals to display
   * @param thisTerminalManager my parent terminal manager
   * @param images Description of Parameter
   * @param noTerminalCols Description of Parameter
   */
  public TerminalManagerPanel(ImageIcon[] newImages, int noTerminals,
      int noTerminalCols, int noTerminalRows,
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
    views = new JButton[maxTerminals];
  }

  /**
   * Sets a new terminal manager animator.
   *
   * @param newTerminalAnimator the new terminal manager animator.
   */
  void setManager(TerminalManagerAnimator newTerminalAnimator)
  {
    terminalManager = newTerminalAnimator;
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    JPanel main = new JPanel();
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
        views[currentTerminal] = new JButton("Terminal #" + currentTerminal);
        views[currentTerminal].setFont(defaultFont);
        gridBag.setConstraints(views[currentTerminal], constraints);
        main.add(views[currentTerminal]);
        views[currentTerminal].addActionListener(new TerminalButtonListener());
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
    terminalManager.setTerminalOn(terminalNo);
    terminals[terminalNo].setButtonUpPic(images[0]);
    terminals[terminalNo].setButtonDownPic(images[0]);
    views[terminalNo].setText("Hide #" + terminalNo);
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
    terminalManager.setTerminalOff(terminalNo);
    terminals[terminalNo].setButtonUpPic(images[1]);
    terminals[terminalNo].setButtonDownPic(images[1]);
    views[terminalNo].setText("Terminal #" + terminalNo);
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
    terminalManager.setTerminalFront(terminalNo);
    views[terminalNo].setText("Hide #" + terminalNo);
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
    terminalManager.setTerminalBack(terminalNo);
    views[terminalNo].setText("View #" + terminalNo);
    views[terminalNo].repaint();
  }

  /**
   * Attached to each terminal screen and terminal button. If a terminal is
   * clicked on it will toggle the terminal off/on. If the button was view it
   * will bring it to the front and fouce and the hide will do the opposite.
   */
  private class TerminalHandler extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      String whichObject = e.getSource().toString();
      toggleButtons(whichObject);
    }
  }

  /**
   * Records when a user clicks on a button.
   */
  private class TerminalButtonListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      toggleButtons(e.getActionCommand());
    }
  }

  /**
   * Depending on the string passed (from a button or terminal) toggle the
   * buttons and send any required messages.
   *
   * @param whichObject the object that was selected.
   */
  private void toggleButtons(String whichObject)
  {
    int terminalNumber;

    if (whichObject.startsWith("Terminal #"))
    {
      terminalNumber = (Integer.parseInt(whichObject.substring(10)));
      terminalManager.sendToggleTerminal(terminalNumber);
    }
    if (whichObject.startsWith("View #"))
    {
      terminalNumber = (Integer.parseInt(whichObject.substring(6)));
      terminalManager.sendTerminalFront(terminalNumber);
      terminalFront(terminalNumber);
    }
    if (whichObject.startsWith("Hide #"))
    {
      terminalNumber = (Integer.parseInt(whichObject.substring(6)));
      terminalManager.sendTerminalBack(terminalNumber);
      terminalBack(terminalNumber);
    }
  }
}
