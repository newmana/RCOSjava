package net.sourceforge.rcosjava.messaging.messages.os;

import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.terminal.SoftwareTerminal;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Message sent by several operating system components when a system level
 * event (characters from a terminal, semaphores, etc) need to return a value
 * back to the kernel.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 28th of March 1996
 */
public class ReturnValue extends OSMessageAdapter
{
  private short returnValue;

  public ReturnValue(OSMessageHandler theSource, short newReturnValue)
  {
    super(theSource);
    returnValue = newReturnValue;
  }

  public void setReturnValue(short newReturnValue)
  {
    returnValue = newReturnValue;
  }

  public void doMessage(Kernel theElement)
  {
    theElement.returnValue(returnValue);
  }
}