// ************************************************************
// FILE:    SharedMemory.java
// PURPOSE: Base Shared Memory class - uses Memory class
//          as the storage type.
// AUTHOR:  Bruce Jamieson
// MODIFIED: Andrew Newman
// HISTORY: 30/03/96  Completed.
//          11/08/98  Improved implementation. AN
//          13/08/98  Write return old value on success. AN
// ************************************************************


package net.sourceforge.rcosjava.software.ipc;

import java.util.Hashtable;
import java.util.Vector;
import java.lang.String;
import net.sourceforge.rcosjava.hardware.memory.Memory;

import net.sourceforge.rcosjava.software.memory.*;

// Basic Shared Memory class - Belongs to the Q&N school..
// BJJ

public class SharedMemory
{
  private int theSize = -1;
  private Hashtable theConnections = new Hashtable();
  private Memory ShrmBlock;
  private int numConnected = 0;
  private int ShrmID;
  private String StrID;

  public SharedMemory(String aStrID,int aShrmID,int CreatorID, int aSize)
  {
    ShrmBlock = new Memory(aSize);
    open(CreatorID);
    theSize = aSize;
    ShrmID = aShrmID;
    StrID = aStrID;
    // Assume Higher level will detect duplicate Shrm Segs
    // (aID) and return error - not to be done here?
  }

  public int getShrmID()
  {
    return ShrmID;
  }

  public String getStrID()
  {
    return StrID;
  }

  public void open(int anID)
  {
    Integer aInt = new Integer(anID);
    theConnections.put(aInt,"A Shrm");
    numConnected++;
  }

  public int close(int anID)
  {
    Integer aInt = new Integer(anID);
    theConnections.remove(aInt);
    numConnected--;
    // IF there are no connections open to the sem, then the
    // sem should die..
    // Do this by returning the number connected
    // (Let someone else deal with the problem..)
    return numConnected;
  }

  public short read(int aOffset)
  {
    if (aOffset >= theSize)
    {
      // A read past the end of the block - not on!
      return -1;
    }
    else
    {
      // Do a read
      short aByte = ShrmBlock.read(aOffset);
      return aByte;
    }
  }

  public short write(int aOffset, short aByte)
  {
    if (aOffset >= theSize)
    {
      // A write past the end of the block - not on!
      return -1;
    }
    else
    {
      // Save old value
      // short sOldValue = ShrmBlock.read(aOffset,1);
      // Do a write
      ShrmBlock.write(aOffset, aByte);
      //return sOldValue;
      //return ShrmBlock.read(aOffset,1);
      return 0;
    }
  }

  public int size()
  {
    return theSize;
  }
}