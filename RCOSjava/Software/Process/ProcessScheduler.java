//********************************************************************/
// FILE     : ProcessScheduler.java
// PACKAGE  : Process
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// PURPOSE  : Management of processes for RCOS.java
// HISTORY  : 24/03/96  Created. DJ
//            01/01/97  First process set to 1. AN
//            05/01/97  Messages sent to Animators. AN
//            05/04/97  Problems with the order that the messages are
//                      sent fixed. AN
//            07/04/97  Kills processes from all Queues. AN
//            08/08/98  Allocate by pages instead of bytes (always). AN
//            11/11/98  Added fork() (should be right place). AN
//
//********************************************************************/

package Software.Process;

import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.Messages.Universal.NewProcess;
import MessageSystem.Messages.OS.AllocatePages;
import MessageSystem.Messages.OS.DeallocatePages;
import MessageSystem.Messages.OS.TerminalRelease;
import MessageSystem.Messages.Universal.GetTerminal;
import MessageSystem.Messages.Universal.KillProcess;
import MessageSystem.Messages.Universal.NullProcess;
import MessageSystem.Messages.Universal.ProcessSwitch;
import MessageSystem.Messages.Universal.WriteBytes;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import MessageSystem.PostOffices.OS.OSOffice;
import Software.Memory.MemoryManager;
import Software.Memory.MemoryReturn;
import Software.Memory.MemoryRequest;
import Software.Util.ProcessQueue;

public class ProcessScheduler extends OSMessageHandler
{
  public static final int READYQ = 1;
  public static final int BLOCKEDQ = 2;
  public static final int ZOMBIEQ = 3;
  private static ProcessQueue pqZombieCreatedQ, pqZombieDeadQ, pqReadyQ, pqBlockedQ;
  private static ProcessQueue pqExecutingQ;
  private static final String MESSENGING_ID = "ProcessScheduler";
  private static int iCurrentPID;

  public ProcessScheduler(OSOffice aPostOffice)
  {
    super(MESSENGING_ID, aPostOffice);

    pqZombieCreatedQ = new ProcessQueue(10,1);
    pqZombieDeadQ = new ProcessQueue(10,1);
    pqBlockedQ = new ProcessQueue(10,1);
    pqReadyQ = new ProcessQueue(10,1);
    pqExecutingQ = new ProcessQueue(10,1);
    iCurrentPID = 1;
 }

  // Return next allocated Process ID.
  // Simply increment current counter by one but
  // could be improved upon to reuse PIDs.
  private int getNextPID()
  {
    return iCurrentPID++;
  }

  private void insertIntoZombieCreatedQ(RCOSProcess rpZombieProcess)
  {
    System.out.println("Insert into zombie created q: " + rpZombieProcess.getPID());
    pqZombieCreatedQ.insert(rpZombieProcess);
  }

  private RCOSProcess removeFromZombieCreatedQ(int iPID)
  {
    System.out.println("Remove from zombie created q: " + iPID);
    return (RCOSProcess) pqZombieCreatedQ.getProcess(iPID);
  }

  private void insertIntoBlockedQ(RCOSProcess rpBlockedProcess)
  {
    System.out.println("Insert into blocked q: " + rpBlockedProcess.getPID());
    pqBlockedQ.insert(rpBlockedProcess);
  }

  private RCOSProcess removeFromBlockedQ(int iPID)
  {
    System.out.println("Remove from blocked q: " + iPID);
    return (RCOSProcess) pqBlockedQ.getProcess(iPID);
  }

  private void insertIntoReadyQ(RCOSProcess rpReadyProcess)
  {
    System.out.println("Insert into ready q: " + rpReadyProcess.getPID());
    pqReadyQ.insert(rpReadyProcess);
    System.out.println("Ready Q size: " + pqReadyQ.size());
    System.out.println("First element: " + ((RCOSProcess) pqReadyQ.peek(0)).getPID());
    if (pqReadyQ.size() > 1)
      System.out.println("2nd element: " + ((RCOSProcess) pqReadyQ.peek(1)).getPID());
  }

  private RCOSProcess removeFromReadyQ(int iPID)
  {
    System.out.println("Remove from ready q: " + iPID);
    return (RCOSProcess) pqReadyQ.getProcess(iPID);
  }

  //For multiple CPU we could have an index here.  At the moment on CPU is
  //assumed.
  public RCOSProcess getExecutingProcess()
  {
    return (RCOSProcess) pqExecutingQ.peek();
  }

  public void setExecutingProcess(RCOSProcess newProcess)
  {
    pqExecutingQ.insert(newProcess);
  }

  public void setCurrentProcessNull()
  {
    pqExecutingQ.removeAllElements();
  }

  private RCOSProcess removeExecutingProcess(int iPID)
  {
    return (RCOSProcess) pqExecutingQ.getProcess(iPID);
  }

  //True if running process
  public boolean runningProcess()
  {
    return pqExecutingQ.size() >= 1;
  }

  public void newProcess(NewProcess msgBody)
  {
    // The message contains info about a process to be
    // created including file size, file name and code.
    // Get new Process ID.
    int iMyNewPID = getNextPID();

    // Create new RCOSProcess.
    RCOSProcess rpNewProc = new RCOSProcess(iMyNewPID, msgBody);

    MemoryRequest mrNewMemory;
    int iNoPages;
    // Allocate memory for code by pages
    iNoPages = (int) (rpNewProc.getFileSize() / MemoryManager.PAGE_SIZE) + 1;
    mrNewMemory = new MemoryRequest(iMyNewPID, MemoryManager.CODE_SEGMENT,
      MemoryManager.CODE_SEGMENT, iNoPages);
    AllocatePages allocMsg = new AllocatePages(this, mrNewMemory);
    sendMessage(allocMsg);

    // Write code to allocated bytes - should check for successful
    // allocation really.
    mrNewMemory = new MemoryRequest(iMyNewPID, MemoryManager.CODE_SEGMENT,
      rpNewProc.getFileSize(), msgBody.getMemory());
    WriteBytes wbMsg = new WriteBytes(this, mrNewMemory);
    sendMessage(wbMsg);

    // Allocate memory for stack
    mrNewMemory = new MemoryRequest(iMyNewPID, MemoryManager.STACK_SEGMENT,
      MemoryManager.STACK_SEGMENT, rpNewProc.getStackPages());
    allocMsg = new AllocatePages(this, mrNewMemory);
    sendMessage(allocMsg);

    // Save this process in Zombie Q
    insertIntoZombieCreatedQ(rpNewProc);

    // Ask for new terminal for the new process.
    GetTerminal gtMsg = new GetTerminal(this, iMyNewPID);
    sendMessage(gtMsg);
  }

  public void processAllocatedTerminal(int newPID, String newTerminalID)
  {
    // Try and retrieve the next process in the ZombieQ
    RCOSProcess rpNewProc = (RCOSProcess) removeFromZombieCreatedQ(newPID);

    // If a process is retrieve allocate terminal and move
    // to Ready Q.
    if (rpNewProc != null)
    {
      rpNewProc.setTerminalID(newTerminalID);
      rpNewProc.setStatus(RCOSProcess.READY);
      insertIntoReadyQ(rpNewProc);
    }

    // If there is no current process running schedule
    // process to be run.
    schedule();
  }

  public void runningToReady(RCOSProcess newProcess)
  {
    RCOSProcess rm = removeExecutingProcess(newProcess.getPID());
    System.out.println("New Process: " + newProcess.getPID());
    System.out.println("Removed: " + rm.getPID());
    newProcess.setStatus(RCOSProcess.READY);
    insertIntoReadyQ(newProcess);
    schedule();
  }

  public void runningToBlocked(RCOSProcess newProcess)
  {
    removeExecutingProcess(newProcess.getPID());
    newProcess.setStatus(RCOSProcess.BLOCKED);
    insertIntoBlockedQ(newProcess);
    schedule();
  }

  public void blockedToReady(int iPID)
  {
    // Get the process from the blocked Q
    RCOSProcess rpProcess = removeFromBlockedQ(iPID);

    // Set it to READY status and move it to the Ready Q.
    rpProcess.setStatus(RCOSProcess.READY);
    insertIntoReadyQ(rpProcess);

    schedule();
  }

  public void processFinished(RCOSProcess rpOldProcess)
  {
    System.out.println("Process Finished: " + rpOldProcess.getPID());
    removeExecutingProcess(rpOldProcess.getPID());
    if (rpOldProcess.getTerminalID() != null)
    {
      TerminalRelease msg = new TerminalRelease(this,
        rpOldProcess.getTerminalID());
      sendMessage(msg);
    }

    // Deallocate Memory
    DeallocatePages msg = new DeallocatePages(this, rpOldProcess.getPID());
    sendMessage(msg);

    // Set current process to null
    schedule();
  }

  public void killProcess(int iPID)
  {
    if (runningProcess())
    {
      if (getExecutingProcess().getPID() == iPID)
      {
        KillProcess kpMessage = new KillProcess(this, iPID, true);
        //Resend to kernel if okay
        sendMessage(kpMessage);
      }
      else
      {
        // If it's blocked, zombie or ready kill it.
        RCOSProcess rpTmpProcess = removeFromReadyQ(iPID);
        if (rpTmpProcess == null)
        {
          rpTmpProcess = removeFromBlockedQ(iPID);
        }
        if (rpTmpProcess == null)
        {
          rpTmpProcess = removeFromZombieCreatedQ(iPID);
        }
      }
    }
  }

  // Create a new process. P-code processes that
  // fork will duplicate their stack and text,
  // retaining any handles to shared memory, semiphores
  // and open streams.
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

  //public synchronized void schedule()
  public void schedule()
  {
    System.out.println("----- Start Schuduling-----");
    //Make sure that there isn't a process currently running.
    //Check to see if there are processes ready to execute.
    if (!runningProcess())
    {
      if (!pqReadyQ.queueEmpty())
      {
        RCOSProcess rpCurrentProcess = (RCOSProcess) pqReadyQ.retrieve();

        System.out.println("Running: " + rpCurrentProcess.getPID());
        setExecutingProcess(rpCurrentProcess);
        rpCurrentProcess.setStatus(RCOSProcess.RUNNING);

        ProcessSwitch aMsg = new ProcessSwitch(this,
          rpCurrentProcess);
        sendMessage(aMsg);
        System.out.println("Running: " + rpCurrentProcess.getPID());
      }
      else
      {
        NullProcess osmMsg = new NullProcess(this);
        sendMessage(osmMsg);
      }
    }
    else
      System.out.println("Already running!");
    System.out.println("-----End Schuduling-----");
  }

  public void processMessage(OSMessageAdapter aMsg)
  {
    try
    {
      aMsg.doMessage(this);
    }
    catch (Exception e)
    {
      System.out.println("Error processing message: "+e);
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
      System.out.println("Error processing message: "+e);
      e.printStackTrace();
    }
  }
}
