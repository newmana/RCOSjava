package MessageSystem.Messages.OS;

import Software.Process.ProgramManager;
import MessageSystem.Messages.Universal.NewProcess;
import Software.Interrupt.ProgManInterruptHandler;
import MessageSystem.PostOffices.OS.OSMessageHandler;

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
