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
   * Constructor for the BlockCurrentProcess object
   *
   * @param theSource Description of Parameter
   */
  public BlockCurrentProcess(OSMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(Kernel theElement)
  {
    theElement.blockCurrentProcess();
  }
}

