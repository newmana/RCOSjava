package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.process.ProcessScheduler;

/**
 * Made by the kernel every instruction cycle to ensure schedule is called in
 * process scheduler.
 * <P>
 * @author Andrew Newman.
 * @created 28th March 1997
 * @version 1.00 $Date$
 */
public class Schedule extends OSMessageAdapter
{
  /**
   * Constructor for the Schedule object
   *
   * @param theElement Description of Parameter
   */
  public Schedule(OSMessageHandler theElement)
  {
    super(theElement);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.schedule();
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return false;
  }
}

