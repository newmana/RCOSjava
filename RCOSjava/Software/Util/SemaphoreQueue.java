//**********************************************************************/
// FILE     : SemaphoreQueue.java
// PACKAGE  : Util
// PURPOSE  : For IPC to hold a queue of semaphores
//            written because of problems with HashTables
// AUTHOR   : David Jones using Bruce Jamieson's Semaphore class
//            and Andrew Newman's FIFOQueue class
// MODIFIED : Andrew Newman
// HISTORY  : 31/03/96  Created
//            11/08/98  Last Modified AN.
//**********************************************************************/

package Software.Util;

import Software.IPC.Semaphore;

public class SemaphoreQueue extends FIFOQueue
{
  public SemaphoreQueue(int initialCapacity, int capacityIncrement)
  {
    super(initialCapacity, capacityIncrement);
  }

  // return String type of Process to occur at the specified time
  // return null it no interrupt
  public Semaphore getSemaphore(int iSemaphoreID)
  {
    Semaphore tmp;
    String type;

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
      tmp = (Semaphore) peek();
      if ( tmp.getID() == iSemaphoreID )
      {
        tmp = (Semaphore) retrieveCurrent();
        return tmp;
      }
      goToNext();
    } while (!atTail());

    tmp = (Semaphore) peek();
    if (tmp.getID() == iSemaphoreID)
    {
      tmp = (Semaphore) retrieveCurrent();
      return tmp;
    }
    return null;
  }

  // isMember by numeric id (inc int value)
  public boolean isMember(int iSempahoreID)
  {
    return (peek(iSempahoreID) != null);
  }

  // isMember by string id (defined in program)
  public boolean isMember(String sSempahoreID)
  {
    return (peek(sSempahoreID) != null);
  }

  // peek by numeric id (inc int value)
  public Object peek(int iSemaphoreID)
  {
    Semaphore tmp;

    if (queueEmpty())
      return null;

    goToHead();

    do
    {
      tmp = (Semaphore) peek();
      System.out.println("tmpSID = " + tmp.getID());
      System.out.println("mySID = " + iSemaphoreID);
      if (tmp.getID() == iSemaphoreID)
        return (Object) tmp;
      goToNext();
    } while (!atTail());

    return null;
  }

  // peek by string id (defined in program)
  public Semaphore peek(String sSempahoreID)
  {
    Semaphore tmp;

    if (queueEmpty())
      return null;

    goToHead();

    do
    {
      tmp = (Semaphore)peek();
      if (tmp.getName().compareTo(sSempahoreID) == 0)
        return tmp;
      goToNext();
    } while (!atTail());
    return null;
  }
}
   
