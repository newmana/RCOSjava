package net.sourceforge.rcosjava.software.ipc;

import java.lang.String;
import java.util.Hashtable;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmInit;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmRet;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmSize;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmRead;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmWrite;
import net.sourceforge.rcosjava.messaging.messages.os.ReturnValue;
import net.sourceforge.rcosjava.messaging.messages.universal.SemaphoreCreated;
import net.sourceforge.rcosjava.messaging.messages.universal.BlockedToReady;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.util.SemaphoreQueue;

/**
 * Provide Shared memory and semaphore facilities to the system.
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 30/03/96 Completed w/o MMU page requests. BJ
 * </DD><DD>
 * 08/10/98 Rewrote memory handling (sem and shr mem). AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson
 * @version 1.00 $Date$
 * @created 1st March 1996
 */
public class IPC extends OSMessageHandler
{
  private SemaphoreQueue semaphoreTable = new SemaphoreQueue(10, 10);
  private int semaphoreCount = 0;
  // Two hashtables with different indexes - no restrictions
  // on number of segments etc.
  private Hashtable sharedMemoryTable = new Hashtable();
  private Hashtable sharedMemoryIdTable = new Hashtable();
  private int shmCount = 0;
  private static final String MESSENGING_ID = "IPC";

  public IPC(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);
  }

  public void semaphoreCreate(String semaphoreName, int pid, int initValue)
  {
    System.out.println("IPC: Creating Semaphore: " + semaphoreName);
    if (semaphoreTable.isMember(semaphoreName))
    {
      // This means that someone has already created this
      // semaphore - we have got to nicely reply "no"
      // Use -1 SemID
      //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
    else
    {
      semaphoreCount++;
      // We give this nice process a semaphore
      Semaphore semNewSemaphore = new Semaphore(semaphoreName,
        semaphoreCount, pid, initValue);
      semaphoreTable.insert(semNewSemaphore);
      //Return the integer value (SemID) of the semaphore created.
      //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) semaphoreCount);
      //sendMessage(aMessage);
      SemaphoreCreated message = new SemaphoreCreated(this, semaphoreName,
        pid, initValue);
      sendMessage(message);
      // End of story - consider the semaphore created and
      // connected to.. (Value must be > 0 else probs)
    }
  }

  public void semaphoreOpen(String semaphoreName, int pid)
  {
      System.out.println("IPC: Open Semaphore: " + semaphoreName);
      if (semaphoreTable.isMember(semaphoreName))
      {
        // Check that the semaphore exists - it does
        // Open Semaphore
        Semaphore semExistingSemaphore = (Semaphore)
          semaphoreTable.peek(semaphoreName);
        semExistingSemaphore.open(pid);
        int semId = semExistingSemaphore.getId();
        // Done.  Now we return a message
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) semaphoreCount);
        //sendMessage(aMessage);
        //SemaphoreOpenedMessage aMessage = new SemaphoreOpenedMessage(this, semaphoreCount);
        //sendMessage(aMessage);
      }
      else
      {
        // The semaphore does not exist (someone trying to open
        // a "non created" semaphore)
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }
  }

  public void sempahoreClose(int semaphoreId, int pid)
  {
      System.out.println( "IPC - Closing Semaphore: " + semaphoreId);
      if (semaphoreTable.isMember(semaphoreId))
      {
        // Check that the semaphore exists - it does
        // Now Close it
        Semaphore semExistingSemaphore = (Semaphore)
          semaphoreTable.peek(semaphoreId);
        int iNoConnectedProcesses = semExistingSemaphore.close(pid);
        if (iNoConnectedProcesses == 0)
        {
          // Ok - That was the last connected PID
          // Remove sempahore
          semaphoreTable.getSemaphore(semaphoreId);
        }
        // Default Message Back
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) semaphoreId);
        //sendMessage(aMessage);
        //SemaphoreClosedMessage aMessage = new SemaphoreClosedMessage(this, semaphoreId);
        //sendMessage(aMessage);
      }
      else
      {
        // Some mistake?  Sem does NOT exist?
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }
  }

  public void sempahoreSignal(int semaphoreId, int pid)
  {
      System.out.println( "IPC - Semaphore Signal: " + semaphoreId);
      if (semaphoreTable.isMember(semaphoreId))
      {
        // The semaphore exists - now issue a signal
        Semaphore semExistingSemaphore = (Semaphore)
          semaphoreTable.peek(semaphoreId);
        int iProcessID = semExistingSemaphore.signal();

        // Send the reply
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) iProcessID);
        //sendMessage(aMessage);
        //SemaphoreSignalledMessage aMessage = new SemaphoreSignalledMessage(this, iProcessID);
        //sendMessage(aMessage);

        // NOW - if process id wasn't -1, then we have to wake
        // the process up.
        if (iProcessID != -1)
        {
          //blockedToReadyMessage aMessage = new BlockedToReadyMessage(this, iProcessID);
          //sendMessage(aMessage);
        }
      }
      else
      {
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }
  }

  public void semaphoreWait(int semaphoreId, int pid)
  {
      System.out.println( "IPC - Semaphore Wait: " + semaphoreId);
      if (semaphoreTable.isMember(semaphoreId))
      {
        // The semaphore exists - now do a wait on it
        Semaphore semExistingSemaphore = (Semaphore)
          semaphoreTable.peek(semaphoreId);
        int iValue = semExistingSemaphore.wait(pid);
        // Send the default response
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, (short) semaphoreId);
        //sendMessage(aMessage);
        //aMessage = new MessageAdapter(this, semaphoreId);
        //sendMessage(aMessage);
        // NOW - if the value was -1, then we have to block
        // the process
        if (iValue == -1)
        {
          //BlockCurrentProcessMessage aMessage = new BlockCurrentProcessMessage(this);
          //sendMessage(aMessage);
        }
      }
      else
      {
        //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }
  }

  public void sharedMemoryCreate(String shmName, int pid, int size)
  {
    if (sharedMemoryTable.containsKey(shmName))
    {
      // This means that someone has already created this
      // shrm block (on that id) - nicely say no.
      //ReturnValueMessage aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
    else
    {
      shmCount++;
      // We give this nice process some shared memory ;-)
      SharedMemory shShrm = new SharedMemory(shmName,
        shmCount, pid, size);
      sharedMemoryTable.put(shmName, shShrm);
      Integer iShrmID = new Integer(shmCount);
      sharedMemoryIdTable.put(iShrmID, shShrm);
      // Two Tables - 1 indexed by the String shrm, one by
      // the simpleint shrm number.
      //aMessage = new ReturnValueMessage(this, (short) shmCount);
      //sendMessage(aMessage);
      //aMessage = new SharedMemoryCreatedMessage(this, shmCount,
      //  pid, size);
      //sendMessage(aMessage);
    }
  }

  public void sharedMemoryOpen(String shmName, int pid)
  {
    if (sharedMemoryTable.containsKey(shmName))
    {
      // Check that the shrm exists - it does
      // Open shrm
      SharedMemory shShrm = (SharedMemory)
        sharedMemoryTable.get(shmName);
      shShrm.open(pid);
      int iSharedMemID = shShrm.getShrmID();
      // Done.  Now we return a message
      //aMessage = new ReturnValueMessage(this, (short) iSharedMemID);
      //sendMessage(aMessage);
      //aMessage = new SharedMemoryOpenedMessage(this, iSharedMemID, pid);
      //sendMessage(aMessage);
    }
    else
    {
      // The shrm does not exist (someone trying to open
      // a "non created" shrm segment)
      //aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
  }

  public void sharedMemoryClose(int shmId, int pid)
  {
    Integer iShrmID = new Integer(shmId);
    if (sharedMemoryIdTable.containsKey(iShrmID))
    {
        // Check that the semaphore exists - it does
        // Now Close it
        SharedMemory shShrm = (SharedMemory) sharedMemoryIdTable.get(iShrmID);
        int iNoConnections = shShrm.close(pid);
        if (iNoConnections == 0)
        {
          // Ok - That was the last connected PID - kill the semaphore
          String semaphoreName = shShrm.getStrID();
          sharedMemoryTable.remove(semaphoreName);
          sharedMemoryIdTable.remove(iShrmID);
          // Assume other handle this when the receive the MemoryClosed
          // successful message.
        }
        // Was successful return 0 to Kernel.
        //aMessage = new ReturnValueMessage(this, 0);
        //sendMessage(aMessage);
        // Let everyone else know.
        //aMessage = new SharedMemoryClosedMessage(this,
        //  shmId, pid);
        //sendMessage(aMessage);
      }
      else
      {
        // Some mistake?  Shrm does NOT exist?
        //aMessage = new ReturnValueMessage(this, -1);
        //sendMessage(aMessage);
      }
  }

  public void sharedMemoryRead(int shmId, int iOffset)
  {
    Integer iShrmID = new Integer(shmId);
    if (sharedMemoryIdTable.containsKey(iShrmID))
    {
      // Check that the shrm exists - it does
      SharedMemory shShrm = (SharedMemory) sharedMemoryIdTable.get(iShrmID);
      short sData = shShrm.read(iOffset);
      // Return result to Kernel
      //aMessage = new ReturnValueMessage(this, sData);
      //sendMessage(aMessage);
      // Was it a success?
      if(sData != -1)
      {
        // Was successful let everyone else know.
        //aMessage = new SharedMemoryReadedMessage(this,
        //  shmId);
        //sendMessage(aMessage);
      }
    }
    else
    {
      // No such Shrm
      //aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
  }

  public void sharedMemoryWrite(int shmId, int offset, short newValue)
  {
    Integer iShrmID = new Integer(shmId);
    if (sharedMemoryIdTable.containsKey(iShrmID))
    {
        // Check that the shrm exists - it does
        SharedMemory shShrm = (SharedMemory) sharedMemoryIdTable.get(iShrmID);
        short sResult = shShrm.write(offset, newValue);
        // Let kernel know the result
        //aMessage = new ReturnValueMessage(this, sResult);
        //sendMessage(aMessage);
        // Was it a success?
        if(sResult != -1)
        {
          // Then let others know
          //aMessage = new MessageAdapter(this, shmId);
          //sendMessage(aMessage);
        }
    }
    else
    {
      // No such Shrm?
      //aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
  }

  public void sharedMemorySize(int shmId)
  {
    Integer iShrmID = new Integer(shmId);
    if (sharedMemoryIdTable.containsKey(iShrmID))
    {
      // Check that the shrm exists - it does
      //SharedMemory shShrm = (SharedMemory)
      //  sharedMemoryIdTable.get(shmId);
      //int size = shShrm.size();
      // Let kernel know the result
      //aMessage = new ReturnValueMessage(this, (short) size);
      //sendMessage(aMessage);
      //if (size != -1)
      //{
        //aMessage = new SharedMemorySizeMessage(this,
        //  shmId);
        //sendMessage(aMessage);
      //}
    }
    else
    {
      // Don't know about this segment?
      //aMessage = new ReturnValueMessage(this, -1);
      //sendMessage(aMessage);
    }
  }

  public void processMessage(OSMessageAdapter message)
  {
    try
    {
      message.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }

  public void processMessage(UniversalMessageAdapter aMessage)
  {
    try
    {
      aMessage.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }
}
