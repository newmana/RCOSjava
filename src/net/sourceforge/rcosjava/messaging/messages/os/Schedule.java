package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Made by the kernel every instruction cycle to ensure schedule is called in
 * process scheduler.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1997
 */
public class Schedule extends OSMessageAdapter
{
  public Schedule(OSMessageHandler theElement)
  {
    super(theElement);
  }

  public void doMessage(ProcessScheduler theElement)
  {
    theElement.schedule();
  }

  public boolean undoableMessage()
  {
    return false;
  }
}

