package org.rcosjava.software.animator.multimedia;

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
public class MultimediaRecordFrame extends RCOSFrame
{
  /**
   * Description of the Field
   */
  private MultimediaAnimator myMultimediaAnimator;

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
   * @param newMultimediaAnimator Description of Parameter
   */
  public MultimediaRecordFrame(int x, int y, ImageIcon[] images,
      MultimediaAnimator newMultimediaAnimator)
  {
    super();
    setTitle("Tour Recording");
    if (images != null)
    {
      myImages = new Image[images.length];
      for (int index = 0; index < images.length-1; index++)
      {
        myImages[index] = images[index].getImage();
      }
    }
    myMultimediaAnimator = newMultimediaAnimator;
    setSize(x, y);
  }

  /**
   * Makes the frame visible and resets the file name and updates the directory
   * and file lists.
   *
   * @param visibility whether to make it visible or not.
   */
  public void setVisible(boolean visibility)
  {
    super.setVisible(visibility);
    if (visibility)
    {
      myMultimediaAnimator.setRecordingName("");
      myMultimediaAnimator.updateList();
    }
  }

  /**
   * Sets up the layout for the whole program manager window. It creates three
   * panels.  It puts adds the labels and windows for the directories and
   * filenames available.  It also has a filename and terminal option panel
   * which it also adds to the main panel. This is then added to the content
   * panel to the center area and the bottom (south) contains the Close button.
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    getContentPane().setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel();
    mainPanel.setBackground(defaultBgColour);
    mainPanel.setForeground(defaultFgColour);
    JPanel fileNamePanel = new JPanel();
    fileNamePanel.setBackground(defaultBgColour);
    fileNamePanel.setForeground(defaultFgColour);
    JPanel terminOptionPanel = new JPanel();
    terminOptionPanel.setBackground(defaultBgColour);
    terminOptionPanel.setForeground(defaultFgColour);
    JPanel buttonPanel = new JPanel();
    buttonPanel.setBackground(defaultBgColour);
    buttonPanel.setForeground(defaultFgColour);

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
    directoryListBox.setFixedCellWidth(100);
    directoryListBox.setModel(directoryListModel);
    directoryListBox.setBackground(listColour);
    directoryListBox.setForeground(defaultFgColour);
    JScrollPane directoryListPane = new JScrollPane(directoryListBox);
    directoryListPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    directoryListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    gridBag.setConstraints(directoryListPane, constraints);
    mainPanel.add(directoryListPane);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    fileListBox = new JList();
    fileListModel = new DefaultListModel();
    fileListBox.setModel(fileListModel);
    fileListBox.setVisibleRowCount(8);
    fileListBox.setFixedCellWidth(100);
    fileListBox.setBackground(listColour);
    fileListBox.setForeground(defaultFgColour);
    JScrollPane fileListPane = new JScrollPane(fileListBox);
    fileListPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    fileListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    gridBag.setConstraints(fileListPane, constraints);
    mainPanel.add(fileListPane);

    tmpLabel = new JLabel("Filename: ");
    tmpLabel.setBackground(defaultBgColour);
    tmpLabel.setForeground(defaultFgColour);
    fileNamePanel.add(tmpLabel);
    fileNameTextField.setFont(defaultFont);
    fileNameTextField.setBackground(textBoxColour);
    fileNameTextField.setForeground(defaultFgColour);
    fileNamePanel.add(fileNameTextField);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    gridBag.setConstraints(fileNamePanel, constraints);
    mainPanel.add(fileNamePanel);

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
    fileListModel.removeAllElements();

    startTerminal = true;
    startTerminalCheckbox.setSelected(true);

    while (data.peek() != null)
    {
      fileListModel.addElement((String) data.retrieve());
    }
  }

  /**
   * Description of the Method
   *
   * @param data Description of Parameter
   */
  void updateDirectoryList(FIFOQueue data)
  {
    directoryListModel.removeAllElements();
    while (data.peek() != null)
    {
      directoryListModel.addElement((String) data.retrieve());
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  public class OkayButtonListener extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      myMultimediaAnimator.setRecordingName(fileNameTextField.getText());
      myMultimediaAnimator.createNewRecording();
      myMultimediaAnimator.toggleRecording();
      setVisible(false);
    }
  }
}
