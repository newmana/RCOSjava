//******************************************************/
// FILE     : InstructionExecutionMessage.java
// PURPOSE  : Currently executing code inside CPU.
// AUTHOR   : Andrew Newman
// MODIFIED :
// HISTORY  : 01/01/1998  Created
//******************************************************/

package net.sourceforge.rcosjava.messaging.messages.universal;

import net.sourceforge.rcosjava.software.animator.cpu.CPUAnimator;
import net.sourceforge.rcosjava.hardware.memory.Memory;
import net.sourceforge.rcosjava.messaging.postoffices.os.OSMessageHandler;
import java.io.IOException;
import java.io.Serializable;
import fr.dyade.koala.serialization.GeneratorInputStream;

public class InstructionExecution extends UniversalMessageAdapter
{
  private Memory stack;

  public InstructionExecution(OSMessageHandler theSource,
    Memory theStack)
  {
    super(theSource);
    stack = (Memory) theStack.clone();
  }

  public void doMessage(CPUAnimator theElement)
  {
    theElement.updateStack(stack);
  }

  private void writeObject(java.io.ObjectOutputStream out)
		throws IOException
  {
    out.writeObject(stack);
  }

  private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException
  {
    stack = (Memory) in.readObject();
  }

  // Koala XML serialization requirements
  public static void readObject(GeneratorInputStream in)
    throws IOException, ClassNotFoundException
  {
    in.readObject();
  }
}
