package org.rcosjava.software.animator.process;
import java.applet.*;

import java.awt.*;
import javax.swing.ImageIcon;
import java.net.*;
import java.util.*;
import org.rcosjava.messaging.messages.universal.Kill;
import org.rcosjava.messaging.messages.universal.RequestProcessPriority;
import org.rcosjava.messaging.messages.universal.Run;
import org.rcosjava.messaging.messages.universal.SetProcessPriority;
import org.rcosjava.messaging.messages.universal.Stop;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.RCOSAnimator;
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
public class ProcessManagerAnimator extends RCOSAnimator
{
  /**
   * The id to register with the animator office.
   */
  private final static String MESSENGING_ID = "ProcessManagerAnimator";

  /**
   * Description of the Field
   */
  private LIFOQueue currentProcesses;

  /**
   * Description of the Field
   */
  private ProcessManagerFrame myFrame;

  /**
   * Description of the Field
   */
  private int currentProcessId;

  /**
   * Create an animator office, register with the animator office, set the size
   * of the frame and the images to use to represent the processes and the
   * buttons.
   *
   * @param postOffice the post office to register to.
   * @param x width of frame
   * @param y height of frame
   * @param images the images to use for process and buttons.
   */
  public ProcessManagerAnimator(AnimatorOffice postOffice, int x, int y,
      ImageIcon[] images)
  {
    super(MESSENGING_ID, postOffice);
    currentProcesses = new LIFOQueue(5, 1);
    myFrame = new ProcessManagerFrame(x, y, images, this);
    myFrame.pack();
    myFrame.setSize(x, y);
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
   * Remove the frame (called when closing the applet).
   */
  public void disposeFrame()
  {
    myFrame.dispose();
  }

  /**
   * Display the frame (setVisible to true)
   */
  public void showFrame()
  {
    myFrame.setVisible(true);
  }

  /**
   * Hide the frame (setVisible to false)
   */
  public void hideFrame()
  {
    myFrame.setVisible(false);
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
  public void deleteProcess(Integer process)
  {
    currentProcesses.goToHead();
    while (!currentProcesses.atTail())
    {
      if (((Integer) currentProcesses.peek()).intValue() ==
          (process).intValue())
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
    ((ProcessManagerFrame) myFrame).clearProcesses();
    if (!currentProcesses.queueEmpty())
    {
      currentProcesses.goToHead();
      while (!currentProcesses.atTail())
      {
        Integer tmp = (Integer) currentProcesses.peek();

        try
        {
          ((ProcessManagerFrame) myFrame).addProcess(
              String.valueOf(tmp.intValue()));
        }
        catch (Exception e)
        {
          System.err.println(this + "- exception: " + e);
        }
        currentProcesses.goToNext();
      }
    }
  }

  /**
   * When the animator requests a priority of a certain process it returns it
   * here to be displayed to the user to change.
   *
   * @param priority Description of Parameter
   */
  public void returnProcessPriority(int priority)
  {
  }

  /**
   * Send a run message to the kernel. Called by the Process Manager Frame to
   * start the kernel running again.
   */
  public void sendRunMessage()
  {
    Run newMsg = new Run(this);

    sendMessage(newMsg);
  }

  /**
   * Send a step message to the kernel. Called by the Process Manager Frame to
   * step the execution of a process by one command.
   */
  public void sendStopMessage()
  {
    Stop newMsg = new Stop(this);

    sendMessage(newMsg);
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
   * @param processId Description of Parameter
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
   * @param processId Description of Parameter
   * @param processPriority Description of Parameter
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
