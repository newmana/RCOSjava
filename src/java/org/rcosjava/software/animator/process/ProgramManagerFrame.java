package org.rcosjava.software.animator.process;
import java.applet.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.*;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.support.NewLabel;
import org.rcosjava.software.util.FIFOQueue;

/**
 * Communicates via messages and via awt Events with the main RCOS frame and
 * other components. It's functions are to set up and comunicate with a remote
 * server for the loading of programs.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 31/03/96 DJ Added fifoQueue for filenames. </DD>
 * <DD> 30/12/96 Rewritten with frame moved to Animators. </DD>
 * <DD> 01/01/97 Section from Program Manager moved here. </DD>
 * <DD> 02/01/97 New layout (GridBagLayout). Option to automatically start
 * terminal added. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @author David Jones.
 * @created 19th March 1996
 * @version 1.00 $Date$
 */
public class ProgramManagerFrame extends RCOSFrame
{
  /**
   * Description of the Field
   */
  private ProgramManagerAnimator myProgramManager;

  /**
   * Description of the Field
   */
  private Image myImages[];

  /**
   * Description of the Field
   */
  private java.awt.List directoryListBox;

  /**
   * Description of the Field
   */
  private java.awt.List fileListBox;

  /**
   * Description of the Field
   */
  private TextField fileNameTextField = new TextField(30);

  /**
   * Description of the Field
   */
  private boolean startTerminal = true;

  /**
   * Description of the Field
   */
  private Checkbox startTerminalCheckbox = new Checkbox();

  /**
   * Constructor for the ProgramManagerFrame object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param pmImages Description of Parameter
   * @param thisProgramManager Description of Parameter
   */
  public ProgramManagerFrame(int x, int y, ImageIcon[] images,
      ProgramManagerAnimator thisProgramManager)
  {
    super();
    setTitle("Program Manager");
    if (images != null)
    {
      myImages = new Image[images.length];
      for (int index = 0; index < images.length-1; index++)
      {
        myImages[index] = images[index].getImage();
      }
    }
    myProgramManager = thisProgramManager;
    setSize(x, y);
  }

  /**
   * Sets the Visible attribute of the ProgramManagerFrame object
   *
   * @param visibility The new Visible value
   */
  public void setVisible(boolean visibility)
  {
    super.setVisible(visibility);
    if (visibility)
    {
      myProgramManager.setCurrentFile("");
      myProgramManager.updateList();
    }
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    Panel mainPanel = new Panel();
    Panel fileNamePanel = new Panel();
    Panel terminOptionPanel = new Panel();
    Panel buttonPanel = new Panel();

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();

    mainPanel.setLayout(gridBag);
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;
    constraints.insets = new Insets(1, 15, 1, 15);

    NewLabel tmpLabel;

    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpLabel = new NewLabel("Directories", titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpLabel = new NewLabel("Files", titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.CENTER;
    directoryListBox = new java.awt.List(8, false);
    directoryListBox.setBackground(listColour);
    directoryListBox.setForeground(defaultFgColour);
    gridBag.setConstraints(directoryListBox, constraints);
    mainPanel.add(directoryListBox);
    directoryListBox.addMouseListener(new directoryListBoxListener());

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    fileListBox = new java.awt.List(8, false);
    fileListBox.setBackground(listColour);
    fileListBox.setForeground(defaultFgColour);
    gridBag.setConstraints(fileListBox, constraints);
    mainPanel.add(fileListBox);
    fileListBox.addMouseListener(new fileListBoxListener());

    fileNamePanel.add(new NewLabel("Filename: ", titleFont));
    fileNameTextField.setFont(defaultFont);
    fileNameTextField.setBackground(textBoxColour);
    fileNameTextField.setForeground(defaultFgColour);
    fileNamePanel.add(fileNameTextField);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    gridBag.setConstraints(fileNamePanel, constraints);
    mainPanel.add(fileNamePanel);

    startTerminalCheckbox.setState(startTerminal);
    terminOptionPanel.add(startTerminalCheckbox);
    startTerminalCheckbox.addItemListener(new startTerminalListener());
    terminOptionPanel.add(new NewLabel("Automatically start terminal.", defaultFont));

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(terminOptionPanel, constraints);
    mainPanel.add(terminOptionPanel);

    JButton tmpOpenAWTButton = new JButton("Open");

    buttonPanel.add(tmpOpenAWTButton);
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpOpenAWTButton.addMouseListener(new okayButtonListener());

    Button tmpAWTButton = new Button("Close");

    buttonPanel.add(tmpAWTButton);
    tmpAWTButton.addMouseListener(new RCOSFrame.CloseAnimator());

    add("Center", mainPanel);
    add("South", buttonPanel);
  }

  /**
   * Description of the Method
   *
   * @param data Description of Parameter
   */
  void updateFileList(FIFOQueue data)
  {
    fileListBox.removeAll();

    startTerminal = true;
    startTerminalCheckbox.setState(true);

    while (data.peek() != null)
    {
      fileListBox.add((String) data.retrieve());
    }
    updateSelected();
  }

  /**
   * Description of the Method
   *
   * @param data Description of Parameter
   */
  void updateDirectoryList(FIFOQueue data)
  {
    directoryListBox.removeAll();
    while (data.peek() != null)
    {
      directoryListBox.add((String) data.retrieve());
    }
  }

  /**
   * Description of the Method
   */
  void updateSelected()
  {
    fileNameTextField.setText(myProgramManager.getCurrentDirectory() +
        myProgramManager.getCurrentFile());
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  public class okayButtonListener extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      setVisible(false);
      fileNameTextField.setText("");
      fileListBox.deselect(fileListBox.getSelectedIndex());
      myProgramManager.newProcess(startTerminal);
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class startTerminalListener implements ItemListener
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void itemStateChanged(ItemEvent e)
    {
      if (e.getSource().equals(startTerminalCheckbox))
      {
        startTerminal = !startTerminal;
      }
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class fileListBoxListener extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      switch (e.getClickCount())
      {
          case 1:
            //File name has been selected.
            myProgramManager.setCurrentFile(fileListBox.getSelectedItem());
            updateSelected();
            break;
          case 2:
            //File has been double clicked on.
            myProgramManager.setCurrentFile(fileListBox.getSelectedItem());
            fileNameTextField.setText("");
            fileListBox.deselect(fileListBox.getSelectedIndex());
            myProgramManager.newProcess(startTerminal);
            setVisible(false);
            break;
      }
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class directoryListBoxListener extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      if (e.getClickCount() == 2)
      {
        //A double click was detected.

        String selected = directoryListBox.getSelectedItem();

        if (selected.compareTo(".") == 0)
        {
          myProgramManager.upDirectory();
        }
        else if (selected.compareTo(".") == 0)
        {
          myProgramManager.updateList();
        }
        else
        {
          // Go into directory
          myProgramManager.setCurrentDirectory(
              myProgramManager.getCurrentDirectory() + selected);
          myProgramManager.setCurrentFile("");
          myProgramManager.updateList();
        }
      }
    }
  }

}
