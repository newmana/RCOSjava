package org.rcosjava.software.animator.process;

import java.applet.*;
import java.awt.*;
import java.net.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import org.rcosjava.RCOS;
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
import org.rcosjava.software.util.Queue;
import org.rcosjava.software.util.LIFOQueue;
import org.rcosjava.software.util.FIFOQueue;
import org.rcosjava.software.util.PriorityQueue;

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
   * Holds which processes are the the queue.
   */
  private Queue zombieQueue, readyQueue, blockedQueue;

  /**
   * Process Control Block frame to display.
   */
  private ProcessControlBlockFrame pcbFrame;

  /**
   * The seletected by the user to display.
   */
  private int pcbSelectedProcess;

  /**
   * The current processes running.
   */
  private HashMap currentProcesses;

  /**
   * How long to delay between refreshes.
   */
  private long delay = 1;

  /**
   * The selected quantum.
   */
  private int quantum = 2;

  /**
   * The queue type.  1 is FIFO, 2 is LIFO, 3 is Priority.
   */
  private int queueType = 1;

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

    zombieQueue = new LIFOQueue(10, 0);
    blockedQueue = new LIFOQueue(10, 0);
    readyQueue = new LIFOQueue(10, 0);

    panel = new ProcessSchedulerPanel(images, this);
    panel.repaint();

    // Setup Process Control Block Frame
    pcbFrame = new ProcessControlBlockFrame(350, 250, this);
    pcbFrame.setupLayout(panel);
    currentProcesses = new HashMap();
  }

  /**
   * Get the sync delay.
   *
   * @return the number of milliseconds to deplay painting.
   */
  long getDelay()
  {
    return delay;
  }

  /**
   * Set the sync delay.
   *
   * @param newDelayValue new delay value.
   */
  void setDelay(int newDelayValue)
  {
    delay = newDelayValue;
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
   * Returns the Process Control Block frame.
   *
   * @return the Process Control Block frame.
   */
  public ProcessControlBlockFrame getPCBFrame()
  {
    return pcbFrame;
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

    // Set internal version of quantum.
    quantum = quantumValue.intValue();

    //Create and send Quantum message
    Quantum msg = new Quantum(this, quantumValue.intValue());
    sendMessage(msg);
    sendMessage(new Run(this));
  }

  /**
   * Called by the frame to change the priority queue to FIFO.
   */
  public void switchToFIFO()
  {
    // Set to queue type FIFO
    queueType = 1;

    // Stop processing
    sendMessage(new Stop(this));

    // Switch to FIFO
    zombieQueue = new FIFOQueue(10, 0, zombieQueue.iterator());
    blockedQueue = new LIFOQueue(10, 0, blockedQueue.iterator());
    readyQueue = new LIFOQueue(10, 0, readyQueue.iterator());

    // Send switch message
    SwitchToFIFO msg = new SwitchToFIFO(this);
    sendMessage(msg);

    // Start processing
    sendMessage(new Run(this));
  }

  /**
   * Called by the frame to change the priority queue to LIFO.
   */
  public void switchToLIFO()
  {
    // Set to queue type LIFO
    queueType = 2;

    // Stop processing
    sendMessage(new Stop(this));

    // Switch to LIFO
    zombieQueue = new LIFOQueue(10, 0, zombieQueue.iterator());
    blockedQueue = new LIFOQueue(10, 0, blockedQueue.iterator());
    readyQueue = new LIFOQueue(10, 0, readyQueue.iterator());

    // Send switch message
    SwitchToLIFO msg = new SwitchToLIFO(this);
    sendMessage(msg);

    // Start processing
    sendMessage(new Run(this));
  }

  /**
   * Called by the frame to change the priority queue to Priority.
   */
  public void switchToPriority()
  {
    // Set to queue type Priority
    queueType = 3;

    // Stop processing
    sendMessage(new Stop(this));

    // Switch to Priority
    zombieQueue = new PriorityQueue(10, 0, zombieQueue.iterator());
    blockedQueue = new PriorityQueue(10, 0, blockedQueue.iterator());
    readyQueue = new PriorityQueue(10, 0, readyQueue.iterator());

    // Send switch message
    SwitchToPriority msg = new SwitchToPriority(this);
    sendMessage(msg);

    // Start processing
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
    Integer PID = new Integer(process.getPID());
    removeQueue(ProcessScheduler.READYQ, PID);
    removeQueue(ProcessScheduler.BLOCKEDQ, PID);
    removeQueue(ProcessScheduler.ZOMBIEQ, PID);

    panel.killProcess(process.getPID());

    // Remove the process from the list of current processes
    currentProcesses.remove(PID);

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
    Integer PID = new Integer(process.getPID());
    currentProcesses.put(PID, process);
    panel.cpuToBlocked(process.getPID());
    addQueue(ProcessScheduler.BLOCKEDQ, PID);

    if (process.getPID() == pcbSelectedProcess)
    {
      pcbFrame.updateDisplay(process);
    }

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
    Integer PID = new Integer(process.getPID());
    currentProcesses.put(PID, process);
    removeQueue(ProcessScheduler.BLOCKEDQ, PID);
    panel.blockedToReady(PID.intValue());
    addQueue(ProcessScheduler.READYQ, PID);

    if (process.getPID() == pcbSelectedProcess)
    {
      pcbFrame.updateDisplay(process);
    }

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
    Integer PID = new Integer(process.getPID());
    currentProcesses.put(PID, process);
    panel.cpuToReady(PID.intValue());
    addQueue(ProcessScheduler.READYQ, PID);

    if (process.getPID() == pcbSelectedProcess)
    {
      pcbFrame.updateDisplay(process);
    }
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
    Integer PID = new Integer(process.getPID());
    currentProcesses.put(PID, process);
    removeQueue(ProcessScheduler.READYQ, PID);
    panel.readyToCPU(PID.intValue());

    if (process.getPID() == pcbSelectedProcess)
    {
      pcbFrame.updateDisplay(process);
    }

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
    Integer PID = new Integer(process.getPID());
    currentProcesses.put(PID, process);
    pcbFrame.updateDisplay(process);
    removeQueue(ProcessScheduler.ZOMBIEQ, PID);
    panel.zombieToReady(PID.intValue());
    addQueue(ProcessScheduler.READYQ, PID);

    if (process.getPID() == pcbSelectedProcess)
    {
      pcbFrame.updateDisplay(process);
    }

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
    Integer PID = new Integer(process.getPID());
    currentProcesses.put(PID, process);
    panel.newProcess(PID.intValue());
    addQueue(ProcessScheduler.ZOMBIEQ, PID);

    if (process.getPID() == pcbSelectedProcess)
    {
      pcbFrame.updateDisplay(process);
    }

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
   * @param process the process to add to the queue.
   */
  private void addQueue(int queueType, Integer PID)
  {
    switch (queueType)
    {
      case ProcessScheduler.READYQ:
        readyQueue.insert(PID);
        panel.moveReadyQueue(PID.intValue(), readyQueue.itemCount());
        panel.refreshQueue(ProcessScheduler.READYQ, readyQueue);
      break;
      case ProcessScheduler.BLOCKEDQ:
        blockedQueue.insert(PID);
        panel.moveBlockedQueue(PID.intValue(), blockedQueue.itemCount());
        panel.refreshQueue(ProcessScheduler.BLOCKEDQ, blockedQueue);
      break;
      case ProcessScheduler.ZOMBIEQ:
        zombieQueue.insert(PID);
        panel.moveZombieQueue(PID.intValue(), zombieQueue.itemCount());
        panel.refreshQueue(ProcessScheduler.ZOMBIEQ, zombieQueue);
      break;
    }
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

  /**
   * Each of the three queues have a numeric value. This simply calls the
   * removeQueue on the Frame. Exposed for Animator Messages.
   *
   * @param queueType the numeric representation of the queue.
   * @param PID the process remove from the queue.
   */
  private void removeQueue(int queueType, Integer PID)
  {
    switch (queueType)
    {
      case ProcessScheduler.READYQ:
        removeProcess(PID, readyQueue);
        panel.refreshQueue(queueType, readyQueue);
      break;
      case ProcessScheduler.BLOCKEDQ:
        removeProcess(PID, blockedQueue);
        panel.refreshQueue(queueType, blockedQueue);
      break;
      case ProcessScheduler.ZOMBIEQ:
        removeProcess(PID, zombieQueue);
        panel.refreshQueue(queueType, zombieQueue);
      break;
    }
  }

  /**
   * Remove a process from a given queue.
   *
   * @param PID the process to remove from the queue.
   * @param tmpQueue the queue to remove the pid from.
   */
  private synchronized void removeProcess(Integer PID, Queue tmpQueue)
  {
    tmpQueue.remove(PID);
  }

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
    // Process state
    os.writeObject(currentProcesses);
    os.writeObject(readyQueue);
    os.writeObject(blockedQueue);
    os.writeObject(zombieQueue);

    // Panel state
    os.writeLong(delay);
    os.writeInt(quantum);
    os.writeInt(queueType);

    // PCB state
    os.writeInt(pcbSelectedProcess);
    os.writeBoolean(pcbFrame.isVisible());
  }

  /**
   * Handle deserialization of the contents.  Ensures non-serializable
   * components correctly created.
   *
   * @param is stream that is being read.
   */
  private void readObject(ObjectInputStream is) throws IOException,
      ClassNotFoundException
  {
    register(MESSENGING_ID, RCOS.getAnimatorPostOffice());

    // Process state
    currentProcesses = (HashMap) is.readObject();
    readyQueue = (Queue) is.readObject();
    blockedQueue = (Queue) is.readObject();
    zombieQueue = (Queue) is.readObject();

    // Panel state
    delay = is.readLong();
    quantum = is.readInt();
    queueType = is.readInt();

    // Get the panel and PCB Frame.
    panel = (ProcessSchedulerPanel) RCOS.getProcessSchedulerAnimator().getPanel();
    pcbFrame = RCOS.getProcessSchedulerAnimator().getPCBFrame();
    panel.setManager(this);

    // Get the PCB State.
    pcbSelectedProcess = is.readInt();
    boolean pcbVisible = is.readBoolean();

    // Refresh queues
    RCOSProcess tmpProcess;

    for (int index = 0; index < readyQueue.size(); index++)
    {
      tmpProcess = (RCOSProcess) readyQueue.peek(index);
      panel.newProcess(tmpProcess.getPID());
      panel.moveZombieQueue(tmpProcess.getPID(), readyQueue.itemCount());
      panel.refreshQueue(ProcessScheduler.READYQ, readyQueue);
    }
    panel.refreshQueue(ProcessScheduler.READYQ, readyQueue);

    for (int index = 0; index < blockedQueue.size(); index++)
    {
      tmpProcess = (RCOSProcess) blockedQueue.peek(index);
      panel.newProcess(tmpProcess.getPID());
      panel.moveZombieQueue(tmpProcess.getPID(), blockedQueue.itemCount());
      panel.refreshQueue(ProcessScheduler.BLOCKEDQ, blockedQueue);
    }
    panel.refreshQueue(ProcessScheduler.BLOCKEDQ, blockedQueue);

    for (int index = 0; index < zombieQueue.itemCount(); index++)
    {
      tmpProcess = (RCOSProcess) zombieQueue.peek(index);
      panel.newProcess(tmpProcess.getPID());
      panel.moveZombieQueue(tmpProcess.getPID(), zombieQueue.itemCount());
      panel.refreshQueue(ProcessScheduler.ZOMBIEQ, zombieQueue);
    }
    panel.refreshQueue(ProcessScheduler.ZOMBIEQ, zombieQueue);

    // Set delay
    panel.setDelay(delay);

    // Set quantum
    panel.setQuantum(quantum);

    // Set queue type
    panel.setQueueType(queueType);
  }
}
