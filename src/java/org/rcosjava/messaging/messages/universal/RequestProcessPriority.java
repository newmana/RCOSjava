package org.rcosjava.messaging.messages.universal;

import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.process.ProcessScheduler;

/**
 * Sent from Program Manager Animator to return an a processes priority.
 * <P>
 * @author Andrew Newman.
 * @created 29th April 2001
 * @version 1.00 $Date$
 */
public class RequestProcessPriority extends UniversalMessageAdapter
{
  /**
   * The process id to get the priority for.
   */
  private int processId;

  /**
   * Constructor for the RequestProcessPriority object
   *
   * @param theSource who sent the message.
   * @param newProcessId process id to get the priority for.
   */
  public RequestProcessPriority(AnimatorMessageHandler theSource,
      int newProcessId)
  {
    super(theSource);
    processId = newProcessId;
  }

  /**
   * Calls requestProcessPriority on Process Scheduler.
   *
   * @param theElement the object to do work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.requestProcessPriority(processId);
  }
}

