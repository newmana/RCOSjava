package org.rcosjava.messaging.messages.os;

import java.io.Serializable;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.interrupt.InterruptHandler;
import org.rcosjava.software.kernel.Kernel;

/**
 * Registers an interrupt handler with the kernel.
 *
 * @author Andrew Newman
 * @created 28 April 2002
 */
public class RegisterInterruptHandler extends OSMessageAdapter
{
  /**
   * The interrupt handler to register.
   */
  private InterruptHandler handler;

  /**
   * Create a new message from the given source.
   *
   * @param theSource the sender of the message.
   */
  public RegisterInterruptHandler(OSMessageHandler theSource,
      InterruptHandler newHandler)
  {
    super(theSource);
    handler = newHandler;
  }

  /**
   * Call insertInterruptHandler on the kernel.
   *
   * @param theElement the kernel to call.
   */
  public void doMessage(Kernel theElement)
  {
    theElement.insertInterruptHandler(handler);
  }
}