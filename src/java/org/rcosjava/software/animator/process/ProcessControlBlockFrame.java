package org.rcosjava.software.animator.process;
import java.applet.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import java.net.*;
import java.util.*;
import org.rcosjava.messaging.messages.Message;
import org.rcosjava.software.animator.RCOSFrame;
import org.rcosjava.software.animator.support.GraphicButton;
import org.rcosjava.software.animator.support.NewLabel;
import org.rcosjava.software.animator.support.RCOSList;
import org.rcosjava.software.process.ProcessPriority;
import org.rcosjava.software.process.RCOSProcess;

/**
 * It is the interface which allows users to display the current process control
 * block of a selected process.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 10/1/97 Created. AN </DD>
 * <DD> 23/11/98 Converted to Java 1.1. AN </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @created 14th January 1996
 * @version 1.00 $Date$
 */
public class ProcessControlBlockFrame extends RCOSFrame
{
  private DefaultMutableTreeNode idNode;
  private DefaultMutableTreeNode priorityNode;
  private DefaultMutableTreeNode stateNode;
  private DefaultMutableTreeNode fileNameNode;
  private DefaultMutableTreeNode fileSizeNode;
  private DefaultMutableTreeNode terminalIdNode;
  private DefaultMutableTreeNode codePagesNode;
  private DefaultMutableTreeNode stackPagesNode;
  private DefaultMutableTreeNode cpuTicksNode;
  private DefaultMutableTreeNode pcNode;
  private DefaultMutableTreeNode spNode;
  private DefaultMutableTreeNode bpNode;

  private static final String ID = "ID: ";
  private static final String PRIORITY = "Priority: ";
  private static final String STATE = "State: ";
  private static final String FILE_NAME = "Filename: ";
  private static final String FILE_SIZE = "File Size: ";
  private static final String TERMINAL_ID = "Terminal ID: ";
  private static final String CODE_PAGES = "Code Pages: ";
  private static final String STACK_PAGES = "Stack Pages: ";
  private static final String CPU_TICKS = "CPU Ticks: ";
  private static final String PROGRAM_COUNTER = "Program Counter: ";
  private static final String STACK_POINTER = "Stack Pointer: ";
  private static final String BASE_POINTER = "Base Pointer: ";

  /**
   * Constructor for the ProcessManagerFrame object
   *
   * @param x Description of Parameter
   * @param y Description of Parameter
   * @param newProcessScheduler Description of Parameter
   */
  public ProcessControlBlockFrame(int x, int y,
      ProcessSchedulerAnimator newProcessScheduler)
  {
    setTitle("Process Control Block");
//    myProcessManager = thisProcessManager;
    setSize(x, y);
  }

  /**
   * Description of the Method
   *
   * @param c Description of Parameter
   */
  public void setupLayout(JComponent c)
  {
    super.setupLayout(c);

    DefaultMutableTreeNode details = null;
    DefaultMutableTreeNode node = null;

    DefaultMutableTreeNode top = new DefaultMutableTreeNode(
        "Process Control Block");

    details = new DefaultMutableTreeNode("Details");
    top.add(details);

    idNode = new DefaultMutableTreeNode(ID);
    details.add(idNode);
    priorityNode = new DefaultMutableTreeNode(PRIORITY);
    details.add(priorityNode);
    stateNode = new DefaultMutableTreeNode(STATE);
    details.add(stateNode);

    details = new DefaultMutableTreeNode("I/O");
    top.add(details);

    fileNameNode = new DefaultMutableTreeNode(FILE_NAME);
    details.add(fileNameNode);
    fileSizeNode = new DefaultMutableTreeNode(FILE_SIZE);
    details.add(fileSizeNode);
    terminalIdNode = new DefaultMutableTreeNode(TERMINAL_ID);
    details.add(terminalIdNode);

    details = new DefaultMutableTreeNode("Memory");
    top.add(details);

    codePagesNode = new DefaultMutableTreeNode(CODE_PAGES);
    details.add(codePagesNode);
    stackPagesNode = new DefaultMutableTreeNode(STACK_PAGES);
    details.add(stackPagesNode);

    details = new DefaultMutableTreeNode("Accounting");
    top.add(details);

    cpuTicksNode = new DefaultMutableTreeNode(CPU_TICKS);
    details.add(cpuTicksNode);

    details = new DefaultMutableTreeNode("Context");
    top.add(details);

    pcNode = new DefaultMutableTreeNode(PROGRAM_COUNTER);
    details.add(pcNode);
    spNode = new DefaultMutableTreeNode(STACK_POINTER);
    details.add(spNode);
    bpNode = new DefaultMutableTreeNode(BASE_POINTER);
    details.add(bpNode);

    //Create a tree that allows one selection at a time.
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setBackground(defaultBgColour);
    renderer.setForeground(defaultFgColour);
    renderer.setTextNonSelectionColor(defaultFgColour);
    renderer.setTextSelectionColor(defaultFgColour);
    renderer.setBackgroundNonSelectionColor(defaultBgColour);
    renderer.setBackgroundSelectionColor(defaultBgColour);
    renderer.setBorderSelectionColor(defaultBgColour);
    renderer.setOpenIcon(null);
    renderer.setClosedIcon(null);
    renderer.setLeafIcon(null);

    final JTree tree = new JTree(top);
    tree.putClientProperty("JTree.lineStyle", "Angled");
    tree.setBackground(defaultBgColour);
    tree.setForeground(defaultFgColour);
    tree.setCellRenderer(renderer);
    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);

    //Listen for when the selection changes.
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
//        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
//                                      tree.getLastSelectedPathComponent();
//
//        if (node == null) return;
//
//        Object nodeInfo = node.getUserObject();
//        if (node.isLeaf()) {
//        }
//        else {
//        }
      }
    });

    //Create the scroll pane and add the tree to it.
    JScrollPane treeView = new JScrollPane(tree);
    treeView.setBackground(defaultBgColour);
    treeView.setForeground(defaultFgColour);
    treeView.setBorder(new EmptyBorder(3,3,3,3));

    JPanel okPanel = new JPanel();
    okPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    okPanel.setBackground(defaultBgColour);
    okPanel.setForeground(defaultFgColour);
    JButton tmpOkayButton = new JButton("Ok");
    okPanel.add(tmpOkayButton);
    tmpOkayButton.addMouseListener(new OkPCB());

    getContentPane().add(treeView, BorderLayout.CENTER);
    getContentPane().add(okPanel, BorderLayout.SOUTH);
  }

  /**
   * Updates the display of current values of the process.
   *
   * @param newProcess the new process details that have been updated.
   */
  public void updateDisplay(RCOSProcess newProcess)
  {
    idNode.setUserObject(ID + newProcess.getPID());
    priorityNode.setUserObject(PRIORITY + newProcess.getPriority().toString());
    stateNode.setUserObject(STATE + newProcess.getState());

    fileNameNode.setUserObject(FILE_NAME + newProcess.getFileName());
    fileSizeNode.setUserObject(FILE_SIZE + newProcess.getFileSize());
    terminalIdNode.setUserObject(TERMINAL_ID + newProcess.getTerminalId());

    codePagesNode.setUserObject(CODE_PAGES + newProcess.getCodePages());
    stackPagesNode.setUserObject(STACK_PAGES + newProcess.getStackPages());

    cpuTicksNode.setUserObject(CPU_TICKS + newProcess.getCPUTicks());

    pcNode.setUserObject(PROGRAM_COUNTER +
        newProcess.getContext().getProgramCounter());
    spNode.setUserObject(STACK_POINTER +
        newProcess.getContext().getStackPointer());
    bpNode.setUserObject(BASE_POINTER +
        newProcess.getContext().getBasePointer());

    repaint();
  }

  /**
   */
  class OkPCB extends MouseAdapter
  {
    /**
     * Description of the Method
     *
     * @param e Description of Parameter
     */
    public void mouseClicked(MouseEvent e)
    {
      ProcessControlBlockFrame.this.setVisible(false);
    }
  }
}
