package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.terminal.TerminalManagerAnimator;
import org.rcosjava.software.process.ProcessScheduler;

/**
 * Used to assign a terminal to the next waiting process.
 * <P>
 * @author Andrew Newman.
 * @created 24th March 1996
 * @version 1.00 $Date$
 */
public class ProcessAllocatedTerminalMessage extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private String terminalId;
  /**
   * Description of the Field
   */
  private int pid;

  /**
   * Create a new process allocated terminal message.
   *
   * @param newSource the source of the message.
   * @param newTerminalId the terminal id to assign to the process.
   * @param newPID the process id that is assigned the terminal.
   */
  public ProcessAllocatedTerminalMessage(OSMessageHandler newSource,
      String newTerminalId, int newPID)
  {
    super(newSource);
    terminalId = newTerminalId;
    pid = newPID;
  }

  /**
   * Executes the terminalOn to indicate that the terminal has been allocated.
   * Takes the terminalId and expects that the last character is a number which
   * is the current terminal.
   *
   * @param theElement the terminal manager animator to do work on.
   */
  public void doMessage(TerminalManagerAnimator theElement)
  {
    theElement.terminalOn(Integer.parseInt(terminalId.substring(terminalId.length() - 1)));
  }

  /**
   * Executes the processAllocatedTerminal method passing the stored process id.
   *
   * @param theElement the process scheduler to do work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.processAllocatedTerminal(pid, terminalId);
  }

  /**
   * Set to false so it is not recorded.
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return false;
  }
}

