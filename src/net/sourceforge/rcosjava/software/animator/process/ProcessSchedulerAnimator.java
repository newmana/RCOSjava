package net.sourceforge.rcosjava.software.animator.process;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.RCOSBox;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.animator.ShowCPU;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.Quantum;
import net.sourceforge.rcosjava.messaging.messages.universal.Run;
import net.sourceforge.rcosjava.messaging.messages.universal.Stop;
import net.sourceforge.rcosjava.messaging.messages.universal.SwitchToFIFO;
import net.sourceforge.rcosjava.messaging.messages.universal.SwitchToLIFO;
import net.sourceforge.rcosjava.messaging.messages.universal.SwitchToPriority;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;

/**
 * Receives messages from Process Scheduler and manipulates processScheduler
 * frame based on messages received.  This tracks the location of the processes
 * in the Queue.  The frame is merely a representation of the queues which means
 * it should be easier to modify.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 10th of January 1997
 */
public class ProcessSchedulerAnimator extends RCOSAnimator
{
  /**
   * The frame in which to display all the details to.
   */
  private static ProcessSchedulerFrame myFrame;

  /**
   * The string (e.g. "PID 1") of the current process running.
   */
  private static String currentProcess = new String();

  /**
   * Uniquely identifies the process scheduler to the post office.
   */
  private static final String MESSENGING_ID = "ProcessSchedulerAnimator";

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
  public ProcessSchedulerAnimator (AnimatorOffice postOffice,
    int x, int y, Image[] images)
  {
    super(MESSENGING_ID, postOffice);
    myFrame = new ProcessSchedulerFrame(x, y, images, this);
    myFrame.pack();
    myFrame.setSize(x,y);
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
   * Called by the frame to set the quantum in the kernel.  Sends a new
   * quantum message.
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
   * Each of the three queues have a numeric value.  This simply calls the
   * addQueue on the Frame.  Exposed for Animator Messages.
   *
   * @param queueType the numeric representation of the queue.
   * @param pid the process id to add to the queue.
   */
  private void addQueue(int queueType, int pid)
  {
    myFrame.addQueue(queueType, pid);
  }

  /**
   * Each of the three queues have a numeric value.  This simply calls the
   * removeQueue on the Frame.  Exposed for Animator Messages.
   *
   * @param queueType the numeric representation of the queue.
   * @param pid the process id to add to the queue.
   */
  private void removeQueue(int queueType, int pid)
  {
    myFrame.removeQueue(queueType, pid);
  }

  /**
   * When a process has finished executed it needs to be removed from display.
   * This merely exposes this method to the messages.
   *
   * @param pid the process id to remove from the CPU.
   */
  public void processFinished(int pid)
  {
    myFrame.processFinished(pid);
  }

  /**
   * When a process is killed (abruptly halted executed) it must be removed from
   * any of the queues or CPU.  This calls the Frame method of the same name.
   *
   * @param pid the process id to find and remove.
   */
  public void killProcess(int pid)
  {
    myFrame.killProcess(pid);
  }

  /**
   * Exposes the frame method of the same name.  Moves the process from the CPU
   * to the Blocked Queue.
   *
   * @param pid the process id to move.
   */
  public void cpuToBlocked(int pid)
  {
    sendMessage(new Stop(this));
    myFrame.cpuToBlocked(pid);
    addQueue(ProcessScheduler.BLOCKEDQ, pid);
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name.  Moves the process from the
   * Blocked Queue to the Ready Queue.
   *
   * @param pid the process id to move.
   */
  public void blockedToReady(int pid)
  {
    sendMessage(new Stop(this));
    removeQueue(ProcessScheduler.BLOCKEDQ, pid);
    myFrame.blockedToReady(pid);
    addQueue(ProcessScheduler.READYQ, pid);
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name.  Moves the process from the
   * CPU to the Ready Queue.
   *
   * @param pid the process id to move.
   */
  public void cpuToReady(int pid)
  {
    sendMessage(new Stop(this));
    myFrame.cpuToReady(pid);
    addQueue(ProcessScheduler.READYQ, pid);
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name.  Moves the process from the
   * Ready Queue to the CPU.
   *
   * @param pid the process id to move.
   */
  public void readyToCPU(int pid)
  {
    sendMessage(new Stop(this));
    removeQueue(ProcessScheduler.READYQ, pid);
    myFrame.readyToCPU(pid);
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name.  Moves the process from the
   * Zombie Queue to the Ready Queue.
   *
   * @param pid the process id to move.
   */
  public void zombieToReady(int pid)
  {
    sendMessage(new Stop(this));
    removeQueue(ProcessScheduler.ZOMBIEQ, pid);
    myFrame.zombieToReady(pid);
    addQueue(ProcessScheduler.READYQ, pid);
    sendMessage(new Run(this));
  }

  /**
   * Exposes the frame method of the same name.  Adds a process to the Zombie
   * Queue.
   *
   * @param pid the process id to move.
   */
  public void zombieCreated(int pid)
  {
    sendMessage(new Stop(this));
    myFrame.newProcess(pid);
    addQueue(ProcessScheduler.ZOMBIEQ, pid);
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
}
