package org.rcosjava.software.animator.process;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.util.*;
import org.rcosjava.software.animator.RCOSFrame;
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
   * Filename and directories that's currently selected.
   */
  private JTextField fileNameTextField = new JTextField(20);

  /**
   * Description of the Field
   */
  private Image myImages[];

  /**
   * List of directories.
   */
  private JList directoryListBox;

  /**
   * Model for the list of directories.
   */
  private DefaultListModel directoryListModel;

  /**
   * List of files.
   */
  private JList fileListBox;

  /**
   * Model for the list of files.
   */
  private DefaultListModel fileListModel;

  /**
   * Whether to start a new terminal with each process or not.
   */
  private boolean startTerminal = true;

  /**
   * Description of the Field
   */
  private JCheckBox startTerminalCheckbox = new JCheckBox();

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
    setBackground(defaultBgColour);
    setForeground(defaultFgColour);
    this.getContentPane().setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel();
    JPanel fileNamePanel = new JPanel();
    JPanel terminOptionPanel = new JPanel();
    JPanel buttonPanel = new JPanel();

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();

    mainPanel.setLayout(gridBag);
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;
    constraints.insets = new Insets(1, 15, 1, 15);

    JLabel tmpLabel;

    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpLabel = new JLabel("Directories");
    tmpLabel.setFont(titleFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpLabel = new JLabel("Files");
    tmpLabel.setFont(titleFont);
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    gridBag.setConstraints(tmpLabel, constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.CENTER;
    directoryListBox = new JList();
    directoryListModel = new DefaultListModel();
    directoryListBox.setVisibleRowCount(8);
    directoryListBox.setModel(directoryListModel);
    directoryListBox.setBackground(listColour);
    directoryListBox.setForeground(defaultFgColour);

    gridBag.setConstraints(directoryListBox, constraints);
    mainPanel.add(directoryListBox);
    directoryListBox.addMouseListener(new DirectoryListBoxListener());

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    fileListBox = new JList();
    fileListModel = new DefaultListModel();
    fileListBox.setVisibleRowCount(8);
    fileListBox.setModel(fileListModel);
    fileListBox.setBackground(listColour);
    fileListBox.setForeground(defaultFgColour);
    gridBag.setConstraints(fileListBox, constraints);
    mainPanel.add(fileListBox);
    fileListBox.addMouseListener(new FileListBoxListener());

    fileNamePanel.add(new JLabel("Filename: "));
    fileNameTextField.setFont(defaultFont);
    fileNameTextField.setBackground(textBoxColour);
    fileNameTextField.setForeground(defaultFgColour);
    fileNamePanel.add(fileNameTextField);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    gridBag.setConstraints(fileNamePanel, constraints);
    mainPanel.add(fileNamePanel);

    startTerminalCheckbox.setSelected(startTerminal);
    terminOptionPanel.add(startTerminalCheckbox);
    startTerminalCheckbox.addItemListener(new StartTerminalListener());
    terminOptionPanel.add(new JLabel("Automatically start terminal."));

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(terminOptionPanel, constraints);
    mainPanel.add(terminOptionPanel);

    JButton openButton = new JButton("Open");

    buttonPanel.add(openButton);
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    openButton.addMouseListener(new OkayButtonListener());

    JButton closeButton = new JButton("Close");

    buttonPanel.add(closeButton);
    closeButton.addMouseListener(new RCOSFrame.CloseAnimator());

    getContentPane().add("Center", mainPanel);
    getContentPane().add("South", buttonPanel);
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
    startTerminalCheckbox.setSelected(true);

    while (data.peek() != null)
    {
      fileListModel.addElement((String) data.retrieve());
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
      directoryListModel.addElement((String) data.retrieve());
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
  public class OkayButtonListener extends MouseAdapter
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
      fileListBox.clearSelection();
      myProgramManager.newProcess(startTerminal);
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class StartTerminalListener implements ItemListener
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void itemStateChanged(ItemEvent e)
    {
      startTerminal = e.getStateChange() == ItemEvent.SELECTED;
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class FileListBoxListener extends MouseAdapter
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
          myProgramManager.setCurrentFile((String) fileListModel.getElementAt(
              fileListBox.getSelectedIndex()));
          updateSelected();
          break;
        case 2:
          //File has been double clicked on.
          myProgramManager.setCurrentFile((String) fileListModel.getElementAt(
              fileListBox.getSelectedIndex()));
          fileNameTextField.setText("");
          fileListBox.clearSelection();
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
  class DirectoryListBoxListener extends MouseAdapter
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

        String selected = (String) directoryListModel.getElementAt(
            directoryListBox.getSelectedIndex());

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
