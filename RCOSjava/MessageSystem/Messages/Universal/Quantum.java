//******************************************************/
// FILE     : QuantumMessage.java
// PURPOSE  : Process moves from blocked to ready.
// AUTHOR   : David Jones
// MODIFIED : Andrew Newman
// HISTORY  : 24/03/96   Created
//          : 01/07/96   Uses Memory
//          : 03/08/97   Moved to message system
//******************************************************/

package MessageSystem.Messages.Universal;

import Software.Kernel.Kernel;
import Software.Animator.Process.ProcessSchedulerAnimator;
import MessageSystem.PostOffices.Animator.AnimatorMessageHandler;

public class Quantum extends UniversalMessageAdapter
{
  private int iQuantum;
  
  public Quantum(AnimatorMessageHandler theSource, int iNewQuantum)
  {
    super(theSource);
    iQuantum = iNewQuantum;
  }
  
  public void setQuantum(int iNewQuantum)
  {
    iQuantum = iNewQuantum;
  }
  
  public void doMessage(Kernel theElement)
  {
    theElement.setQuantum(iQuantum);
  }  
}

