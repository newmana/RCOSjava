//*************************************************************************
// FILE:        MemoryManagement.java
// PURPOSE:     Base interface class for ALL memory managers.
// AUTHOR:      Bruce Jamieson
// MODIFIED:    Andrew Newman
// HISTORY:     ??/??/96	Completed
//              10/08/99	Removed connect (just handled in IPC for now).
//                        Changed to interface.
//*************************************************************************

package net.sourceforge.rcosjava.software.memory;

import java.util.Hashtable;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;
import net.sourceforge.rcosjava.hardware.memory.Memory;

public interface MemoryManagement
{
  // Allocate pages to a Process ID (PID), with a given type, and a given size.
  public MemoryReturn open(int PID, int Type, int iSize)
    throws MemoryOpenFailedException;
  // Deallocate page that were allocated to a Process ID (PID)
  public MemoryReturn close(int PID);
  // Get all memory of a specific PID, Type and given maximum size.
  public Memory getAllMemory(int PID, int Type, int Size);
  // Read a section of memory given a PID, Type, maximum size and offset.
  public Memory readBytes(int PID, int Type, int Size, int Offset);
  // Write a section of memory given a new section of Memory (Mem) that has a PID, Type,
  // maximum size and offset.
  public void writeBytes(int PID, int Type, int Size, int Offset, Memory Mem);
}

