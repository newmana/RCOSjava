package net.sourceforge.rcosjava.software.animator.multimedia;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;
import net.sourceforge.rcosjava.software.animator.support.RCOSList;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.util.FIFOQueue;

/**
 * It is the interface that allows users to turn on/off or playback
 * pre-recorded messages.
 * <P>
 * <DT><B>History:</B>
 * </DT>
 *
 * @author Andrew Newman.
 * @created 1st January 2001
 * @version 1.00 $Date$
 */
public class MultimediaFrame extends RCOSFrame
{
  private MultimediaAnimator myMultimediaAnimator;
  private Image myImages[];
  private Message msg;
  private java.awt.List movieList;
  private Button recordButton, stepButton;
  private TextField fileNameTextField = new TextField(20);
  private Dialog fileNameDialog;
  private TextField dialogTextField;

  public MultimediaFrame (int x, int y, Image[] mmImages,
    MultimediaAnimator newMultimediaAnimator)
  {
    setTitle("Multimedia Tour");
    myImages = mmImages;
    myMultimediaAnimator = newMultimediaAnimator;
    setSize(x,y);
  }

  public synchronized void addNotify()
  {
    repaint();
    super.addNotify();
  }

  public void setVisible(boolean visibility)
  {
     super.setVisible(visibility);
     if (visibility)
     {
       myMultimediaAnimator.setCurrentFile("");
       myMultimediaAnimator.updateList();
     }
  }

  void updateDirectoryList(FIFOQueue data)
  {
    movieList.removeAll();
    String tmpString;
    while (data.peek() != null)
    {
      tmpString = (String) data.retrieve();
      movieList.add(tmpString.substring(0,tmpString.length()-1));
    }
  }

  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    //Set up modal file name dialog

    Panel dialogPanel = new Panel();
    fileNameDialog = new
      Dialog(MultimediaFrame.this , "Recording name", true);
    fileNameDialog.setBackground(defaultBgColour);
    fileNameDialog.setForeground(defaultFgColour);
    fileNameDialog.setFont(defaultFont);
    fileNameDialog.setSize(new Dimension(250,90));
    dialogPanel.add(new NewLabel("Filename: ", titleFont));
    dialogTextField = new TextField(20);
    dialogTextField.setBackground(defaultBgColour);
    dialogTextField.setForeground(defaultFgColour);
    dialogPanel.add(dialogTextField);
    Button tmpOkayButton = new Button("Ok");
    tmpOkayButton.addMouseListener(new OkFileDialog());
    dialogPanel.add(tmpOkayButton);
    Button tmpCancelButton = new Button("Cancel");
    tmpCancelButton.addMouseListener(new CancelFileDialog());
    dialogPanel.add(tmpCancelButton);
    fileNameDialog.add(dialogPanel);

    // Set up main windows

    Panel mainPanel = new Panel();
    Panel closePanel = new Panel();
    NewLabel tmpLabel;

    GridBagConstraints constraints = new GridBagConstraints();
    GridBagLayout gridBag = new GridBagLayout();
    mainPanel.setLayout(gridBag);
    constraints.gridwidth=1;
    constraints.gridheight=1;
    constraints.weighty=1;
    constraints.weightx=1;
    constraints.insets= new Insets(3,1,3,1);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    tmpLabel = new NewLabel("Existing Recordings", titleFont);
    gridBag.setConstraints(tmpLabel,constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    movieList = new java.awt.List(5,false);
    movieList.setBackground(listColour);
    movieList.setForeground(defaultFgColour);
    gridBag.setConstraints(movieList,constraints);
    mainPanel.add(movieList);
    movieList.addMouseListener(new movieListListener());

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    mainPanel.add(new NewLabel("Filename: ", titleFont));
    fileNameTextField.setFont(defaultFont);
    fileNameTextField.setBackground(textBoxColour);
    fileNameTextField.setForeground(defaultFgColour);

    constraints.anchor = GridBagConstraints.CENTER;
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    mainPanel.add(fileNameTextField);
    fileNameTextField.setEditable(false);

    stepButton = new Button("Step");
    closePanel.add(stepButton);
    stepButton.addMouseListener(new StepListener());
    stepButton.setEnabled(false);

    recordButton = new Button("Record");
    closePanel.add(recordButton);
    recordButton.addMouseListener(new RecordToggle());

    closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new Button("Close");
    closePanel.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());

    add("Center", mainPanel);
    add("South", closePanel);
  }

  public void setFilenameAndRecord(String fileName)
  {
    recordButton.setLabel("Pause");
    stepButton.setEnabled(false);
  }

////////////////////////////////////////////////////////////////////////////////

  class RecordToggle extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      if (!myMultimediaAnimator.getRecording())
      {
        fileNameDialog.show();
        dialogTextField.transferFocus();
      }
      else
      {
        stepButton.setEnabled(true);
        recordButton.setLabel("Record");
      }
    }
  }

  class StepListener extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      myMultimediaAnimator.playStep();
    }
  }

  class movieListListener extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      if (e.getClickCount() == 2)
      {
        //A double click was detected.

        String selected = movieList.getSelectedItem();

        myMultimediaAnimator.setCurrentFile(selected);
        fileNameTextField.setText(selected);
        recordButton.setEnabled(false);
        stepButton.setEnabled(true);
      }
    }
  }

  class OkFileDialog extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      fileNameTextField.setText(dialogTextField.getText());
      myMultimediaAnimator.setCurrentFile(dialogTextField.getText());
      myMultimediaAnimator.createDirectory();
      myMultimediaAnimator.recordToggle();
      recordButton.setLabel("Stop");
      stepButton.setEnabled(false);
      fileNameDialog.setVisible(false);
    }
  }

  class CancelFileDialog extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      fileNameDialog.setVisible(false);
    }
  }
}