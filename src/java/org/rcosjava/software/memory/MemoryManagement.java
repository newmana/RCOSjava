package org.rcosjava.software.memory;

import java.io.Serializable;
import org.rcosjava.hardware.memory.Memory;

/**
 * Base interface class for ALL memory managers.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 10/08/99 Removed connect (just handled in IPC for now). </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public interface MemoryManagement extends Serializable
{
  /**
   * Allocate pages to a Process ID (pid), with a given type, and a given size.
   *
   * @param pid Description of Parameter
   * @param type Description of Parameter
   * @param size Description of Parameter
   * @return Description of the Returned Value
   * @exception MemoryOpenFailedException Description of Exception
   */
  public MemoryReturn open(int pid, byte type, int size)
    throws MemoryOpenFailedException;

  /**
   * Deallocate page that were allocated to a Process ID (pid)
   *
   * @param pid Description of Parameter
   * @return Description of the Returned Value
   */
  public MemoryReturn close(int pid);

  /**
   * Get all memory of a specific pid, type and given maximum size.
   *
   * @param pid Description of Parameter
   * @param type Description of Parameter
   * @return The AllMemory value
   */
  public Memory getAllMemory(int pid, byte type);

  /**
   * Read all pages for a particular process, type, size and offset and return
   * it as a single memory block.
   *
   * @param pid the process id that own the memory
   * @param type whether stack or program code memory to read
   * @param size Description of Parameter
   * @param offset Description of Parameter
   * @return the total contiguous memory block found with the given parameters.
   */
  public Memory readBytes(int pid, byte type, int size, int offset);

  /**
   * Write a section of memory given a new section of Memory (Mem) that has a
   * pid, type, maximum size and offset.
   *
   * @param pid the process id that own the memory
   * @param type whether stack or program code memory to read
   * @param size the number of bytes to read
   * @param offset the offset to start in memory to begin reading
   * @param memory Description of Parameter
   */
  public void writeBytes(int pid, byte type, int size, int offset,
      Memory memory);
}