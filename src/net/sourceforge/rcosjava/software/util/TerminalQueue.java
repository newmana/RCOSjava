//*******************************************************************/
// FILE     : TerminalQueue.java
// PURPOSE  : Implement a queue of Terminals used
//            by the TerminalManager of RCOS.java
//            Extension of fifoQueue class by Andrew Newman
//            Additional features include:
//             - Terminal retrieve (String TerminalId)
//	    	     given a terminal Id return the appropriate terminal
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24 March     1996   Created
//            7  Decemeber 1996   Modified retrieve which can leave
//                                the Terminal in the queue.
//*******************************************************************/

package net.sourceforge.rcosjava.software.util;

import net.sourceforge.rcosjava.hardware.terminal.HardwareTerminal;

public class TerminalQueue extends FIFOQueue
{
  public TerminalQueue( int initialCapacity, int capacityIncrement )
  {
    super( initialCapacity, capacityIncrement );
  }

  // retrieve a Terminal matching the passed String id
  // bRemove is true if you want to remove the Hardware
  // terminal from the queue.
  public HardwareTerminal retrieve (String termId, boolean bRemove)
  {
    if (queueEmpty())
      return null;

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
        if (bRemove)
        {
          tmp = (HardwareTerminal) retrieveCurrent();
        }
        return tmp;
      }

      goToNext();

    } while (!atTail());

    HardwareTerminal ltmp = (HardwareTerminal) peek();

    if (ltmp.getTitle().compareTo(termId) == 0)
    {
      if (bRemove)
      {
        ltmp = (HardwareTerminal) retrieveCurrent();
      }
      return ltmp;
    }
    return null;
  }
}

