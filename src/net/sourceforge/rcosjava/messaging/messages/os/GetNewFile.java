package net.sourceforge.rcosjava.messaging.messages.os;


import net.sourceforge.rcosjava.software.process.ProgramManager;
import net.sourceforge.rcosjava.messaging.messages.universal.NewProcess;
import net.sourceforge.rcosjava.software.interrupt.ProgManInterruptHandler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Starts a new thread to load the file in and run the scheduler.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th March 1996
 */
public class GetNewFile extends OSMessageAdapter
{
  public GetNewFile(OSMessageHandler theSource)
  {
    super(theSource);
  }

  public void doMessage(ProgramManager theElement)
  {
    theElement.startThread();
  }

  public boolean undoableMessage()
  {
    return false;
  }
}
