package Software.Animator.Process;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import Software.Animator.RCOSFrame;
import Software.Animator.Support.GraphicButton;
import Software.Animator.Support.NewLabel;
import Software.Animator.Support.Positions.Position;
import MessageSystem.PostOffices.MessageHandler;
import Software.Util.FIFOQueue;

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
  private TextField tfFilename = new TextField(30);
  private boolean bStartTerminal = true;
  private Checkbox cbStartTerminal = new Checkbox();

  public ProgramManagerFrame (int x, int y, Image[] pmImages,
    ProgramManagerAnimator thisProgramManager)
  {
    super();
    setTitle("Program Manager");
    myImages = pmImages;
    myProgramManager = thisProgramManager;
    setSize(x,y);
  }

  public void setVisible (boolean b)
  {
     super.setVisible(b);
     if (b)
     {
       myProgramManager.setCurrentFile("");
       myProgramManager.updateList();
     }
  }

  public void setupLayout(Component c)
  {
    super.setupLayout(c);
    Panel pMain = new Panel();
    Panel pFilename = new Panel();
    Panel pTerminalOption = new Panel();
    Panel pButtons = new Panel();

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    pMain.setLayout(gridBag);
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;
    constraints.insets= new Insets(1,15,1,15);

    NewLabel lTmpLabel;

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    lTmpLabel = new NewLabel("Directories", titleFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pMain.add(lTmpLabel);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    lTmpLabel = new NewLabel("Files", titleFont);
    gridBag.setConstraints(lTmpLabel,constraints);
    pMain.add(lTmpLabel);

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    directoryListBox = new java.awt.List(8,false);
    directoryListBox.setBackground(listColour);
    directoryListBox.setForeground(defaultFgColour);
    gridBag.setConstraints(directoryListBox,constraints);
    pMain.add(directoryListBox);
    directoryListBox.addMouseListener(new directoryListBoxListener());

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    fileListBox = new java.awt.List(8,false);
    fileListBox.setBackground(listColour);
    fileListBox.setForeground(defaultFgColour);
    gridBag.setConstraints(fileListBox,constraints);
    pMain.add(fileListBox);
    fileListBox.addMouseListener(new fileListBoxListener());

    pFilename.add(new NewLabel("Filename: ", titleFont));
    tfFilename.setFont(defaultFont);
    tfFilename.setBackground(textBoxColour);
    tfFilename.setForeground(defaultFgColour);
    pFilename.add(tfFilename);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    gridBag.setConstraints(pFilename,constraints);
    pMain.add(pFilename);

    cbStartTerminal.setState(bStartTerminal);
    pTerminalOption.add(cbStartTerminal);
    cbStartTerminal.addItemListener(new startTerminalListener());
    pTerminalOption.add(new NewLabel("Automatically start terminal.", defaultFont));

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    gridBag.setConstraints(pTerminalOption,constraints);
    pMain.add(pTerminalOption);

    Button tmpOpenAWTButton = new Button("Open");
    pButtons.add(tmpOpenAWTButton);
    pButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpOpenAWTButton.addMouseListener(new okayButtonListener());

    Button tmpAWTButton = new Button("Close");
    pButtons.add(tmpAWTButton);
    tmpAWTButton.addMouseListener(new RCOSFrame.CloseAnimator());

    add("Center",pMain);
    add("South",pButtons);
  }

  void updateFileList(FIFOQueue data)
  {
    fileListBox.removeAll();

    bStartTerminal = true;
    cbStartTerminal.setState(true);

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
    tfFilename.setText(myProgramManager.getCurrentDirectory() +
      myProgramManager.getCurrentFile());
  }

  public class okayButtonListener extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      setVisible(false);
      tfFilename.setText("");
      fileListBox.deselect(fileListBox.getSelectedIndex());
      myProgramManager.newProcess(bStartTerminal);
    }
  }

  class startTerminalListener implements ItemListener
  {
    public void itemStateChanged(ItemEvent e)
    {
      if (e.getSource().equals(cbStartTerminal))
      {
        bStartTerminal = !bStartTerminal;
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
          tfFilename.setText("");
          fileListBox.deselect(fileListBox.getSelectedIndex());
          myProgramManager.newProcess(bStartTerminal);
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

        if (selected.compareTo("..") == 0)
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
