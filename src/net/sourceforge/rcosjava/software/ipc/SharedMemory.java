package net.sourceforge.rcosjava.software.ipc;

import java.util.*;
import net.sourceforge.rcosjava.hardware.memory.Memory;

import net.sourceforge.rcosjava.software.memory.*;

/**
 * A basic shared memory class allowing the IPC manager to handle processes
 * reading and writing the same block of memory.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 11/08/98  Improved implementation. AN
 * </DD><DD>
 * 13/08/98  Write return old value on success. AN
 * </DD></DT>
 * <P>
 * @author Bruce Jamieson.
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 30th March 1996
 */
public class SharedMemory
{
  /**
   * The size in bytes of the shared memory block.
   */
  private int memorySize = -1;

  /**
   * A list of all the processes with open connections to this memory block.
   */
  private List processConnections = new ArrayList();

  /**
   * The numerical identifier of this block.
   */
  private int sharedMemoryId;

  /**
   * The shared memory block.
   */
  private Memory sharedMemoryBlock;

  /**
   * The string identifier of this block.
   */
  private String name;

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

    // Connect the creating process to this memory block
    open(newOriginalProcessId);
    memorySize =  newSize;
    sharedMemoryBlock = new Memory(memorySize);

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
   * Read one byte of memory from the given offset.
   *
   * @param offset the number of bytes offset to start reading from.
   */
  public short read(int offset)
  {
    if (offset >= memorySize)
    {
      // A read past the end of the block - not on!
      return -1;
    }
    else
    {
      // Do a read
      short returnedByte = sharedMemoryBlock.read(offset);
      return returnedByte;
    }
  }

  /**
   * Write one byte of memory from the given offset.
   *
   * @param offset the number of bytes offset to write to.
   * @param newValue the value to write to the memory.
   */
  public short write(int offset, short newValue)
  {
    if (offset >= memorySize)
    {
      // A write past the end of the block - not on!
      return -1;
    }
    else
    {
      // Do a write
      sharedMemoryBlock.write(offset, newValue);
      System.out.println("SM wrote from: " + offset + " value: " + newValue);
      return 0;
    }
  }

  /**
   * Returns the number of bytes that the memory segment is.
   *
   * @return the number of bytes that the memory segment is.
   */
  public int size()
  {
    return memorySize;
  }
}