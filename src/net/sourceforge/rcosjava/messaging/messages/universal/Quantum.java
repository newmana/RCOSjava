package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.kernel.Kernel;
import net.sourceforge.rcosjava.software.animator.process.ProcessSchedulerAnimator;
import net.sourceforge.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;

/**
 * Set the quantum that process has to execute on the CPU.
 * <P>
 * @author David Jones.
 * @author Andrew Newman.
 * @version 1.00 $Date$
 * @created 24th March 1996
 */
public class Quantum extends UniversalMessageAdapter
{
  /**
   * The number of cycles for a CPU to execute.
   */
  private int quantum;

  /**
   * Create a quantum message from the Animator with the given quantum value.
   *
   * @param theSource the sender of the message
   * @param newQuantum the new value to set the kernel to when received.
   */
  public Quantum(AnimatorMessageHandler theSource, int newQuantum)
  {
    super(theSource);
    quantum = newQuantum;
  }

  public void setQuantum(int newQuantum)
  {
    quantum = newQuantum;
  }

  public void doMessage(Kernel theElement)
  {
    theElement.setQuantum(quantum);
  }
}

