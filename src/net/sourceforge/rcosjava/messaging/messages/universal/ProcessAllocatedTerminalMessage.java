package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.software.process.RCOSProcess;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.software.terminal.TerminalManager;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Used to assign a terminal to the next waiting process.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
public class ProcessAllocatedTerminalMessage extends UniversalMessageAdapter
{
  private String terminalId;
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
   */
  public boolean undoableMessage()
  {
    return false;
  }
}

