package org.rcosjava.software.animator.process;

import java.applet.*;
import java.awt.*;
import java.net.*;
import javax.swing.*;
import java.util.*;
import org.rcosjava.software.animator.RCOSPanel;
import org.rcosjava.messaging.messages.animator.ShowCPU;
import org.rcosjava.messaging.messages.universal.Quantum;
import org.rcosjava.messaging.messages.universal.Run;
import org.rcosjava.messaging.messages.universal.Stop;
import org.rcosjava.messaging.messages.universal.SwitchToFIFO;
import org.rcosjava.messaging.messages.universal.SwitchToLIFO;
import org.rcosjava.messaging.messages.universal.SwitchToPriority;
import org.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import org.rcosjava.software.animator.RCOSAnimator;
import org.rcosjava.software.process.ProcessScheduler;
import org.rcosjava.software.process.RCOSProcess;

/**
 * Receives messages from Process Scheduler and manipulates processScheduler
 * frame based on messages received. This tracks the location of the processes
 * in the Queue. The frame is merely a representation of the queues which means
 * it should be easier to modify.
 * <P>
 * @author Andrew Newman.
 * @created 10th of January 1997
 * @version 1.00 $Date$
 */
public class ProcessSchedulerAnimator extends RCOSAnimator
{
  /**
   * Uniquely identifies the process scheduler to the post office.
   */
  private final static String MESSENGING_ID = "ProcessSchedulerAnimator";

  /**
   * The panel in which to display all the details to.
   */
  private static ProcessSchedulerPanel panel;

  /**
   * Process Control Block frame to display.
   */
  private ProcessControlBlockFrame pcbFrame;

  /**
   * The current processes running.
   */
  private HashMap currentProcesses;

  /**
   * The seletected by the user to display.
   */
  private int pcbSelectedProcess;

  /**
   * Create an animator office, register with the animator office, set the size
   * of the frame and the images to use to represent the processes and the
   * buttons.
   *
   * @param postOffice the post office to register to.
   * @param images the images to use for process and buttons.
   */
  public ProcessSchedulerAnimator(AnimatorOffice postOffice, ImageIcon[] images)
  {
    super(MESSENGING_ID, postOffice);
    panel = new ProcessSchedulerPanel(images, this);
    panel.repaint();

    // Setup Process Control Block Frame
    pcbFrame = new ProcessControlBlockFrame(350, 250, this);
    pcbFrame.setupLayout(panel);
    currentProcesses = new HashMap();
  }

  /**
   * Setup the layout of the frame (menus, etc).
   *
   * @param c the parent component.
   */
  public void setupLayout(Component c)
  {
    panel.setupLayout(c);
  }

  /**
   * Returns the panel of this component.
   *
   * @return the panel of this component.
   */
  public RCOSPanel getPanel()
  {
    return panel;
  }

  /**
   * Called by the frame to set the quantum in the kernel. Sends a new quantum
   * message.
   *
   * @param quantumValue the number of CPU ticks to set the quantum to.
   */
  public void sendQuantum(Integer quantumValue)
  {
    sendMessage(new Stop(this));

    //Create and send Quantum message
    Quantum msg = new Quantum(this, quantumValue.intValue());

    sendMessage(msg);
    sendMessage(new Run(this));
  }

  /**
   * Called by the frame to change the priority queue to FIFO.
   */
  public void sendSwitchFIFO()
  {
    sendMessage(new Stop(this));

    SwitchToFIFO msg = new SwitchToFIFO(this);

    sendMessage(msg);
    sendMessage(new Run(this));
  }

  /**
   * Called by the frame to change the priority queue to LIFO.
   */
  public void sendSwitchLIFO()
  {
    sendMessage(new Stop(this));

    SwitchToLIFO msg = new SwitchToLIFO(this);

    sendMessage(msg);
    sendMessage(new Run(this));
  }

  /**
   * Called by the frame to change the priority queue to Priority.
   */
  public void sendSwitchPriority()
  {
    sendMessage(new Stop(this));

    SwitchToPriority msg = new SwitchToPriority(this);

    sendMessage(msg);
    sendMessage(new Run(this));
  }

  /**
   * When a process has finished executed it needs to be removed from display.
   * This merely exposes this method to the messages.
   *
   * @param process the process to remove from the CPU.
   */
  public void processFinished(RCOSProcess process)
  {
    panel.processFinished(process.getPID());

    // Remove the process from the list of current processes
    currentProcesses.remove(new Integer(process.getPID()));

    if (process.getPID() == pcbSelectedProcess)
    {
      pcbFrame.setVisible(false);
    }
  }

  /**
   * When a process is killed (abruptly halted executed) it must be removed from
   * any of the queues or CPU. This calls the Frame method of the same name.
   *
   * @param process the process to find and remove.
   */
  public void killProcess(RCOSProcess process)
  {
    panel.killProcess(process.getPID());

    // Remove the process from the list of current processes
    currentProcesses.remove(new Integer(process.getPID()));

    if (process.getPID() == pcbSelectedProcess)
    {
      pcbFrame.setVisible(false);
    }
  }

  /**
   * Exposes the frame method of the same name. Moves the process from the CPU
   * to the Blocked Queue.
   *
   * @param process the process to move.
   */
  public void cpuToBlocked(RCOSProcess process)
  {
    // Stop the execution of the OS
    sendMessage(new Stop(this));

    // Update the values of the current processes and animate
    currentProcesses.put(new Integer(process.getPID()), process);
    pcbFrame.updateDisplay(process);
    panel.cpuToBlocked(process.getPID());
    addQueue(ProcessScheduler.BLOCKEDQ, process.getPID());

    // Start the execution of the OS
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name. Moves the process from the
   * Blocked Queue to the Ready Queue.
   *
   * @param process the process to move.
   */
  public void blockedToReady(RCOSProcess process)
  {
    // Stop the execution of the OS
    sendMessage(new Stop(this));

    // Update the values of the current processes and animate
    currentProcesses.put(new Integer(process.getPID()), process);
    pcbFrame.updateDisplay(process);
    removeQueue(ProcessScheduler.BLOCKEDQ, process.getPID());
    panel.blockedToReady(process.getPID());
    addQueue(ProcessScheduler.READYQ, process.getPID());

    // Start the execution of the OS
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name. Moves the process from the CPU
   * to the Ready Queue.
   *
   * @param process the process to move.
   */
  public void cpuToReady(RCOSProcess process)
  {
    // Stop the execution of the OS
    sendMessage(new Stop(this));

    // Update the values of the current processes and animate
    currentProcesses.put(new Integer(process.getPID()), process);
    pcbFrame.updateDisplay(process);
    panel.cpuToReady(process.getPID());
    addQueue(ProcessScheduler.READYQ, process.getPID());

    // Start the execution of the OS
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name. Moves the process from the Ready
   * Queue to the CPU.
   *
   * @param process the process to move.
   */
  public void readyToCPU(RCOSProcess process)
  {
    // Stop the execution of the OS
    sendMessage(new Stop(this));

    // Update the values of the current processes and animate
    currentProcesses.put(new Integer(process.getPID()), process);
    pcbFrame.updateDisplay(process);
    removeQueue(ProcessScheduler.READYQ, process.getPID());
    panel.readyToCPU(process.getPID());

    // Start the execution of the OS
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name. Moves the process from the
   * Zombie Queue to the Ready Queue.
   *
   * @param process the process to move.
   */
  public void zombieToReady(RCOSProcess process)
  {
    // Stop the execution of the OS
    sendMessage(new Stop(this));

    // Update the values of the current processes and animate
    currentProcesses.put(new Integer(process.getPID()), process);
    pcbFrame.updateDisplay(process);
    removeQueue(ProcessScheduler.ZOMBIEQ, process.getPID());
    panel.zombieToReady(process.getPID());
    addQueue(ProcessScheduler.READYQ, process.getPID());

    // Start the execution of the OS
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name. Adds a process to the Zombie
   * Queue.
   *
   * @param process the process to move.
   */
  public void zombieCreated(RCOSProcess process)
  {
    // Stop the execution of the OS
    sendMessage(new Stop(this));

    // Update the values of the current processes and animate
    currentProcesses.put(new Integer(process.getPID()), process);
    pcbFrame.updateDisplay(process);
    panel.newProcess(process.getPID());
    addQueue(ProcessScheduler.ZOMBIEQ, process.getPID());

    // Start the execution of the OS
    sendMessage(new Run(this));
  }

  /**
   * Sends a message to the CPU Animator when someone clicks on the CPU
   * representation in the ProcessSchedulerFrame.
   */
  public void showCPU()
  {
    ShowCPU scmMsg = new ShowCPU(this);
    localSendMessage(scmMsg);
  }

  /**
   * Each of the three queues have a numeric value. This simply calls the
   * addQueue on the Frame. Exposed for Animator Messages.
   *
   * @param queueType the numeric representation of the queue.
   * @param pid the process id to add to the queue.
   */
  private void addQueue(int queueType, int pid)
  {
    panel.addQueue(queueType, pid);
  }

  /**
   * Each of the three queues have a numeric value. This simply calls the
   * removeQueue on the Frame. Exposed for Animator Messages.
   *
   * @param queueType the numeric representation of the queue.
   * @param pid the process id to add to the queue.
   */
  private void removeQueue(int queueType, int pid)
  {
    panel.removeQueue(queueType, pid);
  }

  /**
   * Displays a frame with the given PCB of the process given.
   *
   * @param int processId the id of the process to display.
   */
  public void displayPCB(int processId)
  {
    pcbFrame.setVisible(true);

    // Get the details of the process selected
    Integer tmpProcessId = new Integer(processId);
    if (currentProcesses.containsKey(tmpProcessId))
    {
      pcbSelectedProcess = processId;
      pcbFrame.updateDisplay((RCOSProcess) currentProcesses.get(tmpProcessId));
    }
  }
}
