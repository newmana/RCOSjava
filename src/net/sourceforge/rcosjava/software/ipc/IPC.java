package net.sourceforge.rcosjava.software.ipc;

import java.util.*;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.os.BlockCurrentProcess;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmInit;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmRet;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmSize;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmRead;
import net.sourceforge.rcosjava.messaging.messages.os.ShrmWrite;
import net.sourceforge.rcosjava.messaging.messages.os.ReturnValue;
import net.sourceforge.rcosjava.messaging.messages.universal.BlockedToReady;
import net.sourceforge.rcosjava.messaging.messages.universal.SemaphoreClosed;
import net.sourceforge.rcosjava.messaging.messages.universal.SemaphoreCreated;
import net.sourceforge.rcosjava.messaging.messages.universal.SemaphoreOpened;
import net.sourceforge.rcosjava.messaging.messages.universal.SemaphoreWaiting;
import net.sourceforge.rcosjava.messaging.messages.universal.SemaphoreSignalled;
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
  private SemaphoreQueue semaphoreTable = new SemaphoreQueue(10, 1);
  private int semaphoreCount = 0;
  // Two hashtables with different indexes - no restrictions
  // on number of segments etc.
  private HashMap sharedMemoryTable = new HashMap();
  private HashMap sharedMemoryIdTable = new HashMap();
  private int shmCount = 0;
  private static final String MESSENGING_ID = "IPC";

  public IPC(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);
  }

  /**
   * Synchronized method to return the semaphore queue.
   */
  private SemaphoreQueue getSemaphoreTable()
  {
    return semaphoreTable;
  }

  /**
   * Creates a semaphore which belongs to the given process and sets its
   * initial value.  The process that created it receives a pointer back to
   * it.  It does not need to open the semaphore in order to use it.  The
   * initial value can be used by other processes to determine the semaphores
   * use (whether it is free or not).
   *
   * @param semaphoreName the name of the semaphore.
   * @param pid process identifier.
   * @param initValue initial value of the semaphore.
   */
  public void semaphoreCreate(String semaphoreName, int pid, int initValue)
  {
    if (getSemaphoreTable().isMember(semaphoreName))
    {
      // This means that someone has already created this
      // semaphore - we have got to nicely reply "no"
      // Use -1 SemID
      ReturnValue returnMessage = new ReturnValue(this, (short) -1);
      sendMessage(returnMessage);
    }
    else
    {
      semaphoreCount++;

      // We give this nice process a semaphore
      Semaphore newSemaphore = new Semaphore(semaphoreName, semaphoreCount, pid,
        initValue);
      getSemaphoreTable().insert(newSemaphore);

      //Return the integer value (SemID) of the semaphore created.
      ReturnValue returnMessage = new ReturnValue(this,
        (short) newSemaphore.getId());
      sendMessage(returnMessage);

      //Inform other components that the semaphore was created.
      SemaphoreCreated createdMessage = new SemaphoreCreated(this,
        semaphoreName, pid, initValue);
      sendMessage(createdMessage);

      // End of story - consider the semaphore created and
      // connected to.. (Value must be > 0 else probs)
    }
  }

  /**
   * Once a semaphore has been created it can be opened by other processes.  In
   * order to get a reference to it they use open to get semaphore with that
   * name.
   *
   * @param semaphoreName the name of the semaphore to locate.
   * @param pid the process to attach the semaphore to.
   */
  public void semaphoreOpen(String semaphoreName, int pid)
  {
    // Check that the semaphore exists - if it does then open it
    if (getSemaphoreTable().isMember(semaphoreName))
    {
      //Retrieve the semaphore and numeric id
      Semaphore existingSemaphore = (Semaphore)
        getSemaphoreTable().peek(semaphoreName);
      existingSemaphore.open(pid);
      int semId = existingSemaphore.getId();

      // Done.  Now we return a message with the id?
      ReturnValue returnMessage = new ReturnValue(this, (short) semId);
      sendMessage(returnMessage);

      //Let other components know that the semaphore was created
      SemaphoreOpened openedMessage = new SemaphoreOpened(this,
        existingSemaphore.getName(), pid, existingSemaphore.getValue());
      sendMessage(openedMessage);
    }
    else
    {
      // The semaphore does not exist (someone trying to open
      // a "non created" semaphore)
      ReturnValue returnMessage = new ReturnValue(this, (short) -1);
      sendMessage(returnMessage);
    }
  }

  public void semaphoreWait(int semaphoreId, int pid)
  {
    if (getSemaphoreTable().isMember(semaphoreId))
    {
      // The semaphore exists - now do a wait on it
      Semaphore existingSemaphore = (Semaphore)
        getSemaphoreTable().peek(semaphoreId);

      // Increment the value of the semaphore as more processes are added
      // -1 if process is to be blocked, or positive if haven't run out.
      int value = existingSemaphore.wait(pid);

      // Send the default response
      //ReturnValue returnMessage = new ReturnValue(this, (short) semaphoreId);
      ReturnValue returnMessage = new ReturnValue(this, (short) value);
      sendMessage(returnMessage);

      // Inform other components that wait has been called
      SemaphoreWaiting waitingMessage = new SemaphoreWaiting(this,
        existingSemaphore.getName(), pid, existingSemaphore.getValue());
      sendMessage(waitingMessage);

      // NOW - if the value was -1, then we have to block
      // the process
      if (value == -1)
      {
        BlockCurrentProcess blockMessage = new BlockCurrentProcess(this);
        sendMessage(blockMessage);
      }
    }
    else
    {
      ReturnValue returnMessage = new ReturnValue(this, (short) -1);
      sendMessage(returnMessage);
    }
  }

  public void sempahoreSignal(int semaphoreId, int pid)
  {
    if (getSemaphoreTable().isMember(semaphoreId))
    {
      // Get the semaphore and find the next process that is attached to
      // it
      Semaphore existingSemaphore = (Semaphore)
        getSemaphoreTable().peek(semaphoreId);
      int processId = existingSemaphore.signal();

      // Send the reply
      ReturnValue returnMessage = new ReturnValue(this, (short) processId);
      sendMessage(returnMessage);

      // Let other components know that a signal has been issued.
      SemaphoreSignalled signalledMessage = new SemaphoreSignalled(this,
        existingSemaphore.getName(), pid, existingSemaphore.getValue());
      sendMessage(signalledMessage);

      // NOW - if process id wasn't -1, then we have to wake
      // the process up.
      if (processId != -1)
      {
        BlockedToReady toReadyMessage = new BlockedToReady(this, processId);
        sendMessage(toReadyMessage);
      }
    }
    else
    {
      ReturnValue returnMessage = new ReturnValue(this, (short) -1);
      sendMessage(returnMessage);
    }
  }

  public void sempahoreClose(int semaphoreId, int pid)
  {
    if (getSemaphoreTable().isMember(semaphoreId))
    {
      // Check that the semaphore exists - it does
      // Now Close it
      Semaphore existingSemaphore = (Semaphore)
        getSemaphoreTable().peek(semaphoreId);
      int noConnectedProcesses = existingSemaphore.close(pid);
      if (noConnectedProcesses == 0)
      {
        // Ok - That was the last connected PID
        // Remove sempahore
        getSemaphoreTable().remove(existingSemaphore);
      }

      // Default Message Back
      ReturnValue returnMessage = new ReturnValue(this, (short) semaphoreId);
      sendMessage(returnMessage);

      SemaphoreClosed closedMessage = new SemaphoreClosed(this,
        existingSemaphore.getName(), pid, existingSemaphore.getValue());
      sendMessage(closedMessage);
    }
    else
    {
      // Some mistake?  Sem does NOT exist?
      ReturnValue returnMessage = new ReturnValue(this, (short) -1);
      sendMessage(returnMessage);
    }
  }

  public void sharedMemoryCreate(String shmName, int pid, int size)
  {
    if (sharedMemoryTable.containsKey(shmName))
    {
      // This means that someone has already created this
      // shrm block (on that id) - nicely say no.
      //ReturnValue message = new ReturnValue(this, -1);
      //sendMessage(message);
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
      //message = new ReturnValue(this, (short) shmCount);
      //sendMessage(message);
      //message = new SharedMemoryCreatedMessage(this, shmCount,
      //  pid, size);
      //sendMessage(message);
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
      //message = new ReturnValue(this, (short) iSharedMemID);
      //sendMessage(message);
      //message = new SharedMemoryOpenedMessage(this, iSharedMemID, pid);
      //sendMessage(message);
    }
    else
    {
      // The shrm does not exist (someone trying to open
      // a "non created" shrm segment)
      //message = new ReturnValue(this, -1);
      //sendMessage(message);
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
      //message = new ReturnValue(this, sData);
      //sendMessage(message);
      // Was it a success?
      if(sData != -1)
      {
        // Was successful let everyone else know.
        //message = new SharedMemoryReadedMessage(this,
        //  shmId);
        //sendMessage(message);
      }
    }
    else
    {
      // No such Shrm
      //message = new ReturnValue(this, -1);
      //sendMessage(message);
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
        //message = new ReturnValue(this, sResult);
        //sendMessage(message);
        // Was it a success?
        if(sResult != -1)
        {
          // Then let others know
          //message = new MessageAdapter(this, shmId);
          //sendMessage(message);
        }
    }
    else
    {
      // No such Shrm?
      //message = new ReturnValue(this, -1);
      //sendMessage(message);
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
        //message = new ReturnValue(this, 0);
        //sendMessage(message);
        // Let everyone else know.
        //message = new SharedMemoryClosedMessage(this,
        //  shmId, pid);
        //sendMessage(message);
      }
      else
      {
        // Some mistake?  Shrm does NOT exist?
        //message = new ReturnValue(this, -1);
        //sendMessage(message);
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
      //message = new ReturnValue(this, (short) size);
      //sendMessage(message);
      //if (size != -1)
      //{
        //message = new SharedMemorySizeMessage(this,
        //  shmId);
        //sendMessage(message);
      //}
    }
    else
    {
      // Don't know about this segment?
      //message = new ReturnValue(this, -1);
      //sendMessage(message);
    }
  }

  /**
   * Remove all of the share memory and sempahore resources that the process is
   * currently using.
   *
   * @param pid the process id to find in the list of semaphore to remove.
   */
  public void deallocateMemory(int pid)
  {
    Iterator tmpIter = getSemaphoreTable().iterator();
    while (tmpIter.hasNext())
    {
      Semaphore existingSemaphore = (Semaphore) tmpIter.next();
      if (existingSemaphore.close(pid) == 0)
      {
        getSemaphoreTable().remove(existingSemaphore);
      }
    }
  }
}
