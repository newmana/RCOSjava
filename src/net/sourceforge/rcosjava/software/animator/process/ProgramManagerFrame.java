package net.sourceforge.rcosjava.software.animator.process;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;
import net.sourceforge.rcosjava.software.animator.support.positions.Position;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.software.util.FIFOQueue;

/**
 * Communicates via messages and via awt Events with the main RCOS frame and
 * other components. It's functions are to set up and comunicate with a
 * remote server for the loading of programs.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 31/03/96 DJ Added fifoQueue for filenames.
 * </DD><DD>
 * 30/12/96 Rewritten with frame moved to Animators.
 * </DD><DD>
 * 01/01/97 Section from Program Manager moved here.
 * </DD><DD>
 * 02/01/97 New layout (GridBagLayout). Option to automatically start terminal
 * added.
 * </DD></DT>
 * @author Andrew Newman.
 * @author Brett Carter.
 * @author David Jones.
 * @created 19th March 1996
 * @version 1.00 $Date$
 */
 public class ProgramManagerFrame extends RCOSFrame
{
  private ProgramManagerAnimator myProgramManager;
  private Image myImages[];
  private java.awt.List directoryListBox;
  private java.awt.List fileListBox;
  private TextField fileNameTextField = new TextField(30);
  private boolean startTerminal = true;
  private Checkbox startTerminalCheckbox = new Checkbox();

  public ProgramManagerFrame(int x, int y, Image[] pmImages,
    ProgramManagerAnimator thisProgramManager)
  {
    super();
    setTitle("Program Manager");
    myImages = pmImages;
    myProgramManager = thisProgramManager;
    setSize(x,y);
  }

  public void setVisible(boolean visibility)
  {
     super.setVisible(visibility);
     if (visibility)
     {
       myProgramManager.setCurrentFile("");
       myProgramManager.updateList();
     }
  }

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
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;
    constraints.insets= new Insets(1,15,1,15);

    NewLabel tmpLabel;

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpLabel = new NewLabel("Directories", titleFont);
    gridBag.setConstraints(tmpLabel,constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpLabel = new NewLabel("Files", titleFont);
    gridBag.setConstraints(tmpLabel,constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    directoryListBox = new java.awt.List(8,false);
    directoryListBox.setBackground(listColour);
    directoryListBox.setForeground(defaultFgColour);
    gridBag.setConstraints(directoryListBox,constraints);
    mainPanel.add(directoryListBox);
    directoryListBox.addMouseListener(new directoryListBoxListener());

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    fileListBox = new java.awt.List(8,false);
    fileListBox.setBackground(listColour);
    fileListBox.setForeground(defaultFgColour);
    gridBag.setConstraints(fileListBox,constraints);
    mainPanel.add(fileListBox);
    fileListBox.addMouseListener(new fileListBoxListener());

    fileNamePanel.add(new NewLabel("Filename: ", titleFont));
    fileNameTextField.setFont(defaultFont);
    fileNameTextField.setBackground(textBoxColour);
    fileNameTextField.setForeground(defaultFgColour);
    fileNamePanel.add(fileNameTextField);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    gridBag.setConstraints(fileNamePanel,constraints);
    mainPanel.add(fileNamePanel);

    startTerminalCheckbox.setState(startTerminal);
    terminOptionPanel.add(startTerminalCheckbox);
    startTerminalCheckbox.addItemListener(new startTerminalListener());
    terminOptionPanel.add(new NewLabel("Automatically start terminal.", defaultFont));

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(terminOptionPanel,constraints);
    mainPanel.add(terminOptionPanel);

    Button tmpOpenAWTButton = new Button("Open");
    buttonPanel.add(tmpOpenAWTButton);
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpOpenAWTButton.addMouseListener(new okayButtonListener());

    Button tmpAWTButton = new Button("Close");
    buttonPanel.add(tmpAWTButton);
    tmpAWTButton.addMouseListener(new RCOSFrame.CloseAnimator());

    add("Center",mainPanel);
    add("South",buttonPanel);
  }

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

  void updateDirectoryList(FIFOQueue data)
  {
    directoryListBox.removeAll();
    while (data.peek() != null)
    {
      directoryListBox.add((String) data.retrieve());
    }
  }

  void updateSelected()
  {
    fileNameTextField.setText(myProgramManager.getCurrentDirectory() +
      myProgramManager.getCurrentFile());
  }

  public class okayButtonListener extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      setVisible(false);
      fileNameTextField.setText("");
      fileListBox.deselect(fileListBox.getSelectedIndex());
      myProgramManager.newProcess(startTerminal);
    }
  }

  class startTerminalListener implements ItemListener
  {
    public void itemStateChanged(ItemEvent e)
    {
      if (e.getSource().equals(startTerminalCheckbox))
      {
        startTerminal = !startTerminal;
      }
    }
  }

  class fileListBoxListener extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      switch(e.getClickCount())
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

  class directoryListBoxListener extends MouseAdapter
  {
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
