package org.rcosjava.messaging.messages.os;

import org.rcosjava.hardware.cpu.Interrupt;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.kernel.Kernel;

/**
 * Adds an interrupt to the CPU's interrupt queue to be handled.
 *
 * @author Andrew Newman
 * @created 28 April 2002
 */
public class AddInterrupt extends OSMessageAdapter
{
  /**
   * The interrupt to add to the queue.
   */
  private Interrupt interrupt;

  /**
   * Create a new HandleInterrupt message.
   *
   * @param theSource the messaging handler that is sending the message.
   * @param newInterrupt the interrupt to add.
   */
  public AddInterrupt(OSMessageHandler theSource, Interrupt newInterrupt)
  {
    super(theSource);
    interrupt = newInterrupt;
  }

  /**
   * Calls addInterrupt on the CPU via the Kernel.
   *
   * @param theElement the Kernel object to call.
   */
  public void doMessage(Kernel theElement)
  {
    theElement.addInterrupt(interrupt);
  }

  /**
   * Description of the Method
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    if (interrupt.getType().equals("NewProcess"))
    {
      return false;
    }
    else
    {
      return true;
    }
  }

  /**
   * Sets a new interrupt.
   *
   * @param newInterrupt The new Interrupt value
   */
  private void setInterrupt(Interrupt newInterrupt)
  {
    interrupt = newInterrupt;
  }
}