package org.rcosjava.messaging.messages.universal;
import org.rcosjava.messaging.postoffices.animator.AnimatorMessageHandler;
import org.rcosjava.software.kernel.Kernel;

/**
 * Set the quantum that process has to execute on the CPU.
 * <P>
 * @author David Jones.
 * @author Andrew Newman.
 * @created 24th March 1996
 * @version 1.00 $Date$
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

  /**
   * Sets the Quantum attribute of the Quantum object
   *
   * @param newQuantum The new Quantum value
   */
  public void setQuantum(int newQuantum)
  {
    quantum = newQuantum;
  }

  /**
   * Description of the Method
   *
   * @param theElement Description of Parameter
   */
  public void doMessage(Kernel theElement)
  {
    theElement.setQuantum(quantum);
  }
}

