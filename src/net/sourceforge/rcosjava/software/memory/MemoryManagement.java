package net.sourceforge.rcosjava.software.memory;

import java.util.Hashtable;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;
import net.sourceforge.rcosjava.hardware.memory.Memory;

/**
 * Base interface class for ALL memory managers.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 10/08/99 Removed connect (just handled in IPC for now).
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson.
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
public interface MemoryManagement
{
  /**
   * Allocate pages to a Process ID (pid), with a given type, and a given size.
   */
  public MemoryReturn open(int pid, byte type, int size)
    throws MemoryOpenFailedException;

  /**
   * Deallocate page that were allocated to a Process ID (pid)
   */
  public MemoryReturn close(int pid);

  /**
   * Get all memory of a specific pid, type and given maximum size.
   */
  public Memory getAllMemory(int pid, byte type);

  /**
   * Read a section of memory given a pid, type, maximum size and offset.
   */
  public Memory readBytes(int pid, byte type, int Size, int Offset);

  /**
   * Write a section of memory given a new section of Memory (Mem) that has a
   * pid, type,maximum size and offset.
   */
  public void writeBytes(int pid, byte type, int Size, int Offset, Memory Mem);
}

