package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.process.ProcessManagerAnimator;
import org.rcosjava.software.process.ProcessPriority;
import org.rcosjava.software.process.RCOSProcess;

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
   * The process.
   */
  private RCOSProcess process;

  /**
   * The priority of the process.
   */
  private ProcessPriority priority;

  /**
   * Constructor for the ReturnProcessPriority object
   *
   * @param theSource who sent the message.
   * @param newProcess process to get the priority for.
   * @param newPriority the priority of the process.
   */
  public ReturnProcessPriority(OSMessageHandler theSource,
      RCOSProcess newProcess, ProcessPriority newPriority)
  {
    super(theSource);
    process = newProcess;
    priority = newPriority;
  }

  /**
   * Calls returnProcessPriority on the process manager animator.
   *
   * @param theElement the object to do work on.
   */
  public void doMessage(ProcessManagerAnimator theElement)
  {
    theElement.returnProcessPriority(process, priority);
  }
}