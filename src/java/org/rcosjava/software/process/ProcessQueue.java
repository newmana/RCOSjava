package org.rcosjava.software.process;

import java.util.*;
import org.rcosjava.software.util.BaseQueue;

/**
 * Implement a queue of processes used by the process scheduler. It stores the
 * processes in a queue object which can be a subtype of Queue. This includes
 * FIFO, LIFO, and Priority.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 01/09/1997 Tidy up and modified. AN </DD> 14/04/2001 Modified to have
 * different queue types instead of FIFO hard coded. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public class ProcessQueue
{
  /**
   * Description of the Field
   */
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
   *      contains all the existing values.
   */
  public void setProcessQueue(BaseQueue newProcessQueue)
  {
    processes = newProcessQueue;
  }

  /**
   * @return the iterator containing all the processes stored in the current
   *      queue.
   */
  public Iterator getProcessQueue()
  {
    return processes.iterator();
  }

  /**
   * Gets the Process attribute of the ProcessQueue object
   *
   * @param pid Description of Parameter
   * @return The Process value
   * @throws ProcessNotFoundException if there is not process given by the PID.
   */
  public RCOSProcess getProcess(int pid) throws ProcessNotFoundException
  {
    return findProcess(pid);
  }

  /**
   * Insert a process using Queue's insert method.  Does not insert it if its
   * already in the queue.
   *
   * @param newProcess the process to add to the queue.
   */
  public void insertProcess(RCOSProcess newProcess)
  {
    if (!processes.contains(newProcess))
    {
      processes.insert(newProcess);
    }
  }

  /**
   * @return a process using Queue's peek method. Uses the current index and
   *      does not remove it.
   */
  public RCOSProcess peekProcess()
  {
    return (RCOSProcess) processes.peek();
  }

  /**
   * @return a process using Queue's retrieve method. Takes it from the head of
   *      the queue and does remove the process from the Queue.
   */
  public RCOSProcess retrieveProcess()
  {
    return (RCOSProcess) processes.retrieve();
  }

  /**
   * @return a process using Queue's retrieveCurrent method. Takes it from the
   *      current position of the index. Removes it from the Queue.
   */
  public RCOSProcess retrieveCurrentProcess()
  {
    return (RCOSProcess) processes.retrieveCurrent();
  }

  /**
   * Removes all processes from the queue object. Calls removeAllElements.
   */
  public void removeAllProcesses()
  {
    processes.clear();
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
   * Locate a process with a certain process id and remove it from the queue.
   *
   * @param pid the process identifier to find in the queue.
   * @return the process with the given PID or null if not found.
   * @throws ProcessNotFoundException
   */
  public RCOSProcess removeProcess(int pid) throws ProcessNotFoundException
  {
    RCOSProcess tmpProcess = findProcess(pid);
    processes.remove(tmpProcess);
    return tmpProcess;
  }

  /**
   * Locate a process without removing it from the current lot of processes.
   *
   * @param pid the process identifier to find in the queue.
   * @return Description of the Returned Value
   */
  private RCOSProcess findProcess(int pid) throws ProcessNotFoundException
  {
    RCOSProcess tmpProcess = null;

    // loop through contents of queue until
    // - we find a match, which should be removed and the process returned
    // - or we reach the end of the Q
    // - or the PID of the Processs are greater than the PID we
    //   are looking for
    for (int index = 0; index < processes.size(); index++)
    {
      tmpProcess = (RCOSProcess) processes.get(index);
      if (tmpProcess.getPID() == pid)
      {
        break;
      }
      processes.goToNext();
    }

    if (tmpProcess == null)
    {
      throw new ProcessNotFoundException("Couldn't find process with id: " +
          pid);
    }

    return tmpProcess;
  }
}
