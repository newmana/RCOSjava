package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.cpu.CPUAnimator;
import org.rcosjava.software.kernel.Kernel;
import org.rcosjava.software.process.ProcessScheduler;

/**
 * Called by the Process Scheduler when the process running has complete and
 * there are no other processes waiting to be executed.
 * <P>
 * @author Andrew Newman.
 * @created 24th March 1998
 * @version 1.00 $Date$
 */
public class NullProcess extends UniversalMessageAdapter
{
  /**
   * Constructor for the NullProcess object
   *
   * @param theSource Description of Parameter
   */
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
    //theElement.nullProcess();
  }

  /**
   * Calls screenReset on the CPU Animator. Resets all the pointers, stack and
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
   * process running with the set to true. Set to false to prevent this.
   *
   * @return Description of the Returned Value
   */
  public boolean undoableMessage()
  {
    return false;
  }
}

