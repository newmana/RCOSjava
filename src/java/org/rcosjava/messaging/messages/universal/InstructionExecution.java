package org.rcosjava.messaging.messages.universal;
import fr.dyade.koala.serialization.GeneratorInputStream;
import java.io.IOException;
import java.io.Serializable;
import org.rcosjava.hardware.memory.Memory;
import org.rcosjava.messaging.postoffices.os.OSMessageHandler;
import org.rcosjava.software.animator.cpu.CPUAnimator;

/**
 * Message indicating the currently executing code inside CPU.
 * <P>
 * @author Andrew Newman.
 * @created 1st January 1998
 * @version 1.00 $Date$
 */
public class InstructionExecution extends UniversalMessageAdapter
{
  /**
   * Description of the Field
   */
  private Memory stack;

  /**
   * Constructor for the InstructionExecution object
   *
   * @param theSource Description of Parameter
   * @param theStack Description of Parameter
   */
  public InstructionExecution(OSMessageHandler theSource,
      Memory theStack)
  {
    super(theSource);
    stack = (Memory) theStack.clone();
  }

  // Koala XML serialization requirements
  /**
   * Description of the Method
   *
   * @param in Description of Parameter
   * @exception IOException Description of Exception
   * @exception ClassNotFoundException Description of Exception
   */
  public static void readObject(GeneratorInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.readObject();
  }

  /**
   * Calls the updateStack on the CPU Animator.
   *
   * @param theElement the CPU Animator to do the work on.
   */
  public void doMessage(CPUAnimator theElement)
  {
    theElement.updateStack(stack);
  }

  /**
   * Description of the Method
   *
   * @param out Description of Parameter
   * @exception IOException Description of Exception
   */
  private void writeObject(java.io.ObjectOutputStream out)
    throws IOException
  {
    out.writeObject(stack);
  }

  /**
   * Description of the Method
   *
   * @param in Description of Parameter
   * @exception IOException Description of Exception
   * @exception ClassNotFoundException Description of Exception
   */
  private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    stack = (Memory) in.readObject();
  }
}
