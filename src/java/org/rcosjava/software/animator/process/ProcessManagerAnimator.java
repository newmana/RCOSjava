package org.rcosjava.software.animator.process;

import java.applet.*;
import java.awt.*;
import javax.swing.*;
import java.net.*;
import java.util.*;

import org.rcosjava.RCOS;
import org.rcosjava.messaging.messages.universal.Kill;
import org.rcosjava.messaging.messages.universal.RequestProcessPriority;
import org.rcosjava.messaging.messages.universal.Run;
import org.rcosjava.messaging.messages.universal.SetProcessPriority;
import org.rcosjava.messaging.messages.universal.Stop;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.util.LIFOQueue;

/**
 * Interface which sends messages to the Process Manager to execute a command
 * given by the user. Receives messages from the Process Manager about which
 * processes are currently being run.
 * <P>
 * @author Andrew Newman.
 * @created 10th of January 1997
 * @version 1.00 $Date$
 */
public class ProcessManagerAnimator extends AnimatorMessageHandler
{
  /**
   * The id to register with the animator office.
   */
  private final static String MESSENGING_ID = "ProcessManagerAnimator";

  /**
   * Queue containing the current running processes.
   */
  private LIFOQueue currentProcesses;

  /**
   * Current id of the process running.
   */
  private int currentProcessId;

  /**
   * Reference to the main applet.
   */
  private RCOS rcos;

  /**
   * Menu items to add and delete process ids from.
   */
  private JMenuItem processMenuItem, killMenuItem, changeMenuItem;

  /**
   * Parent menu item that contains the new, kill and change options.
   */
  private JMenu menu;

  /**
   * Process manager frame which displays the priority of a selected process.
   */
  private ProcessManagerFrame myFrame;

  /**
   * Create an animator office, register with the animator office, set the size
   * of the frame and the images to use to represent the processes and the
   * buttons.
   *
   * @param postOffice the post office to register to.
   * @param images the images to use for process and buttons.
   */
  public ProcessManagerAnimator(AnimatorOffice postOffice, RCOS newRCOS)
  {
    super(MESSENGING_ID, postOffice);
    rcos = newRCOS;
    currentProcesses = new LIFOQueue(5, 1);
    myFrame = new ProcessManagerFrame(100, 100, this);
  }

  /**
   * Setup the layout of the frame (menus, etc).
   *
   * @param c the parent component.
   */
  public void setupLayout(Component c)
  {
    myFrame.setupLayout(c);
  }

  /**
   * Adds the menu items to the animator.  This is from RCOS main process and
   * used to display to the user.  This should only need to be done once, after
   * this object has been created.
   *
   * @param newMenu the parent menu bar.
   * @param newProcessMenuItem the menu item for loadi1ng new processes.
   * @param newKillMenuItem the menu item to display all processes currently
   *   running so they maybe killed.
   * @param newChangeMenuItem the menu item to display all processes currently
   *   running so their priority maybe changed.
   */
  public void addMenuItems(JMenu newMenu, JMenuItem newProcessMenuItem,
      JMenuItem newKillMenuItem, JMenuItem newChangeMenuItem)
  {
    menu = newMenu;
    processMenuItem = newProcessMenuItem;
    killMenuItem = newKillMenuItem;
    changeMenuItem = newChangeMenuItem;
  }

  /**
   * Add new process to list.
   *
   * @param process the process id of the process to remove
   */
  public void newProcess(int process)
  {
    currentProcesses.insert(new Integer(process));
    updateProcessList();
  }

  /**
   * Delete process from list.
   *
   * @param process the process id of the process to add
   */
  public void deleteProcess(int process)
  {
    currentProcesses.goToHead();
    while (!currentProcesses.atTail())
    {
      if (((Integer) currentProcesses.peek()).intValue() == process)
      {
        int tmp = ((Integer) currentProcesses.retrieveCurrent()).intValue();
      }
      currentProcesses.goToNext();
    }
    updateProcessList();
  }

  /**
   * Removes all the processes in the Process Manager's Frame and goes through
   * the entire list of process and add thems back.
   */
  public void updateProcessList()
  {
    menu.remove(killMenuItem);
    menu.remove(changeMenuItem);

    // If there are processes still executing setup the menus to have sub menus
    // and add the existing ones.
    if (!currentProcesses.queueEmpty())
    {
      killMenuItem = new JMenu(killMenuItem.getText());
      changeMenuItem = new JMenu(changeMenuItem.getText());

      JMenuItem killTmpItem;
      JMenuItem priorityTmpItem;

      currentProcesses.goToHead();
      while (!currentProcesses.atTail())
      {
        Integer tmp = (Integer) currentProcesses.peek();
        killTmpItem = new JMenuItem(tmp.toString());
        priorityTmpItem = new JMenuItem(tmp.toString());

        killTmpItem.addActionListener(rcos.new KillProcessListener(this));
        priorityTmpItem.addActionListener(rcos.new ChangePriorityListener(this));

        killMenuItem.add(killTmpItem);
        changeMenuItem.add(priorityTmpItem);

        currentProcesses.goToNext();
      }
    }
    // Create empty menu items if there are no processes executing.
    else
    {
      killMenuItem = new JMenuItem(killMenuItem.getText());
      changeMenuItem = new JMenuItem(changeMenuItem.getText());
    }

    menu.add(killMenuItem);
    menu.add(changeMenuItem);
  }

  /**
   * Send a new kill message to the kernel. Called by the Process Manager Frame
   * to halt the execution of the process if it is running or remove it from one
   * of the queues.
   *
   * @param processId Description of Parameter
   */
  public void sendKillMessage(int processId)
  {
    Kill newMsg = new Kill(this, processId);
    sendMessage(newMsg);
  }

  /**
   * Request the priority values of the process so that they can be displayed
   * and modified by the user.
   *
   * @param processId used to find the process in order to get the priority.
   */
  public void sendRequestProcessPriority(int processId)
  {
    currentProcessId = processId;
    RequestProcessPriority newMsg = new RequestProcessPriority(this, processId);
    sendMessage(newMsg);
  }

  /**
   * Display a given process Id and it's process priority so that the user may
   * change it. Calls promptProcessPriority in the frame.
   *
   * @param processId process id to display.
   * @param processPriority the priority of the process.
   */
  public void returnProcessPriority(int processId, int processPriority)
  {
    myFrame.promptProcessPriority(processId, processPriority);
  }

  /**
   * Description of the Method
   *
   * @param processPriority Description of Parameter
   */
  public void sendSetProcessPriority(int processPriority)
  {
    SetProcessPriority tmpMessage = new SetProcessPriority(this,
        currentProcessId, processPriority);
    sendMessage(tmpMessage);
  }
}
