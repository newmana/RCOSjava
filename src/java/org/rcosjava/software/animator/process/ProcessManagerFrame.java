package org.rcosjava.software.animator.process;
import java.applet.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.*;
import org.rcosjava.messaging.messages.Message;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.support.GraphicButton;
import org.rcosjava.software.animator.support.NewLabel;
import org.rcosjava.software.animator.support.RCOSList;

/**
 * It is the interface which allows users to manipulate running processes. It
 * allows them to be killed, to stop the CPU or to execute one instruction at a
 * time.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 10/1/97 Created. AN </DD>
 * <DD> 23/11/98 Converted to Java 1.1. AN </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @created 14th January 1996
 * @version 1.00 $Date$
 */
public class ProcessManagerFrame extends RCOSFrame
{
  /**
   * Description of the Field
   */
  private ProcessManagerAnimator myProcessManager;

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
  private RCOSList processList;

  /**
   * Description of the Field
   */
  private Dialog changePriorityDialog;

  /**
   * Description of the Field
   */
  private NewLabel processPrompt;

  /**
   * Description of the Field
   */
  private TextField priorityTextField;

  /**
   * Constructor for the ProcessManagerFrame object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param images Description of Parameter
   * @param thisProcessManager Description of Parameter
   */
  public ProcessManagerFrame(int x, int y, ImageIcon[] images,
      ProcessManagerAnimator thisProcessManager)
  {
    setTitle("Process Manager");
    myImages = new Image[images.length];
    for (int index = 0; index < images.length; index++)
    {
      myImages[index] = images[index].getImage();
    }
    myProcessManager = thisProcessManager;
    setSize(x, y);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(Component c)
  {
    Panel dialogPanel = new Panel();

    changePriorityDialog = new
        Dialog(ProcessManagerFrame.this, "Change Priority", true);
    changePriorityDialog.setBackground(defaultBgColour);
    changePriorityDialog.setForeground(defaultFgColour);
    changePriorityDialog.setFont(defaultFont);
    changePriorityDialog.setSize(new Dimension(250, 90));
    processPrompt = new NewLabel("Priority of Process XX (1-100): ", titleFont);
    dialogPanel.add(processPrompt);
    priorityTextField = new TextField(2);
    priorityTextField.setBackground(defaultBgColour);
    priorityTextField.setForeground(defaultFgColour);
    dialogPanel.add(priorityTextField);

    JButton tmpOkayButton = new JButton("Ok");

    tmpOkayButton.addMouseListener(new OkPriorityDialog());
    dialogPanel.add(tmpOkayButton);

    JButton tmpCancelButton = new JButton("Cancel");

    tmpCancelButton.addMouseListener(new CancelPriorityDialog());
    dialogPanel.add(tmpCancelButton);
    changePriorityDialog.add(dialogPanel);

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

    GraphicButton tmpGButton;

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    tmpLabel = new NewLabel("Processes", titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    constraints.gridheight = 2;
    constraints.anchor = GridBagConstraints.CENTER;
    processList = new RCOSList(this, 5, false);
    gridBag.setConstraints(processList, constraints);
    mainPanel.add(processList);

    constraints.gridheight = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton(myImages[0], myImages[1],
        "Priority", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton, constraints);
    mainPanel.add(tmpGButton);
    tmpGButton.addMouseListener(new ChangePriority());

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton(myImages[0], myImages[1],
        "Kill", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton, constraints);
    mainPanel.add(tmpGButton);
    tmpGButton.addMouseListener(new KillProcess());

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    tmpLabel = new NewLabel("Command", titleFont);
    gridBag.setConstraints(tmpLabel, constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth = 1;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton(myImages[0], myImages[1],
        "Stop", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton, constraints);
    mainPanel.add(tmpGButton);
    tmpGButton.addMouseListener(new StopProcess());

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton(myImages[0], myImages[1],
        "Run", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton, constraints);
    mainPanel.add(tmpGButton);
    tmpGButton.addMouseListener(new RunProcess());

    closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new JButton("Close");
    closePanel.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());

    getContentPane().add("Center", mainPanel);
    getContentPane().add("South", closePanel);
  }

  /**
   * Adds a feature to the Notify attribute of the ProcessManagerFrame object
   */
  public void addNotify()
  {
    repaint();
    super.addNotify();
  }

  /**
   * Description of the Method
   *
   * @param processId Description of Parameter
   * @param processPriority Description of Parameter
   */
  public void promptProcessPriority(int processId, int processPriority)
  {
    processPrompt.setText("Priority of Process " + processId + " (1-100): ");
    priorityTextField.setText(Integer.toString(processPriority));
    changePriorityDialog.show();
    priorityTextField.transferFocus();
  }

  /**
   * Description of the Method
   */
  void clearProcesses()
  {
    this.processList.removeAll();
  }

  /**
   * Adds a feature to the Process attribute of the ProcessManagerFrame object
   *
   * @param sNewProcess The feature to be added to the Process attribute
   */
  void addProcess(String sNewProcess)
  {
    this.processList.add(sNewProcess);
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class KillProcess extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      if (processList.getSelectedItem() != null)
      {
        int process = Integer.parseInt(processList.getSelectedItem());
        myProcessManager.sendKillMessage(process);
      }
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class ChangePriority extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      if (processList.getSelectedItem() != null)
      {
        int process = Integer.parseInt(processList.getSelectedItem());

        myProcessManager.sendRequestProcessPriority(process);
      }
    }
  }


  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class StopProcess extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      //myProcessManager.sendStopMessage();
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class RunProcess extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      //myProcessManager.sendRunMessage();
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class OkPriorityDialog extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      myProcessManager.sendSetProcessPriority(
          Integer.parseInt(priorityTextField.getText()));
      changePriorityDialog.setVisible(false);
    }
  }

  /**
   * Description of the Class
   *
   * @author administrator
   * @created 28 April 2002
   */
  class CancelPriorityDialog extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      changePriorityDialog.setVisible(false);
    }
  }
}
