package org.rcosjava.software.process;

import java.io.*;
import java.util.*;

import org.rcosjava.RCOS;
import org.rcosjava.messaging.messages.os.AllocatePages;
import org.rcosjava.messaging.messages.os.DeallocateMemory;
import org.rcosjava.messaging.messages.os.TerminalRelease;
import org.rcosjava.messaging.messages.universal.GetTerminal;
import org.rcosjava.messaging.messages.universal.NewProcess;
import org.rcosjava.messaging.messages.universal.ProcessSwitch;
import org.rcosjava.messaging.messages.universal.ReturnProcessPriority;
import org.rcosjava.messaging.messages.universal.WriteBytes;
import org.rcosjava.messaging.messages.universal.ZombieCreated;
import org.rcosjava.messaging.messages.universal.ZombieToReady;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.software.memory.MemoryManager;
import org.rcosjava.software.memory.MemoryRequest;
import org.rcosjava.software.util.FIFOQueue;
import org.rcosjava.software.util.LIFOQueue;
import org.rcosjava.software.util.PriorityQueue;

/**
 * Management of processes for RCOSjava
 * <P>
 * <DT> <B>History:</B>
 * <DD> 01/01/1997 First process set to 1. AN </DD>
 * <DD> 05/01/1997 Messages sent to Animators. AN </DD>
 * <DD> 05/04/1997 Problems with the order that the messages are sent fixed. AN
 * </DD>
 * <DD> 07/04/1997 Kills processes from all Queues. AN </DD>
 * <DD> 08/08/1998 Allocate by pages instead of bytes (always). AN </DD>
 * <DD> 11/11/1998 Added fork() (should be right place). AN </DD>
 * <DD> 02/04/2001 Removed schedule() call. Turned into message called by
 * Kernel. </DD>
 * <DD> 11/08/98 Removed String comparison for instructions. AN </DD>
 * <DD> 12/08/98 Implemented Shared Memory and File system calls. AN </DD>
 * <DD> 13/08/98 Fixed incomplete/buggy Semaphore and Shared memory. AN </DD>
 * </DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public class ProcessScheduler extends OSMessageHandler
{
  /**
   * The numeric identifier of the Ready Queue.
   */
  public final static int READYQ = 1;

  /**
   * The numeric identifier of the Blocked Queue.
   */
  public final static int BLOCKEDQ = 2;

  /**
   * The numeric identifier of the Zombie Queue.
   */
  public final static int ZOMBIEQ = 3;

  /**
   * The current set of queues whether zombie, ready, blocked or executing.
   */
  private static HashMap allProcesses = new HashMap();

  /**
   * Represents a queue for each of the different type of processes.
   */
  private static ProcessQueue zombieCreatedQueue, readyQueue, blockedQueue;

  /**
   * The queue of executing programs. Should only be one in a single CPU
   * simulation.
   */
  private static ProcessQueue executingQueue;

  /**
   * The identifier of the scheduler to the post office.
   */
  private final static String MESSENGING_ID = "ProcessScheduler";

  /**
   * The last process ID that has been issued.
   */
  private static int currentPID;

  /**
   * Initializes the queue and registers the scheduler with the postoffice. By
   * default creates FIFO queues to be used for the queueing process.
   *
   * @param postOffice the post office to register the process scheduler to and
   *      the one that sends messages to it.
   */
  public ProcessScheduler(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);

    zombieCreatedQueue = new ProcessQueue(new FIFOQueue(10, 1));
    blockedQueue = new ProcessQueue(new FIFOQueue(10, 1));
    readyQueue = new ProcessQueue(new FIFOQueue(10, 1));
    executingQueue = new ProcessQueue(new FIFOQueue(10, 1));
    currentPID = 1;
  }

  /**
   * Overwrite any existing process with the given process.
   *
   * @param newProcess the process to set to the executing queue.
   */
  public void setExecutingProcess(RCOSProcess newProcess)
  {
    executingQueue.insertProcess(newProcess);
  }

  /**
   * Sets a given process id to the new priority given.
   *
   * @param pid the process id to locate in the list of currently available
   *      processes.
   * @param priority the new priority value to set to the process (between 1 and
   *      100).
   */
  public void setProcessPriority(int pid, ProcessPriority priority)
  {
    RCOSProcess tmpProcess = locateProcess(pid);

    if (tmpProcess != null)
    {
      tmpProcess.setPriority(priority);
    }
  }

  /**
   * Will find the first process currently on the executing queue. Currently,
   * one CPU is assumed.
   *
   * @return the currently executing process on the CPU - does not remove it
   *      from being executed.
   */
  public RCOSProcess getExecutingProcess()
  {
    return executingQueue.peekProcess();
  }

  /**
   * Removes all processes from the Executing Queue.
   */
  public void nullProcess()
  {
    executingQueue.removeAllProcesses();
  }

  /**
   * @return true if running process (the executing queue is greater than 1).
   */
  public boolean runningProcess()
  {
    return executingQueue.size() >= 1;
  }

  /**
   * Called by the NewProcess message. Called when a new process has just been
   * created. Memory is allocated (stack and code), added to the zombie queue,
   * and a terminal is attempted to be allocated.
   *
   * @param messageBody Description of Parameter
   */
  public void newProcess(NewProcess messageBody)
  {
    // The message contains info about a process to be
    // created including file size, file name and code.
    // Get new Process ID.
    int newPID = getNextPID();

    // Create new RCOSProcess.
    RCOSProcess newTmpProcess = new RCOSProcess(newPID, messageBody);

    // Add to set of new queues
    allProcesses.put(new Integer(newTmpProcess.getPID()), newTmpProcess);

    MemoryRequest newMemoryRequest;
    int numberOfPages;

    // Allocate memory for code by pages
    numberOfPages = (int) (newTmpProcess.getFileSize() /
        MemoryManager.PAGE_SIZE) + 1;
    newMemoryRequest = new MemoryRequest(newPID, MemoryManager.CODE_SEGMENT,
        numberOfPages);
    AllocatePages tmpAllocatePagesMessage = new AllocatePages(this,
        newMemoryRequest);
    sendMessage(tmpAllocatePagesMessage);

    // Write code to allocated bytes - should check for successful
    // allocation really.
    newMemoryRequest = new MemoryRequest(newPID, MemoryManager.CODE_SEGMENT,
        newTmpProcess.getFileSize(), messageBody.getMemory());
    WriteBytes wbMsg = new WriteBytes(this, newMemoryRequest);
    sendMessage(wbMsg);

    // Allocate memory for stack
    newMemoryRequest = new MemoryRequest(newPID, MemoryManager.STACK_SEGMENT,
        newTmpProcess.getStackPages());
    tmpAllocatePagesMessage = new AllocatePages(this, newMemoryRequest);
    sendMessage(tmpAllocatePagesMessage);

    // Save this process in Zombie Q
    ZombieCreated newMessage = new ZombieCreated(this, newTmpProcess);
    sendMessage(newMessage);

    // Ask for new terminal for the new process.
    GetTerminal tmpGetTerminalMessage = new GetTerminal(this, newPID);
    sendMessage(tmpGetTerminalMessage);
  }

  /**
   * Called by the zombie created message. Inserts the new process into the
   * Zombie Created Queue.
   *
   * @param newProcess the process that's just been created to add to the queue.
   */
  public void zombieCreated(RCOSProcess newProcess)
  {
    insertIntoZombieCreatedQ(newProcess);
  }

  /**
   * Called by the ProcessAllocatedTerminalMessage. Called when a process is
   * trying to be allocated a process. Takes the process from the zombie queue
   * and assigns the process id to it.
   *
   * @paran newProcess the process to use to acquire the process from the zombie
   *      process queue.
   * @param newTerminalId the unique name of terminal.
   */
  public void processAllocatedTerminal(RCOSProcess newProcess,
      String newTerminalId)
  {
    try
    {
      // Try and retrieve the next process in the ZombieQ
      RCOSProcess tmpProcess = removeFromZombieCreatedQ(newProcess.getPID());

      // If a process is retrieve allocate terminal and move
      // to Ready Q.
      tmpProcess.setTerminalId(newTerminalId);
      tmpProcess.setStatus(ProcessState.READY);

      ZombieToReady newMessage = new ZombieToReady(this, tmpProcess);
      sendMessage(newMessage);
    }
    catch (ProcessNotFoundException pnfe)
    {
      System.err.println("Failed to remove zombie process in allocate " +
          "terminal: " + newProcess.getPID());
      pnfe.printStackTrace();
    }
  }

  /**
   * Used by the ZombieToReady message to directly insert the process into the
   * ready queue.
   *
   * @param newProcess the process to add to the ready queue.
   */
  public void zombieToReady(RCOSProcess newProcess)
  {
    insertIntoReadyQueue(newProcess);
  }

  /**
   * Takes the running process, sets it status to Ready and inserts it into the
   * Ready Queue.
   *
   * @param newProcess the process to find and if found to move.
   */
  public void runningToReady(RCOSProcess newProcess)
  {
    try
    {
      RCOSProcess rm = removeExecutingProcess(newProcess.getPID());
      newProcess.setStatus(ProcessState.READY);
      insertIntoReadyQueue(newProcess);
    }
    catch (ProcessNotFoundException pnfe)
    {
      System.err.println("Failed to remove executing process in Running to " +
          "blocked: " + newProcess.getPID());
      pnfe.printStackTrace();
    }
  }

  /**
   * Takes the running process, sets it blocked to Ready and inserts it into the
   * Blocked Queue.
   *
   * @param newProcess the process to find and if found to move.
   */
  public void runningToBlocked(RCOSProcess newProcess)
  {
    try
    {
      RCOSProcess tmpProcess = removeExecutingProcess(newProcess.getPID());
      newProcess.setStatus(ProcessState.BLOCKED);
      insertIntoBlockedQ(newProcess);
    }
    catch (ProcessNotFoundException pnfe)
    {
      System.err.println("Failed to remove executing process in Running to " +
          "blocked: " + newProcess.getPID());
      pnfe.printStackTrace();
    }
  }

  /**
   * Takes a blocked process and moves it to the ready queue.
   *
   * @param process the process id to find in the blocked queue, removed and
   *   then inserted into the ready queue after its status is changed to
   *   ready.
   */
  public void blockedToReady(RCOSProcess process)
  {
    try
    {
      // Get the process from the blocked Q
      RCOSProcess tmpProcess = removeFromBlockedQ(process.getPID());

      // Set it to READY status and move it to the Ready Q.
      tmpProcess.setStatus(ProcessState.READY);
      insertIntoReadyQueue(tmpProcess);
    }
    catch (ProcessNotFoundException pnfe)
    {
      System.err.println("Failed to remove blocked process in Blocked to " +
          "Ready: " + process.getPID());
      pnfe.printStackTrace();
    }
  }

  /**
   * Remove the process from the executing queue, disassociate the terminal,
   * memory and other resources.
   *
   * @param oldProcess Description of Parameter
   */
  public void processFinished(RCOSProcess oldProcess)
  {
    try
    {
      removeExecutingProcess(oldProcess.getPID());
      cleanupResources(oldProcess);
    }
    catch (ProcessNotFoundException pnfe)
    {
      System.err.println("Failed to remove executing process in Running to " +
          "blocked: " + oldProcess.getPID());
      pnfe.printStackTrace();
    }
  }

  /**
   * Seek and destroy and exsting process that is any of the queue or is
   * currently running.
   *
   * @param process the process to kill.
   */
  public void killProcess(RCOSProcess process)
  {
    // Get the real process
    RCOSProcess tmpProcess = (RCOSProcess)
        allProcesses.get(new Integer(process.getPID()));
    ProcessState state = tmpProcess.getState();

    try
    {
      // Do not remove it if it's running - handle this with kernel's
      // ProcessFinished

      // If it's in ready q.
      if (state == ProcessState.READY)
      {
        tmpProcess = removeFromReadyQueue(process.getPID());
      }
      // If it's in blocked q.
      else if (state == ProcessState.BLOCKED)
      {
        tmpProcess = removeFromBlockedQ(process.getPID());
      }
      // If it's in zombie q.
      else if (state == ProcessState.ZOMBIE)
      {
        tmpProcess = removeFromZombieCreatedQ(process.getPID());
      }

      cleanupResources(tmpProcess);
    }
    catch (ProcessNotFoundException pnfe)
    {
      System.err.println("Failed to kill process.  State: " +
          process.getState() + ", PID: " + process.getPID());
      pnfe.printStackTrace();
    }
  }

  /**
   * Finds a process and returns its current priority. May be generalised to
   * return all of the processes details in the future.
   *
   * @param pid the process id to locate in the list of currently available
   *      processes.
   */
  public void requestProcessPriority(int pid)
  {
    RCOSProcess tmpProcess = locateProcess(pid);

    if (tmpProcess != null)
    {
      ReturnProcessPriority tmpMsg = new ReturnProcessPriority(this,
          tmpProcess, tmpProcess.getPriority());
      sendMessage(tmpMsg);
    }
  }

  /**
   * Create a new process. P-code processes that fork will duplicate their stack
   * and text, retaining any handles to shared memory, semiphores and open
   * streams. Not yet implemented.
   */
  public void fork()
  {
    /*    if (uProcCnt < MAX_PROC)
    {
      for (INT16 i = 1; i < MAX_PROC; i++)
      {
        if (arrPCB[i].uPid == NO_PROC)
          break;
      }
      arrPCB[i].uStatus = PS_Zombie;
      MSG msg(ID_Kernel, MMU_Duplicate, arrMCB[uCurProc].hText);
      pTx->SendMsg(ID_MMU, &msg);
      if (arrMCB[i].hText = (HANDLE)msg.wParam)
      {
        msg = message(ID_Kernel, MMU_Duplicate, arrMCB[uCurProc].hStack);
        pTx->SendMsg(ID_MMU, &msg);
        if (0 == (arrMCB[i].hStack = (HANDLE)msg.wParam))
        {
          msg = message(ID_Kernel, KM_Close, arrMCB[i].hText);
          pTx->SendMsg(ID_MMU, &msg);
        }
        else
        {
          ++uProcCnt;
          arrPCB[i].uPid = i;
          arrPCB[i].uPidp = uCurProc;
          arrPCB[i].nPriority = DEF_PRIORITY;
          arrPCB[i].pReply = NULL;
          arrPCB[i].pFile = NULL;
          arrPCB[i].uIP = arrPCB[uCurProc].uIP;
          arrPCB[i].uSP = arrPCB[uCurProc].uSP;
          arrPCB[i].uBP = arrPCB[uCurProc].uBP;
          arrPCB[i].uStatus = PS_Created;
          PqIn.PqAdd(arrPCB[i]);
          UINT16 *pn = (UINT16*)arrPCB[uCurProc].Share.DblGetHead();
          while (pn)
          {
            ShareMem.QmIncCnt(*pn);
            arrPCB[i].Share.DblAppend((void*)pn, sizeof(UINT16));
            pn = (UINT16*)arrPCB[uCurProc].Share.DblGetNext();
          }
          msg = message(ID_Kernel, ANI_FORKS, i);
          pTx->SendMsg(ID_ANIM, &msg);
          return i;
        }
      }
    }
    return NO_PROC;*/
  }

  /**
   * Copies all existing processes in the existing queues and converts them to a
   * LIFO queueing structure.
   */
  public void switchToLIFOQueue()
  {
    zombieCreatedQueue.setProcessQueue(new
        LIFOQueue(10, 1, zombieCreatedQueue.getProcessQueue()));
    blockedQueue.setProcessQueue(new LIFOQueue(10, 1, blockedQueue.getProcessQueue()));
    readyQueue.setProcessQueue(new LIFOQueue(10, 1, readyQueue.getProcessQueue()));
    executingQueue.setProcessQueue(new
        LIFOQueue(10, 1, executingQueue.getProcessQueue()));
  }

  /**
   * Copies all existing processes in the existing queues and converts them to a
   * LIFO queueing structure.
   */
  public void switchToFIFOQueue()
  {
    zombieCreatedQueue.setProcessQueue(new
        FIFOQueue(10, 1, zombieCreatedQueue.getProcessQueue()));
    blockedQueue.setProcessQueue(new FIFOQueue(10, 1, blockedQueue.getProcessQueue()));
    readyQueue.setProcessQueue(new FIFOQueue(10, 1, readyQueue.getProcessQueue()));
    executingQueue.setProcessQueue(new
        FIFOQueue(10, 1, executingQueue.getProcessQueue()));
  }

  /**
   * Copies all existing processes in the existing queues and converts them to a
   * Priority queueing structure.
   */
  public void switchToPriorityQueue()
  {
    zombieCreatedQueue.setProcessQueue(new
        PriorityQueue(10, 1, zombieCreatedQueue.getProcessQueue()));
    blockedQueue.setProcessQueue(new
        PriorityQueue(10, 1, blockedQueue.getProcessQueue()));
    readyQueue.setProcessQueue(new PriorityQueue(10, 1, readyQueue.getProcessQueue()));
    executingQueue.setProcessQueue(new
        PriorityQueue(10, 1, executingQueue.getProcessQueue()));
  }

  /**
   * Executed every cycle to make sure that if there is not a currently
   * executing process that one is taken from the ready queue.
   */
  public void schedule()
  {
    //Make sure that there isn't a process currently running.
    //Check to see if there are processes ready to execute.
    if (!runningProcess())
    {
      if (!readyQueue.queueEmpty())
      {
        RCOSProcess currentProcess = readyQueue.retrieveProcess();

        setExecutingProcess(currentProcess);
        currentProcess.setStatus(ProcessState.RUNNING);

        ProcessSwitch tmpMessage = new ProcessSwitch(this, currentProcess);
        sendMessage(tmpMessage);
      }
    }
  }

  /**
   * Returns current processes.
   *
   * @return current processes.
   */
  public HashMap getAllProcesses()
  {
    return allProcesses;
  }

  /**
   * Returns zombie created queue.
   *
   * @return zombie created queue.
   */
  public ProcessQueue getZombieCreatedQueue()
  {
    return zombieCreatedQueue;
  }

  /**
   * Returns the blocked queue.
   *
   * @return the blocked queue.
   */
  public ProcessQueue getBlockedQueue()
  {
    return blockedQueue;
  }

  /**
   * Returns the ready queue.
   *
   * @return the ready queue.
   */
  public ProcessQueue getReadyQueue()
  {
    return readyQueue;
  }

  /**
   * Returns the executing queue.
   *
   * @return the executing queue.
   */
  public ProcessQueue getExecutingQueue()
  {
    return executingQueue;
  }

  /**
   * Simply increments a counter by one but could be improved in the future to
   * reuse PIDs.
   *
   * @return next allocat Processes ID.
   */
  private int getNextPID()
  {
    return currentPID++;
  }

  /**
   * Finds a given process from the Zombie Created Queue without removing it
   * from the queue.
   *
   * @param pid the process to remove
   * @return the RCOSProcess given with PID
   */
  private RCOSProcess getFromZombieCreatedQ(int pid)
  {
    try
    {
      return zombieCreatedQueue.getProcess(pid);
    }
    catch (ProcessNotFoundException pnfe)
    {
      pnfe.printStackTrace();
      return null;
    }
  }

  /**
   * Finds a given process from the Blocked Queue without removing it from the
   * queue.
   *
   * @param pid the process to remove
   * @return The FromBlockedQ value
   */
  private RCOSProcess getFromBlockedQ(int pid)
  {
    try
    {
      return blockedQueue.getProcess(pid);
    }
    catch (ProcessNotFoundException pnfe)
    {
      pnfe.printStackTrace();
      System.err.println("Couldn't find the process id: " + pid);
      return null;
    }
  }

  /**
   * Finds a given process from the Ready Queue without removing it from the
   * queue.
   *
   * @param pid the process to remove
   * @return The FromReadyQueue value
   */
  private RCOSProcess getFromReadyQueue(int pid)
  {
    try
    {
      return readyQueue.getProcess(pid);
    }
    catch (ProcessNotFoundException pnfe)
    {
      pnfe.printStackTrace();
      System.err.println("Couldn't find the process id: " + pid);
      return null;
    }
  }

  /**
   * Inserts a new process in the Zombie Queue.
   *
   * @param zombieProcess the process to insert
   */
  private void insertIntoZombieCreatedQ(RCOSProcess zombieProcess)
  {
    zombieCreatedQueue.insertProcess(zombieProcess);
  }

  /**
   * Removes a given process from the Zombie Created Queue.
   *
   * @param pid the process to remove
   * @return the process that was removed.
   * @throws ProcessNotFoundException if the given pid was not found on the
   *     executing queue.
   */
  private RCOSProcess removeFromZombieCreatedQ(int pid)
      throws ProcessNotFoundException
  {
    return zombieCreatedQueue.removeProcess(pid);
  }

  /**
   * Inserts a given process into the Blocked Queue.
   *
   * @param blockedProcess the process to insert
   */
  private void insertIntoBlockedQ(RCOSProcess blockedProcess)
  {
    blockedQueue.insertProcess(blockedProcess);
  }

  /**
   * Removes a given process from the Blocked Queue.
   *
   * @param pid the process to remove
   * @return the process in the queue.
   * @throws ProcessNotFoundException if the given pid was not found on the
   *   executing queue.
   */
  private RCOSProcess removeFromBlockedQ(int pid)
      throws ProcessNotFoundException
  {
    return blockedQueue.removeProcess(pid);
  }

  /**
   * Inserts a given process into the Ready Queue.
   *
   * @param readyPrococess Description of Parameter
   */
  private void insertIntoReadyQueue(RCOSProcess readyPrococess)
  {
    readyQueue.insertProcess(readyPrococess);
  }

  /**
   * Removes a given process from the Ready Queue.
   *
   * @param pid the process to remove
   * @return the process in the queue.
   * @throws ProcessNotFoundException if the given pid was not found on the
   *   executing queue.
   */
  private RCOSProcess removeFromReadyQueue(int pid)
      throws ProcessNotFoundException
  {
    return readyQueue.removeProcess(pid);
  }

  /**
   * Removes a given process from the Executing Queue.
   *
   * @param pid the process id to remove it from.
   * @return the process that was removed.
   * @throws ProcessNotFoundException if the given pid was not found on the
   *     executing queue.
   */
  private RCOSProcess removeExecutingProcess(int pid)
      throws ProcessNotFoundException
  {
    return executingQueue.removeProcess(pid);
  }

  /**
   * Attempts to remove all resources currently used by a process.
   *
   * @param oldProcess Description of Parameter
   */
  private void cleanupResources(RCOSProcess oldProcess)
  {
    //If it wasn't the executing program clear up resources
    if (oldProcess.getTerminalId() != null)
    {
      TerminalRelease msg = new TerminalRelease(this,
          oldProcess.getTerminalId());
      sendMessage(msg);
    }

    // Deallocate Memory
    DeallocateMemory deallocateMsg = new DeallocateMemory(this,
        oldProcess.getPID());
    sendMessage(deallocateMsg);

    // Remove process from hash map
    allProcesses.remove(new Integer(oldProcess.getPID()));
  }

  /**
   * Locates a process from any of the queues without removing it from the
   * queue.
   *
   * @param pid the process id to locate and find.
   * @return the process.
   */
  private RCOSProcess locateProcess(int pid)
  {
    RCOSProcess tmpProcess = null;

    tmpProcess = getExecutingProcess();
    if ((tmpProcess.getPID() != pid) || (tmpProcess == null))
    {
      // If it's in ready q.
      tmpProcess = getFromReadyQueue(pid);
      if (tmpProcess == null)
      {
        // If it's in blocked q.
        tmpProcess = getFromBlockedQ(pid);
        if (tmpProcess == null)
        {
          // If it's in zombie q.
          tmpProcess = getFromZombieCreatedQ(pid);
        }
      }
    }
    return tmpProcess;
  }

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
    os.defaultWriteObject();
  }

  /**
   * Handle deserialization of the contents.  Ensures non-serializable
   * components correctly created.
   *
   * @param is stream that is being read.
   */
  private void readObject(ObjectInputStream is) throws IOException,
      ClassNotFoundException
  {
    register(MESSENGING_ID, RCOS.getOSPostOffice());
    is.defaultReadObject();
  }
}
