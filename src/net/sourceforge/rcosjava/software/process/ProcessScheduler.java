package net.sourceforge.rcosjava.software.process;

import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.messages.universal.NewProcess;
import net.sourceforge.rcosjava.messaging.messages.os.AllocatePages;
import net.sourceforge.rcosjava.messaging.messages.os.DeallocatePages;
import net.sourceforge.rcosjava.messaging.messages.os.TerminalRelease;
import net.sourceforge.rcosjava.messaging.messages.universal.GetTerminal;
import net.sourceforge.rcosjava.messaging.messages.universal.KillProcess;
import net.sourceforge.rcosjava.messaging.messages.universal.NullProcess;
import net.sourceforge.rcosjava.messaging.messages.universal.ProcessSwitch;
import net.sourceforge.rcosjava.messaging.messages.universal.WriteBytes;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.ZombieCreated;
import net.sourceforge.rcosjava.messaging.messages.universal.ZombieToReady;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.software.memory.MemoryManager;
import net.sourceforge.rcosjava.software.memory.MemoryReturn;
import net.sourceforge.rcosjava.software.memory.MemoryRequest;
import net.sourceforge.rcosjava.software.util.ProcessQueue;

/**
 * Management of processes for RCOS.java
 * <P>
 * <DT><B>History:</B>
 * <DD>
 * 01/01/1997 First process set to 1. AN
 * </DD><DD>
 * 05/01/1997 Messages sent to Animators. AN
 * </DD><DD>
 * 05/04/1997 Problems with the order that the messages are sent fixed. AN
 * </DD><DD>
 * 07/04/1997 Kills processes from all Queues. AN
 * </DD><DD>
 * 08/08/1998 Allocate by pages instead of bytes (always). AN
 * </DD><DD>
 * 11/11/1998 Added fork() (should be right place). AN
 * </DD><DD>
 * 02/04/2001 Removed schedule() call.  Turned into message called by Kernel.
 * </DD><DD>
 * 11/08/98  Removed String comparison for instructions.  AN
 * </DD><DD>
 * 12/08/98  Implemented Shared Memory and File system calls. AN
 * </DD><DD>
 * 13/08/98  Fixed incomplete/buggy Semaphore and Shared memory. AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
public class ProcessScheduler extends OSMessageHandler
{
  /**
   * The numeric identifier of the Ready Queue.
   */
  public static final int READYQ = 1;

  /**
   * The numeric identifier of the Blocked Queue.
   */
  public static final int BLOCKEDQ = 2;

  /**
   * The numeric identifier of the Zombie Queue.
   */
  public static final int ZOMBIEQ = 3;

  /**
   * Represents a queue for each of the different type of processes.
   */
  private static ProcessQueue zombieCreatedQ, zombieDeadQ, readyQ, blockedQ;

  /**
   * The queue of executing programs.  Should only be one in a single CPU
   * simulation.
   */
  private static ProcessQueue executingQ;

  /**
   * The identifier of the scheduler to the post office.
   */
  private static final String MESSENGING_ID = "ProcessScheduler";

  /**
   * The last process ID that has been issued.
   */
  private static int currentPID;

  /**
   * Initializes the queue and registers the scheduler with the postoffice.
   *
   * @param postOffice the post office to register the process scheduler to and
   * the one that sends messages to it.
   */
  public ProcessScheduler(OSOffice postOffice)
  {
    super(MESSENGING_ID, postOffice);

    zombieCreatedQ = new ProcessQueue(10,1);
    zombieDeadQ = new ProcessQueue(10,1);
    blockedQ = new ProcessQueue(10,1);
    readyQ = new ProcessQueue(10,1);
    executingQ = new ProcessQueue(1,1);
    currentPID = 1;
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
   * Inserts a new process in the Zombie Queue.
   *
   * @param zombieProcess the process to insert
   */
  private void insertIntoZombieCreatedQ(RCOSProcess zombieProcess)
  {
    //System.out.println("Insert into zombie created q: " + zombieProcess.getPID());
    zombieCreatedQ.insert(zombieProcess);
  }

  /**
   * Removes a given process from the Zombie Queue.
   *
   * @param pid the process to remove
   */
  private RCOSProcess removeFromZombieCreatedQ(int pid)
  {
    //System.out.println("Remove from zombie created q: " + pid);
    return (RCOSProcess) zombieCreatedQ.getProcess(pid);
  }

  /**
   * Inserts a given process into the Blocked Queue.
   *
   * @param blockedProcess the process to insert
   */
  private void insertIntoBlockedQ(RCOSProcess blockedProcess)
  {
    //System.out.println("Insert into blocked q: " + blockedProcess.getPID());
    blockedQ.insert(blockedProcess);
  }

  /**
   * Removes a given process from the Blocked Queue.
   *
   * @param pid the process to remove
   */
  private RCOSProcess removeFromBlockedQ(int pid)
  {
    //System.out.println("Remove from blocked q: " + pid);
    return (RCOSProcess) blockedQ.getProcess(pid);
  }

  /**
   * Inserts a given process into the Ready Queue.
   *
   * @param readyProcess the process to insert
   */
  private void insertIntoReadyQ(RCOSProcess readyPrococess)
  {
    //System.out.println("Insert into ready q: " + readyPrococess.getPID());
    readyQ.insert(readyPrococess);
    //System.out.println("Ready Q size: " + readyQ.size());
    //System.out.println("First element: " + ((RCOSProcess) readyQ.peek(0)).getPID());
    //if (readyQ.size() > 1)
    //  System.out.println("2nd element: " + ((RCOSProcess) readyQ.peek(1)).getPID());
  }

  /**
   * Removes a given process from the Ready Queue.
   *
   * @param pid the process to remove
   */
  private RCOSProcess removeFromReadyQ(int pid)
  {
    //System.out.println("Remove from ready q: " + pid);
    return (RCOSProcess) readyQ.getProcess(pid);
  }

  /**
   * For multiple CPU we could have an index here.  At the moment on CPU is
   * assumed.
   *
   * @return the currently executing process on the CPU - does not remove it
   * from being executed.
   */
  public RCOSProcess getExecutingProcess()
  {
    return (RCOSProcess) executingQ.peek();
  }

  /**
   * Overwrite any existing process with the given process.
   *
   * @param newProcess the process to set to the executing queue.
   */
  public void setExecutingProcess(RCOSProcess newProcess)
  {
    executingQ.insert(newProcess);
  }

  /**
   * Removes all processes from the Executing Queue.
   */
  public void setCurrentProcessNull()
  {
    executingQ.removeAllElements();
  }

  /**
   * Removes a given process from the Executing Queue.
   *
   * @param pid the process id to remove it from.
   */
  private RCOSProcess removeExecutingProcess(int pid)
  {
    return (RCOSProcess) executingQ.getProcess(pid);
  }

  /**
   * @return true if running process (the executing queue is greater than 1).
   */
  public boolean runningProcess()
  {
    return executingQ.size() >= 1;
  }

  /**
   * Called by the NewProcess message.  Called when a new process has just been
   * created.  Memory is allocated (stack and code), added to the zombie queue,
   * and a terminal is attempted to be allocated.
   *
   * @param newRCOSProcess the process that's just been created.
   */
  public void newProcess(NewProcess messageBody)
  {
    // The message contains info about a process to be
    // created including file size, file name and code.
    // Get new Process ID.
    int newPID = getNextPID();

    // Create new RCOSProcess.
    RCOSProcess newTmpProcess = new RCOSProcess(newPID, messageBody);

    MemoryRequest newMemoryRequest;
    int numberOfPages;
    // Allocate memory for code by pages
    numberOfPages = (int) (newTmpProcess.getFileSize() /
      MemoryManager.PAGE_SIZE) + 1;
    newMemoryRequest = new MemoryRequest(newPID, MemoryManager.CODE_SEGMENT,
      MemoryManager.CODE_SEGMENT, numberOfPages);
    AllocatePages tmpAllocatePagesMessage = new
      AllocatePages(this, newMemoryRequest);
    sendMessage(tmpAllocatePagesMessage);

    // Write code to allocated bytes - should check for successful
    // allocation really.
    newMemoryRequest = new MemoryRequest(newPID, MemoryManager.CODE_SEGMENT,
      newTmpProcess.getFileSize(), messageBody.getMemory());
    WriteBytes wbMsg = new WriteBytes(this, newMemoryRequest);
    sendMessage(wbMsg);

    // Allocate memory for stack
    newMemoryRequest = new MemoryRequest(newPID, MemoryManager.STACK_SEGMENT,
      MemoryManager.STACK_SEGMENT, newTmpProcess.getStackPages());
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
   * Called by the zombie created message.  Inserts the new process into the
   * Zombie Created Queue.
   *
   * @param newProcess the process that's just been created to add to the queue.
   */
  public void zombieCreated(RCOSProcess newProcess)
  {
    insertIntoZombieCreatedQ(newProcess);
  }

  /**
   * Called by the ProcessAllocatedTerminalMessage.  Called when a process is
   * trying to be allocated a process.  Takes the process from the zombie queue
   * and assigns the process id to it.
   *
   * @paran newPID the process id to use to acquire the process from the zombie
   * process queue.
   * @param newTerminalId the unique name of terminal.
   */
  public void processAllocatedTerminal(int newPID, String newTerminalId)
  {
    // Try and retrieve the next process in the ZombieQ
    RCOSProcess newProcess = (RCOSProcess) removeFromZombieCreatedQ(newPID);
    if (newProcess != null)
    {
      // If a process is retrieve allocate terminal and move
      // to Ready Q.
      newProcess.setTerminalId(newTerminalId);
      newProcess.setStatus(RCOSProcess.READY);
      ZombieToReady newMessage = new ZombieToReady(this, newProcess);
      sendMessage(newMessage);
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
    insertIntoReadyQ(newProcess);
  }

  /**
   * Takes the running process, sets it status to Ready and inserts it into
   * the Ready Queue.
   *
   * @param newProcess the process to find and if found to move.
   */
  public void runningToReady(RCOSProcess newProcess)
  {
    RCOSProcess rm = removeExecutingProcess(newProcess.getPID());
    //System.out.println("New Process: " + newProcess.getPID());
    //System.out.println("Removed: " + rm.getPID());
    newProcess.setStatus(RCOSProcess.READY);
    insertIntoReadyQ(newProcess);
  }

  /**
   * Takes the running process, sets it blocked to Ready and inserts it into
   * the Blocked Queue.
   *
   * @param newProcess the process to find and if found to move.
   */
  public void runningToBlocked(RCOSProcess newProcess)
  {
    removeExecutingProcess(newProcess.getPID());
    newProcess.setStatus(RCOSProcess.BLOCKED);
    insertIntoBlockedQ(newProcess);
  }

  /**
   * Takes a blocked process and moves it to the ready queue.
   *
   * @param pid the process id to find in the blocked queue, removed and then
   * inserted into the ready queue after its status is changed to ready.
   */
  public void blockedToReady(int pid)
  {
    // Get the process from the blocked Q
    RCOSProcess tmpProcess = removeFromBlockedQ(pid);

    // Set it to READY status and move it to the Ready Q.
    tmpProcess.setStatus(RCOSProcess.READY);
    insertIntoReadyQ(tmpProcess);
  }

  /**
   * Remove the process from the executing queue, disassociate the terminal,
   * memory and other resources.
   */
  public void processFinished(RCOSProcess oldProcess)
  {
    //System.out.println("Process Finished: " + rpOldProcess.getPID());
    removeExecutingProcess(oldProcess.getPID());
    if (oldProcess.getTerminalId() != null)
    {
      TerminalRelease msg = new TerminalRelease(this,
        oldProcess.getTerminalId());
      sendMessage(msg);
    }

    // Deallocate Memory
    DeallocatePages msg = new DeallocatePages(this, oldProcess.getPID());
    sendMessage(msg);
  }

  /**
   * Seek and destroy and exsting process that is any of the queue or is
   * currently running.
   *
   * @param pid the unique identifier of the process id.
   */
  public void killProcess(int pid)
  {
    if (runningProcess())
    {
      if (getExecutingProcess().getPID() == pid)
      {
        KillProcess kpMessage = new KillProcess(this, pid, true);
        //Resend to kernel if okay
        sendMessage(kpMessage);
      }
      else
      {
        // If it's blocked, zombie or ready kill it.
        RCOSProcess rpTmpProcess = removeFromReadyQ(pid);
        if (rpTmpProcess == null)
        {
          rpTmpProcess = removeFromBlockedQ(pid);
        }
        if (rpTmpProcess == null)
        {
          rpTmpProcess = removeFromZombieCreatedQ(pid);
        }
      }
    }
  }

  /**
   * Create a new process. P-code processes that fork will duplicate their
   * stack and text, retaining any handles to shared memory, semiphores and
   * open streams.  Not yet implemented.
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
   * Executed every cycle to make sure that if there is not a currently
   * executing process that one is taken from the ready queue.
   */
  public void schedule()
  {
    //System.out.println("----- Start Schuduling-----");
    //Make sure that there isn't a process currently running.
    //Check to see if there are processes ready to execute.
    if (!runningProcess())
    {
      if (!readyQ.queueEmpty())
      {
        RCOSProcess currentProcess = (RCOSProcess) readyQ.retrieve();

        //System.out.println("Running: " + rpCurrentProcess.getPID());
        setExecutingProcess(currentProcess);
        currentProcess.setStatus(RCOSProcess.RUNNING);

        ProcessSwitch tmpMessage = new ProcessSwitch(this, currentProcess);
        sendMessage(tmpMessage);
        //System.out.println("Running: " + rpCurrentProcess.getPID());
      }
      else
      {
        NullProcess tmpMessage = new NullProcess(this);
        sendMessage(tmpMessage);
      }
    }
    //else
      //System.out.println("Already running!");
    //System.out.println("-----End Schuduling-----");
  }

  public void processMessage(OSMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }

  public void processMessage(UniversalMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.err.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }
}
