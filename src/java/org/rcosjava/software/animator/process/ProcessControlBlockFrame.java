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
 * It is the interface which allows users to see the current process control
 * block of a selected process.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 10/1/97 Created. AN </DD>
 * <DD> 23/11/98 Converted to Java 1.1. AN </DD> </DT>
 * <P>
 * @author Andrew Newman
 * @created 15th November 2002
 * @version 1.00 $Date$
 */
public class ProcessControlBlockFrame extends RCOSFrame
{
  /**
   * The tree node to display the ID of the process.
   */
  private DefaultMutableTreeNode idNode;

  /**
   * The tree node to display the priority of the process.
   */
  private DefaultMutableTreeNode priorityNode;

  /**
   * The tree node to display the state of the process.
   */
  private DefaultMutableTreeNode stateNode;

  /**
   * The tree node to display the file name of the process.
   */
  private DefaultMutableTreeNode fileNameNode;

  /**
   * The tree node to display the file size of the process.
   */
  private DefaultMutableTreeNode fileSizeNode;

  /**
   * The tree node to display the terminal ID of the process.
   */
  private DefaultMutableTreeNode terminalIdNode;

  /**
   * The tree node to display the number of code pages of the process.
   */
  private DefaultMutableTreeNode codePagesNode;

  /**
   * The tree node to display the number of stack pages of the process.
   */
  private DefaultMutableTreeNode stackPagesNode;

  /**
   * The tree node to display the number of CPU ticks of the process.
   */
  private DefaultMutableTreeNode cpuTicksNode;

  /**
   * The tree node to display the process' context, its program counter.
   */
  private DefaultMutableTreeNode pcNode;

  /**
   * The tree node to display the process' context, its stack pointer.
   */
  private DefaultMutableTreeNode spNode;

  /**
   * The tree node to display the process' context, its base pointer.
   */
  private DefaultMutableTreeNode bpNode;

  /**
   * The string to display before the id value.
   */
  private static final String ID = "ID: ";

  /**
   * The string to display before the priority value.
   */
  private static final String PRIORITY = "Priority: ";

  /**
   * The string to display before the state value.
   */
  private static final String STATE = "State: ";

  /**
   * The string to display before the file name value.
   */
  private static final String FILE_NAME = "Filename: ";

  /**
   * The string to display before the file size value.
   */
  private static final String FILE_SIZE = "File Size: ";

  /**
   * The string to display before the terminal id value.
   */
  private static final String TERMINAL_ID = "Terminal ID: ";

  /**
   * The string to display before the code pages value.
   */
  private static final String CODE_PAGES = "Code Pages: ";

  /**
   * The string to display before the stack pages value.
   */
  private static final String STACK_PAGES = "Stack Pages: ";

  /**
   * The string to display before the cpu ticks value.
   */
  private static final String CPU_TICKS = "CPU Ticks: ";

  /**
   * The string to display before the program counter value.
   */
  private static final String PROGRAM_COUNTER = "Program Counter: ";

  /**
   * The string to display before the stack pointer value.
   */
  private static final String STACK_POINTER = "Stack Pointer: ";

  /**
   * The string to display before the base pointer value.
   */
  private static final String BASE_POINTER = "Base Pointer: ";

  /**
   * Create a new ProcessControlBlock with the given dimensions.
   *
   * @param x The width of the frame.
   * @param y The height of the frame.
   * @param newProcessScheduler not used.
   */
  public ProcessControlBlockFrame(int x, int y,
      ProcessSchedulerAnimator newProcessScheduler)
  {
    setTitle("Process Control Block");
    setSize(x, y);
  }

  /**
   * Create a simple tree view to display all of a process details.
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

    // Add the standard values of the process its id, priority and state.
    details = new DefaultMutableTreeNode("Details");
    top.add(details);

    idNode = new DefaultMutableTreeNode(ID);
    details.add(idNode);
    priorityNode = new DefaultMutableTreeNode(PRIORITY);
    details.add(priorityNode);
    stateNode = new DefaultMutableTreeNode(STATE);
    details.add(stateNode);

    // Add the I/O details such as file details and terminal details
    details = new DefaultMutableTreeNode("I/O");
    top.add(details);

    fileNameNode = new DefaultMutableTreeNode(FILE_NAME);
    details.add(fileNameNode);
    fileSizeNode = new DefaultMutableTreeNode(FILE_SIZE);
    details.add(fileSizeNode);
    terminalIdNode = new DefaultMutableTreeNode(TERMINAL_ID);
    details.add(terminalIdNode);

    // Add the number of code and stack pages.
    details = new DefaultMutableTreeNode("Memory");
    top.add(details);

    codePagesNode = new DefaultMutableTreeNode(CODE_PAGES);
    details.add(codePagesNode);
    stackPagesNode = new DefaultMutableTreeNode(STACK_PAGES);
    details.add(stackPagesNode);

    // Add the accounting details i.e. CPU ticks.
    details = new DefaultMutableTreeNode("Accounting");
    top.add(details);

    cpuTicksNode = new DefaultMutableTreeNode(CPU_TICKS);
    details.add(cpuTicksNode);

    // Add the context details of program counter, stack pointer and base
    // pointer
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

    // Make the cells black background and white text at all times.
    renderer.setBackground(defaultBgColour);
    renderer.setForeground(defaultFgColour);
    renderer.setTextNonSelectionColor(defaultFgColour);
    renderer.setTextSelectionColor(defaultFgColour);
    renderer.setBackgroundNonSelectionColor(defaultBgColour);
    renderer.setBackgroundSelectionColor(defaultBgColour);
    renderer.setBorderSelectionColor(defaultBgColour);
    renderer.setFont(defaultFont);

    // Remove all icons.
    renderer.setOpenIcon(null);
    renderer.setClosedIcon(null);
    renderer.setLeafIcon(null);

    // Set the size of the leaf so that it always has room to display values.
    // 40 characters should be enough.
    FontMetrics fm = getFontMetrics(defaultFont);
    renderer.setSize(fm.charWidth('X')*40, fm.getHeight());
    renderer.setPreferredSize(new Dimension(fm.charWidth('X')*40,
        fm.getHeight()));

    // Finally create the tree with lines between each node and the normal
    // colours
    final JTree tree = new JTree(top);
    tree.putClientProperty("JTree.lineStyle", "Angled");
    tree.setBackground(defaultBgColour);
    tree.setForeground(defaultFgColour);
    tree.setCellRenderer(renderer);
    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setToggleClickCount(1);

    //Create the scroll pane and add the tree to it.
    JScrollPane treeView = new JScrollPane(tree);
    treeView.setBackground(defaultBgColour);
    treeView.setForeground(defaultFgColour);
    treeView.setBorder(new EmptyBorder(3,3,3,3));

    // Create the ok panel and the ok button.
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
   * A mouse adapter that will listen is added to the ok button.
   */
  class OkPCB extends MouseAdapter
  {
    /**
     * When a mouse is click hide this frame.
     *
     * @param e the event that occurred.
     */
    public void mouseClicked(MouseEvent e)
    {
      ProcessControlBlockFrame.this.setVisible(false);
    }
  }
}
