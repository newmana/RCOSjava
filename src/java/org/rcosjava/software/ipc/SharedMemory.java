package org.rcosjava.software.ipc;

import java.io.Serializable;
import java.util.*;

import org.rcosjava.hardware.memory.Memory;

/**
 * A basic shared memory class allowing the IPC manager to handle processes
 * reading and writing the same block of memory.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 11/08/98 Improved implementation. AN </DD>
 * <DD> 13/08/98 Write return old value on success. AN </DD> </DT>
 * <P>
 * @author Bruce Jamieson.
 * @author Andrew Newman.
 * @created 30th March 1996
 * @version 1.00 $Date$
 */
public class SharedMemory implements Serializable
{
  /**
   * A list of all the processes with open connections to this memory block.
   */
  private List processConnections = new ArrayList();

  /**
   * The numerical identifier of this block.
   */
  private int sharedMemoryId;

  /**
   * The shared memory block size.
   */
  private int size;

  /**
   * The string identifier of this block.
   */
  private String name;

  /**
   * The process that first created the shared memory segment.
   */
  private int originalProcessId;

  /**
   * Create a new shared memory object.
   *
   * @param newName the unique name of the shared memory block.
   * @param newSharedMemoryId the unique numerical identifier.
   * @param newOriginalProcessId the process that created this block.
   * @param newSize the size of the shared memory block.
   */
  public SharedMemory(String newName, int newSharedMemoryId,
      int newOriginalProcessId, int newSize)
  {
    name = newName;
    sharedMemoryId = newSharedMemoryId;
    originalProcessId = newOriginalProcessId;

    // Connect the creating process to this memory block
    open(newOriginalProcessId);
    size = newSize;

    // Assume Higher level will detect duplicate Shrm Segs
    // (aID) and return error - not to be done here?
  }

  /**
   * Returns the id of the shared memory.
   *
   * @return the id of the shared memory.
   */
  public int getSharedMemoryId()
  {
    return sharedMemoryId;
  }

  /**
   * Returns the name of the shared memory.
   *
   * @return the name of the shared memory.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Open a connection from a process to the shared memory.
   *
   * @param newPID the new process id to connect.
   */
  public void open(int newPID)
  {
    Integer pid = new Integer(newPID);
    processConnections.add(pid);
  }

  /**
   * Close a connection to a process to the shared memory.
   *
   * @param existingPID the process to disconnect from.
   * @return Description of the Returned Value
   */
  public int close(int existingPID)
  {
    Integer pid = new Integer(existingPID);
    processConnections.remove(pid);

    // IF there are no connections open to the sem, then the
    // sem should die..
    // Do this by returning the number connected
    // (Let someone else deal with the problem..)
    return processConnections.size();
  }

  /**
   * Returns the number of bytes that the memory segment is.
   *
   * @return the number of bytes that the memory segment is.
   */
  public int size()
  {
    return size;
  }

  /**
   * Returns the creator of the shared memory segments process id.
   *
   * @return the creator of the shared memory segments process id.
   */
  public int getProcessId()
  {
    return originalProcessId;
  }
}
