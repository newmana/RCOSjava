package net.sourceforge.rcosjava.software.animator.process;

import java.awt.*;
import java.applet.*;
import java.util.*;
import java.net.*;
import net.sourceforge.rcosjava.software.animator.RCOSAnimator;
import net.sourceforge.rcosjava.software.animator.RCOSFrame;
import net.sourceforge.rcosjava.software.animator.support.GraphicButton;
import net.sourceforge.rcosjava.software.animator.support.NewLabel;
import net.sourceforge.rcosjava.software.animator.support.RCOSList;
import net.sourceforge.rcosjava.messaging.messages.Message;
import net.sourceforge.rcosjava.messaging.messages.animator.AnimatorMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorOffice;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.Run;
import net.sourceforge.rcosjava.messaging.messages.universal.Step;
import net.sourceforge.rcosjava.messaging.messages.universal.Kill;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.util.LIFOQueue;

/**
 * Interface which sends messages to the Process Manager to execute a command
 * given by the user.  Receives messages from the Process Manager about which
 * processes are currently being run.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 10th of January 1997
 */
public class ProcessManagerAnimator extends RCOSAnimator
{
  private LIFOQueue currentProcesses;
  private ProcessManagerFrame myFrame;
  private static final String MESSENGING_ID = "ProcessManagerAnimator";

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
  public ProcessManagerAnimator (AnimatorOffice postOffice, int x, int y,
    Image[] images)
  {
    super(MESSENGING_ID, postOffice);
    currentProcesses = new LIFOQueue(5,1);
    myFrame = new ProcessManagerFrame(x, y, images, this);
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
   * Add new process to list.
   *
   * @param process the process id of the process to remove
   */
  public void newProcess(Integer process)
  {
    currentProcesses.insert(process);
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
      while(!currentProcesses.atTail())
      {
        Integer tmp = (Integer) currentProcesses.peek();
        try
        {
          ((ProcessManagerFrame) myFrame).addProcess(
            String.valueOf(tmp.intValue()));
        }
        catch (Exception e)
        {
          System.out.println(this + "- exception: " + e);
        }
        currentProcesses.goToNext();
      }
    }
  }

  /**
   * Send a run message to the kernel.  Called by the Process Manager Frame to
   * start the kernel running again.
   */
  public void run()
  {
    Run newMsg = new Run(this);
    sendMessage(newMsg);
  }

  /**
   * Send a step message to the kernel.  Called by the Process Manager Frame to
   * step the execution of a process by one command.
   */
  public void step()
  {
    Step newMsg = new Step(this);
    sendMessage(newMsg);
  }

  /**
   * Send a new kill message to the kernel.  Called by the Process Manager Frame
   * to halt the execution of the process if it is running or remove it from one
   * of the queues.
   */
  public void kill(int processId)
  {
    Kill newMsg = new Kill(this, processId);
    sendMessage(newMsg);
  }

  public void processMessage(AnimatorMessageAdapter message)
  {
  }

  public void processMessage(UniversalMessageAdapter message)
  {
    try
    {
      message.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error processing: "+e);
      e.printStackTrace();
    }
  }

}
