package net.sourceforge.rcosjava.software.ipc;

import java.util.*;
import net.sourceforge.rcosjava.hardware.memory.Memory;

import net.sourceforge.rcosjava.software.memory.*;

/**
 * Uses Memory class as the storage type.
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