//**************************************************************************/
// FILE     : InterruptQueue.java
// PACKAGE  : Interrupt
// PURPOSE  : Implement a queue for Interrupts used
//            by the CPU of RCOS.java
//            Extension of priorityQueue class by Andrew Newman
//            Additional features include
//            - a compare function that knows about the Interrupt class
//            - a GetInterrupt function that accepts a time and
//              returns the Interrupt type of an Interrupt that
//              occurs at that time
// AUTHOR   :	David Jones
// MODIFIED : Andrew Newman
// HISTORY  :	23/03/95 Created
//            05/09/98 Change getInterrupt to return Interrupt instead of String AN
//
//**************************************************************************/

package net.sourceforge.rcosjava.software.interrupt;

import net.sourceforge.rcosjava.software.util.PriorityQueue;
import net.sourceforge.rcosjava.hardware.cpu.Interrupt;

public class InterruptQueue extends PriorityQueue
{
  public InterruptQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  //********************************
  // return -1, 0, 1 if first object is less than, equal to or greater than
  // second object
  // In Interrupt Queue order is based on the time the Interrupt is scheduled
  // to occur

  public int compare(Object first, Object second)
  {
    Interrupt intOne = (Interrupt) first;
    Interrupt intTwo = (Interrupt) second;
    int iOne, iTwo;
    iOne = intOne.getTime();
    iTwo = intTwo.getTime();

    if (iOne == iTwo)
      return 0;
    else if (iOne < iTwo)
      return -1;
    else
      return 1;
  }

  //****************************************************
  // return String type of Interrupt to occur at the specified time
  // return null if no interrupt

  public Interrupt getInterrupt(int iTime)
  {
    Interrupt intTmp;

    if (queueEmpty())
      return null;

    goToHead();
    // loop through contents of queue until
    // - we find a match, which should be removed and the type returned
    // - or we reach the end of the Q
    // - or the times of the Interrupts are greater than the time we
    //   are looking for
    do
    {
      intTmp = (Interrupt) peek();
      if (intTmp.getTime() <= iTime)
      {
        intTmp = (Interrupt) retrieveCurrent();
        return intTmp;
      }
      goToNext();
    } while (!atTail());

    intTmp = (Interrupt) peek();
    if (intTmp.getTime() <= iTime)
    {
      intTmp = (Interrupt) retrieveCurrent();
      return intTmp;
    }
    return null;
  }
}
