package net.sourceforge.rcosjava.software.terminal;

import net.sourceforge.rcosjava.hardware.terminal.HardwareTerminal;
import net.sourceforge.rcosjava.messaging.messages.MessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.MessageHandler;
import net.sourceforge.rcosjava.messaging.messages.os.OSMessageAdapter;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSOffice;
import net.sourceforge.rcosjava.messaging.messages.os.AllocateTerminal;
import net.sourceforge.rcosjava.messaging.messages.universal.ProcessAllocatedTerminalMessage;
import net.sourceforge.rcosjava.messaging.messages.universal.UniversalMessageAdapter;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalOn;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalOff;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalFront;
import net.sourceforge.rcosjava.messaging.messages.universal.TerminalBack;
import net.sourceforge.rcosjava.software.util.TerminalQueue;
import net.sourceforge.rcosjava.software.util.FIFOQueue;

/**
 * Manages the allocation and deallocation of terminals for the system.
 * Processes waiting for an allocated terminal wait in a FIFO queue maintained
 * by this manager.  Two TerminalQueue objects hold a list of all the allocated
 * and deallocated terminals.  It possible that one or the other can be empty
 * at any time.  The system holds the allocation of terminals in a separate
 * array.
 * <P>
 * HISTORY: 01/12/96  Added the ability to add and remove a terminal - AN.<BR>
 *          07/12/96  Added the ability to show terminals or hide them - AN.<BR>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @version 1.00 $Date$
 * @created 28th March 1997
 */
 public class TerminalManager extends OSMessageHandler
{
  private TerminalQueue allocatedTerminals, unallocatedTerminals;
  private FIFOQueue waitingProcesses;
  private int maxTerminals;
  private boolean[] terminalOn;
  private final String terminalPrefix = "Terminal#";
  private static final String MESSENGING_ID = "TerminalManager";

  /**
   * Accept maximum number of terminals able to be created, TerminalManagers
   * Id and a point to the PostOffice.
   *
   * @param newPostOffice the post office to register to.
   * @param newNumberOfTerminals the number of terminals to create and keep
   * track of.
   */
  public TerminalManager (OSOffice newPostOffice, int newNumberOfTerminals)
  {
    super(MESSENGING_ID, newPostOffice);

    allocatedTerminals = new TerminalQueue(newNumberOfTerminals, 1);
    unallocatedTerminals = new TerminalQueue(newNumberOfTerminals, 1);
    waitingProcesses = new FIFOQueue(5,1);

    maxTerminals = newNumberOfTerminals+1;
    terminalOn = new boolean[maxTerminals];

    for (int iCount = 1; iCount < maxTerminals; iCount++)
    {
      terminalOn[iCount] = false;
    }
  }

  /**
   * Turns the given terminal on.  Called by TerminalOn message.  If 0 will
   * turn the next available terminal on.
   */
  public void terminalOn(int terminalNo)
  {
    if (terminalNo == 0)
    {
      //Will call terminal on with a number if found.
      addNextTerminal();
    }
    else
    {
      if (addTerminal(terminalNo))
      {
        //Terminal On
        TerminalOn toMessage = new TerminalOn(this, terminalNo, true);
        sendMessage(toMessage);
      }
    }
  }

  /**
   * Turns the given terminal off.  Called by TerminalOff message.
   */
  public void terminalOff(int terminalNo)
  {
    if (removeTerminal(terminalNo))
    {
      //Terminal Off
      TerminalOff toMessage = new TerminalOff(this, terminalNo, true);
      sendMessage(toMessage);
    }
  }

  /**
   * Set the given terminal to the front.  Called by the terminal front message.
   */
  public void terminalFront(int terminalNo)
  {
    if (viewTerminal(terminalNo, true))
    {
      TerminalFront tmpMsg = new TerminalFront(this, terminalNo, true);
      this.sendMessage(tmpMsg);
    }
  }

  /**
   * Called by terminal back message.  Terminal no is the terminal to hide.
   */
  public void terminalBack(int terminalNo)
  {
    if (viewTerminal(terminalNo, false))
    {
      TerminalBack tmpMsg = new TerminalBack(this, terminalNo, true);
      this.sendMessage(tmpMsg);
    }
  }

  /**
   * Toggles the given terminal number either on or off.
   */
  public void terminalToggle(int terminalNo)
  {
    if (isTerminalOn(terminalNo))
    {
      terminalOff(terminalNo);
    }
    else
    {
      terminalOn(terminalNo);
    }
  }

  /**
   * Returns the maximum number of terminal allocatable.
   */
  public int getMaxTerminals()
  {
    return maxTerminals;
  }

  /**
   * Returns whether the given terminal number is on.  Terminal numbers begin
   * at 1.
   */
  public boolean isTerminalOn(int terminalNo)
  {
    return (terminalOn[terminalNo]);
  }

  /**
   * Called to remove one terminal by terminal number only removes terminals
   * that are allocated.  Calls the string version with a defined prefix.
   *
   * @param terminalId the terminal to remove.
   * @return true if the operating succeeded.
   */
  public boolean removeTerminal(int terminalId)
  {
    String terminalIdStr = terminalPrefix + terminalId;
    return(removeTerminal(terminalIdStr, terminalId));
  }

  /**
   * Called to remove one terminal by terminal string identifier.  Only removes
   * terminals that are allocated.
   *
   * @param terminalIdStr the string name of the terminal.
   * @return whether the operation was successful.
   */
  public boolean removeTerminal(String terminalIdStr)
  {
    int terminalId = Integer.parseInt(
      terminalIdStr.substring(terminalPrefix.length()));
    return(removeTerminal(terminalIdStr, terminalId));
  }

  /**
   * Called to bring the terminal into focus.  Called by @see TerminalBack and
   * @see TerminalFont messages.
   *
   * @param terminalNumber the terminal to display.
   * @param windowVisible set the window to display/not display.
   * @return whether the operation was successful.
   */
  private boolean viewTerminal(int terminalNumber, boolean windowVisible)
  {
    if ((terminalNumber <= maxTerminals)  && (terminalNumber >= 0))
    {
      if (terminalOn[terminalNumber])
      {
        String terminalIdStr = new String (terminalPrefix +
          String.valueOf(terminalNumber));
        HardwareTerminal tmp = (HardwareTerminal)
          unallocatedTerminals.retrieve(terminalIdStr, false);
        if (tmp == null)
        {
          tmp = (HardwareTerminal)
            allocatedTerminals.retrieve(terminalIdStr, false);
        }
        if (tmp != null)
        {
          tmp.setVisible(windowVisible);
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Called to tidy up, in particular get rid of windows
   */
  public void disposeFrame()
  {
    HardwareTerminal tmp = (HardwareTerminal) allocatedTerminals.retrieve();

    while (tmp != null)
    {
      tmp.dispose();
      tmp = (HardwareTerminal) allocatedTerminals.retrieve();
    }

    tmp = (HardwareTerminal) unallocatedTerminals.retrieve();

    while (tmp != null)
    {
      tmp.dispose();
      tmp = (HardwareTerminal) unallocatedTerminals.retrieve();
    }
  }

  /**
   * Attemps to get a terminal for a process.  If there are none left then the
   * process is put in a queue.  Currently there is no way to ask for the
   * terminal to be removed from the queue.
   *
   * @param processId the process requiring a terminal
   */
  public void getTerminal(int processId)
  {
    if (unallocatedTerminals.queueEmpty())
    {
      // Oops, no terminals are avaialable
      // place PID in waitingProcess Queue
      waitingProcesses.insert(new Integer(processId));
    }
    else
    {
      AllocateTerminal allocateTerminal = new AllocateTerminal(this, processId);
      sendMessage(allocateTerminal);
    }
  }

  /**
   * Attempts to allocate a given terminal.
   *
   * @param processId the process to allocate the a terminal to.
   */
  public void allocateTerminal(int processId)
  {
    HardwareTerminal newTerminal = (HardwareTerminal)
      unallocatedTerminals.retrieve();
    newTerminal.setCurrentProcessId(processId);
    allocatedTerminals.insert(newTerminal);
    ProcessAllocatedTerminalMessage msg = new ProcessAllocatedTerminalMessage
      (this, newTerminal.getTitle(), processId);
    sendMessage(msg);
  }

  /**
   * Checks to see if there are any processes waiting for a terminal and if so
   * gives them a terminal.
   */
  private void allocateWaitingProcesses()
  {
    if (!waitingProcesses.queueEmpty())
    {
      // there's a process waiting on a free terminal so allocate it
      Integer tmp = (Integer)
        waitingProcesses.retrieve();
      AllocateTerminal allocateTerminal = new AllocateTerminal(this, tmp.intValue());
      sendMessage(allocateTerminal);
    }
  }

  /**
   * Sets the next terminal available to on.
   */
  private boolean addNextTerminal()
  {
    int count = 1;
    if (allocatedTerminals.size() != getMaxTerminals())
    {
      while (count < getMaxTerminals())
      {
        if (!terminalOn[count])
        {
          if(addTerminal(count))
          {
            return true;
          }
        }
        count++;
      }
    }
    return false;
  }

  /**
   * Called to add one more terminal with a specific terminal number.
   *
   * @return true if the action succeeded.
   */
  private boolean addTerminal(int terminalNumber)
  {
    if ((terminalNumber <= maxTerminals)  && (terminalNumber >= 0))
    {
      if (!terminalOn[terminalNumber])
      {
        terminalOn[terminalNumber] = true;
        String terminalId = new String(terminalPrefix +
          String.valueOf(terminalNumber));
        HardwareTerminal newTerminal = new
          HardwareTerminal(terminalId, postOffice);
        unallocatedTerminals.insert(newTerminal);
        allocateWaitingProcesses();
        return true;
      }
    }
    return false;
  }

  /**
   * Attempt to remove a given terminal.
   *
   * @param terminalIdStr the terminal string identifier of the terminal.
   * @param terminalId the number identifier of the terminal.
   * @return boolean if the operation was successful.
   */
  private boolean removeTerminal(String terminalIdStr, int terminalId)
  {
    if ((terminalId <= maxTerminals)  && (terminalId >= 0))
    {
      if (terminalOn[terminalId])
      {
        HardwareTerminal tmpTerminal = (HardwareTerminal)
          unallocatedTerminals.retrieve(terminalIdStr, true);
        if (tmpTerminal != null)
        {
          tmpTerminal.dispose();
          terminalOn[terminalId] = false;
          return true;
        }
      }
    }
    return false;
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
