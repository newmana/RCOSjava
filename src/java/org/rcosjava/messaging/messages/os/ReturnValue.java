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
   * The value to send.
   */
  private short returnValue;

  /**
   * Create a new ReturnValue message - an external process wishes to give a
   * value to the kernel.
   *
   * @param theSource the sender of the message.
   * @param newReturnValue the value to send.
   */
  public ReturnValue(OSMessageHandler theSource, short newReturnValue)
  {
    super(theSource);
    returnValue = newReturnValue;
  }

  /**
   * Sets the return value.
   *
   * @param newReturnValue The new return value.
   */
  public void setReturnValue(short newReturnValue)
  {
    returnValue = newReturnValue;
  }

  /**
   * Calls the return value on the kernel.
   *
   * @param theElement the kernel which has returnValue called.
   */
  public void doMessage(Kernel theElement)
  {
    theElement.returnValue(returnValue);
  }
}
