package net.sourceforge.rcosjava.software.ipc;

import java.util.Hashtable;
import java.util.Vector;
import java.lang.String;
import net.sourceforge.rcosjava.software.memory.*;
import net.sourceforge.rcosjava.software.util.FIFOQueue;

/**
 * Implements a basic counting semaphore.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 10/08/98  Changed to FIFO Queue for queued processes and changed to Vector
 * for connected processes. AN
 * </DD><DD>
 * 11/08/98  Removed iBlocked and iNumConnected. AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman
 * @author Bruce Jamieson
 * @version 1.00 $Date$
 * @created 30th March 1996
 */
public class Semaphore
{
  /**
   * Name given by application
   */
  private String sempahoreName;

  /**
   * Arbitary ID generated by system.
   */
  private int id;

  /**
   * ID of process that created the seamphore.
   */
  private int ownerPID;

  /**
   * Current value in the semaphore.
   */
  private int value = -1;

  /**
   * List of processes using the semaphore
   */
  private Vector connectedProcesses = new Vector();

  /**
   * List of process blocked/waiting.
   */
  private FIFOQueue theQueue = new FIFOQueue();

  /**
   * Creates a new semaphore with a new name, id, process owner and value.  Will
   * automatically open the semaphore with the owner of the semaphore.
   *
   * @param newSemaphoreName the name of the semaphore.
   * @param newId the system generated process id.
   * @param ownerPID the process id that is creating this semaphore.
   * @param newValue a non-negative value indicating the value to set the
   * semaphore.
   */
  public Semaphore(String newSemaphoreName, int newId, int ownerPID,
    int newValue)
  {
    this.open(ownerPID);
    sempahoreName = newSemaphoreName;
    id = newId;
    value = newValue;
  }

  /**
   * @return the numeric identifier of the semaphore.
   */
  public int getId()
  {
    return id;
  }

  /**
   * @return the string identifier of the semaphore.
   */
  public String getName()
  {
    return sempahoreName;
  }

  /**
   * @return the value of the semaphore.
   */
  public int getValue()
  {
    return value;
  }

  /**
   * Connect a give process to this semaphore.
   *
   * @param pid the process id to connect the semaphore to.
   */
  public void open(int pid)
  {
    Integer newPID = new Integer(pid);
    connectedProcesses.addElement(newPID);
  }

  /**
   * Called when a process is waiting on semaphore.  This will decrement
   * its value to indicate one less resource available.  If it reaches zero
   * it will add the process waiting and return -1.
   *
   * @param pid process id of the process waiting.
   * @return the value of the semaphore if it's -1 means that the semaphore is
   * not available.
   */
  public int wait(int pid)
  {
    int tmpValue;
    // Dec the value of the semaphore OR add to the blocked Q
    if (value == 0)
    {
      //Add to the blocked Q and signal as such
      Integer newPID = new Integer(pid);
      theQueue.insert(newPID);
      tmpValue = -1;
    }
    else
    {
      value--;
      tmpValue = value;
    }
    return tmpValue;
  }

  /**
   * Called when a process is signalling that it has finished and the semaphore
   * is free.  Will increment its value to indicate its availability.
   *
   * @return will return the process id that was blocked or it will return -1
   * if not process was blocked.
   */
  public int signal()
  {
    int tmpValue;
    // Raise the value of the semaphore OR return the
    // 1st id in the blocked Q..
    if (theQueue.size() == 0)
    {
      value++;
      // No one blocked so return -1
      tmpValue = -1;
    }
    else
    {
      // Get the first element and remove it
      Integer oldPID = (Integer) theQueue.retrieve();
      tmpValue = oldPID.intValue();
    }
    return tmpValue;
  }

  /**
   * This says that a process has finished using the semaphore.  There may-be
   * more processes using it so it returns the number of processes using it.
   * If it returns zero then some clean-up is expected.
   *
   * @param pid process id that has stopped using this semaphore.
   * @return the number of processes still using the semaphore.
   */
  public int close(int pid)
  {
    Integer oldPID = new Integer(pid);
    connectedProcesses.removeElement(oldPID);
    // IF there are no connections open to the sem, then the
    // sem should die..
    // Do this by returning the number connected
    // (Let someone else deal with the problem..)
    return (connectedProcesses.size());
  }
}
