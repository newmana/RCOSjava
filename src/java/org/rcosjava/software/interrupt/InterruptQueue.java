package org.rcosjava.software.interrupt;
import org.rcosjava.hardware.cpu.Interrupt;
import org.rcosjava.software.util.PriorityQueue;

/**
 * Implement a queue for Interrupts used by the CPU of RCOS.java. Extension of
 * priorityQueue class by Andrew Newman. Additional features include: a compare
 * function that knows about the Interrupt class, a GetInterrupt function that
 * accepts a time and returns the Interrupt type of an Interrupt that occurs at
 * that time
 * <P>
 * <DT> <B>History:</B>
 * <DD> 05/09/98 Change getInterrupt to return Interrupt instead of String AN
 * </DD></DT>
 * <P>
 * @author Andrew Newman
 * @author David Jones
 * @created 23 March 1996
 * @version 1.00 $Date$
 */
public class InterruptQueue extends PriorityQueue
{
  /**
   * Constructor for the InterruptQueue object
   *
   * @param initialCapacity Description of Parameter
   * @param capacityIncrement Description of Parameter
   */
  public InterruptQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  /**
   * Returns an interrupt if its time is less than the passed time.
   *
   * @param time the number of clicks that the CPU has executed.
   * @return an interrupt that should be generated or null if none is found.
   */
  public Interrupt getInterrupt(int time)
  {
    Interrupt interrupt;

    if (queueEmpty())
    {
      return null;
    }

    goToHead();
    // loop through contents of queue until
    // - we find a match, which should be removed and the type returned
    // - or we reach the end of the Q
    // - or the times of the Interrupts are greater than the time we
    //   are looking for
    do
    {
      interrupt = (Interrupt) peek();
      if (interrupt.getTime() <= time)
      {
        interrupt = (Interrupt) retrieveCurrent();
        return interrupt;
      }
      goToNext();
    } while (!atTail());

    interrupt = (Interrupt) peek();
    if (interrupt.getTime() <= time)
    {
      interrupt = (Interrupt) retrieveCurrent();
      return interrupt;
    }
    return null;
  }

  /**
   * Return if the object is -1, 0, 1 depending on time that the queue has been
   * executing on.
   *
   * @param first the first object to compare to.
   * @param second the second object to compare to.
   * @return -1, 0, 1 if first object is less than, equal to or greater than
   *      second object In Interrupt Queue order is based on the time the
   *      Interrupt is scheduled to occur
   * @throws ClassCastException if the casting of the passed object fails.
   */
  public int compare(Object first, Object second) throws ClassCastException
  {
    Interrupt intOne = (Interrupt) first;
    Interrupt intTwo = (Interrupt) second;
    int iOne;
    int iTwo;

    iOne = intOne.getTime();
    iTwo = intTwo.getTime();

    if (iOne == iTwo)
    {
      return 0;
    }
    else if (iOne < iTwo)
    {
      return -1;
    }
    else
    {
      return 1;
    }
  }
}
