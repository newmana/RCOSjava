package net.sourceforge.rcosjava.software.process;

import net.sourceforge.rcosjava.software.util.*;
import java.util.Iterator;

/**
 * Implement a queue of processes used by the process scheduler.  It stores
 * the processes in a queue object which can be a subtype of Queue.  This
 * includes FIFO, LIFO, and Priority.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 01/09/1997 Tidy up and modified. AN
 * </DD>
 * 14/04/2001 Modified to have different queue types instead of FIFO hard coded.
 * </DD></DT>
 * <P>
 * @see net.sourceforge.rcosjava.software.util.FIFOQueue
 * @see net.sourceforge.rcosjava.software.util.LIFOQueue
 * @see net.sourceforge.rcosjava.software.util.PriorityQueue
 * @see net.sourceforge.rcosjava.software.util.Queue
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
public class ProcessQueue
{
  private BaseQueue processes;

  /**
   * Create a new process queue.
   *
   * @param newProcessQueue initialise the queues with given value.
   */
  public ProcessQueue(BaseQueue newProcessQueue)
  {
    processes = newProcessQueue;
  }

  /**
   * Sets the stored queues to the given value.
   *
   * @param newProcessQueue the process queue to set assumes that the queue
   * contains all the existing values.
   */
  public void setProcessQueue(BaseQueue newProcessQueue)
  {
    processes = newProcessQueue;
  }

  /**
   * @return the iterator containing all the processes stored in the current
   * queue.
   */
  public Iterator getProcessQueue()
  {
    return processes.iterator();
  }

  /**
   * Insert a process using Queue's insert method.
   *
   * @param newProcess the process to add to the queue.
   */
  public void insertProcess(RCOSProcess newProcess)
  {
    processes.insert(newProcess);
  }

  /**
   * @return a process using Queue's peek method.  Uses the current index and
   * does not remove it.
   */
  public RCOSProcess peekProcess()
  {
    return (RCOSProcess) processes.peek();
  }

  /**
   * @return a process using Queue's retrieve method.  Takes it from the head of
   * the queue and does remove the process from the Queue.
   */
  public RCOSProcess retrieveProcess()
  {
    return (RCOSProcess) processes.retrieve();
  }

  /**
   * @return a process using Queue's retrieveCurrent method.  Takes it from the
   * current position of the index.  Removes it from the Queue.
   */
  public RCOSProcess retrieveCurrentProcess()
  {
    return (RCOSProcess) processes.retrieveCurrent();
  }

  /**
   * Removes all processes from the queue object.  Calls removeAllElements.
   */
  public void removeAllProcesses()
  {
    processes.removeAllElements();
  }

  /**
   * @return if the queue is empty (no processes).
   */
  public boolean queueEmpty()
  {
    return processes.queueEmpty();
  }

  /**
   * @return the size of queue (number of processes).
   */
  public int size()
  {
    return processes.size();
  }

  /**
   * In Process Queue order is based on the time the Process is scheduled
   * to occur.
   * @return return -1, 0, 1 if first object is less than, equal to or greater than
   * second object
   */
  public int compare(RCOSProcess firstProcess, RCOSProcess secondProcess)
  {
    if (firstProcess.getPriority() == secondProcess.getPriority())
      return 0;
    else if (firstProcess.getPriority() < secondProcess.getPriority())
      return -1;
    else
      return 1;
  }

  /**
   * Locate a process with a certain process id.
   *
   * @param pid the process identifier to find in the queue.
   * @return the process with the given PID or null if not found.
   */
  public RCOSProcess getProcess(int pid)
  {
    RCOSProcess tmpProcess;

    if (processes.queueEmpty())
      return null;

    processes.goToHead();

    // loop through contents of queue until
    // - we find a match, which should be removed and the process returned
    // - or we reach the end of the Q
    // - or the PID of the Processs are greater than the PID we
    //   are looking for
    do
    {
      tmpProcess = (RCOSProcess) processes.peek();
      if (tmpProcess.getPID() == pid)
      {
        tmpProcess = (RCOSProcess) processes.retrieveCurrent();
        return tmpProcess;
      }
      processes.goToNext();
    } while (!processes.atTail());

    tmpProcess = (RCOSProcess) processes.peek();
    if (tmpProcess.getPID() == pid)
    {
      tmpProcess = (RCOSProcess) processes.retrieveCurrent();
      return tmpProcess;
    }
    return null;
  }
}