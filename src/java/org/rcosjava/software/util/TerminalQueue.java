package org.rcosjava.software.util;
import org.rcosjava.hardware.terminal.HardwareTerminal;

/**
 * Implement a queue of Terminals used by the TerminalManager of RCOS.java
 * Extension of fifoQueue class by Andrew Newman Additional features include:
 * Terminal retrieve (String TerminalId) given a terminal Id return the
 * appropriate terminal.
 * <P>
 * <DT> <B>History:</B>
 * <DD> 7 Decemeber 1996 Modified retrieve which can leave the Terminal in the
 * queue. </DD> </DT>
 * <P>
 * @author Andrew Newman.
 * @created 24th March 1996
 * @see org.rcosjava.software.terminal.TerminalManager
 * @version 1.00 $Date$
 */
public class TerminalQueue extends FIFOQueue
{
  /**
   * Constructor for the TerminalQueue object
   *
   * @param initialCapacity Description of Parameter
   * @param capacityIncrement Description of Parameter
   */
  public TerminalQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  /**
   * Retrieve a Terminal matching. Will return null if the terminal is not
   * found.
   *
   * @param termId the passed String identifying the terminal.
   * @param remove is true if you want to remove the Hardware terminal from the
   *      queue.
   * @return Description of the Returned Value
   */
  public HardwareTerminal retrieve(String termId, boolean remove)
  {
    if (queueEmpty())
    {
      return null;
    }

    goToHead();

    // loop through contents of queue until
    // - we find a match, which should be removed and the process returned
    // - or we reach the end of the Q
    // - or the PID of the Processs are greater than the PID we
    //   are looking for

    do
    {
      HardwareTerminal tmp = (HardwareTerminal) peek();

      if (tmp.getTitle().compareTo(termId) == 0)
      {
        if (remove)
        {
          tmp = (HardwareTerminal) retrieveCurrent();
        }
        return tmp;
      }

      goToNext();

    } while (!atTail());

    HardwareTerminal tmpTerminal = (HardwareTerminal) peek();

    if (tmpTerminal.getTitle().compareTo(termId) == 0)
    {
      if (remove)
      {
        tmpTerminal = (HardwareTerminal) retrieveCurrent();
      }
      return tmpTerminal;
    }
    return null;
  }
}

