package org.rcosjava.messaging.messages.os;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.kernel.Kernel;

/**
 * Sent from various sources to block the current process usually due to I/O
 * bound function (keyboard, disk, etc).
 * <P>
 * @author Andrew Newman.
 * @created 28th March 1998
 * @version 1.00 $Date$
 */
public class BlockCurrentProcess extends OSMessageAdapter
{
  /**
   * Indicates whether to decrement the program counter (execute the same
   * instruction after the process is blocked).
   */
  private boolean decrementProgramCounter = false;

  /**
   * Create a new BlockCurrentProcess message.
   *
   * @param theSource the sender of the message.
   * @param newDecrementProgramCounter
   */
  public BlockCurrentProcess(OSMessageHandler theSource,
      boolean newDecrementProgramCounter)
  {
    super(theSource);
    decrementProgramCounter = newDecrementProgramCounter;
  }

  /**
   * Call blockCurrentProcess on the Kernel.
   *
   * @param theElement the kernel to block on.
   */
  public void doMessage(Kernel theElement)
  {
    theElement.blockCurrentProcess(decrementProgramCounter);
  }
}

