//************************************************************************/
// FILE     : ProcessQueue.java
// PACKAGE  : Util
// PURPOSE  : Implement a queue of Processes used
//            by the ProcessScheduler of RCOS.java
//            Extension of priorityQueue class by Andrew Newman
//            Additional features include
//            - a compare function that knows about the Process class
//            - a GetProcess function that accepts a PID and
//              returns the Process with that PID
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/1996 Created. DJ
//            01/09/1997 Tidy up and modified. AN
//************************************************************************/

package Software.Util;

import Software.Process.*;

public class ProcessQueue extends FIFOQueue
{
  public ProcessQueue( int initialCapacity, int capacityIncrement )
  {
    super(initialCapacity, capacityIncrement);
  }

  // return -1, 0, 1 if first object is less than, equal to or greater than
  // second object
  // In Process Queue order is based on the time the Process is scheduled
  // to occur
  public int compare(Object oFirstProcess, Object oSecondProcess)
  {
    RCOSProcess rpOne = (RCOSProcess) oFirstProcess;
    RCOSProcess rpTwo = (RCOSProcess) oSecondProcess;
  
    if (rpOne.getPriority() == rpTwo.getPriority())
      return 0;
    else if (rpOne.getPriority() < rpTwo.getPriority())
      return -1;
    else
      return 1;
  }

  // return String type of Process to occur at the specified time
  // return null it no interrupt
  public RCOSProcess getProcess(int iPID)
  {
    RCOSProcess rpTmp;
    String sType;

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
      rpTmp = (RCOSProcess) peek();
      if (rpTmp.getPID() == iPID)
      {
        rpTmp = (RCOSProcess) retrieveCurrent();
        return rpTmp;
      }
      goToNext();
    } while (!atTail());

    rpTmp = (RCOSProcess) peek();
    if (rpTmp.getPID() == iPID)
    {
      rpTmp = (RCOSProcess) retrieveCurrent();
      return rpTmp;
    }
    
    return null;
  }
}
   
