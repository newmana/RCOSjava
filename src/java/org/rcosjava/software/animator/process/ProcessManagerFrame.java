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
import org.rcosjava.software.process.ProcessPriority;

/**
 * It is the interface which allows users to manipulate running processes. It
 * allows the user to modify the priority of the process.
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
   * The process manager that I am representing.
   */
  private ProcessManagerAnimator myProcessManager;

  /**
   * Description of the Field
   */
  private JDialog changePriorityDialog;

  /**
   * Description of the Field
   */
  private JLabel processPrompt;

  /**
   * Description of the Field
   */
  private JTextField priorityTextField;

  /**
   * Constructor for the ProcessManagerFrame object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param thisProcessManager Description of Parameter
   */
  public ProcessManagerFrame(int x, int y,
      ProcessManagerAnimator thisProcessManager)
  {
    setTitle("Process Manager");
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
    changePriorityDialog = new JDialog(ProcessManagerFrame.this,
        "Change Priority", false);
    Container dialogContainer = changePriorityDialog.getContentPane();
    dialogContainer.setLayout(new BorderLayout());

    changePriorityDialog.setBackground(defaultBgColour);
    changePriorityDialog.setForeground(defaultFgColour);
    changePriorityDialog.setFont(defaultFont);
    changePriorityDialog.setSize(new Dimension(250, 90));

    JPanel priorityPanel = new JPanel();
    priorityPanel.setBackground(defaultBgColour);
    priorityPanel.setForeground(defaultFgColour);
    processPrompt = new JLabel("Priority of Process XX (1-100): ");
    processPrompt.setBackground(defaultBgColour);
    processPrompt.setForeground(defaultFgColour);
    priorityPanel.add(processPrompt,  BorderLayout.CENTER);
    priorityTextField = new JTextField(3);
    priorityTextField.setBackground(defaultBgColour);
    priorityTextField.setForeground(defaultFgColour);
    priorityPanel.add(priorityTextField);

    JPanel okCancelPanel = new JPanel();
    okCancelPanel.setBackground(defaultBgColour);
    okCancelPanel.setForeground(defaultFgColour);
    JButton tmpOkayButton = new JButton("Ok");
    tmpOkayButton.addMouseListener(new OkPriorityDialog());
    okCancelPanel.add(tmpOkayButton);
    JButton tmpCancelButton = new JButton("Cancel");
    tmpCancelButton.addMouseListener(new CancelPriorityDialog());
    okCancelPanel.add(tmpCancelButton, BorderLayout.SOUTH);

    dialogContainer.add(priorityPanel, BorderLayout.CENTER);
    dialogContainer.add(okCancelPanel, BorderLayout.SOUTH);
  }

  /**
   * Display the process prompt and change it to the process id.
   *
   * @param processId the process id to modify.
   * @param processPriority the current priority of the process.
   */
  public void promptProcessPriority(int processId,
      ProcessPriority processPriority)
  {
    processPrompt.setText("Priority of Process " + processId + " (1-100): ");
    priorityTextField.setText(Byte.toString(
        processPriority.getPriorityValue()));
    changePriorityDialog.show();
    priorityTextField.transferFocus();
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
      myProcessManager.sendSetProcessPriority(new ProcessPriority((byte)
          Integer.parseInt(priorityTextField.getText())));
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
