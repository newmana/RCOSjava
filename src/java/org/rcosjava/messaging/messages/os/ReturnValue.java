package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.kernel.Kernel;

/**
 * Message sent by several operating system components when a system level event
 * (characters from a terminal, semaphores, etc) need to return a value back to
 * the kernel.
 * <P>
 * @author Andrew Newman.
 * @created 28th of March 1996
 * @version 1.00 $Date$
 */
public class ReturnValue extends OSMessageAdapter
{
  /**
   * Description of the Field
   */
  private short returnValue;

  /**
   * Constructor for the ReturnValue object
   *
   * @param theSource Description of Parameter
   * @param newReturnValue Description of Parameter
   */
  public ReturnValue(OSMessageHandler theSource, short newReturnValue)
  {
    super(theSource);
    returnValue = newReturnValue;
  }

  /**
   * Sets the ReturnValue attribute of the ReturnValue object
   *
   * @param newReturnValue The new ReturnValue value
   */
  public void setReturnValue(short newReturnValue)
  {
    returnValue = newReturnValue;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(Kernel theElement)
  {
    theElement.returnValue(returnValue);
  }
}
