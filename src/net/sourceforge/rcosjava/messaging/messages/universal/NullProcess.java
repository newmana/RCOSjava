package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.software.process.ProcessScheduler;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;

/**
 * Called by the Process Scheduler when the process running has complete and
 * there are no other processes waiting to be executed.
 * <P>
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1998
 */
public class NullProcess extends UniversalMessageAdapter
{
  public NullProcess(OSMessageHandler theSource)
  {
    super(theSource);
  }

  /**
   * Calls setCurrentProcessNull on the kernel setting to null the memory and
   * executing process if any.
   *
   * @param theElement the Kenerl to do work on.
   */
  public void doMessage(Kernel theElement)
  {
    theElement.processNull();
  }

  /**
   * Calls setCurrentProcessNull on the Process Scheduler.  Should remove all
   * processes from the execution queue.
   *
   * @param theElement the Process Scheduler to do the work on.
   */
  public void doMessage(ProcessScheduler theElement)
  {
    theElement.setCurrentProcessNull();
  }

  /**
   * Calls screenReset on the CPU Animator.  Resets all the pointers, stack and
   * code execution to their default values.
   *
   * @param theElement the CPU Animator to do the work on.
   */
  public void doMessage(CPUAnimator theElement)
  {
    theElement.screenReset();
  }

  /**
   * A good way to fill up the hard drive is to leave messaging on with no
   * process running with the set to true.  Set to false to prevent this.
   */
  public boolean undoableMessage()
  {
    return false;
  }
}

