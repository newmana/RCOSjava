package org.rcosjava.software.animator.multimedia;
import java.applet.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.*;
import org.rcosjava.messaging.messages.Message;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.support.NewLabel;
import org.rcosjava.software.util.FIFOQueue;

/**
 * It is the interface that allows users to turn on/off or playback pre-recorded
 * messages.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 2001
 * @version 1.00 $Date$
 */
public class MultimediaFrame extends RCOSFrame
{
  /**
   * Description of the Field
   */
  private MultimediaAnimator myMultimediaAnimator;
  /**
   * Description of the Field
   */
  private Image myImages[];
  /**
   * Description of the Field
   */
  private Message msg;
  /**
   * Description of the Field
   */
  private java.awt.List movieList;
  /**
   * Description of the Field
   */
  private JButton recordButton, stepButton;
  /**
   * Description of the Field
   */
  private TextField fileNameTextField = new TextField(20);
  /**
   * Description of the Field
   */
  private Dialog fileNameDialog;
  /**
   * Description of the Field
   */
  private TextField dialogTextField;

  /**
   * Constructor for the MultimediaFrame object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param mmImages Description of Parameter
   * @param newMultimediaAnimator Description of Parameter
   */
  public MultimediaFrame(int x, int y, ImageIcon[] images,
      MultimediaAnimator newMultimediaAnimator)
  {
    setTitle("Multimedia Tour");
    myImages = new Image[images.length];
    for (int index = 0; index < images.length; index++)
    {
      myImages[index] = images[index].getImage();
    }
    myMultimediaAnimator = newMultimediaAnimator;
    setSize(x, y);
  }

  /**
   * Sets the Visible attribute of the MultimediaFrame object
   *
   * @param visibility The new Visible value
   */
  public void setVisible(boolean visibility)
  {
    super.setVisible(visibility);
    if (visibility)
    {
      myMultimediaAnimator.setCurrentFile("");
      myMultimediaAnimator.updateList();
    }
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    //Set up modal file name dialog

    Panel dialogPanel = new Panel();

    fileNameDialog = new
        Dialog(MultimediaFrame.this, "Recording Name", true);
    fileNameDialog.setBackground(defaultBgColour);
    fileNameDialog.setForeground(defaultFgColour);
    fileNameDialog.setFont(defaultFont);
    fileNameDialog.setSize(new Dimension(250, 90));
    dialogPanel.add(new NewLabel("Filename: ", titleFont));
    dialogTextField = new TextField(20);
    dialogTextField.setBackground(defaultBgColour);
    dialogTextField.setForeground(defaultFgColour);
    dialogPanel.add(dialogTextField);

    JButton tmpOkayButton = new JButton("Ok");

    tmpOkayButton.addMouseListener(new OkFileDialog());
    dialogPanel.add(tmpOkayButton);

    JButton tmpCancelButton = new JButton("Cancel");

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
    constraints.gridwidth = 1;
    constraints.gridheight = 1;
    constraints.weighty = 1;
    constraints.weightx = 1;
    constraints.insets = new Insets(3, 1, 3, 1);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    tmpLabel = new NewLabel("Existing Recordings", titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    movieList = new java.awt.List(5, false);
    movieList.setBackground(listColour);
    movieList.setForeground(defaultFgColour);
    gridBag.setConstraints(movieList, constraints);
    mainPanel.add(movieList);
    movieList.addMouseListener(new movieListListener());

    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.CENTER;
    mainPanel.add(new NewLabel("Filename: ", titleFont));
    fileNameTextField.setFont(defaultFont);
    fileNameTextField.setBackground(textBoxColour);
    fileNameTextField.setForeground(defaultFgColour);

    constraints.anchor = GridBagConstraints.CENTER;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    mainPanel.add(fileNameTextField);
    fileNameTextField.setEditable(false);

    stepButton = new JButton("Step");
    closePanel.add(stepButton);
    stepButton.addMouseListener(new StepListener());
    stepButton.setEnabled(false);

    recordButton = new JButton("Record");
    closePanel.add(recordButton);
    recordButton.addMouseListener(new RecordToggle());

    closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new JButton("Close");
    closePanel.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());

    getContentPane().add("Center", mainPanel);
    getContentPane().add("South", closePanel);
  }

  /**
   * Sets the FilenameAndRecord attribute of the MultimediaFrame object
   *
   * @param fileName The new FilenameAndRecord value
   */
  public void setFilenameAndRecord(String fileName)
  {
    recordButton.setLabel("Pause");
    stepButton.setEnabled(false);
  }

  /**
   * Adds a feature to the Notify attribute of the MultimediaFrame object
   */
  public void addNotify()
  {
    repaint();
    super.addNotify();
  }

  /**
   * Description of the Method
   *
   * @param data Description of Parameter
   */
  void updateDirectoryList(FIFOQueue data)
  {
    movieList.removeAll();

    String tmpString;

    while (data.peek() != null)
    {
      tmpString = (String) data.retrieve();
      movieList.add(tmpString.substring(0, tmpString.length() - 1));
    }
  }

////////////////////////////////////////////////////////////////////////////////

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class RecordToggle extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
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

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class StepListener extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      myMultimediaAnimator.playStep();
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class movieListListener extends MouseAdapter
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

        String selected = movieList.getSelectedItem();

        myMultimediaAnimator.setCurrentFile(selected);
        fileNameTextField.setText(selected);
        recordButton.setEnabled(false);
        stepButton.setEnabled(true);
      }
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class OkFileDialog extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
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

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class CancelFileDialog extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      fileNameDialog.setVisible(false);
    }
  }
}
