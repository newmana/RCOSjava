package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;

/**
 * Sent from Process Scheduler to return a process' priority.
 * <P>
 * @author Andrew Newman.
 * @created 29th April 2001
 * @version 1.00 $Date$
 */
public class ReturnProcessPriority extends UniversalMessageAdapter
{
  /**
   * The id of the process.
   */
  private int processId;

  /**
   * The priority of the process.
   */
  private int priority;

  /**
   * Constructor for the ReturnProcessPriority object
   *
   * @param theSource who sent the message.
   * @param newProcessId process id to get the priority for.
   * @param newPriority the priority of the process.
   */
  public ReturnProcessPriority(OSMessageHandler theSource, int newProcessId,
      int newPriority)
  {
    super(theSource);
    processId = newProcessId;
    priority = newPriority;
  }

  /**
   * Calls returnProcessPriority on the process manager animator.
   *
   * @param theElement the object to do work on.
   */
  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.returnProcessPriority(processId, priority);
  }
}