package net.sourceforge.rcosjava.software.ipc;

import java.lang.String;
import java.util.Hashtable;
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
import net.sourceforge.rcosjava.messaging.messages.universal.SemaphoreCreated;
import net.sourceforge.rcosjava.messaging.messages.universal.SemaphoreWaiting;
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
      ReturnValue message = new ReturnValue(this, (short) -1);
      sendMessage(message);
    }
    else
    {
      semaphoreCount++;
      // We give this nice process a semaphore
      Semaphore semNewSemaphore = new Semaphore(semaphoreName,
        semaphoreCount, pid, initValue);
      semaphoreTable.insert(semNewSemaphore);
      //Return the integer value (SemID) of the semaphore created.
      ReturnValue message = new ReturnValue(this, (short) semaphoreCount);
      sendMessage(message);
      SemaphoreCreated createdMessage = new SemaphoreCreated(this,
        semaphoreName, pid, initValue);
      sendMessage(createdMessage);
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
        Semaphore existingSemaphore = (Semaphore)
          semaphoreTable.peek(semaphoreName);
        existingSemaphore.open(pid);
        int semId = existingSemaphore.getId();
        // Done.  Now we return a message
        ReturnValue message = new ReturnValue(this, (short) semaphoreCount);
        sendMessage(message);
        //SemaphoreOpened message = new SemaphoreOpenedMessage(this, semaphoreCount);
        //sendMessage(message);
      }
      else
      {
        // The semaphore does not exist (someone trying to open
        // a "non created" semaphore)
        ReturnValue message = new ReturnValue(this, (short) -1);
        sendMessage(message);
      }
  }

  public void sempahoreClose(int semaphoreId, int pid)
  {
      System.out.println( "IPC - Closing Semaphore: " + semaphoreId);
      if (semaphoreTable.isMember(semaphoreId))
      {
        // Check that the semaphore exists - it does
        // Now Close it
        Semaphore existingSemaphore = (Semaphore)
          semaphoreTable.peek(semaphoreId);
        int noConnectedProcesses = existingSemaphore.close(pid);
        if (noConnectedProcesses == 0)
        {
          // Ok - That was the last connected PID
          // Remove sempahore
          semaphoreTable.getSemaphore(semaphoreId);
        }
        // Default Message Back
        ReturnValue message = new ReturnValue(this, (short) semaphoreId);
        sendMessage(message);
        //SemaphoreClosedMessage message = new SemaphoreClosedMessage(this, semaphoreId);
        //sendMessage(message);
      }
      else
      {
        // Some mistake?  Sem does NOT exist?
        ReturnValue message = new ReturnValue(this, (short) -1);
        sendMessage(message);
      }
  }

  public void sempahoreSignal(int semaphoreId, int pid)
  {
      System.out.println( "IPC - Semaphore Signal: " + semaphoreId);
      if (semaphoreTable.isMember(semaphoreId))
      {
        // The semaphore exists - now issue a signal
        Semaphore existingSemaphore = (Semaphore)
          semaphoreTable.peek(semaphoreId);
        System.out.println("Got: " + existingSemaphore);
        int processId = existingSemaphore.signal();

        // Send the reply
        ReturnValue message = new ReturnValue(this, (short) processId);
        sendMessage(message);

        //SemaphoreSignalledMessage message = new SemaphoreSignalledMessage(this, iProcessID);
        //sendMessage(message);

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
        ReturnValue message = new ReturnValue(this, (short) -1);
        sendMessage(message);
      }
  }

  public void semaphoreWait(int semaphoreId, int pid)
  {
      System.out.println( "IPC - Semaphore Wait: " + semaphoreId);
      if (semaphoreTable.isMember(semaphoreId))
      {
        // The semaphore exists - now do a wait on it
        Semaphore existingSemaphore = (Semaphore)
          semaphoreTable.peek(semaphoreId);
        int value = existingSemaphore.wait(pid);
        // Send the default response
        ReturnValue message = new ReturnValue(this, (short) semaphoreId);
        sendMessage(message);
        SemaphoreWaiting waitingMessage = new SemaphoreWaiting(this,
          existingSemaphore.getName(), pid, value);
        sendMessage(message);
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
        ReturnValue message = new ReturnValue(this, (short) -1);
        sendMessage(message);
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

  public void processMessage(UniversalMessageAdapter message)
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
}
