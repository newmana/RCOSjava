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

    node = new DefaultMutableTreeNode("ID: 1");
    details.add(node);
    node = new DefaultMutableTreeNode("Priority: 50");
    details.add(node);
    node = new DefaultMutableTreeNode("State: Running");
    details.add(node);

    details = new DefaultMutableTreeNode("I/O");
    top.add(details);

    node = new DefaultMutableTreeNode("Filename: numbers.pcd");
    details.add(node);
    node = new DefaultMutableTreeNode("File size: 123");
    details.add(node);
    node = new DefaultMutableTreeNode("Terminal ID: 1");
    details.add(node);

    details = new DefaultMutableTreeNode("Memory");
    top.add(details);

    node = new DefaultMutableTreeNode("Code Pages: 1");
    details.add(node);
    node = new DefaultMutableTreeNode("Stack Pages: 1");
    details.add(node);

    details = new DefaultMutableTreeNode("Accounting");
    top.add(details);

    node = new DefaultMutableTreeNode("CPU Ticks: 333");
    details.add(node);

    details = new DefaultMutableTreeNode("Context");
    top.add(details);

    node = new DefaultMutableTreeNode("Program Counter: 2");
    details.add(node);
    node = new DefaultMutableTreeNode("Stack Pointer: 13");
    details.add(node);
    node = new DefaultMutableTreeNode("Base Pointer: 1");
    details.add(node);

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
