package net.sourceforge.rcosjava.software.animator.process;

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

/**
 * It is the interface which allows users to manipulate running processes.
 * It allows them to be killed, to stop the CPU or to execute one instruction
 * at a time.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 10/1/97  Created. AN
 * </DD><DD>
 * 23/11/98 Converted to Java 1.1. AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman
 * @version 1.00 $Date$
 * @created 14th January 1996
 */
public class ProcessManagerFrame extends RCOSFrame
{
  private ProcessManagerAnimator myProcessManager;
  private Image myImages[];
  private Message msg;
  private RCOSList processList;
  private Dialog changePriorityDialog;
  private NewLabel processPrompt;
  private TextField priorityTextField;

  public ProcessManagerFrame (int x, int y, Image[] images,
    ProcessManagerAnimator thisProcessManager)
  {
    setTitle("Process Manager");
    myImages = images;
    myProcessManager = thisProcessManager;
    setSize(x,y);
  }

  void clearProcesses()
  {
    this.processList.removeAll();
  }

  void addProcess(String sNewProcess)
  {
    this.processList.add(sNewProcess);
  }

  public void addNotify()
  {
    repaint();
    super.addNotify();
  }

  public void setupLayout(Component c)
  {
    super.setupLayout(c);

    Panel dialogPanel = new Panel();
    changePriorityDialog = new
      Dialog(ProcessManagerFrame.this , "Change Priority", true);
    changePriorityDialog.setBackground(defaultBgColour);
    changePriorityDialog.setForeground(defaultFgColour);
    changePriorityDialog.setFont(defaultFont);
    changePriorityDialog.setSize(new Dimension(250,90));
    processPrompt = new NewLabel("Priority of Process XX (1-100): ", titleFont);
    dialogPanel.add(processPrompt);
    priorityTextField = new TextField(2);
    priorityTextField.setBackground(defaultBgColour);
    priorityTextField.setForeground(defaultFgColour);
    dialogPanel.add(priorityTextField);
    Button tmpOkayButton = new Button("Ok");
    tmpOkayButton.addMouseListener(new OkPriorityDialog());
    dialogPanel.add(tmpOkayButton);
    Button tmpCancelButton = new Button("Cancel");
    tmpCancelButton.addMouseListener(new CancelPriorityDialog());
    dialogPanel.add(tmpCancelButton);
    changePriorityDialog.add(dialogPanel);

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

    GraphicButton tmpGButton;

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    tmpLabel = new NewLabel("Processes", titleFont);
    gridBag.setConstraints(tmpLabel,constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth=1;
    constraints.gridheight=2;
    constraints.anchor = GridBagConstraints.CENTER;
    processList = new RCOSList(this,5,false);
    gridBag.setConstraints(processList,constraints);
    mainPanel.add(processList);

    constraints.gridheight=1;
    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton (myImages[0], myImages[1],
      "Priority", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton,constraints);
    mainPanel.add(tmpGButton);
    tmpGButton.addMouseListener(new ChangePriority());

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton (myImages[0], myImages[1],
      "Kill", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton,constraints);
    mainPanel.add(tmpGButton);
    tmpGButton.addMouseListener(new KillProcess());

    constraints.gridwidth=GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.WEST;
    tmpLabel = new NewLabel("Command", titleFont);
    gridBag.setConstraints(tmpLabel,constraints);
    mainPanel.add(tmpLabel);

    constraints.gridwidth=1;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton (myImages[0], myImages[1],
      "Step", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton,constraints);
    mainPanel.add(tmpGButton);
    tmpGButton.addMouseListener(new StepProcess());

    constraints.gridwidth = GridBagConstraints.REMAINDER;
    constraints.anchor = GridBagConstraints.CENTER;
    tmpGButton = new GraphicButton (myImages[0], myImages[1],
      "Run", defaultFont, buttonColour, true);
    gridBag.setConstraints(tmpGButton,constraints);
    mainPanel.add(tmpGButton);
    tmpGButton.addMouseListener(new RunProcess());

    closePanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    tmpButton = new Button("Close");
    closePanel.add(tmpButton);
    tmpButton.addMouseListener(new CloseAnimator());

    add("Center",mainPanel);
    add("South",closePanel);
  }

  public void promptProcessPriority(int processId, int processPriority)
  {
    processPrompt.setText("Priority of Process " + processId + " (1-100): ");
    priorityTextField.setText(Integer.toString(processPriority));
    changePriorityDialog.show();
    priorityTextField.transferFocus();
  }

  class KillProcess extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      if (processList.getSelectedItem() != null)
      {
        int process = Integer.parseInt(processList.getSelectedItem());
        myProcessManager.kill(process);
      }
    }
  }

  class ChangePriority extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      if (processList.getSelectedItem() != null)
      {
        int process = Integer.parseInt(processList.getSelectedItem());
        myProcessManager.getProcessPriority(process);
      }
    }
  }


  class StepProcess extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      myProcessManager.step();
    }
  }

  class RunProcess extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      myProcessManager.run();
    }
  }

  class OkPriorityDialog extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      myProcessManager.setProcessPriority(
        Integer.parseInt(priorityTextField.getText()));
      changePriorityDialog.setVisible(false);
    }
  }

  class CancelPriorityDialog extends MouseAdapter
  {
    public void mouseClicked(MouseEvent e)
    {
      changePriorityDialog.setVisible(false);
    }
  }
}
