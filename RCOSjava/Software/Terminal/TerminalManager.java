//**************************************************************************
// FILE    : TerminalManager.java
// PURPOSE : Manages the allocation and deallocation of terminals
//           for rcos.java
// AUTHOR  : David Jones
// MODIFIED: Andrew Newman
// HISTORY : 28/03/96  Created - DJ.
//           01/12/96  Added the ability to add and remove
//                     a terminal - AN.
//           07/12/96  Added the ability to show terminals
//                     or hide them - AN.
//
//**************************************************************************

package Software.Terminal;

import Hardware.Terminal.HardwareTerminal;
import MessageSystem.Messages.MessageAdapter;
import MessageSystem.PostOffices.MessageHandler;
import MessageSystem.Messages.OS.OSMessageAdapter;
import MessageSystem.PostOffices.OS.OSMessageHandler;
import MessageSystem.PostOffices.OS.OSOffice;
import MessageSystem.Messages.Universal.ProcessAllocatedTerminalMessage;
import MessageSystem.Messages.Universal.UniversalMessageAdapter;
import Software.Util.TerminalQueue;
import Software.Util.FIFOQueue;

public class TerminalManager extends OSMessageHandler
{
  private TerminalQueue tqAllocatedTerminals, tqUnallocatedTerminals;
  private FIFOQueue fqWaitingProcesses;
  private int iMaxTerminals;
  private boolean[] bTerminalOn;
  private final String sTerminalPrefix = "Terminal#";
	private static final String MESSENGING_ID = "TerminalManager";

  // - accept maximum number of terminals able to be created,
  //   TerminalManagers Id and a point to the PostOffice.

  public TerminalManager (OSOffice aPostOffice,
                          int iNumberOfTerminals)
  {
    super(MESSENGING_ID, aPostOffice);

    tqAllocatedTerminals = new TerminalQueue(iNumberOfTerminals,1);
    tqUnallocatedTerminals = new TerminalQueue(iNumberOfTerminals,1);
    fqWaitingProcesses = new FIFOQueue(5,1);

    iMaxTerminals = iNumberOfTerminals+1;
    bTerminalOn = new boolean[iMaxTerminals];

    for (int iCount = 1; iCount < iMaxTerminals; iCount++)
    {
      bTerminalOn[iCount] = false;
    }
  }

  public int getMaxTerminals()
  {
    return iMaxTerminals;
  }

  public boolean terminalOn(int iTerminalNo)
  {
    return (bTerminalOn[iTerminalNo]);
  }

  //*************************
  // AllocateWaitingProcesses
  // - checks to see if there are any processes waiting for
  //   a terminal and if so gives them a terminal.

  public void allocateWaitingProcesses()
  {
    if (!fqWaitingProcesses.queueEmpty())
    {
      // there's a process waiting on a free terminal so allocate it

      Integer tmp = (Integer)
        fqWaitingProcesses.retrieve();
      allocateTerminal(tmp.intValue());
    }
  }

  public void allocateTerminal(int iProcessID)
  {
    HardwareTerminal htNewTerminal = (HardwareTerminal)
      tqUnallocatedTerminals.retrieve();
    htNewTerminal.setCurrentProcessID(iProcessID);
    tqAllocatedTerminals.insert(htNewTerminal);
    ProcessAllocatedTerminalMessage mMsg = new ProcessAllocatedTerminalMessage
      (this, htNewTerminal.getTitle(), iProcessID);
    sendMessage(mMsg);
  }

  //**********************************************
  // addTerminal
  // - called to add one more terminal with a
  //   specific terminal number.
  // - returns true if the action succeeded.

  public boolean addTerminal (int terminalNumber)
  {
    if ((terminalNumber <= iMaxTerminals)  && (terminalNumber >= 0))
    {
      if (!bTerminalOn[terminalNumber])
      {
        bTerminalOn[terminalNumber] = true;
        String terminalID = new String(sTerminalPrefix +
          String.valueOf(terminalNumber));
        HardwareTerminal newTerminal = new
          HardwareTerminal(terminalID, mhThePostOffice);
        tqUnallocatedTerminals.insert(newTerminal);
        allocateWaitingProcesses();
        return true;
      }
    }
    return false;
  }

  //**********************************************
  // removeTerminal
  // - called to remove one terminal by terminal number.
  // - only removes terminals that aren't allocated.
  // - returns true if operation succeeded.

  public boolean removeTerminal(int iTerminalID)
  {
    String sTerminalID = sTerminalPrefix + iTerminalID;
    return(removeTerminal(sTerminalID, iTerminalID));
  }

  public boolean removeTerminal(String sTerminalID)
  {
    int iTerminalID = Integer.parseInt(sTerminalID.substring(sTerminalPrefix.length()));
    return(removeTerminal(sTerminalID, iTerminalID));
  }

  private boolean removeTerminal(String sTerminalID, int iTerminalID)
  {
    if ((iTerminalID <= iMaxTerminals)  && (iTerminalID >= 0))
    {
      if (bTerminalOn[iTerminalID])
      {
        HardwareTerminal htTmp = (HardwareTerminal)
          tqUnallocatedTerminals.retrieve(sTerminalID,true);
        if (htTmp != null)
        {
          htTmp.dispose();
          bTerminalOn[iTerminalID] = false;
          return true;
        }
      }
    }
    return false;
  }

  public int setNextTerminal(String sTheSource)
  {
    int count = 1;
    boolean bOkay = false;
    while ((count < getMaxTerminals()) && (!bOkay))
    {
      if (!bTerminalOn[count])
      {
        bOkay = addTerminal(count);
      }
      count++;
    }
    return (count-1);
  }

  //**********************************************
  // viewTerminal
  // - called to view an active terminal.
  // - returns true if okay

  public boolean viewTerminal(int terminalNumber, boolean bMaximise)
  {
    if ((terminalNumber <= iMaxTerminals)  && (terminalNumber >= 0))
    {
      if (bTerminalOn[terminalNumber])
      {
        String terminalID = new String (sTerminalPrefix +
          String.valueOf(terminalNumber));
        HardwareTerminal tmp = (HardwareTerminal)
          tqUnallocatedTerminals.retrieve(terminalID, false);
        if (tmp == null)
        {
          tmp = (HardwareTerminal)
            tqAllocatedTerminals.retrieve(terminalID, false);
        }
        if (tmp != null)
        {
          if (bMaximise)
            tmp.setVisible(true);
          else
            tmp.setVisible(false);
          return true;
        }
      }
    }
    return false;
  }

  //**********************************************
  // dispose
  // - called to tidy up, in particular get rid of windows

  public void disposeFrame()
  {
    HardwareTerminal tmp = (HardwareTerminal) tqAllocatedTerminals.retrieve();

    while (tmp != null)
    {
      tmp.dispose();
      tmp = (HardwareTerminal) tqAllocatedTerminals.retrieve();
    }

    tmp = (HardwareTerminal) tqUnallocatedTerminals.retrieve();

    while (tmp != null)
    {
      tmp.dispose();
      tmp = (HardwareTerminal) tqUnallocatedTerminals.retrieve();
    }
  }

  public void getTerminal(int iProcessID)
  {
    if (tqUnallocatedTerminals.queueEmpty())
    {
      // Oops, no terminals are avaialable
      // place PID in waitingProcess Queue
      fqWaitingProcesses.insert(new Integer(iProcessID));
    }
    else
    {
      allocateTerminal(iProcessID);
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
}
