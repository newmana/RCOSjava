package org.rcosjava.software.ipc;

import java.util.*;
import org.rcosjava.hardware.memory.*;
import org.rcosjava.software.memory.*;
import org.rcosjava.messaging.messages.os.AllocateSharedMemoryPages;
import org.rcosjava.messaging.messages.os.BlockCurrentProcess;
import org.rcosjava.messaging.messages.os.ReturnValue;
import org.rcosjava.messaging.messages.universal.BlockedToReady;
import org.rcosjava.messaging.messages.universal.ReadBytes;
import org.rcosjava.messaging.messages.universal.SemaphoreClosed;
import org.rcosjava.messaging.messages.universal.SemaphoreCreated;
import org.rcosjava.messaging.messages.universal.SemaphoreOpened;
import org.rcosjava.messaging.messages.universal.SemaphoreSignalled;
import org.rcosjava.messaging.messages.universal.SemaphoreWaiting;
import org.rcosjava.messaging.messages.universal.SharedMemoryClosed;
import org.rcosjava.messaging.messages.universal.SharedMemoryCreated;
import org.rcosjava.messaging.messages.universal.SharedMemoryOpened;
import org.rcosjava.messaging.messages.universal.SharedMemoryWrite;
import org.rcosjava.messaging.messages.universal.SharedMemoryWrote;
import org.rcosjava.messaging.messages.universal.WriteBytes;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.software.util.SemaphoreQueue;

/**
 * Provide Shared memory and semaphore facilities to the system.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 30/03/96 Completed w/o MMU page requests. BJ </DD>
 * <DD> 08/10/98 Rewrote memory handling (sem and shr mem). AN </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @author Bruce Jamieson
 * @created 1st March 1996
 * @version 1.00 $Date$
 */
public class IPC extends OSMessageHandler
{
  /**
   * Unique message id for the os messaging queue.
   */
  private final static String MESSENGING_ID = "IPC";

  /**
   * Contains all of the semaphores current active.
   */
  private SemaphoreQueue semaphoreTable = new SemaphoreQueue(10, 1);

  /**
   * The number of semaphore current active.
   */
  private int semaphoreCount = 0;

  /**
   * Shared memory table the key being the unique name of the shared memory
   * segment.
   */
  private HashMap sharedMemoryTable = new HashMap();

  /**
   * Shared memory table the key being the unique id (ascending numeric) of the
   * shared memory segment.
   */
  private HashMap sharedMemoryIdTable = new HashMap();

  /**
   * The number of shared memory segments currently active.
   */
  private int shmCount = 0;

  /**
   * Constructor for the IPC object
   *
   * @param postOffice Description of Parameter
   */
  public IPC(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);
  }

  /**
   * Creates a semaphore which belongs to the given process and sets its initial
   * value. The process that created it receives a pointer back to it. It does
   * not need to open the semaphore in order to use it. The initial value can be
   * used by other processes to determine the semaphores use (whether it is free
   * or not).
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
   * Once a semaphore has been created it can be opened by other processes. In
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

  /**
   * Indicates that a process will wait until for a signal.
   *
   * @param semaphoreId the numeric semaphore id.
   * @param pid the process id waiting on the semaphore.
   */
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

  /**
   * Do a semaphore signal (indicates it's out of the critical block) on the
   * given semaphore id from the given process.
   *
   * @param semaphoreId the numeric semaphore id.
   * @param pid the numeric process id of the signalling process.
   */
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

  /**
   * Closes a process from using a semaphore.  If it's the last process attached
   * it removes it.
   *
   * @param semaphoreId the unique id of the semaphore.
   * @param pid the process id that is closing the semaphore.
   */
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

  /**
   * Creates a new memory segment.
   *
   * @param shmName the name of the segment to create.
   * @param pid the process id that is creating the segment.
   * @param size the size (in bytes) of the memory segment.
   */
  public void sharedMemoryCreate(String shmName, int pid, int size)
  {
    if (sharedMemoryTable.containsKey(shmName))
    {
      // This means that someone has already created this
      // shrm block (on that id) - nicely say no.
      ReturnValue message = new ReturnValue(this, (short) -1);
      sendMessage(message);
    }
    else
    {
      MemoryRequest newMemoryRequest;
      int numberOfPages;

      // Allocate memory for code by pages
      numberOfPages = (int) (size / MemoryManager.PAGE_SIZE) + 1;
      newMemoryRequest = new MemoryRequest(pid, MemoryManager.SHARED_SEGMENT,
          numberOfPages);

      AllocateSharedMemoryPages allocate = new AllocateSharedMemoryPages(this,
          newMemoryRequest, shmName, size);
      sendMessage(allocate);
    }
  }

  /**
   * A successfully created a new shared memory page.
   *
   * @param shmName the name of the shared memory.
   * @param returnMemory the pid, size, the pages, etc allocated.
   * @param memory the actual memory values.
   */
  public void allocatedSharedMemoryPages(String shmName, int shmSize,
      MemoryReturn memoryReturn, Memory memory)
  {
    shmCount++;

    // We give this nice process some shared memory ;-)
    SharedMemory shShrm = new SharedMemory(shmName, shmCount,
        memoryReturn.getPID(), shmSize);
    sharedMemoryTable.put(shmName, shShrm);
    Integer shrmId = new Integer(shmCount);
    sharedMemoryIdTable.put(shrmId, shShrm);

    // Two Tables - 1 indexed by the String shrm, one by
    // the simpleint shrm number.

    //Return the integer value (SemID) of the semaphore created.
    ReturnValue returnMessage = new ReturnValue(this, (short) shmCount);
    sendMessage(returnMessage);

    //Inform other components that the shared memory was created.
    SharedMemoryCreated createdMessage = new SharedMemoryCreated(this, shmName,
        memoryReturn, memory);
    sendMessage(createdMessage);
  }

  /**
   * Opens an existing shared memory segment readying it for reading/writing.
   *
   * @param shmName the name of the memory segment to open.
   * @param pid the process opening the process.
   */
  public void sharedMemoryOpen(String shmName, int pid)
  {
    if (sharedMemoryTable.containsKey(shmName))
    {
      // Check that the shrm exists - it does
      // Open shrm
      SharedMemory shShrm = (SharedMemory) sharedMemoryTable.get(shmName);
      shShrm.open(pid);

      // Done.  Now we return a message
      int sharedMemId = shShrm.getSharedMemoryId();
      ReturnValue returnMessage = new ReturnValue(this, (short) sharedMemId);
      sendMessage(returnMessage);

      SharedMemoryOpened message = new SharedMemoryOpened(this, shmName, pid);
      sendMessage(message);
    }
    else
    {
      // The shrm does not exist (someone trying to open
      // a "non created" shrm segment)
      ReturnValue returnMessage = new ReturnValue(this, (short) -1);
      sendMessage(returnMessage);
    }
  }

  /**
   * Reads a value from the shared memory segment.
   *
   * @param currentShmId shared memory id.
   * @param offset offset into the segment to read.
   * @param pid process that is reading the value.
   */
  public void sharedMemoryRead(int currentShmId, int offset, int pid)
  {
    Integer shrmId = new Integer(currentShmId);

    if (sharedMemoryIdTable.containsKey(shrmId))
    {
      SharedMemory shShrm = (SharedMemory) sharedMemoryIdTable.get(shrmId);

      if (offset > shShrm.size())
      {
        ReturnValue message = new ReturnValue(this, (short) -1);
        sendMessage(message);
      }
      else
      {
        MemoryRequest request = new MemoryRequest(pid,
            MemoryManager.SHARED_SEGMENT, 1, offset);
        ReadBytes message = new ReadBytes(this, request);
        sendMessage(message);
      }
    }
    else
    {
      // No such Shrm
      ReturnValue message = new ReturnValue(this, (short) -1);
      sendMessage(message);
    }
  }

  /**
   * Assume that we found the Shared Memory segment correctly and return the
   * result of the read.
   */
  public void finishedSharedMemoryRead(short data)
  {
    // Return result to Kernel
    ReturnValue message = new ReturnValue(this, data);
    sendMessage(message);

    // Was it a success?
    if (data != -1)
    {
      // Was successful let everyone else know.
      //message = new SharedMemoryReadedMessage(this,
      //  shmId);
      //sendMessage(message);
    }
  }

  /**
   * Writes a value to the shared memory segment.
   *
   * @param currentShmId shared memory identifier.
   * @param offset offset into the segment to begin writing.
   * @param newValue value to set.
   * @param pid process that is writing the value.
   */
  public void sharedMemoryWrite(int currentShmId, int offset, short newValue,
      int pid)
  {
    System.out.println("Write Offset: " + offset);
    System.out.println("New Value: " + newValue);
    System.out.println("PID: " + pid);

    Integer shrmId = new Integer(currentShmId);

    if (sharedMemoryIdTable.containsKey(shrmId))
    {
      SharedMemory shShrm = (SharedMemory) sharedMemoryIdTable.get(shrmId);

      if (offset > shShrm.size())
      {
        System.out.println("Shrm outside offset");
        ReturnValue message = new ReturnValue(this, (short) -1);
        sendMessage(message);
      }
      else
      {
        System.out.println("Writing shm");
        MemoryRequest request = new MemoryRequest(pid,
            MemoryManager.SHARED_SEGMENT, 1, offset,
            new Memory(String.valueOf(newValue)));
        SharedMemoryWrite write = new SharedMemoryWrite(this, shShrm.getName(),
            request);
        sendMessage(write);
      }
    }
    else
    {
      System.out.println("No shmr");
      // No such Shrm?
      ReturnValue message = new ReturnValue(this, (short) -1);
      sendMessage(message);
    }
  }

  /**
   * Closes a shared memory segment.
   *
   * @param currentShmId shared memory identifier.
   * @param pid process id that is closing the segment.
   */
  public void sharedMemoryClose(int currentShmId, int pid)
  {
    Integer shrmId = new Integer(currentShmId);

    if (sharedMemoryIdTable.containsKey(shrmId))
    {
      // Check that the semaphore exists - it does
      // Now Close it
      SharedMemory shShrm = (SharedMemory) sharedMemoryIdTable.get(shrmId);
      int noConnections = shShrm.close(pid);

      if (noConnections == 0)
      {
        // Ok - That was the last connected PID - kill the semaphore
        String semaphoreName = shShrm.getName();
        sharedMemoryTable.remove(semaphoreName);
        sharedMemoryIdTable.remove(shrmId);
        // Assume other handle this when the receive the MemoryClosed
        // successful message.
      }

      // Was successful return 0 to Kernel.
      ReturnValue returnMessage = new ReturnValue(this, (short) 0);
      sendMessage(returnMessage);

      // Let everyone else know.
      SharedMemoryClosed message = new SharedMemoryClosed(this, shShrm.getName(),
          pid);
      sendMessage(message);
    }
    else
    {
      // Was successful return 0 to Kernel.
      ReturnValue returnMessage = new ReturnValue(this, (short) -1);
      sendMessage(returnMessage);
    }
  }

  /**
   * Returns the size of a shared memory segment.
   *
   * @param currentShmId shared memory id.
   */
  public void sharedMemorySize(int currentShmId)
  {
    Integer shrmId = new Integer(currentShmId);

    if (sharedMemoryIdTable.containsKey(shrmId))
    {
      // Check that the shrm exists - it does
      SharedMemory shShrm = (SharedMemory) sharedMemoryIdTable.get(shrmId);
      int size = shShrm.size();

      // Let kernel know the result
      ReturnValue returnMessage = new ReturnValue(this, (short) size);
      sendMessage(returnMessage);

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
      ReturnValue message = new ReturnValue(this, (short) -1);
      sendMessage(message);
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

  /**
   * Synchronized method to return the semaphore queue.
   *
   * @return The SemaphoreTable value
   */
  private synchronized SemaphoreQueue getSemaphoreTable()
  {
    return semaphoreTable;
  }
}