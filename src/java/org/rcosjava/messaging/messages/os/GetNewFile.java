package org.rcosjava.messaging.messages.os;

import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.process.ProgramManager;

/**
 * Starts a new thread to load the file in and run the scheduler.
 * <P>
 * @author Andrew Newman.
 * @created 28th March 1996
 * @version 1.00 $Date$
 */
public class GetNewFile extends OSMessageAdapter
{
  /**
   * Constructor for the GetNewFile object
   *
   * @param theSource Description of Parameter
   */
  public GetNewFile(OSMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(ProgramManager theElement)
  {
    //theElement.startThread();
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
