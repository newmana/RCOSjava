package org.rcosjava.messaging.messages.os;

import org.rcosjava.hardware.cpu.Interrupt;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.interrupt.ProgManInterruptHandler;
import org.rcosjava.software.kernel.Kernel;

/**
 * Handle an interrupt.
 *
 * @author Andrew Newman
 * @created 28 April 2002
 */
public class HandleInterrupt extends OSMessageAdapter
{
  /**
   * The interrupt to process.
   */
  private Interrupt interrupt;

  /**
   * Create a new handle interrupt message.
   *
   * @param theSource the sender of the message.
   * @param newInterrupt the interrupt to send.
   */
  public HandleInterrupt(OSMessageHandler theSource,
      Interrupt newInterrupt)
  {
    super(theSource);
    interrupt = newInterrupt;
  }

  /**
   * Call handleInterrupt on the Kernel.
   *
   * @param theElement the kernel object to call.
   */
  public void doMessage(Kernel theElement)
  {
    theElement.handleInterrupt(interrupt);
  }

  /**
   * Determines if the message is undoable.
   *
   * @return true if message can be undone.
   */
  public boolean undoableMessage()
  {
    return true;
  }

  /**
   * Sets a new interrupt.
   *
   * @param newInterrupt the new Interrupt value
   */
  private void setInterrupt(Interrupt newInterrupt)
  {
    interrupt = newInterrupt;
  }
}

