package org.rcosjava.software.terminal;

import java.io.*;
import org.rcosjava.RCOS;
import org.rcosjava.hardware.terminal.HardwareTerminal;
import org.rcosjava.messaging.messages.os.AllocateTerminal;
import org.rcosjava.messaging.messages.universal.ProcessAllocatedTerminalMessage;
import org.rcosjava.messaging.messages.universal.TerminalBack;
import org.rcosjava.messaging.messages.universal.TerminalFront;
import org.rcosjava.messaging.messages.universal.TerminalOff;
import org.rcosjava.messaging.messages.universal.TerminalOn;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.messaging.postoffices.os.OSOffice;
import org.rcosjava.software.process.RCOSProcess;
import org.rcosjava.software.util.FIFOQueue;
import org.rcosjava.software.util.TerminalQueue;

/**
 * Manages the allocation and deallocation of terminals for the system.
 * Processes waiting for an allocated terminal wait in a FIFO queue maintained
 * by this manager. Two TerminalQueue objects hold a list of all the allocated
 * and deallocated terminals. It possible that one or the other can be empty at
 * any time. The system holds the allocation of terminals in a separate array.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 01/12/96 Added the ability to add and remove a terminal - AN. </DD>
 * <DD> 07/12/96 Added the ability to show terminals or hide them - AN. </DD>
 * </DT>
 * <P>
 * @author Andrew Newman.
 * @author David Jones.
 * @created 28th March 1997
 * @version 1.00 $Date$
 */
public class TerminalManager extends OSMessageHandler
{
  /**
   * The name of the terminal manager.
   */
  private final static String MESSENGING_ID = "TerminalManager";

  /**
   * The collection of terminals that have been allocated to a terminal.
   */
  private TerminalQueue allocatedTerminals;

  /**
   * The collection of termianls that have not been allocated a terminal.
   */
  private TerminalQueue unallocatedTerminals;

  /**
   * A collections of processes waiting to be allocated a terminal.
   */
  private FIFOQueue waitingProcesses;

  /**
   * The maximum number of terminals that are avaiable.
   */
  private int maxTerminals;

  /**
   * A simple array to track which terminals are "on" (viewable but not
   * necessarily allocated to a terminal).
   */
  private boolean[] terminalOn;

  /**
   * The string which prefixes the names of all the terminals (for the post
   * office).
   */
  private final String terminalPrefix = "Terminal#";

  /**
   * Accept maximum number of terminals able to be created, TerminalManagers Id
   * and a point to the PostOffice.
   *
   * @param newPostOffice the post office to register to.
   * @param newNumberOfTerminals the number of terminals to create and keep
   *      track of.
   */
  public TerminalManager(OSOffice newPostOffice, int newNumberOfTerminals)
  {
    super(MESSENGING_ID, newPostOffice);

    allocatedTerminals = new TerminalQueue(newNumberOfTerminals, 1);
    unallocatedTerminals = new TerminalQueue(newNumberOfTerminals, 1);
    waitingProcesses = new FIFOQueue(5, 1);

    maxTerminals = newNumberOfTerminals + 1;
    terminalOn = new boolean[maxTerminals];

    for (int count = 1; count < maxTerminals; count++)
    {
      terminalOn[count] = false;
    }
  }

  /**
   * Returns the maximum number of terminal allocatable.
   *
   * @return The MaxTerminals value
   */
  public int getMaxTerminals()
  {
    return maxTerminals;
  }

  /**
   * Returns whether the given terminal number is on. Terminal numbers begin at
   * 1.
   *
   * @param terminalNo Description of Parameter
   * @return The TerminalOn value
   */
  public boolean isTerminalOn(int terminalNo)
  {
    return (terminalOn[terminalNo]);
  }

  /**
   * Attemps to get a terminal for a process. If there are none left then the
   * process is put in a queue. Currently there is no way to ask for the
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
   * Turns the given terminal on. Called by TerminalOn message. If 0 will turn
   * the next available terminal on.
   *
   * @param terminalNo Description of Parameter
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
   * Turns the given terminal off. Called by TerminalOff message.
   *
   * @param terminalNo Description of Parameter
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
   * Set the given terminal to the front. Called by the terminal front message.
   *
   * @param terminalNo Description of Parameter
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
   * Called by terminal back message. Terminal no is the terminal to hide.
   *
   * @param terminalNo Description of Parameter
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
   *
   * @param terminalNo Description of Parameter
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
   * Called to remove one terminal by terminal number only removes terminals
   * that are allocated. Calls the string version with a defined prefix.
   *
   * @param terminalId the terminal to remove.
   * @return true if the operating succeeded.
   */
  public boolean removeTerminal(int terminalId)
  {
    String terminalIdStr = terminalPrefix + terminalId;

    return (removeTerminal(terminalIdStr, terminalId));
  }

  /**
   * Called to remove one terminal by terminal string identifier. Only removes
   * terminals that are allocated.
   *
   * @param terminalIdStr the string name of the terminal.
   * @return whether the operation was successful.
   */
  public boolean releaseTerminal(String terminalIdStr)
  {
    boolean succeeded = false;
    int terminalId = Integer.parseInt(
        terminalIdStr.substring(terminalPrefix.length()));
    HardwareTerminal tmpTerminal = (HardwareTerminal)
        allocatedTerminals.retrieve(terminalIdStr, true);

    if (tmpTerminal != null)
    {
      unallocatedTerminals.insert(tmpTerminal);
      allocateWaitingProcesses();
      succeeded = true;
    }
    return succeeded;
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
        (this, newTerminal.getTitle(), new RCOSProcess(processId, ""));
    sendMessage(msg);
  }

  /**
   * Called to bring the terminal into focus. Called by
   *
   * @param terminalNumber the terminal to display.
   * @param windowVisible set the window to display/not display.
   * @return whether the operation was successful.
   * @see org.rcosjava.messaging.messages.universal.TerminalBack and
   * @see org.rcosjava.messaging.messages.universal.TerminalFront messages.
   */
  private boolean viewTerminal(int terminalNumber, boolean windowVisible)
  {
    if ((terminalNumber <= maxTerminals) && (terminalNumber >= 0))
    {
      if (terminalOn[terminalNumber])
      {
        String terminalIdStr = new String(terminalPrefix +
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
   *
   * @return Description of the Returned Value
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
          if (addTerminal(count))
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
   * @param terminalNumber The feature to be added to the Terminal attribute
   * @return true if the action succeeded.
   */
  private boolean addTerminal(int terminalNumber)
  {
    if ((terminalNumber <= maxTerminals) && (terminalNumber >= 0))
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
    if ((terminalId <= maxTerminals) && (terminalId >= 0))
    {
      if (terminalOn[terminalId])
      {
        HardwareTerminal tmpTerminal;

        tmpTerminal = (HardwareTerminal)
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

  /**
   * Handle the serialization of the contents.
   */
  private void writeObject(ObjectOutputStream os) throws IOException
  {
    os.writeObject(waitingProcesses);
    os.writeInt(maxTerminals);

    for (int index = 1; index < terminalOn.length; index++)
    {
      os.writeBoolean(terminalOn[index]);
    }
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

    waitingProcesses = (FIFOQueue) is.readObject();
    maxTerminals = is.readInt();

    allocatedTerminals = new TerminalQueue(maxTerminals, 1);
    unallocatedTerminals = new TerminalQueue(maxTerminals, 1);

    terminalOn = new boolean[maxTerminals];
    for (int index = 1; index < maxTerminals; index++)
    {
      boolean tmpTerminalOn = is.readBoolean();

      if (tmpTerminalOn)
      {
        addTerminal(index);
      }
    }
  }
}
